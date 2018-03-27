package com.networkedassets.git4c.core

import com.atlassian.confluence.core.service.NotAuthorizedException
import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.GetCommitHistoryForFileByMacroIdQuery
import com.networkedassets.git4c.boundary.GetCommitHistoryForFileResultRequest
import com.networkedassets.git4c.boundary.outbound.BasicCommitInfo
import com.networkedassets.git4c.boundary.outbound.Commits
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.bussiness.ComputationCache
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.process.ICheckUserPermissionProcess
import com.networkedassets.git4c.core.usecase.async.ComputationResultUseCase
import com.networkedassets.git4c.core.usecase.async.MultiThreadAsyncUseCase

class GetCommitHistoryForFileByMacroIdUseCase(
        components: BussinesPluginComponents,
        val importer: SourcePlugin = components.macro.importer,
        val repositoryDatabase: RepositoryDatabase = components.providers.repositoryProvider,
        val macroSettingsDatabase: MacroSettingsDatabase = components.providers.macroSettingsProvider,
        val checkUserPermissionProcess: ICheckUserPermissionProcess = components.processing.checkUserPermissionProcess,
        computations: ComputationCache<Commits> = components.async.getCommitHistoryForFileByMacroIdUseCaseCache
) : MultiThreadAsyncUseCase<GetCommitHistoryForFileByMacroIdQuery, Commits>
(components, computations, 1) {

    override fun executedAsync(requestId: String, request: GetCommitHistoryForFileByMacroIdQuery) {

        val macroId = request.macroId
        val user = request.user

        if (checkUserPermissionProcess.userHasPermissionToMacro(macroId, user) == false) {
            return error(requestId, NotAuthorizedException("User doesn't have permission to this space"))
        }

        val repositoryUuid = macroSettingsDatabase.get(request.macroId)?.repositoryUuid
                ?: return error(requestId, NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val repository = repositoryDatabase.get(repositoryUuid)
                ?: return error(requestId, NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))

        try {
            val commits = importer.getCommitsForFile(repository, request.details.branch, request.details.file)
                    .map { it -> BasicCommitInfo(it.id, it.authorName, it.message, it.timeInMs) }
            success(requestId, Commits(commits))
        } catch (e: Exception) {
            error(requestId, e)
        }
    }
}

class GetCommitHistoryForFileByMacroIdResultUseCase(
        components: BussinesPluginComponents,
        computations: ComputationCache<Commits> = components.async.getCommitHistoryForFileByMacroIdUseCaseCache
) : ComputationResultUseCase<GetCommitHistoryForFileResultRequest, Commits>(components, computations)