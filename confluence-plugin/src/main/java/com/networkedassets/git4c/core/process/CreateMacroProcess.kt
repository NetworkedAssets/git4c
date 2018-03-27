package com.networkedassets.git4c.core.process

import com.networkedassets.git4c.core.business.RepositoryPullExecutorHolder
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.datastore.cache.MacroToBeViewedPrepareLockCache
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.data.MacroView
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.utils.getLogger

class CreateMacroProcess(
        val macroViewCache: MacroToBeViewedPrepareLockCache,
        val importer: SourcePlugin,
        val repositoryPullExecutorHolder: RepositoryPullExecutorHolder
) {

    val log = getLogger()

    fun fetchDataFromSource(
            macroSettings: MacroSettings,
            repository: Repository
    ) {
        val macroId = macroSettings.uuid
        macroViewCache.put(macroId, MacroView(macroId, MacroView.MacroViewStatus.CHECKING))
        pullFromRepository(repository, macroSettings)
    }

    private fun pullFromRepository(repository: Repository, macroSettings: MacroSettings) {
        repositoryPullExecutorHolder.getExecutor()
                .execute({ pullFromRemote(repository, macroSettings) })
    }

    private fun pullFromRemote(repository: Repository, macroSettings: MacroSettings) {
        val macroId = macroSettings.uuid
        try {
            macroViewCache.put(macroId, MacroView(macroId, MacroView.MacroViewStatus.CHECKING))
            importer.pull(repository, macroSettings.branch).use {
                macroViewCache.put(macroId, MacroView(macroId, MacroView.MacroViewStatus.READY))
            }
        } catch (e: Exception) {
            macroViewCache.put(macroId, MacroView(macroId, MacroView.MacroViewStatus.FAILED))
            log.warn("There was problem during importing repository: ${repository.repositoryPath} for MacroId=${macroId} - Error=${e.message}")
        }

    }
}
