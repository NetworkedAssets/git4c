package com.networkedassets.git4c.core

import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.CreatePredefinedRepositoryCommand
import com.networkedassets.git4c.boundary.CreatePredefinedRepositoryResultRequest
import com.networkedassets.git4c.boundary.inbound.PredefinedRepository
import com.networkedassets.git4c.boundary.outbound.SavedPredefinedRepository
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.bussiness.ComputationCache
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.common.IdentifierGenerator
import com.networkedassets.git4c.core.datastore.repositories.PredefinedRepositoryDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.usecase.async.ComputationResultUseCase
import com.networkedassets.git4c.core.usecase.async.MultiThreadAsyncUseCase
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.data.RepositoryWithSshKey
import com.networkedassets.git4c.data.RepositoryWithUsernameAndPassword

class CreatePredefinedRepositoryUseCase(
        components: BussinesPluginComponents,
        val predefinedRepositoryDatabase: PredefinedRepositoryDatabase = components.database.predefinedRepositoryDatabase,
        val repositoryDatabase: RepositoryDatabase = components.providers.repositoryProvider,
        val importer: SourcePlugin = components.macro.importer,
        val idGenerator: IdentifierGenerator = components.utils.idGenerator,
        computations: ComputationCache<SavedPredefinedRepository> = components.async.createPredefinedRepositoryUseCaseCache
) : MultiThreadAsyncUseCase<CreatePredefinedRepositoryCommand, SavedPredefinedRepository>
(components, computations, 1) {

    override fun executedAsync(requestId: String, request: CreatePredefinedRepositoryCommand) {
        val repository = detectRepository(request.predefinedRepositoryToCreate)
        val predefinedRepository = com.networkedassets.git4c.data.PredefinedRepository(idGenerator.generateNewIdentifier(), repository.uuid, request.predefinedRepositoryToCreate.repositoryName)

        importer.verify(repository).apply {
            if (isOk()) {
                predefinedRepositoryDatabase.put(predefinedRepository.uuid, predefinedRepository)
                repositoryDatabase.put(repository.uuid, repository)
                return success(requestId, SavedPredefinedRepository(predefinedRepository.uuid))
            } else {
                return error(requestId, IllegalArgumentException(status.name))
            }
        }
        return error(requestId, NotFoundException(request.transactionInfo, ""))
    }


    private fun detectRepository(repositoryToCreate: PredefinedRepository): Repository {
        when (repositoryToCreate.credentials) {
            is inUserNamePassword -> return RepositoryWithUsernameAndPassword(
                    idGenerator.generateNewIdentifier(),
                    repositoryToCreate.sourceRepositoryUrl,
                    repositoryToCreate.editable,
                    repositoryToCreate.credentials.username,
                    repositoryToCreate.credentials.password)
            is inSshKey -> return RepositoryWithSshKey(
                    idGenerator.generateNewIdentifier(),
                    repositoryToCreate.sourceRepositoryUrl,
                    repositoryToCreate.editable,
                    repositoryToCreate.credentials.sshKey)
            is inNoAuth -> return RepositoryWithNoAuthorization(
                    idGenerator.generateNewIdentifier(),
                    repositoryToCreate.sourceRepositoryUrl,
                    repositoryToCreate.editable
            )
            else -> throw RuntimeException("Unknown auth type: ${repositoryToCreate.credentials.javaClass}")

        }
    }

}

class CreatePredefinedRepositoryResultUseCase(
        components: BussinesPluginComponents,
        computations: ComputationCache<SavedPredefinedRepository> = components.async.createPredefinedRepositoryUseCaseCache
) : ComputationResultUseCase<CreatePredefinedRepositoryResultRequest, SavedPredefinedRepository>(components, computations)