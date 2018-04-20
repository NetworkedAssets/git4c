package com.networkedassets.git4c.core

import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.GetSpacesWithMacroQuery
import com.networkedassets.git4c.boundary.GetSpacesWithMacroResultRequest
import com.networkedassets.git4c.boundary.outbound.*
import com.networkedassets.git4c.core.business.ConfluenceQueryExecutorHolder
import com.networkedassets.git4c.core.business.PageMacroExtractor
import com.networkedassets.git4c.core.business.PageManager
import com.networkedassets.git4c.core.business.SpaceManager
import com.networkedassets.git4c.core.bussiness.ComputationCache
import com.networkedassets.git4c.core.datastore.repositories.GlobForMacroDatabase
import com.networkedassets.git4c.core.datastore.repositories.MacroLocationDatabase
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.usecase.async.ComputationResultUseCase
import com.networkedassets.git4c.core.usecase.async.SingleInSystemAsyncUseCase
import com.networkedassets.git4c.data.MacroLocation
import com.networkedassets.git4c.data.MacroType
import com.networkedassets.git4c.utils.getLogger

class GetSpacesWithMacroUseCase(
        components: BussinesPluginComponents,
        private val spaceManager: SpaceManager = components.utils.spaceManager,
        private val pageManager: PageManager = components.utils.pageManager,
        val macroSettingsDatabase: MacroSettingsDatabase = components.database.macroSettingsDatabase,
        val repositoryDatabase: RepositoryDatabase = components.providers.repositoryProvider,
        private val globForMacroDatabase: GlobForMacroDatabase = components.providers.globsForMacroProvider,
        private val pageMacroExtractor: PageMacroExtractor = components.macro.pageMacroExtractor,
        private val confluenceQueryExecutorHolder: ConfluenceQueryExecutorHolder = components.executors.confluenceQueryExecutor,
        val macroLocationDatabase: MacroLocationDatabase = components.database.macroLocationDatabase,
        computation: ComputationCache<Spaces> = components.async.spacesWithMacroComputationCache
) : SingleInSystemAsyncUseCase<GetSpacesWithMacroQuery, Spaces>
(components, computation, confluenceQueryExecutorHolder) {

    val log = getLogger()

    override fun executedAsync(requestId: String, request: GetSpacesWithMacroQuery) {
        firstMakeACleanUp(requestId)
    }

    private fun firstMakeACleanUp(computationId: String) {
        val listOfPages = macroLocationDatabase.getAll()
                .groupBy { Pair(it.spaceKey, it.pageId) }
                .asSequence()
                .map { Pair(it.key, it.value) }.iterator()

        confluenceQueryExecutorHolder.getExecutor()
                .execute { checkIfPageNeedCleanUpLoop(computationId, listOfPages) }
    }

    private fun checkIfPageNeedCleanUpLoop(computationId: String, listOfPages: Iterator<Pair<Pair<String, String>, List<MacroLocation>>>) {
        if (listOfPages.hasNext()) {
            val element = listOfPages.next()
            checkIfPageNeedCleanUpOperation(computationId, element)
            confluenceQueryExecutorHolder.getExecutor()
                    .execute { checkIfPageNeedCleanUpLoop(computationId, listOfPages) }
        } else {
            confluenceQueryExecutorHolder.getExecutor()
                    .execute { prepareDataForSpacesWithMacros(computationId) }
        }
    }

    private fun checkIfPageNeedCleanUpOperation(computationId: String, element: Pair<Pair<String, String>, List<MacroLocation>>) {
        val (_, pageId) = element.first
        val macroList = element.second

        val page = pageManager.getPage(pageId.toLong())

        if (page == null) {
            macroList.forEach {
                macroLocationDatabase.remove(it.uuid)
            }
            return
        }

        val macrosFromDB = macroList.map { it.uuid }.toSet()

        val macrosFromPage = pageMacroExtractor.extractMacro(page.content).map { it.uuid }.toSet()

        val macrosToRemove = macrosFromDB - macrosFromPage

        macrosToRemove.forEach {
            macroLocationDatabase.remove(it)
        }
    }

    private fun prepareDataForSpacesWithMacros(computationId: String) {

        temporal(computationId, Spaces(arrayListOf()))

        val spaces = macroLocationDatabase.getAll()
                .asSequence()
                .map {
                    Triple(it.spaceKey, it.pageId, it.uuid)
                }
                .groupBy({ it.first }, { Pair(it.second, it.third) })
                .asSequence()
                .map { Pair(it.key, it.value) }
                .iterator()

        confluenceQueryExecutorHolder.getExecutor()
                .execute { calculateSpacesWithMacrosLoop(computationId, spaces) }
    }

    private fun calculateSpacesWithMacrosLoop(computationId: String, spaces: Iterator<Pair<String, List<Pair<String, String>>>>) {
        if (spaces.hasNext()) {
            calculateSpacesWithMacroOperation(computationId, spaces)
        } else {
            finishOperation(computationId)
        }
    }

    private fun calculateSpacesWithMacroOperation(computationId: String, spacesIterator: Iterator<Pair<String, List<Pair<String, String>>>>) {
        val spaces = spacesIterator.next()
        val result = data(computationId)
        val spaceId = spaces.first
        val space = spaceManager.getSpace(spaceId) ?: return confluenceQueryExecutorHolder.getExecutor()
                .execute { calculateSpacesWithMacrosLoop(computationId, spacesIterator) }
        val list = mutableListOf<Space>()
        list.addAll(result!!.data!!.spaces)
        list.add(Space(space.name, space.url, listOf()))
        val spacesList = Spaces(list)
        temporal(computationId, spacesList)

        val pages = spaces.second
                .asSequence()
                .groupBy({ it.first }, { it.second })
                .asSequence()
                .map { Pair(it.key, it.value) }
                .iterator()
        confluenceQueryExecutorHolder.getExecutor()
                .execute { pagesForSpaceLoop(computationId, pages, space.name, space.url, spacesIterator) }
    }

    private fun pagesForSpaceLoop(computationId: String, pages: Iterator<Pair<String, List<String>>>, spaceName: String, spaceUrl: String, spacesIterator: Iterator<Pair<String, List<Pair<String, String>>>>) {
        if (pages.hasNext()) {
            pagesForSpaceOperation(computationId, pages, spacesIterator, spaceName, spaceUrl)
        } else {
            calculateSpacesWithMacrosLoop(computationId, spacesIterator)
        }
    }

    private fun pagesForSpaceOperation(computationId: String, pages: Iterator<Pair<String, List<String>>>, spacesIterator: Iterator<Pair<String, List<Pair<String, String>>>>, spaceName: String, spaceUrl: String) {
        val result = data(computationId)
        val currentPages = result!!.data!!.spaces.find { it.name == spaceName && it.url == spaceUrl }!!.pages
        val page = pages.next()
        val pageId = page.first
        val existingPage = pageManager.getPage(pageId.toLong()) ?: return confluenceQueryExecutorHolder.getExecutor()
                .execute { pagesForSpaceLoop(computationId, pages, spaceName, spaceUrl, spacesIterator) }
        val macros = page.second
                .asSequence()
                .mapNotNull macro@ {

                    val macroId = it

                    val macro = macroSettingsDatabase.get(macroId) ?: return@macro null

                    val globs = globForMacroDatabase.getByMacro(macroId)

                    if (macro.type != null && macro.type == MacroType.SINGLEFILE) {
                        return@macro SinglePageMacro(macroId, globs.first().glob)
                    } else if (macro.type != null && macro.type == MacroType.MULTIFILE) {
                        return@macro MultiPageMacro(macroId)
                    } else if (globs.size == 1) {
                        return@macro SinglePageMacro(macroId, globs.first().glob)
                    } else {
                        return@macro MultiPageMacro(macroId)
                    }

                }.filterNotNull().toList()
        val pageToIndex = Page(existingPage.name, existingPage.url, macros)
        val currentPagesList = mutableListOf<Page>()
        currentPagesList.addAll(currentPages)
        currentPagesList.add(pageToIndex)
        val spaceToIndex = Space(spaceName, spaceUrl, currentPagesList)
        val existingSpace = result.data!!.spaces.find { it.name == spaceName && it.url == spaceUrl }
        val existingSpaces = result.data!!.spaces
        val newSpaces = mutableListOf<Space>()
        newSpaces.addAll(existingSpaces)
        newSpaces.remove(existingSpace)
        newSpaces.add(spaceToIndex)
        val spacesList = Spaces(newSpaces)
        temporal(computationId, spacesList)
        confluenceQueryExecutorHolder.getExecutor()
                .execute { pagesForSpaceLoop(computationId, pages, spaceName, spaceUrl, spacesIterator) }
    }


    private fun finishOperation(computationId: String) {
        val result = data(computationId)
        success(computationId, result!!.data!!)
    }
}

class GetSpacesWithMacroResultUseCase(
        components: BussinesPluginComponents,
        computations: ComputationCache<Spaces> = components.async.spacesWithMacroComputationCache
) : ComputationResultUseCase<GetSpacesWithMacroResultRequest, Spaces>
(components, computations)