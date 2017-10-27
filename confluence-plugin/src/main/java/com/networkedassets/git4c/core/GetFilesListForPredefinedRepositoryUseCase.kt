package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetFilesListForPredefinedRepositoryQuery
import com.networkedassets.git4c.boundary.outbound.FilesList
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.datastore.repositories.PredefinedRepositoryDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.process.GetFilesProcess
import com.networkedassets.git4c.delivery.executor.execution.UseCase


class GetFilesListForPredefinedRepositoryUseCase(
        val process: GetFilesProcess,
        val predefinedRepositoryDatabase: PredefinedRepositoryDatabase,
        val repositoryDatabase: RepositoryDatabase
) : UseCase<GetFilesListForPredefinedRepositoryQuery, FilesList> {
    override fun execute(request: GetFilesListForPredefinedRepositoryQuery): Result<FilesList, Exception> {
        val repositoryUuid = predefinedRepositoryDatabase.get(request.repository)?.repositoryUuid ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val repository = repositoryDatabase.get(repositoryUuid)
        val branch = request.branch
        if (repository == null) return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        return Result.of {
            process.getFiles(repository, branch.branch)
            }
        }
}
