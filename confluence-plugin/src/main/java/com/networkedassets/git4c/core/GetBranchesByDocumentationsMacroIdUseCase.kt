package com.networkedassets.git4c.core

import com.atlassian.confluence.core.service.NotAuthorizedException
import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.GetBranchesByDocumentationsMacroIdQuery
import com.networkedassets.git4c.boundary.GetBranchesByDocumentationsMacroIdResultRequest
import com.networkedassets.git4c.boundary.outbound.Branches
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.bussiness.ComputationCache
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.process.GetBranchesProcess
import com.networkedassets.git4c.core.process.ICheckUserPermissionProcess
import com.networkedassets.git4c.core.usecase.async.ComputationResultUseCase
import com.networkedassets.git4c.core.usecase.async.MultiThreadAsyncUseCase
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.data.Repository

class GetBranchesByDocumentationsMacroIdUseCase(
        components: BussinesPluginComponents,
        val getBranchesProcess: GetBranchesProcess = components.processing.getBranchesProcess,
        val macroSettingsDatabase: MacroSettingsDatabase = components.providers.macroSettingsProvider,
        val repositoryDatabase: RepositoryDatabase = components.providers.repositoryProvider,
        val checkUserPermissionProcess: ICheckUserPermissionProcess = components.processing.checkUserPermissionProcess,
        computations: ComputationCache<Branches> = components.async.getBranchesByDocumentationsMacroIdUseCaseCache
) : MultiThreadAsyncUseCase<GetBranchesByDocumentationsMacroIdQuery, Branches>
(components, computations, 6) {

    override fun executedAsync(requestId: String, request: GetBranchesByDocumentationsMacroIdQuery) {

        val macroId = request.macroId
        val user = request.user

        if (checkUserPermissionProcess.userHasPermissionToMacro(macroId, user) == false) {
            return error(requestId, NotAuthorizedException("User doesn't have permission to this space"))
        }

        val macroSettings = macroSettingsDatabase.get(request.macroId)
                ?: return error(requestId, NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        if (macroSettings.repositoryUuid == null) return error(requestId, NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val repository = repositoryDatabase.get(macroSettings.repositoryUuid)
                ?: return error(requestId, NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))

        try {
            val answer = getBranches(macroSettings, repository)
            success(requestId, answer)
        } catch (e: Exception) {
            error(requestId, e)
        }
    }

    private fun getBranches(macroSettings: MacroSettings, repository: Repository) = getBranchesProcess.fetchBranchList(macroSettings, repository)
}

class GetBranchesByDocumentationsMacroIdResultUseCase(
        components: BussinesPluginComponents,
        computations: ComputationCache<Branches> = components.async.getBranchesByDocumentationsMacroIdUseCaseCache
) : ComputationResultUseCase<GetBranchesByDocumentationsMacroIdResultRequest, Branches>(components, computations)