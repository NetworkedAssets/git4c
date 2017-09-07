package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetFilesForExistingRepositoryQuery
import com.networkedassets.git4c.boundary.outbound.Files
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.process.GetFilesProcess
import com.networkedassets.git4c.delivery.executor.execution.UseCase


class GetFilesForExistingRepositoryUseCase (
        val process: GetFilesProcess,
        val repositoryDatabase: RepositoryDatabase
) : UseCase<GetFilesForExistingRepositoryQuery, Files> {
    override fun execute(request: GetFilesForExistingRepositoryQuery): Result<Files, Exception> {
        val repository = repositoryDatabase.get(request.repository)
        val branch = request.branch
        if (repository == null) return@execute Result.error(NotFoundException(request.transactionInfo, ""))
        return Result.of {
            process.getFiles(repository, branch.branch)
        }
    }

}