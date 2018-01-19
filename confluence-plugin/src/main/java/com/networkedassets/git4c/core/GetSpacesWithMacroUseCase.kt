package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetSpacesWithMacroQuery
import com.networkedassets.git4c.boundary.outbound.*
import com.networkedassets.git4c.boundary.outbound.Page
import com.networkedassets.git4c.boundary.outbound.Space
import com.networkedassets.git4c.core.business.*
import com.networkedassets.git4c.core.datastore.cache.SpacesWithMacroResultCache
import com.networkedassets.git4c.core.datastore.repositories.GlobForMacroDatabase
import com.networkedassets.git4c.core.datastore.repositories.MacroLocationDatabase
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.data.MacroLocation
import com.networkedassets.git4c.data.MacroType
import com.networkedassets.git4c.delivery.executor.execution.UseCase
import com.networkedassets.git4c.utils.getLogger
import com.networkedassets.git4c.utils.info
import java.util.*

class GetSpacesWithMacroUseCase(
        private val spaceManager: SpaceManager,
        private val pageManager: PageManager,
        val macroSettingsDatabase: MacroSettingsDatabase,
        val repositoryDatabase: RepositoryDatabase,
        private val globForMacroDatabase: GlobForMacroDatabase,
        private val pageMacroExtractor: PageMacroExtractor,
        private val confluenceQueryExecutorHolder: ConfluenceQueryExecutorHolder,
        val cache: SpacesWithMacroResultCache,
        val macroLocationDatabase: MacroLocationDatabase
) : UseCase<GetSpacesWithMacroQuery, RequestId> {

    val log = getLogger()

    override fun execute(request: GetSpacesWithMacroQuery): Result<RequestId, Exception> {


        val currentElements = cache.getAll()
        val existingResult = currentElements.find { computation ->
            computation.state == Computation.ComputationState.RUNNING
        }

        if (existingResult != null) {
            log.info { "There is another operation currently in progress, so you will receive result from that operation" }
            return Result.of { RequestId(existingResult.id) }
        }

        log.info { "There is no another operation currently in progress, generating new process to gather data" }

        val id = UUID.randomUUID().toString()
        cache.put(id, Computation(id))

        confluenceQueryExecutorHolder.getExecutor()
                .execute { firstMakeACleanUp(id) }

        return Result.of { RequestId(id) }
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

        cache.put(computationId, Computation(computationId, Computation.ComputationState.RUNNING, data = Spaces(arrayListOf())))

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
        val result = cache.get(computationId)
        val spaceId = spaces.first
        val space = spaceManager.getSpace(spaceId) ?: return confluenceQueryExecutorHolder.getExecutor()
                .execute { calculateSpacesWithMacrosLoop(computationId, spacesIterator) }
        val list = mutableListOf<Space>()
        list.addAll(result!!.data!!.spaces)
        list.add(Space(space.name, space.url, listOf()))
        val spacesList = Spaces(list)
        cache.put(computationId, Computation(computationId, Computation.ComputationState.RUNNING, data = spacesList))
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
        val result = cache.get(computationId)
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
                    val repositoryUuid = macro.repositoryUuid ?: return@macro null
                    val repository = repositoryDatabase.get(repositoryUuid) ?: return@macro null

                    val globs = globForMacroDatabase.getByMacro(macroId)

                    if (macro.type != null && macro.type == MacroType.SINGLEFILE) {
                        return@macro SinglePageMacro(macroId, repository.repositoryPath, globs.first().glob)
                    } else if (macro.type != null && macro.type == MacroType.MULTIFILE) {
                        return@macro MultiPageMacro(macroId, repository.repositoryPath)
                    } else if (globs.size == 1) {
                        return@macro SinglePageMacro(macroId, repository.repositoryPath, globs.first().glob)
                    } else {
                        return@macro MultiPageMacro(macroId, repository.repositoryPath)
                    }

                }.filterNotNull().toList()
        val pageToIndex = Page(existingPage.name, existingPage.url, macros)
        val currentPagesList = mutableListOf<Page>()
        currentPagesList.addAll(currentPages)
        currentPagesList.add(pageToIndex)
        val spaceToIndex = Space(spaceName, spaceUrl, currentPagesList)
        val existingSpace = result!!.data!!.spaces.find { it.name == spaceName && it.url == spaceUrl }
        val existingSpaces = result!!.data!!.spaces
        val newSpaces = mutableListOf<Space>()
        newSpaces.addAll(existingSpaces)
        newSpaces.remove(existingSpace)
        newSpaces.add(spaceToIndex)
        val spacesList = Spaces(newSpaces)
        cache.put(computationId, Computation(computationId, Computation.ComputationState.RUNNING, data = spacesList))
        confluenceQueryExecutorHolder.getExecutor()
                .execute { pagesForSpaceLoop(computationId, pages, spaceName, spaceUrl, spacesIterator) }
    }


    private fun finishOperation(computationId: String) {
        val result = cache.get(computationId)
        cache.put(computationId, Computation(computationId, Computation.ComputationState.FINISHED, data = result!!.data))
    }
}