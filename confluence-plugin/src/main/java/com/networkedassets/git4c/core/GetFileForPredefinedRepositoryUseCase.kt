package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetFileForPredefinedRepositoryQuery
import com.networkedassets.git4c.boundary.outbound.File
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.process.GetFileProcess
import com.networkedassets.git4c.core.datastore.repositories.PredefinedRepositoryDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.delivery.executor.execution.UseCase


class GetFileForPredefinedRepositoryUseCase(
        val process: GetFileProcess,
        val predefinedRepositoryRepo: PredefinedRepositoryDatabase,
        val repositoryDatabase: RepositoryDatabase

) : UseCase<GetFileForPredefinedRepositoryQuery, File> {
    override fun execute(request: GetFileForPredefinedRepositoryQuery): Result<File, Exception> {
        val repositoryUuid = predefinedRepositoryRepo.get(request.repository)?.repositoryUuid ?: return@execute Result.error(NotFoundException(request.transactionInfo, ""))
        val repository = repositoryDatabase.get(repositoryUuid)
        val branch = request.detailsToGetFile.branch
        if (repository == null) return@execute Result.error(NotFoundException(request.transactionInfo, ""))
        return Result.of {
            process.getFile(repository, branch, request.detailsToGetFile.file)
        }
    }
}