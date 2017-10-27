package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetFilesListForExistingRepositoryQuery
import com.networkedassets.git4c.boundary.outbound.FilesList
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.process.GetFilesProcess
import com.networkedassets.git4c.delivery.executor.execution.UseCase


class GetFilesListForExistingRepositoryUseCase(
        val process: GetFilesProcess,
        val repositoryDatabase: RepositoryDatabase
) : UseCase<GetFilesListForExistingRepositoryQuery, FilesList> {
    override fun execute(request: GetFilesListForExistingRepositoryQuery): Result<FilesList, Exception> {
        val repository = repositoryDatabase.get(request.repository)
        val branch = request.branch
        if (repository == null) return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        return Result.of {
            process.getFiles(repository, branch.branch)
        }
    }

}