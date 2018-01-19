package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.RefreshMacroLocationsCommand
import com.networkedassets.git4c.boundary.outbound.RequestId
import com.networkedassets.git4c.core.business.*
import com.networkedassets.git4c.core.datastore.cache.RefreshLocationUseCaseCache
import com.networkedassets.git4c.core.datastore.repositories.MacroLocationDatabase
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.data.MacroLocation
import com.networkedassets.git4c.data.MacroType
import com.networkedassets.git4c.delivery.executor.execution.UseCase
import java.util.*

class RefreshMacroLocationsUseCase(
        val macroLocationDatabase: MacroLocationDatabase,
        val macroSettingsDatabase: MacroSettingsDatabase,
        val repositoryDatabase: RepositoryDatabase,
        val spaceManager: SpaceManager,
        val pageManager: PageManager,
        val pageMacroExtractor: PageMacroExtractor,
        val refreshLocationUseCaseCache: RefreshLocationUseCaseCache,
        val confluenceQueryExecutorHolder: ConfluenceQueryExecutorHolder
) : UseCase<RefreshMacroLocationsCommand, RequestId> {

    override fun execute(request: RefreshMacroLocationsCommand): Result<RequestId, Exception> {

        val anyExistingJobInProgress = refreshLocationUseCaseCache.getAll().find { it.state == Computation.ComputationState.RUNNING }
        if (anyExistingJobInProgress != null) {
            return Result.of { RequestId(anyExistingJobInProgress.id) }
        }

        val id = UUID.randomUUID().toString()
        refreshLocationUseCaseCache.put(id, Computation(id))
        confluenceQueryExecutorHolder.getExecutor()
                .execute { refreshProcess(id) }
        return Result.of { RequestId(id) }
    }


    private fun refreshProcess(id: String) {
        macroLocationDatabase.removeAll()
        val spaceKeys = spaceManager.getAllSpaceKeys().asSequence().iterator()
        confluenceQueryExecutorHolder.getExecutor()
                .execute { searchForMacrosInSpaces(id, spaceKeys) }
    }

    private fun searchForMacrosInSpaces(operationId: String, spaceKeys: Iterator<String>) {
        if (spaceKeys.hasNext()) {
            searchForMacrosInSpacesLoop(operationId, spaceKeys)
        } else {
            finishProcess(operationId)
        }
    }

    private fun searchForMacrosInSpacesLoop(operationId: String, spaceKeys: Iterator<String>) {
        val spaceKey = spaceKeys.next()
        spaceManager.getSpace(spaceKey) ?: return confluenceQueryExecutorHolder.getExecutor()
                .execute { searchForMacrosInSpaces(operationId, spaceKeys) }
        confluenceQueryExecutorHolder.getExecutor()
                .execute { searchInPagesForSpace(operationId, spaceKey, spaceKeys) }
    }

    private fun searchInPagesForSpace(operationId: String, spaceKey: String, spaceKeys: Iterator<String>) {
        val pages = pageManager.getAllPagesKeysForSpace(spaceKey).asSequence().iterator()
        confluenceQueryExecutorHolder.getExecutor()
                .execute { searchForMacrosOnPagesAtSpace(operationId, pages, spaceKeys, spaceKey) }
    }

    private fun searchForMacrosOnPagesAtSpace(operationId: String, pages: Iterator<Long>, spaceKeys: Iterator<String>, spaceKey: String) {
        if (pages.hasNext()) {
            confluenceQueryExecutorHolder.getExecutor()
                    .execute { searchMacroOnPageLoop(operationId, pages, spaceKeys, spaceKey) }
        } else {
            searchForMacrosInSpaces(operationId, spaceKeys)
        }
    }

    private fun searchMacroOnPageLoop(operationId: String, pages: Iterator<Long>, spaceKeys: Iterator<String>, spaceKey: String) {
        val pageId = pages.next()
        val page = pageManager.getPage(pageId) ?: return confluenceQueryExecutorHolder.getExecutor()
                .execute { searchForMacrosOnPagesAtSpace(operationId, pages, spaceKeys, spaceKey) }

        pageMacroExtractor.extractMacro(page.content).forEach { macro ->
            macroLocationDatabase.put(macro.uuid, MacroLocation(macro.uuid, pageId.toString(), spaceKey))
            updateMacroType(macro)
        }

        confluenceQueryExecutorHolder.getExecutor()
                .execute { searchForMacrosOnPagesAtSpace(operationId, pages, spaceKeys, spaceKey) }
    }

    private fun updateMacroType(macro: Macro) {
        macroSettingsDatabase.get(macro.uuid)?.apply {
            this.type = MacroType.valueOf(macro.type.name)
            macroSettingsDatabase.put(macro.uuid, this)
        }
    }

    private fun finishProcess(operationId: String) {
        refreshLocationUseCaseCache.put(operationId, Computation(operationId, Computation.ComputationState.FINISHED, data = Unit))
    }
}