package com.networkedassets.git4c.core.process.action

import com.networkedassets.git4c.core.business.RevisionCheckExecutorHolder
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.datastore.cache.DocumentsViewCache
import com.networkedassets.git4c.core.datastore.cache.RepositoryRevisionCache
import com.networkedassets.git4c.core.datastore.repositories.GlobForMacroDatabase
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.data.macro.documents.DocumentationsMacro
import com.networkedassets.git4c.utils.*
import java.util.concurrent.TimeUnit

class RevisionCheckAction(
        val revisionCheckExecutorHolder: RevisionCheckExecutorHolder,
        val importer: SourcePlugin,
        val revisionCache: RepositoryRevisionCache,
        val macroSettingsDatabase: MacroSettingsDatabase,
        val repositoryDatabase: RepositoryDatabase,
        val globForMacroDatabase: GlobForMacroDatabase,
        val documentsViewCache: DocumentsViewCache

) {

    val log = getLogger()

    fun checkRevision(macroId: String, repositoryPath: String, repositoryBranch: String, failed: Runnable, ready: Runnable, pullAction: Runnable) {
        if (importer.isLocked(repositoryPath)) {
            revisionCheckExecutorHolder.getExecutor().schedule({
                checkRevision(macroId, repositoryPath, repositoryBranch, failed, ready, pullAction)
            }, 200, TimeUnit.MILLISECONDS)
            return
        } else
            revisionCheckExecutorHolder.getExecutor().schedule({
                action(macroId, repositoryPath, repositoryBranch, pullAction, ready, failed)
            }, 50, TimeUnit.MILLISECONDS)
    }

    private fun action(macroId: String, repositoryPath: String, repositoryBranch: String, pullAction: Runnable, ready: Runnable, failed: Runnable) {
        try {
            performCheckingOfRevision(macroId, repositoryPath, repositoryBranch, pullAction, ready, failed)
        } catch (e: Exception) {
            log.error({ "There was problem while checking of a revision for MacroId=${macroId}" }, e)
            failed.run()
        }
    }

    private fun performCheckingOfRevision(macroId: String, repositoryPath: String, repositoryBranch: String, pullAction: Runnable, ready: Runnable, failed: Runnable) {

        if (revisionCache.exists(repositoryPath, repositoryBranch)) {
            return ready.run()
        }

        val macroSettings = macroSettingsDatabase.get(macroId)
        if (macroSettings == null) {
            log.info { "Will discard operation on Macro=${macroId} as it has not been found" }
            return failed.run()
        }

        if (macroSettings.repositoryUuid == null) {
            log.info { "Will discard operation on Macro=${macroId} as it has no repository defined inside of a macro settings" }
            return failed.run()
        }

        val repository = repositoryDatabase.get(macroSettings.repositoryUuid)
        if (repository == null) {
            log.info { "Will discard operation on Macro=${macroId} as it's repository with RepositoryId=${macroSettings.repositoryUuid} has not been found" }
            return failed.run()
        }

        val globs = globForMacroDatabase.getByMacro(macroSettings.uuid)

        val macroInCache = documentsViewCache.get(macroSettings.uuid).takeIf {
            it?.currentBranch == macroSettings.branch && globs.map { it.glob } contentEquals it.glob.map { it.glob }
        }
        if (macroInCache == null) {
            log.info { "Macro=${macroId} has not been found in the cache so there is need to perform a plan for pull operation." }
            pullAction.run()
            return
        }

        if (importer.isLocked(repository.repositoryPath)) {
            retry(macroId, repositoryPath, repositoryBranch, failed, ready, pullAction)
            return
        }

        if (importer.accuireLock(repository.repositoryPath)) {
            log.debug { "Will do a revision check of Marco=${macroId} at RepositoryPath=${repository.repositoryPath}" }
            revision(macroSettings, repository, macroInCache, pullAction, ready, failed)
        } else {
            retry(macroId, repositoryPath, repositoryBranch, failed, ready, pullAction)
            return
        }


    }

    private fun revision(macroSettings: MacroSettings, repository: Repository, macroInCache: DocumentationsMacro, pullAction: Runnable, ready: Runnable, failed: Runnable) {
        try {
            log.info { "Checking revision at Macro=${macroSettings.uuid} for RepositoryPath=${repository.repositoryPath}" }
            val changed = importer.revision(macroSettings, repository, true).use { it.revision } != macroInCache.revision
            revisionCache.putCached(repository.repositoryPath, macroSettings.branch)
            if (changed) {
                log.info { "Repository with RepositoryPath=${repository.repositoryPath} has been changed, so pull is need" }
                pullAction.run()
            } else {
                log.debug { "Repository with RepositoryPath=${repository.repositoryPath} has not been changed during revision check" }
                ready.run()
            }

        } catch (e: Exception) {
            log.error({ "There was an error during checking of a revision for RepositoryPath=${repository.repositoryPath}" }, e)
            failed.run()
        }
    }

    private fun retry(macroId: String, repositoryPath: String, repositoryBranch: String, failed: Runnable, ready: Runnable, pullAction: Runnable) {
        revisionCheckExecutorHolder.getExecutor().schedule({
            checkRevision(macroId, repositoryPath, repositoryBranch, failed, ready, pullAction)
        }, 200, TimeUnit.MILLISECONDS)
    }
}