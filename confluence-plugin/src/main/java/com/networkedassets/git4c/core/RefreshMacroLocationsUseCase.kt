package com.networkedassets.git4c.core

import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.RefreshMacroLocationsCommand
import com.networkedassets.git4c.boundary.RefreshMacroLocationsResultCommand
import com.networkedassets.git4c.core.business.*
import com.networkedassets.git4c.core.bussiness.ComputationCache
import com.networkedassets.git4c.core.datastore.repositories.MacroLocationDatabase
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.usecase.async.ComputationResultUseCase
import com.networkedassets.git4c.core.usecase.async.SingleInSystemAsyncUseCase
import com.networkedassets.git4c.data.MacroLocation
import com.networkedassets.git4c.data.MacroType

class RefreshMacroLocationsUseCase(
        components: BussinesPluginComponents,
        val macroLocationDatabase: MacroLocationDatabase = components.database.macroLocationDatabase,
        val macroSettingsDatabase: MacroSettingsDatabase = components.providers.macroSettingsProvider,
        val repositoryDatabase: RepositoryDatabase = components.providers.repositoryProvider,
        val spaceManager: SpaceManager = components.utils.spaceManager,
        val pageManager: PageManager = components.utils.pageManager,
        val pageMacroExtractor: PageMacroExtractor = components.macro.pageMacroExtractor,
        computations: ComputationCache<Unit> = components.async.refreshLocationUseCaseCache,
        confluenceQueryExecutorHolder: ConfluenceQueryExecutorHolder = components.executors.confluenceQueryExecutor
) : SingleInSystemAsyncUseCase<RefreshMacroLocationsCommand, Unit>
(components, computations, confluenceQueryExecutorHolder) {

    override fun executedAsync(requestId: String, request: RefreshMacroLocationsCommand) {
        refreshProcess(requestId)
    }

    private fun refreshProcess(operationId: String) {
        macroLocationDatabase.removeAll()
        val spaceKeys = spaceManager.getAllSpaceKeys().asSequence().iterator()
        executorHolder.getExecutor()
                .execute { searchForMacrosInSpaces(operationId, spaceKeys) }
    }

    private fun searchForMacrosInSpaces(operationId: String, spaceKeys: Iterator<String>) {
        if (spaceKeys.hasNext()) {
            searchForMacrosInSpacesLoop(operationId, spaceKeys)
        } else {
            success(operationId, Unit)
        }
    }

    private fun searchForMacrosInSpacesLoop(operationId: String, spaceKeys: Iterator<String>) {
        val spaceKey = spaceKeys.next()
        spaceManager.getSpace(spaceKey) ?: return executorHolder.getExecutor()
                .execute { searchForMacrosInSpaces(operationId, spaceKeys) }
        executorHolder.getExecutor()
                .execute { searchInPagesForSpace(operationId, spaceKey, spaceKeys) }
    }

    private fun searchInPagesForSpace(operationId: String, spaceKey: String, spaceKeys: Iterator<String>) {
        val pages = pageManager.getAllPagesKeysForSpace(spaceKey).asSequence().iterator()
        executorHolder.getExecutor()
                .execute { searchForMacrosOnPagesAtSpace(operationId, pages, spaceKeys, spaceKey) }
    }

    private fun searchForMacrosOnPagesAtSpace(operationId: String, pages: Iterator<Long>, spaceKeys: Iterator<String>, spaceKey: String) {
        if (pages.hasNext()) {
            executorHolder.getExecutor()
                    .execute { searchMacroOnPageLoop(operationId, pages, spaceKeys, spaceKey) }
        } else {
            searchForMacrosInSpaces(operationId, spaceKeys)
        }
    }

    private fun searchMacroOnPageLoop(operationId: String, pages: Iterator<Long>, spaceKeys: Iterator<String>, spaceKey: String) {
        val pageId = pages.next()
        val page = pageManager.getPage(pageId) ?: return executorHolder.getExecutor()
                .execute { searchForMacrosOnPagesAtSpace(operationId, pages, spaceKeys, spaceKey) }

        pageMacroExtractor.extractMacro(page.content).forEach { macro ->
            macroLocationDatabase.put(macro.uuid, MacroLocation(macro.uuid, pageId.toString(), spaceKey))
            updateMacroType(macro)
        }

        executorHolder.getExecutor()
                .execute { searchForMacrosOnPagesAtSpace(operationId, pages, spaceKeys, spaceKey) }
    }

    private fun updateMacroType(macro: Macro) {
        macroSettingsDatabase.get(macro.uuid)?.apply {
            this.type = MacroType.valueOf(macro.type.name)
            macroSettingsDatabase.put(macro.uuid, this)
        }
    }
}

class RefreshMacroLocationsResultUseCase(
        components: BussinesPluginComponents,
        computations: ComputationCache<Unit> = components.async.refreshLocationUseCaseCache
) : ComputationResultUseCase<RefreshMacroLocationsResultCommand, Unit>
(components, computations)