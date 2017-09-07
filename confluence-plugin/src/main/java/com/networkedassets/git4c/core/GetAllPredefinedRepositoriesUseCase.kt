package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetAllPredefinedRepositoriesCommand
import com.networkedassets.git4c.boundary.outbound.PredefinedRepository
import com.networkedassets.git4c.core.datastore.repositories.PredefinedRepositoryDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.data.RepositoryWithSshKey
import com.networkedassets.git4c.data.RepositoryWithUsernameAndPassword
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class GetAllPredefinedRepositoriesUseCase(
        val predefinedRepositoryDatabase: PredefinedRepositoryDatabase,
        val repositoryDatabase: RepositoryDatabase
) : UseCase<GetAllPredefinedRepositoriesCommand, List<PredefinedRepository>> {

    override fun execute(request: GetAllPredefinedRepositoriesCommand): Result<List<PredefinedRepository>, Exception> {
        return Result.of {
            predefinedRepositoryDatabase.getAll().map {
                val repository = repositoryDatabase.get(it.repositoryUuid) ?: return@map null
                PredefinedRepository(
                        it.uuid,
                        repository.repositoryPath,
                        authType(repository),
                        it.name
                )
            }.filterNotNull()
        }
    }

    private fun authType(repository: Repository) = when (repository) {
        is RepositoryWithNoAuthorization -> "NOAUTH"
        is RepositoryWithUsernameAndPassword -> "USERNAMEANDPASSWORD"
        is RepositoryWithSshKey -> "SSHKEY"
        else -> throw RuntimeException("Unknown auth type: ${repository.javaClass}")

    }

}

