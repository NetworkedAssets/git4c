package com.networkedassets.git4c.core.process.action

import com.networkedassets.git4c.core.business.ConverterExecutorHolder
import com.networkedassets.git4c.core.business.ErrorPageBuilder
import com.networkedassets.git4c.core.bussiness.ConverterPlugin
import com.networkedassets.git4c.core.bussiness.ImportedFileData
import com.networkedassets.git4c.core.bussiness.ImportedFiles
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.datastore.cache.DocumentItemCache
import com.networkedassets.git4c.core.datastore.cache.DocumentToBeConvertedLockCache
import com.networkedassets.git4c.core.datastore.extractors.ExtractorData
import com.networkedassets.git4c.core.datastore.repositories.ExtractorDataDatabase
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.process.ExtractContentProcess
import com.networkedassets.git4c.data.DocumentView
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.data.macro.documents.item.DocumentsItem
import com.networkedassets.git4c.data.macro.documents.item.TableOfContents
import com.networkedassets.git4c.utils.DocumentConversionUtils.idOfConvertedDocument
import com.networkedassets.git4c.utils.debug
import com.networkedassets.git4c.utils.error
import com.networkedassets.git4c.utils.getLogger
import com.networkedassets.git4c.utils.info
import java.util.concurrent.TimeUnit

