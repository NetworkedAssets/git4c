package com.networkedassets.git4c.core

import com.atlassian.plugins.whitelist.NotAuthorizedException
import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetDocumentationsMacroByDocumentationsMacroIdQuery
import com.networkedassets.git4c.boundary.outbound.DocumentationsMacro
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.boundary.outbound.exceptions.NotReadyException
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.datastore.cache.DocumentsViewCache
import com.networkedassets.git4c.core.datastore.cache.MacroToBeViewedPrepareLockCache
import com.networkedassets.git4c.core.datastore.repositories.ExtractorDataDatabase
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.exceptions.VerificationException
import com.networkedassets.git4c.core.process.ICheckUserPermissionProcess
import com.networkedassets.git4c.core.process.MacroViewProcess
import com.networkedassets.git4c.data.MacroView
import com.networkedassets.git4c.delivery.executor.execution.UseCase
import com.networkedassets.git4c.utils.getLogger
import com.networkedassets.git4c.utils.info

class GetDocumentationsMacroByDocumentationsMacroIdUseCase(
        val extractorDataDatabase: ExtractorDataDatabase,
        val checkUserPermissionProcess: ICheckUserPermissionProcess,
        val macroSettingsDatabase: MacroSettingsDatabase,
        val repositoryDatabase: RepositoryDatabase,
        val cache: DocumentsViewCache,
        val macroViewProcess: MacroViewProcess,
        val macroViewCache: MacroToBeViewedPrepareLockCache,
        val importer: SourcePlugin
) : UseCase<GetDocumentationsMacroByDocumentationsMacroIdQuery, DocumentationsMacro> {

    val log = getLogger()

    override fun execute(request: GetDocumentationsMacroByDocumentationsMacroIdQuery): Result<DocumentationsMacro, Exception> {

        val searchedMacroId = request.macroId
        val user = request.user

        val macroViewStatus = macroViewCache.get(request.macroId)?.macroViewStatus ?: MacroView.MacroViewStatus.FAILED

        if (macroViewStatus == MacroView.MacroViewStatus.FAILED) {
            val macroSettings = macroSettingsDatabase.get(request.macroId) ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
            if (macroSettings.repositoryUuid == null) return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
            val repository = repositoryDatabase.get(macroSettings.repositoryUuid) ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
            val verificationError = VerificationException(importer.verify(repository))
            log.info { "During get of Macro=${searchedMacroId} there is problem with verification: ${verificationError.verification}" }
            return@execute Result.error(verificationError)
        }

        if (macroViewStatus != MacroView.MacroViewStatus.READY) {
            return@execute Result.error(NotReadyException())
        }

        if (checkUserPermissionProcess.userHasPermissionToMacro(searchedMacroId, user) == false) {
            return@execute Result.error(NotAuthorizedException("User: ${user} doesn't have permission to space or site"))
        }

        val data = cache.get(request.macroId)
        if (data == null) {
            macroViewProcess.prepareMacroToBeViewed(request.macroId);
            return@execute Result.error(NotReadyException())
        }

        val macroSettings = macroSettingsDatabase.get(searchedMacroId) ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))

        if (macroSettings.repositoryUuid == null) return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val repository = repositoryDatabase.get(macroSettings.repositoryUuid) ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))

        return@execute Result.of {
            DocumentationsMacro(data.uuid, repository.uuid, data.currentBranch, data.revision)
        }
    }

}