package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetFileContentForExistingRepositoryQuery
import com.networkedassets.git4c.boundary.outbound.FileContent
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.process.GetFileProcess
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class GetFileContentForExistingRepositoryUseCase(
        val process: GetFileProcess,
        val repositoryDatabase: RepositoryDatabase

) : UseCase<GetFileContentForExistingRepositoryQuery, FileContent> {
    override fun execute(request: GetFileContentForExistingRepositoryQuery): Result<FileContent, Exception> {
        val repository = repositoryDatabase.get(request.repository)
        val branch = request.detailsToGetFile.branch
        if (repository == null) return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        return Result.of {
            process.getFile(repository, branch, request.detailsToGetFile.file)
        }
    }
}