package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.CreatePredefinedRepositoryCommand
import com.networkedassets.git4c.boundary.inbound.PredefinedRepository
import com.networkedassets.git4c.boundary.outbound.SavedPredefinedRepository
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.common.IdentifierGenerator
import com.networkedassets.git4c.core.datastore.repositories.PredefinedRepositoryDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.data.RepositoryWithSshKey
import com.networkedassets.git4c.data.RepositoryWithUsernameAndPassword
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class CreatePredefinedRepositoryUseCase(
        val predefinedRepositoryDatabase: PredefinedRepositoryDatabase,
        val repositoryDatabase: RepositoryDatabase,
        val importer: SourcePlugin,
        val idGenerator: IdentifierGenerator
) : UseCase<CreatePredefinedRepositoryCommand, SavedPredefinedRepository> {

    override fun execute(request: CreatePredefinedRepositoryCommand): Result<SavedPredefinedRepository, Exception> {

        val repository = detectRepository(request.predefinedRepositoryToCreate)
        val predefinedRepository = com.networkedassets.git4c.data.PredefinedRepository(idGenerator.generateNewIdentifier(), repository.uuid, request.predefinedRepositoryToCreate.repositoryName)

        importer.verify(repository).apply {
            if (isOk()) {
                predefinedRepositoryDatabase.insert(predefinedRepository.uuid, predefinedRepository)
                repositoryDatabase.insert(repository.uuid, repository)
                return@execute Result.of { SavedPredefinedRepository(predefinedRepository.uuid) }
            } else {
                return@execute Result.error(IllegalArgumentException(status.name))
            }
        }
        return Result.error(NotFoundException(request.transactionInfo, ""))
    }


    private fun detectRepository(repositoryToCreate: PredefinedRepository): Repository {
        when (repositoryToCreate.credentials) {
            is inUserNamePassword -> return RepositoryWithUsernameAndPassword(
                    idGenerator.generateNewIdentifier(),
                    repositoryToCreate.sourceRepositoryUrl,
                    repositoryToCreate.credentials.username,
                    repositoryToCreate.credentials.password)
            is inSshKey -> return RepositoryWithSshKey(
                    idGenerator.generateNewIdentifier(),
                    repositoryToCreate.sourceRepositoryUrl,
                    repositoryToCreate.credentials.sshKey)
            is inNoAuth -> return RepositoryWithNoAuthorization(
                    idGenerator.generateNewIdentifier(),
                    repositoryToCreate.sourceRepositoryUrl
            )
            else -> throw RuntimeException("Unknown auth type: ${repositoryToCreate.credentials.javaClass}")

        }
    }

}
