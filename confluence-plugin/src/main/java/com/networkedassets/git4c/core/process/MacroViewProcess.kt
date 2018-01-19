package com.networkedassets.git4c.core.process

import com.networkedassets.git4c.core.datastore.cache.DocumentsViewCache
import com.networkedassets.git4c.core.datastore.cache.MacroToBeViewedPrepareLockCache
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.process.action.RefreshMacroAction
import com.networkedassets.git4c.data.MacroView
import com.networkedassets.git4c.utils.getLogger

class MacroViewProcess(
        val macroViewCache: MacroToBeViewedPrepareLockCache,
        val refreshMacroProcess: RefreshMacroAction,
        val macroSettingsDatabase: MacroSettingsDatabase,
        val repositoryDatabase: RepositoryDatabase,
        val cache: DocumentsViewCache
) {

    val log = getLogger()

    fun prepareMacroToBeViewed(macroId: String) {

        val currentStatus = macroViewCache.get(macroId)?.macroViewStatus ?: MacroView.MacroViewStatus.FAILED

        if (currentStatus != MacroView.MacroViewStatus.CHECKING) {

            macroViewCache.put(macroId, MacroView(macroId, MacroView.MacroViewStatus.CHECKING))
            log.info("Will check macro if should be updated: ${macroId}")

            val macroSettings = macroSettingsDatabase.get(macroId) ?: return failed(macroId)
            if (macroSettings.repositoryUuid == null) return failed(macroId)
            val repository = repositoryDatabase.get(macroSettings.repositoryUuid) ?: return failed(macroId)

            refreshMacroProcess.fetchDataFromSourceThenConvertAndCache(
                    macroSettings.uuid,
                    repository.repositoryPath,
                    macroSettings.branch,
                    Runnable { ready(macroId) },
                    Runnable { failed(macroId) }
            )

        } else log.info("Will skip checking macro, as it's already in progress by another process: ${macroId}")
    }

    private fun failed(macroId: String) {
        macroViewCache.put(macroId, MacroView(macroId, MacroView.MacroViewStatus.FAILED))
        log.info("Has failed to reender macro ${macroId}")
    }

    private fun ready(macroId: String) {
        macroViewCache.put(macroId, MacroView(macroId, MacroView.MacroViewStatus.READY))
        log.info("Will skip checking macro, as it's already done by previous process: ${macroId}")
    }
}