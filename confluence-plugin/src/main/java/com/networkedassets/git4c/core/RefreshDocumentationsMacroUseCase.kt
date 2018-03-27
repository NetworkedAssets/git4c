package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.RefreshDocumentationsMacroCommand
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


class RefreshDocumentationsMacroUseCase(
        components: BussinesPluginComponents,
        val extractorDataDatabase: ExtractorDataDatabase = components.database.extractorDataDatabase,
        val checkUserPermissionProcess: ICheckUserPermissionProcess = components.processing.checkUserPermissionProcess,
        val macroSettingsDatabase: MacroSettingsDatabase = components.providers.macroSettingsProvider,
        val repositoryDatabase: RepositoryDatabase = components.providers.repositoryProvider,
        val documentsViewCache: DocumentsViewCache = components.cache.documentsViewCache,
        val macroViewProcess: MacroViewProcess = components.processing.macroViewProcess,
        val macroViewCache: MacroToBeViewedPrepareLockCache = components.cache.macroViewCache,
        val importer: SourcePlugin = components.macro.importer
) : UseCase<RefreshDocumentationsMacroCommand, DocumentationsMacro>
(components) {

    override fun execute(request: RefreshDocumentationsMacroCommand): Result<DocumentationsMacro, Exception> {

        val searchedMacroId = request.macroId
        val user = request.user

        val macroViewStatus = macroViewCache.get(request.macroId)?.macroViewStatus ?: MacroView.MacroViewStatus.FAILED

        if (macroViewStatus == MacroView.MacroViewStatus.FAILED) {
            val macroSettings = macroSettingsDatabase.get(request.macroId)
                    ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
            if (macroSettings.repositoryUuid == null) return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
            val repository = repositoryDatabase.get(macroSettings.repositoryUuid)
                    ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
            // TODO: Think of how to do this part in separate async
            return@execute Result.error(VerificationException(importer.verify(repository)))
        }

        if (macroViewStatus != MacroView.MacroViewStatus.READY) {
            return@execute Result.error(NotReadyException())
        }

        if (checkUserPermissionProcess.userHasPermissionToMacro(searchedMacroId, user) == false) {
            return@execute Result.error(com.atlassian.plugins.whitelist.NotAuthorizedException("User: ${user} doesn't have permission to space or site"))
        }

        val data = documentsViewCache.get(request.macroId)
        if (data == null) {
            macroViewProcess.prepareMacroToBeViewed(request.macroId);
            return@execute Result.error(NotReadyException())
        }

        val macroSettings = macroSettingsDatabase.get(searchedMacroId)
                ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        if (macroSettings.repositoryUuid == null) return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val repository = repositoryDatabase.get(macroSettings.repositoryUuid)
                ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))

        try {
            return@execute Result.of {
                DocumentationsMacro(data.uuid, repository.uuid, data.currentBranch, data.revision)
            }
        } catch (e: VerificationException) {
            return@execute Result.error(NotFoundException(request.transactionInfo, e.verification.status))
        }

    }
}