package com.networkedassets.git4c.core

import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.GetFileContentForExistingRepositoryQuery
import com.networkedassets.git4c.boundary.GetFileContentForExistingRepositoryResultRequest
import com.networkedassets.git4c.boundary.outbound.FileContent
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.bussiness.ComputationCache
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.process.GetFileProcess
import com.networkedassets.git4c.core.usecase.async.ComputationResultUseCase
import com.networkedassets.git4c.core.usecase.async.MultiThreadAsyncUseCase

class GetFileContentForExistingRepositoryUseCase(
        components: BussinesPluginComponents,
        val process: GetFileProcess = components.processing.getFileProcess,
        val repositoryDatabase: RepositoryDatabase = components.providers.repositoryProvider,
        computations: ComputationCache<FileContent> = components.async.getFileContentForExistingRepositoryUseCaseCache

) : MultiThreadAsyncUseCase<GetFileContentForExistingRepositoryQuery, FileContent>
(components, computations, 2) {

    override fun executedAsync(requestId: String, request: GetFileContentForExistingRepositoryQuery) {
        val repository = repositoryDatabase.get(request.repository)
        val branch = request.detailsToGetFile.branch
        if (repository == null) return error(requestId, NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))

        try {
            val fileContent = process.getFile(repository, branch, request.detailsToGetFile.file)
            success(requestId, fileContent)
        } catch (e: Exception) {
            error(requestId, e)
        }
    }
}

class GetFileContentForExistingRepositoryResultUseCase(
        components: BussinesPluginComponents,
        computations: ComputationCache<FileContent> = components.async.getFileContentForExistingRepositoryUseCaseCache
) : ComputationResultUseCase<GetFileContentForExistingRepositoryResultRequest, FileContent>(components, computations)
