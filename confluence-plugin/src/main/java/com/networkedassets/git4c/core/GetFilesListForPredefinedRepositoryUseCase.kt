package com.networkedassets.git4c.core

import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.GetFilesListForPredefinedRepositoryQuery
import com.networkedassets.git4c.boundary.GetFilesListForPredefinedRepositoryResultRequest
import com.networkedassets.git4c.boundary.outbound.FilesList
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.bussiness.ComputationCache
import com.networkedassets.git4c.core.datastore.repositories.PredefinedRepositoryDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.process.GetFilesProcess
import com.networkedassets.git4c.core.usecase.async.ComputationResultUseCase
import com.networkedassets.git4c.core.usecase.async.MultiThreadAsyncUseCase


class GetFilesListForPredefinedRepositoryUseCase(
        components: BussinesPluginComponents,
        val process: GetFilesProcess = components.processing.getFilesProcess,
        val predefinedRepositoryDatabase: PredefinedRepositoryDatabase = components.database.predefinedRepositoryDatabase,
        val repositoryDatabase: RepositoryDatabase = components.providers.repositoryProvider,
        computations: ComputationCache<FilesList> = components.async.getFilesListForPredefinedRepositoryUseCaseCache
) : MultiThreadAsyncUseCase<GetFilesListForPredefinedRepositoryQuery, FilesList>
(components, computations, 2) {

    override fun executedAsync(requestId: String, request: GetFilesListForPredefinedRepositoryQuery) {
        val repositoryUuid = predefinedRepositoryDatabase.get(request.repository)?.repositoryUuid
                ?: return error(requestId, NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val repository = repositoryDatabase.get(repositoryUuid)
        val branch = request.branch
        if (repository == null) return error(requestId, NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))

        try {
            val files = process.getFiles(repository, branch.branch)
            success(requestId, files)
        } catch (e: Exception) {
            error(requestId, e)
        }
    }
}

class GetFilesListForPredefinedRepositoryResultUseCase(
        components: BussinesPluginComponents,
        computations: ComputationCache<FilesList> = components.async.getFilesListForPredefinedRepositoryUseCaseCache
) : ComputationResultUseCase<GetFilesListForPredefinedRepositoryResultRequest, FilesList>(components, computations)
