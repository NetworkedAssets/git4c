package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetFileForExistingRepositoryQuery
import com.networkedassets.git4c.boundary.outbound.File
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.process.GetFileProcess
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class GetFileForExistingRepositoryUseCase(
        val process: GetFileProcess,
        val repositoryDatabase: RepositoryDatabase

) : UseCase<GetFileForExistingRepositoryQuery, File> {
    override fun execute(request: GetFileForExistingRepositoryQuery): Result<File, Exception> {
        val repository = repositoryDatabase.get(request.repository)
        val branch = request.detailsToGetFile.branch
        if (repository == null) return@execute Result.error(NotFoundException(request.transactionInfo, ""))
        return Result.of {
            process.getFile(repository, branch, request.detailsToGetFile.file)
        }
    }
}