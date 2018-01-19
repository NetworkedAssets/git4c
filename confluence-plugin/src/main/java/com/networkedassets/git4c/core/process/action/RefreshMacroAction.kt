package com.networkedassets.git4c.core.process.action

import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.datastore.cache.DocumentsViewCache
import com.networkedassets.git4c.core.datastore.cache.RepositoryRevisionCache
import com.networkedassets.git4c.core.datastore.repositories.GlobForMacroDatabase
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.exceptions.VerificationException
import com.networkedassets.git4c.utils.contentEquals
import com.networkedassets.git4c.utils.debug
import com.networkedassets.git4c.utils.getLogger
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class RefreshMacroAction(
        val documentsViewCache: DocumentsViewCache,
        val importer: SourcePlugin,
        val revisionCache: RepositoryRevisionCache,
        val globForMacroDatabase: GlobForMacroDatabase,
        val macroSettingsDatabase: MacroSettingsDatabase,
        val repositoryDatabase: RepositoryDatabase,
        val revisionCheckAction: RevisionCheckAction,
        val pullRepositoryAction: PullRepositoryAction
) {

    val checkExecutor = Executors.newScheduledThreadPool(1);

    val log = getLogger()

    @Throws(VerificationException::class)
    fun fetchDataFromSourceThenConvertAndCache(
            macroId: String,
            repositoryPath: String,
            repositoryBranch: String,
            ready: Runnable,
            failed: Runnable
    ) {
        if (importer.isLocked(repositoryPath)) {
            checkExecutor.schedule(retry(macroId, repositoryPath, repositoryBranch, ready, failed), 200, TimeUnit.MILLISECONDS)
            return
        } else
            planToRefreshOfRepository(repositoryPath, macroId, repositoryBranch, ready, failed)
    }

    private fun planToRefreshOfRepository(repositoryPath: String, macroId: String, repositoryBranch: String, ready: Runnable, failed: Runnable) {
        checkExecutor.schedule({

            //log.debug { "Operation of checking Macro=${macroId} if it need to be refreshed has been started" }

            if (importer.isLocked(repositoryPath)) {
                // log.debug { "Macro=${macroId} will need to be check later, as it's repository with RepositoryPath=${repository.repositoryPath} is locked now." }
                checkExecutor.schedule(retry(macroId, repositoryPath, repositoryBranch, ready, failed), 500, TimeUnit.MILLISECONDS)
                return@schedule
            }

            val macroSettings = macroSettingsDatabase.get(macroId)

            if (macroSettings == null) {
                log.debug { "Will discard operation on Macro=${macroId} as it has not been found" }
                return@schedule failed.run()
            }

            if (macroSettings.repositoryUuid == null) {
                log.debug { "Will discard operation on Macro=${macroId} as it has no repository defined inside of a macro settings" }
                return@schedule failed.run()
            }

            val repository = repositoryDatabase.get(macroSettings.repositoryUuid)
            if (repository == null) {
                log.debug { "Will discard operation on Macro=${macroId} as it's repository with RepositoryId=${macroSettings.repositoryUuid} has not been found" }
                return@schedule failed.run()
            }

            val globs = globForMacroDatabase.getByMacro(macroSettings.uuid)

            val macroInCache = documentsViewCache.get(macroSettings.uuid).takeIf {
                it?.currentBranch == macroSettings.branch && globs.map { it.glob } contentEquals it.glob.map { it.glob }
            }
            if (macroInCache == null) {
                // log.debug { "Macro=${macroId} has not been found in the cache so there is need to perform a plan for pull operation." }
                pull(macroId, repositoryPath, repositoryBranch, ready, failed).run()
                return@schedule
            }

            if (revisionCache.exists(repository.repositoryPath, macroSettings.branch)) {
                log.debug { "Macro=${macroId} has a revision present in the cache so no revision checking will be performed." }
                return@schedule ready.run()
            } else {
                log.debug { "Macro=${macroId} has a no revision present in the cache, so planning to check it's revision in repository." }
                checkRevision(macroId, repositoryPath, repositoryBranch, failed, ready).run()
            }

        }, 50, TimeUnit.MILLISECONDS)
    }

    private fun checkRevision(macroId: String, repositoryPath: String, repositoryBranch: String, failed: Runnable, ready: Runnable): Runnable = Runnable {
        revisionCheckAction.checkRevision(macroId, repositoryPath, repositoryBranch, failed, ready, pull(macroId, repositoryPath, repositoryBranch, ready, failed))
    }

    private fun pull(macroId: String, repositoryPath: String, repositoryBranch: String, ready: Runnable, failed: Runnable): Runnable =
            Runnable {
                pullRepositoryAction.pullAndConvert(macroId, repositoryPath, ready, failed,
                        retry(macroId, repositoryPath, repositoryBranch, ready, failed)
                )
            }

    private fun retry(macroId: String, repositoryPath: String, repositoryBranch: String, ready: Runnable, failed: Runnable): Runnable =
            Runnable { fetchDataFromSourceThenConvertAndCache(macroId, repositoryPath, repositoryBranch, ready, failed) }
}
