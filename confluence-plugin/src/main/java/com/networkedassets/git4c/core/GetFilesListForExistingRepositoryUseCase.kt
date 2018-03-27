package com.networkedassets.git4c.core

import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.GetFilesListForExistingRepositoryQuery
import com.networkedassets.git4c.boundary.GetFilesListForExistingRepositoryResultRequest
import com.networkedassets.git4c.boundary.outbound.FilesList
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.bussiness.ComputationCache
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.process.GetFilesProcess
import com.networkedassets.git4c.core.usecase.async.ComputationResultUseCase
import com.networkedassets.git4c.core.usecase.async.MultiThreadAsyncUseCase


class GetFilesListForExistingRepositoryUseCase(
        components: BussinesPluginComponents,
        val process: GetFilesProcess = components.processing.getFilesProcess,
        val repositoryDatabase: RepositoryDatabase = components.providers.repositoryProvider,
        computations: ComputationCache<FilesList> = components.async.getFilesListForExistingRepositoryUseCaseCache
) : MultiThreadAsyncUseCase<GetFilesListForExistingRepositoryQuery, FilesList>
(components, computations, 2) {

    override fun executedAsync(requestId: String, request: GetFilesListForExistingRepositoryQuery) {
        val repository = repositoryDatabase.get(request.repository)
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

class GetFilesListForExistingRepositoryResultUseCase(
        components: BussinesPluginComponents,
        computations: ComputationCache<FilesList> = components.async.getFilesListForExistingRepositoryUseCaseCache
) : ComputationResultUseCase<GetFilesListForExistingRepositoryResultRequest, FilesList>(components, computations)
