package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetFileContentForPredefinedRepositoryQuery
import com.networkedassets.git4c.boundary.outbound.FileContent
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.process.GetFileProcess
import com.networkedassets.git4c.core.datastore.repositories.PredefinedRepositoryDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.delivery.executor.execution.UseCase


class GetFileContentForPredefinedRepositoryUseCase(
        val process: GetFileProcess,
        val predefinedRepositoryRepo: PredefinedRepositoryDatabase,
        val repositoryDatabase: RepositoryDatabase

) : UseCase<GetFileContentForPredefinedRepositoryQuery, FileContent> {
    override fun execute(request: GetFileContentForPredefinedRepositoryQuery): Result<FileContent, Exception> {
        val repositoryUuid = predefinedRepositoryRepo.get(request.repository)?.repositoryUuid ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val repository = repositoryDatabase.get(repositoryUuid)
        val branch = request.detailsToGetFile.branch
        if (repository == null) return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        return Result.of {
            process.getFile(repository, branch, request.detailsToGetFile.file)
        }
    }
}