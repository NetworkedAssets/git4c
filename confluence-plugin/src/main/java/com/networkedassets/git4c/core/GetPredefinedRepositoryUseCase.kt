package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetPredefinedRepositoryCommand
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.datastore.repositories.PredefinedRepositoryDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.data.RepositoryWithSshKey
import com.networkedassets.git4c.data.RepositoryWithUsernameAndPassword
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class GetPredefinedRepositoryUseCase(
        val predefinedRepositoryDatabase: PredefinedRepositoryDatabase,
        val repositoryDatabase: RepositoryDatabase,
        val importer: SourcePlugin
) : UseCase<GetPredefinedRepositoryCommand, com.networkedassets.git4c.boundary.outbound.PredefinedRepository> {

    override fun execute(request: GetPredefinedRepositoryCommand): Result<com.networkedassets.git4c.boundary.outbound.PredefinedRepository, Exception> {

        val predefinedRepository = predefinedRepositoryDatabase.get(request.repositoryId) ?: return@execute Result.error(NotFoundException(request.transactionInfo, ""))
        val repository = repositoryDatabase.get(predefinedRepository.repositoryUuid) ?: return@execute Result.error(NotFoundException(request.transactionInfo, ""))

        importer.verify(repository).apply {
            if (isOk()) {
                return@execute Result.of { com.networkedassets.git4c.boundary.outbound.PredefinedRepository(predefinedRepository.uuid, repository.repositoryPath, authType(repository), predefinedRepository.name) }
            } else {
                return@execute Result.error(IllegalArgumentException(status.name))
            }
        }
        return Result.error(NotFoundException(request.transactionInfo, ""))
    }

    private fun authType(repository: Repository) = when (repository) {
        is RepositoryWithNoAuthorization -> "NOAUTH"
        is RepositoryWithUsernameAndPassword -> "USERNAMEANDPASSWORD"
        is RepositoryWithSshKey -> "SSHKEY"
        else -> throw RuntimeException("Unknown auth type: ${repository.javaClass}")

    }
}


