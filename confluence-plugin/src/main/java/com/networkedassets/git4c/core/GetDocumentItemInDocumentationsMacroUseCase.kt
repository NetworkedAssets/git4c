package com.networkedassets.git4c.core

import com.atlassian.confluence.core.service.NotAuthorizedException
import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetDocumentItemInDocumentationsMacroQuery
import com.networkedassets.git4c.boundary.outbound.DocItem
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.boundary.outbound.exceptions.NotReadyException
import com.networkedassets.git4c.core.datastore.cache.DocumentItemCache
import com.networkedassets.git4c.core.datastore.cache.DocumentToBeConvertedLockCache
import com.networkedassets.git4c.core.datastore.cache.DocumentsViewCache
import com.networkedassets.git4c.core.datastore.cache.MacroToBeViewedPrepareLockCache
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.process.ICheckUserPermissionProcess
import com.networkedassets.git4c.core.process.MacroViewProcess
import com.networkedassets.git4c.core.process.action.ConvertDocumentItemAction
import com.networkedassets.git4c.data.DocumentView
import com.networkedassets.git4c.data.MacroView
import com.networkedassets.git4c.delivery.executor.execution.UseCase
import com.networkedassets.git4c.utils.DocumentConversionUtils.idOfConvertedDocument
import com.networkedassets.git4c.utils.debug
import com.networkedassets.git4c.utils.getLogger


class GetDocumentItemInDocumentationsMacroUseCase(
        val cache: DocumentsViewCache,
        val checkUserPermissionProcess: ICheckUserPermissionProcess,
        val documentItemCache: DocumentItemCache,
        val macroViewProcess: MacroViewProcess,
        val macroViewCache: MacroToBeViewedPrepareLockCache,
        val macroSettingsDatabase: MacroSettingsDatabase,
        val repositoryDatabase: RepositoryDatabase,
        val documentToBeConvertedLockCache: DocumentToBeConvertedLockCache,
        val converter: ConvertDocumentItemAction
) : UseCase<GetDocumentItemInDocumentationsMacroQuery, DocItem> {

    val log = getLogger()

    override fun execute(request: GetDocumentItemInDocumentationsMacroQuery): Result<DocItem, Exception> {

        val searchedMacroId = request.macroId
        val user = request.user
        val searchedDocumentId = request.documentId

        if (checkUserPermissionProcess.userHasPermissionToMacro(searchedMacroId, user) == false) {
            return Result.error(NotAuthorizedException("User doesn't have permission to this space"))
        }

        val macroViewStatus = macroViewCache.get(searchedMacroId)
        if (macroViewStatus == null) {
            macroViewProcess.prepareMacroToBeViewed(searchedMacroId)
            log.debug { "Macro=${request.macroId} has no view status present" }
            return@execute Result.error(NotReadyException())
        }

        if (macroViewStatus.macroViewStatus != MacroView.MacroViewStatus.READY) {
            log.debug { "Macro=${request.macroId} has no view status as ready!" }
            return@execute Result.error(NotReadyException())
        }


        val cachedMacro = cache.get(searchedMacroId)
        if (cachedMacro == null) {
            macroViewProcess.prepareMacroToBeViewed(request.macroId);
            log.debug { "Macro=${request.macroId} is not in cache, it will be refreshed" }
            return@execute Result.error(NotReadyException())
        }

        val fileToSearch = cachedMacro.files.filter { it.index == searchedDocumentId }.firstOrNull() ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))

        val macroSettings = macroSettingsDatabase.get(request.macroId)
        if (macroSettings == null) {
            log.debug { "Will discard operation on Macro=${searchedMacroId} as it has not been found" }
            return@execute Result.error(NotReadyException())
        }

        if (macroSettings.repositoryUuid == null) {
            log.debug { "Will discard operation on Macro=${searchedMacroId} as it has no repository defined inside of a macro settings" }
            return@execute Result.error(NotReadyException())
        }

        val repository = repositoryDatabase.get(macroSettings.repositoryUuid)
        if (repository == null) {
            log.debug { "Will discard operation on Macro=${searchedMacroId} as it's repository with RepositoryId=${macroSettings.repositoryUuid} has not been found" }
            return@execute Result.error(NotReadyException())
        }

        val repositoryPath = repository.repositoryPath
        val repositoryBranch = macroSettings.branch
        val extractorUuid = macroSettings.extractorDataUuid

        val idOfDocumentInConvertion = idOfConvertedDocument(repositoryPath, repositoryBranch, fileToSearch.path, extractorUuid)
//
//        val idOfDocumentInConvertion = "${repositoryPath}_${repositoryBranch}_${fileToSearch.path}_${extractorUuid ?: "0"}"

        val conversionInProgress = documentToBeConvertedLockCache.get(idOfDocumentInConvertion)

        if (conversionInProgress != null && conversionInProgress.macroViewStatus != DocumentView.MacroViewStatus.READY) {
            log.debug { "Convertion of document for Macro=${request.macroId} seems to be in progress!" }
            return@execute Result.error(NotReadyException())
        }

        val fileFromCache = documentItemCache.get(idOfDocumentInConvertion)

        if (fileFromCache != null) {
            return@execute Result.of { DocItem(fileFromCache) }
        } else if (conversionInProgress == null) {

            return@execute retry(idOfDocumentInConvertion, macroSettings.uuid, fileToSearch.path, repositoryPath, repositoryBranch, extractorUuid)
        } else if (conversionInProgress.macroViewStatus == DocumentView.MacroViewStatus.READY) {

            return@execute retry(idOfDocumentInConvertion, macroSettings.uuid, fileToSearch.path, repositoryPath, repositoryBranch, extractorUuid)
        } else {
            log.debug { "Convertion of document for Macro=${request.macroId} seems to be in progress or is not ready yet!" }
            return@execute Result.error(NotReadyException())
        }
    }

    private fun retry(idOfDocumentInConvertion: String, macroId: String, documentPath: String, repositoryPath: String, repositoryBranch: String, extractorUuid: String?): Result.Failure<Nothing, NotReadyException> {
        documentToBeConvertedLockCache.put(idOfDocumentInConvertion, DocumentView(idOfDocumentInConvertion, DocumentView.MacroViewStatus.TO_CONVERT))
        documentItemCache.remove(idOfDocumentInConvertion)
        converter.planConvertion(macroId, documentPath, repositoryPath, repositoryBranch, extractorUuid)

        return Result.error(NotReadyException())
    }
}