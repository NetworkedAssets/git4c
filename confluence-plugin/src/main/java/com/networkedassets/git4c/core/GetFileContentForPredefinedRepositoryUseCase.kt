package com.networkedassets.git4c.core

import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.GetFileContentForPredefinedRepositoryQuery
import com.networkedassets.git4c.boundary.GetFileContentForPredefinedRepositoryResultRequest
import com.networkedassets.git4c.boundary.outbound.FileContent
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.business.Macro
import com.networkedassets.git4c.core.bussiness.ComputationCache
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.datastore.repositories.PredefinedRepositoryDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.process.GetFileProcess
import com.networkedassets.git4c.core.usecase.async.ComputationResultUseCase
import com.networkedassets.git4c.core.usecase.async.MultiThreadAsyncUseCase


class GetFileContentForPredefinedRepositoryUseCase(
        components: BussinesPluginComponents,
        val process: GetFileProcess = components.processing.getFileProcess,
        val predefinedRepositoryRepo: PredefinedRepositoryDatabase = components.database.predefinedRepositoryDatabase,
        val repositoryDatabase: RepositoryDatabase = components.providers.repositoryProvider,
        computations: ComputationCache<FileContent> = components.async.getFileContentForPredefinedRepositoryUseCaseCache
) : MultiThreadAsyncUseCase<GetFileContentForPredefinedRepositoryQuery, FileContent>
(components, computations, 2) {

    override fun executedAsync(requestId: String, request: GetFileContentForPredefinedRepositoryQuery) {
        val repositoryUuid = predefinedRepositoryRepo.get(request.repository)?.repositoryUuid
                ?: return error(requestId, NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val repository = repositoryDatabase.get(repositoryUuid)
        val branch = request.detailsToGetFile.branch
        if (repository == null) return error(requestId, NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val macro = Macro()
        try {
            val file = process.getFile(repository, branch, request.detailsToGetFile.file, macro)
            success(requestId, file)
        } catch (e: Exception) {
            error(requestId, e)
        }
    }
}

class GetFileContentForPredefinedRepositoryResultUseCase(
        components: BussinesPluginComponents,
        computations: ComputationCache<FileContent> = components.async.getFileContentForPredefinedRepositoryUseCaseCache
) : ComputationResultUseCase<GetFileContentForPredefinedRepositoryResultRequest, FileContent>(components, computations)
