package com.networkedassets.git4c.core.process.action

import com.networkedassets.git4c.core.business.RepositoryPullExecutorHolder
import com.networkedassets.git4c.core.bussiness.ImportedFiles
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.datastore.cache.DocumentsViewCache
import com.networkedassets.git4c.core.datastore.cache.RepositoryRevisionCache
import com.networkedassets.git4c.core.datastore.extractors.ExtractorData
import com.networkedassets.git4c.core.datastore.repositories.ExtractorDataDatabase
import com.networkedassets.git4c.core.datastore.repositories.GlobForMacroDatabase
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.data.GlobForMacro
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.data.macro.documents.DocumentationsMacro
import com.networkedassets.git4c.data.macro.documents.item.DocumentsFileIndex
import com.networkedassets.git4c.infrastructure.plugin.filter.GlobFilterPlugin
import com.networkedassets.git4c.utils.debug
import com.networkedassets.git4c.utils.error
import com.networkedassets.git4c.utils.getLogger
import com.networkedassets.git4c.utils.info
import java.nio.file.FileSystems
import java.nio.file.PathMatcher
import java.util.concurrent.Executors

class PullRepositoryAction(
        val repositoryPullExecutorHolder: RepositoryPullExecutorHolder,
        val macroSettingsDatabase: MacroSettingsDatabase,
        val repositoryDatabase: RepositoryDatabase,
        val globForMacroDatabase: GlobForMacroDatabase,
        val extractorDataDatabase: ExtractorDataDatabase,
        val importer: SourcePlugin,
        val indexDocumentsAction: IndexDocumentsAction,
        val documentsViewCache: DocumentsViewCache,
        val revisionCache: RepositoryRevisionCache
) : FinishedConvertionProcess {

    val finishedExecutor = Executors.newScheduledThreadPool(1);

    val log = getLogger()

    fun pullAndConvert(
            macroId: String,
            repositoryPath: String,
            ready: Runnable,
            failed: Runnable,
            retry: Runnable
    ) {
        if (importer.isLocked(repositoryPath)) {
            return retry.run()
        } else {
            executePull(macroId, repositoryPath, failed, ready, retry)
        }
    }

    private fun executePull(macroId: String, repositoryPath: String, failed: Runnable, ready: Runnable, retry: Runnable) {
        repositoryPullExecutorHolder.getExecutor().execute({
            pullThenConvert(macroId, repositoryPath, failed, ready, retry)
        })
    }

    private fun pullThenConvert(macroId: String, repositoryPath: String, failed: Runnable, ready: Runnable, retry: Runnable) {

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
        val extractor = extractorDataDatabase.getNullable(macroSettings.extractorDataUuid)
        val rootDirectory = FileSystems.getDefault().getPathMatcher("glob:${macroSettings.rootDirectory}")
        val globs = globForMacroDatabase.getByMacro(macroSettings.uuid)

        val filter = GlobFilterPlugin(globs.map { it.glob })
        if (importer.isLocked(repositoryPath)) {
            return retry.run()
        }

        try {
            pullFromRemoteAndConvert(repository, macroSettings, rootDirectory, extractor, ready, failed, retry, filter)
        } catch (e: Exception) {
            log.error({ "There was an error during pull of repository RepositoryPath=${repository.repositoryPath}" }, e)
            failed.run()
        }
    }

    private fun pullFromRemoteAndConvert(repository: Repository, macroSettings: MacroSettings, rootDirectory: PathMatcher, extractor: ExtractorData?, ready: Runnable, failed: Runnable, retry: Runnable, globFilter: GlobFilterPlugin) {
        val macroId = macroSettings.uuid
        if (importer.accuireLock(repository.repositoryPath)) {
            log.info { "Operation of pull repository may be performed now for RepositoryPath=${repository.repositoryPath} in Macro=${macroId}" }
            val pullResult = importer.pull(repository, macroSettings.branch, true)
            log.info { "Pull operation has been finished for RepositoryPath=${repository.repositoryPath} in Macro=${macroId} so that conversion process may be started" }
            val repositoryPath = repository.repositoryPath
            val repositoryBranch = macroSettings.branch
            indexDocumentsAction.indexDocuments(pullResult, this, failed, ready, rootDirectory, extractor, macroId, macroSettings.rootDirectory, repositoryPath, repositoryBranch, globFilter)
        } else {
            log.debug { "Will retry an operation from pull of repository for Macro=${macroId} as a RepositoryPath=${repository.repositoryPath} is locked during try to lock it." }
            retry.run()
        }
    }

    override fun finished(macroId: String, ready: Runnable, pullResult: ImportedFiles, failed: Runnable, convertedFilesIndex: Sequence<DocumentsFileIndex>, filesToIgnore: List<String>) {

        val convertedFiles = convertedFilesIndex.filterNot { filesToIgnore.contains(it.path) }

        log.debug { "Repository may be unlocked after a pull operation as all other operations at files are marked as done, for Macro=${macroId}" }

        finishedExecutor.execute({
            val macroSettings = macroSettingsDatabase.get(macroId)
            if (macroSettings == null) {
                log.info { "Will discard operation on Macro=${macroId} as it has not been found" }
                return@execute failed.run()
            }

            if (macroSettings.repositoryUuid == null) {
                log.info { "Will discard operation on Macro=${macroId} as it has no repository defined inside of a macro settings" }
                return@execute failed.run()
            }

            val repository = repositoryDatabase.get(macroSettings.repositoryUuid)
            if (repository == null) {
                log.info { "Will discard operation on Macro=${macroId} as it's repository with RepositoryId=${macroSettings.repositoryUuid} has not been found" }
                return@execute failed.run()
            }
            try {
                val globs = globForMacroDatabase.getByMacro(macroSettings.uuid)
                updateRevisionAndCloseOperation(macroSettings, repository, convertedFiles, globs, macroId, ready, pullResult)
            } catch (e: Exception) {
                log.error({ "There was an error during revision check after pull of repository RepositoryPath=${repository.repositoryPath}" }, e)
                failed.run()
            }
        })
    }

    private fun updateRevisionAndCloseOperation(macroSettings: MacroSettings, repository: Repository, convertedFilesIndex: Sequence<DocumentsFileIndex>, globs: List<GlobForMacro>, macroId: String, ready: Runnable, pullResult: ImportedFiles) {
        val revision = importer.revision(macroSettings, repository, true).revision
        revisionCache.putCached(repository.repositoryPath, macroSettings.branch)
        val macro = DocumentationsMacro(macroSettings, revision, convertedFilesIndex.toList(), globs)
        documentsViewCache.put(macroId, macro)
        log.debug { "Informing all upstream processes about success operation" }
        ready.run()
        log.info { "Closing a pull result operation for Macro=${macroId}" }
        pullResult.close()
    }
}

interface FinishedConvertionProcess {
    fun finished(macroId: String, ready: Runnable, pullResult: ImportedFiles, failed: Runnable, convertedFilesIndex: Sequence<DocumentsFileIndex>, filesToIgnore: List<String>)
}