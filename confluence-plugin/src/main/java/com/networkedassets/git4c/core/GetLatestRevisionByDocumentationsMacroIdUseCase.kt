package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.GetLatestRevisionByDocumentationsMacroIdQuery
import com.networkedassets.git4c.boundary.GetLatestRevisionByDocumentationsMacroIdResultRequest
import com.networkedassets.git4c.boundary.outbound.RequestId
import com.networkedassets.git4c.boundary.outbound.Revision
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.bussiness.ComputationCache
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.datastore.cache.DocumentsViewCache
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.exceptions.VerificationException
import com.networkedassets.git4c.core.process.ICheckUserPermissionProcess
import com.networkedassets.git4c.core.process.MacroViewProcess
import com.networkedassets.git4c.core.usecase.async.ComputationResultUseCase
import com.networkedassets.git4c.core.usecase.async.MultiThreadAsyncUseCase

class GetLatestRevisionByDocumentationsMacroIdUseCase(
        components: BussinesPluginComponents,
        val macroSettingsRepository: MacroSettingsDatabase = components.providers.macroSettingsProvider,
        val repositoryDatabase: RepositoryDatabase = components.providers.repositoryProvider,
        val importer: SourcePlugin = components.macro.importer,
        val documentsViewCache: DocumentsViewCache = components.cache.documentsViewCache,
        val macroViewProcess: MacroViewProcess = components.processing.macroViewProcess,
        val checkUserPermissionProcess: ICheckUserPermissionProcess = components.processing.checkUserPermissionProcess,
        computations: ComputationCache<Revision> = components.async.getLatestRevisionByDocumentationsMacroIdUseCaseCache
) : MultiThreadAsyncUseCase<GetLatestRevisionByDocumentationsMacroIdQuery, Revision>
(components, computations, 2) {

    override fun executedAsync(requestId: String, request: GetLatestRevisionByDocumentationsMacroIdQuery) {
        val searchedMacroId = request.macroId
        val macroSettings = macroSettingsRepository.get(searchedMacroId)
                ?: return error(requestId, NotFoundException(request.transactionInfo, VerificationStatus.REMOVED.name))
        if (macroSettings.repositoryUuid == null)
            return error(requestId, NotFoundException(request.transactionInfo, VerificationStatus.REMOVED.name))
        val repository = repositoryDatabase.get(macroSettings.repositoryUuid)
                ?: return error(requestId, NotFoundException(request.transactionInfo, VerificationStatus.REMOVED.name))

        try {
            val revision = Revision(importer.revision(macroSettings, repository).use { it.revision })
            if (documentsViewCache.get(searchedMacroId)?.revision != revision.id) {
                documentsViewCache.remove(searchedMacroId)
                macroViewProcess.prepareMacroToBeViewed(searchedMacroId)
            }
            success(requestId, revision)
        } catch (e: VerificationException) {
            error(requestId, NotFoundException(request.transactionInfo, e.verification.status.name))
        }
    }
}

class GetLatestRevisionByDocumentationsMacroIdResultUseCase(
        components: BussinesPluginComponents,
        computations: ComputationCache<Revision> = components.async.getLatestRevisionByDocumentationsMacroIdUseCaseCache
) : ComputationResultUseCase<GetLatestRevisionByDocumentationsMacroIdResultRequest, Revision>(components, computations)
