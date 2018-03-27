package com.networkedassets.git4c.core

import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.VerifyDocumentationMacroByDocumentationsMacroIdQuery
import com.networkedassets.git4c.boundary.VerifyDocumentationMacroByDocumentationsMacroIdResultRequest
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.bussiness.ComputationCache
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.datastore.repositories.ExtractorDataDatabase
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.usecase.async.ComputationResultUseCase
import com.networkedassets.git4c.core.usecase.async.MultiThreadAsyncUseCase

class VerifyDocumentationMacroByDocumentationsMacroIdUseCase(
        components: BussinesPluginComponents,
        val macroSettingsRepository: MacroSettingsDatabase = components.providers.macroSettingsProvider,
        val repositoryDatabase: RepositoryDatabase = components.providers.repositoryProvider,
        val extractorDataDatabase: ExtractorDataDatabase = components.database.extractorDataDatabase,
        val importer: SourcePlugin = components.macro.importer,
        computations: ComputationCache<String> = components.async.verifyDocumentationMacroByDocumentationsMacroIdUseCaseCache
) : MultiThreadAsyncUseCase<VerifyDocumentationMacroByDocumentationsMacroIdQuery, String>
(components, computations, 2) {

    override fun executedAsync(requestId: String, request: VerifyDocumentationMacroByDocumentationsMacroIdQuery) {
        val searchedMacroId = request.macroId
        val macroSettings = macroSettingsRepository.get(searchedMacroId)
                ?: return error(requestId, NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        if (macroSettings.repositoryUuid == null)
            return error(requestId, NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val repository = repositoryDatabase.get(macroSettings.repositoryUuid)
                ?: return error(requestId, NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))

        val vi = importer.verify(repository)
        if (vi.isOk()) {
            success(requestId, "")
        } else {
            error(requestId, IllegalArgumentException(""))
        }
    }
}

class VerifyDocumentationMacroByDocumentationsMacroIdResultUseCase(
        components: BussinesPluginComponents,
        computations: ComputationCache<String> = components.async.verifyDocumentationMacroByDocumentationsMacroIdUseCaseCache
) : ComputationResultUseCase<VerifyDocumentationMacroByDocumentationsMacroIdResultRequest, String>(components, computations)
