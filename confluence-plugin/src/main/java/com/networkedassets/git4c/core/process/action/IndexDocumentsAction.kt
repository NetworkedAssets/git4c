package com.networkedassets.git4c.core.process.action

import com.networkedassets.git4c.core.business.FileIgnorer
import com.networkedassets.git4c.core.bussiness.ImportedFileData
import com.networkedassets.git4c.core.bussiness.ImportedFiles
import com.networkedassets.git4c.core.datastore.cache.DocumentItemCache
import com.networkedassets.git4c.core.datastore.extractors.ExtractorData
import com.networkedassets.git4c.data.macro.documents.item.DocumentsFileIndex
import com.networkedassets.git4c.infrastructure.plugin.filter.GlobFilterPlugin
import com.networkedassets.git4c.utils.DocumentConversionUtils.idOfConvertedDocument
import com.networkedassets.git4c.utils.debug
import com.networkedassets.git4c.utils.getLogger
import com.networkedassets.git4c.utils.info
import java.io.File
import java.nio.file.PathMatcher
import java.util.concurrent.Executors

class IndexDocumentsAction(
        val documentItemCache: DocumentItemCache,
        val fileIgnorer: FileIgnorer
) {

    val indexDocumentsExecutor = Executors.newScheduledThreadPool(1);
    val documentReaderExecutor = Executors.newScheduledThreadPool(1);

    val log = getLogger()

    fun indexDocuments(pullResult: ImportedFiles, finish: FinishedConvertionProcess, failed: Runnable, ready: Runnable, rootDirectory: PathMatcher, extractor: ExtractorData?, macroId: String, rootDirectoryPath: String?, repositoryPath: String, repositoryBranch: String, globFilter : GlobFilterPlugin) {
        indexDocumentsExecutor.execute({
            val filesToConvert = pullResult.imported
                    .filter { globFilter.filter(it) }
                    .filter { rootDirectoryPath.isNullOrEmpty() || rootDirectory.matches(File(it.path).toPath()) }
            log.info { "There have been selected ${filesToConvert.size} files to be indexed from a total of ${pullResult.imported.size} based on filters in Macro=${macroId}" }
            val convertedFilesIndex = mutableListOf<DocumentsFileIndex>()
            scheduleNextDocument(pullResult, filesToConvert.listIterator(), finish, macroId, extractor, ready, failed, convertedFilesIndex, filesToConvert.size, repositoryPath, repositoryBranch, setOf())
        })

    }

    private fun scheduleNextDocument(pullResult: ImportedFiles, filesToIndex: ListIterator<ImportedFileData>, finish: FinishedConvertionProcess, macroId: String, extractor: ExtractorData?, ready: Runnable, failed: Runnable, convertedFilesIndex: MutableList<DocumentsFileIndex>, total: Int, repositoryPath: String, repositoryBranch: String, filesToIgnore: Set<String>) {
        documentReaderExecutor.execute({
            indexNextDocument(pullResult, filesToIndex, finish, macroId, extractor, ready, failed, convertedFilesIndex, total, repositoryPath, repositoryBranch, filesToIgnore)
        })
    }

    private fun indexNextDocument(pullResult: ImportedFiles, filesToIndex: ListIterator<ImportedFileData>, finish: FinishedConvertionProcess, macroId: String, extractor: ExtractorData?, ready: Runnable, failed: Runnable, convertedFilesIndex: MutableList<DocumentsFileIndex>, total: Int, repositoryPath: String, repositoryBranch: String, filesToIgnore: Set<String>) {
        if (!filesToIndex.hasNext()) {
            log.info { "There are no more file to index in Macro=${macroId}" }
            return finish.finished(macroId, ready, pullResult, failed, convertedFilesIndex.asSequence(), filesToIgnore.toList())
        }
        val importedFileData = filesToIndex.next()
        val toIgnore = fileIgnorer.getFilesToIgnore(importedFileData)
        val id = idOfConvertedDocument(repositoryPath, repositoryBranch, importedFileData, extractor)
        val existing = documentItemCache.get(id)
        if (existing != null
                && existing.updateAuthorEmail == importedFileData.updateAuthorEmail
                && existing.updateAuthorFullName == importedFileData.updateAuthorFullName
                && existing.updateDate == importedFileData.updateDate) {
            log.debug { "Document ${importedFileData.path} already exists and seems to be not changed." }
        } else if (existing != null) {
            log.debug { "Will removed cached version of document ${importedFileData.path} as it has changed!" }
            documentItemCache.remove(id)
        } else {
            log.debug { "Document ${importedFileData.path} does not exists in cache." }
        }
        convertedFilesIndex.add(DocumentsFileIndex(importedFileData.path))
        scheduleNextDocument(pullResult, filesToIndex, finish, macroId, extractor, ready, failed, convertedFilesIndex, total, repositoryPath, repositoryBranch, filesToIgnore + toIgnore)
    }

}