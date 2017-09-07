package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetFilesForPredefinedRepositoryQuery
import com.networkedassets.git4c.boundary.outbound.Files
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.datastore.repositories.PredefinedRepositoryDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.process.GetFilesProcess
import com.networkedassets.git4c.delivery.executor.execution.UseCase


class GetFilesForPredefinedRepositoryUseCase(
        val process: GetFilesProcess,
        val predefinedRepositoryDatabase: PredefinedRepositoryDatabase,
        val repositoryDatabase: RepositoryDatabase
) : UseCase<GetFilesForPredefinedRepositoryQuery, Files> {
    override fun execute(request: GetFilesForPredefinedRepositoryQuery): Result<Files, Exception> {
        val repositoryUuid = predefinedRepositoryDatabase.get(request.repository)?.repositoryUuid ?: return@execute Result.error(NotFoundException(request.transactionInfo, ""))
        val repository = repositoryDatabase.get(repositoryUuid)
        val branch = request.branch
        if (repository == null) return@execute Result.error(NotFoundException(request.transactionInfo, ""))
        return Result.of {
            process.getFiles(repository, branch.branch)
            }
        }
}