class ConvertDocumentItemAction(
        val importer: SourcePlugin,
        val macroSettingsDatabase: MacroSettingsDatabase,
        val repositoryDatabase: RepositoryDatabase,
        val documentToBeConvertedLockCache: DocumentToBeConvertedLockCache,
        val converterExecutorHolder: ConverterExecutorHolder,
        val documentItemCache: DocumentItemCache,
        val errorPageBuilder: ErrorPageBuilder,
        val extractContentProcess: ExtractContentProcess,
        val extractorDataDatabase: ExtractorDataDatabase,
        val converter: ConverterPlugin) {


    val log = getLogger()


    fun planConvertion(macroId: String, documentPath: String, repositoryPath: String, repositoryBranch: String, extractorUuid: String?) {
        if (importer.isLocked(repositoryPath)) {
            converterExecutorHolder.getExecutor()
                    .schedule({ planConvertion(macroId, documentPath, repositoryPath, repositoryBranch, extractorUuid) }, 200, TimeUnit.MILLISECONDS)
        } else {
            converterExecutorHolder.getExecutor()
                    .schedule({ convertDocumentItem(macroId, documentPath, repositoryPath, repositoryBranch, extractorUuid) }, 10, TimeUnit.MILLISECONDS)
        }
    }

    private fun convertDocumentItem(macroId: String, documentPath: String, repositoryPath: String, repositoryBranch: String, extractorUuid: String?) {

        val idOfDocumentInConvertion = idOfConvertedDocument(repositoryPath, repositoryBranch, documentPath, extractorUuid)

        val macroSettings = macroSettingsDatabase.get(macroId)
        if (macroSettings == null) {
            log.info { "Will discard operation on Macro=${macroId} as it has not been found" }
            documentToBeConvertedLockCache.remove(idOfDocumentInConvertion)
            return
        }

        if (macroSettings.repositoryUuid == null) {
            log.info { "Will discard operation on Macro=${macroId} as it has no repository defined inside of a macro settings" }
            documentToBeConvertedLockCache.remove(idOfDocumentInConvertion)
            return
        }

        val repository = repositoryDatabase.get(macroSettings.repositoryUuid)
        if (repository == null) {
            log.info { "Will discard operation on Macro=${macroId} as it's repository with RepositoryId=${macroSettings.repositoryUuid} has not been found" }
            documentToBeConvertedLockCache.remove(idOfDocumentInConvertion)
            return
        }

        val extractor = if (extractorUuid == null) null else extractorDataDatabase.get(extractorUuid)

        if (importer.isLocked(repositoryPath)) {
            converterExecutorHolder.getExecutor()
                    .schedule({ planConvertion(macroId, documentPath, repositoryPath, repositoryBranch, extractorUuid) }, 200, TimeUnit.MILLISECONDS)
            return
        }

        convert(macroSettings, repository, documentPath, extractor)
    }

    private fun convert(macroSettings: MacroSettings, repository: Repository, documentPath: String, extractor: ExtractorData?) {
        if (importer.accuireLock(repository.repositoryPath)) {
            importer.get(repository, macroSettings.branch, true).use {
                convertFile(it, macroSettings, repository, documentPath, extractor)
            }
        } else {
            converterExecutorHolder.getExecutor()
                    .schedule({
                        planConvertion(macroSettings.uuid, documentPath, repository.repositoryPath, macroSettings.branch, extractor?.uuid)
                    }, 200, TimeUnit.MILLISECONDS)
        }
    }

    private fun convertFile(files: ImportedFiles, macroSettings: MacroSettings, repository: Repository, documentPath: String, extractor: ExtractorData?) {
        try {
            convert(repository, macroSettings, documentPath, extractor, files)
        } catch (e: Exception) {
            val idOfDocumentInConvertion = idOfConvertedDocument(repository.repositoryPath, macroSettings.branch, documentPath, extractor)
            documentToBeConvertedLockCache.remove(idOfDocumentInConvertion)
        }
    }

    private fun convert(repository: Repository, macroSettings: MacroSettings, documentPath: String, extractor: ExtractorData?, files: ImportedFiles) {
        val idOfDocumentInConvertion = idOfConvertedDocument(repository.repositoryPath, macroSettings.branch, documentPath, extractor)
        val fileToConvert = files.imported.filter { it.path == documentPath }.firstOrNull()
        if (fileToConvert == null) {
            log.info("There have been selected a file to convert that seems to not exist!")
            documentToBeConvertedLockCache.remove(idOfDocumentInConvertion)
            return
        }
        convertDocumentIfExist(repository.repositoryPath, macroSettings.branch, fileToConvert, extractor)
    }

    private fun convertDocumentIfExist(repositoryPath: String, repositoryBranch: String, importedFileData: ImportedFileData, extractor: ExtractorData?) {
        val existing = documentItemCache.get(idOfConvertedDocument(repositoryPath, repositoryBranch, importedFileData, extractor))
        if (existing == null
                || existing.updateAuthorEmail != importedFileData.updateAuthorEmail
                || existing.updateAuthorFullName != importedFileData.updateAuthorFullName
                || existing.updateDate != importedFileData.updateDate) {
            convertion(repositoryPath, repositoryBranch, importedFileData, extractor)
        } else {
            log.debug { "Will skip converting of document ${importedFileData.path} as it already exist in cache!" }
            close(repositoryPath, repositoryBranch, importedFileData, extractor)
        }
    }

    private fun convertion(repositoryPath: String, repositoryBranch: String, importedFileData: ImportedFileData, extractor: ExtractorData?) {
        val time = System.currentTimeMillis()
        convertFileToDocumentsItem(repositoryPath, repositoryBranch, importedFileData, extractor)
        log.debug { "Convertion of ConvertFile=${importedFileData.path} at RepositoryPath=${repositoryPath} in RepositoryBranch=${repositoryBranch}" }
        val convertionTime = System.currentTimeMillis() - time
        logConvertionTime(convertionTime, importedFileData, repositoryPath, repositoryBranch)
    }

    private fun logConvertionTime(convertionTime: Long, importedFileData: ImportedFileData, repositoryPath: String, repositoryBranch: String) {
        if (convertionTime > 10000)
            log.info { "Convertion took: ${convertionTime} for ConvertFile=${importedFileData.path} at RepositoryPath=${repositoryPath} in RepositoryBranch=${repositoryBranch}" }
        else
            log.debug { "Convertion took: ${convertionTime} for ConvertFile=${importedFileData.path} at RepositoryPath=${repositoryPath} in RepositoryBranch=${repositoryBranch}" }
    }


    private fun convertFileToDocumentsItem(repositoryPath: String, repositoryBranch: String, fileToConvert: ImportedFileData, extractor: ExtractorData?) {
        try {
            convertItem(fileToConvert, repositoryPath, repositoryBranch, extractor)
        } catch (e: OutOfMemoryError) {
            generateErrorPage(fileToConvert, repositoryPath, e, repositoryBranch, extractor)
        } catch (e: Throwable) {
            generateErrorPage(fileToConvert, repositoryPath, e, repositoryBranch, extractor)
        } finally {
            close(repositoryPath, repositoryBranch, fileToConvert, extractor)
        }
    }

    private fun close(repositoryPath: String, repositoryBranch: String, fileToConvert: ImportedFileData, extractor: ExtractorData?) {
        val idOfDocumentInConvertion = idOfConvertedDocument(repositoryPath, repositoryBranch, fileToConvert, extractor)
        documentToBeConvertedLockCache.put(idOfDocumentInConvertion, DocumentView(idOfDocumentInConvertion, DocumentView.MacroViewStatus.READY))
    }

    private fun convertItem(fileToConvert: ImportedFileData, repositoryPath: String, repositoryBranch: String, extractor: ExtractorData?) {
        val extractionResult = extractContentProcess.extract(extractor, fileToConvert)
        val convertedDocumentItem = converter.convert(fileToConvert, extractionResult)
        if (convertedDocumentItem != null) {
            val documentItem = DocumentsItem(repositoryPath, repositoryBranch, convertedDocumentItem, extractor)
            documentItemCache.put(documentItem.uuid, documentItem)
        }
    }

    private fun generateErrorPage(fileToConvert: ImportedFileData, repositoryPath: String, e: Throwable, repositoryBranch: String, extractor: ExtractorData?) {
        log.error({ "<< Converted ${fileToConvert.path} in RepositoryPath=${repositoryPath} with ERROR" }, e)
        val page = errorPageBuilder.build(fileToConvert, e)
        val documentItem = DocumentsItem(fileToConvert.path, fileToConvert.updateAuthorFullName, fileToConvert.updateAuthorEmail, fileToConvert.updateDate, String(fileToConvert.content()), page, TableOfContents("", "", listOf()), repositoryPath, repositoryBranch, extractor)
        documentItemCache.put(documentItem.uuid, documentItem)
    }

    private fun generateErrorPage(fileToConvert: ImportedFileData, repositoryPath: String, e: OutOfMemoryError, repositoryBranch: String, extractor: ExtractorData?) {
        log.error { "<< Converted ${fileToConvert.path} in RepositoryPath=${repositoryPath} with OUT OF MEMORY - skip document content" }
        System.gc()
        val page = errorPageBuilder.build(fileToConvert, e)
        val documentItem = DocumentsItem(fileToConvert.path, fileToConvert.updateAuthorFullName, fileToConvert.updateAuthorEmail, fileToConvert.updateDate, String(fileToConvert.content()), page, TableOfContents("", "", listOf()), repositoryPath, repositoryBranch, extractor)
        documentItemCache.put(documentItem.uuid, documentItem)
    }

}