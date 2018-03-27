package com.networkedassets.git4c.core

import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.ModifyPredefinedRepositoryCommand
import com.networkedassets.git4c.boundary.ModifyPredefinedRepositoryResultRequest
import com.networkedassets.git4c.boundary.inbound.PredefinedRepository
import com.networkedassets.git4c.boundary.outbound.SavedPredefinedRepository
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.bussiness.ComputationCache
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.datastore.repositories.PredefinedRepositoryDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.usecase.async.ComputationResultUseCase
import com.networkedassets.git4c.core.usecase.async.MultiThreadAsyncUseCase
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.data.RepositoryWithSshKey
import com.networkedassets.git4c.data.RepositoryWithUsernameAndPassword

class ModifyPredefinedRepositoryUseCase(
        components: BussinesPluginComponents,
        val predefinedRepositoryDatabase: PredefinedRepositoryDatabase = components.database.predefinedRepositoryDatabase,
        val repositoryDatabase: RepositoryDatabase = components.providers.repositoryProvider,
        val importer: SourcePlugin = components.macro.importer,
        computations: ComputationCache<SavedPredefinedRepository> = components.async.modifyPredefinedRepositoryUseCaseCache
) : MultiThreadAsyncUseCase<ModifyPredefinedRepositoryCommand, SavedPredefinedRepository>
(components, computations, 2) {

    override fun executedAsync(requestId: String, request: ModifyPredefinedRepositoryCommand) {
        val existingPredefinedRepository = predefinedRepositoryDatabase.get(request.repositoryId)
                ?: return error(requestId, NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val existingRepository = repositoryDatabase.get(existingPredefinedRepository.repositoryUuid)
                ?: return error(requestId, NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))

        val repository = inboundRepositoryToCore(request.predefinedRepositoryToModify, existingRepository.uuid)
        val predefinedRepository = com.networkedassets.git4c.data.PredefinedRepository(request.repositoryId, repository.uuid, request.predefinedRepositoryToModify.repositoryName)

        importer.verify(repository).apply {
            if (isOk()) {
                repositoryDatabase.remove(repository.uuid)
                repositoryDatabase.put(repository.uuid, repository)
                predefinedRepositoryDatabase.put(predefinedRepository.uuid, predefinedRepository)
                return success(requestId, SavedPredefinedRepository(predefinedRepository.uuid))
            } else {
                return error(requestId, IllegalArgumentException(status.name))
            }
        }
        return error(requestId, NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
    }


    private fun inboundRepositoryToCore(repositoryToCreate: PredefinedRepository, uuid: String): Repository {
        when (repositoryToCreate.credentials) {
            is inUserNamePassword -> return RepositoryWithUsernameAndPassword(
                    uuid,
                    repositoryToCreate.sourceRepositoryUrl,
                    repositoryToCreate.editable,
                    repositoryToCreate.credentials.username,
                    repositoryToCreate.credentials.password)
            is inSshKey -> return RepositoryWithSshKey(
                    uuid,
                    repositoryToCreate.sourceRepositoryUrl,
                    repositoryToCreate.editable,
                    repositoryToCreate.credentials.sshKey)
            is inNoAuth -> return RepositoryWithNoAuthorization(
                    uuid,
                    repositoryToCreate.sourceRepositoryUrl,
                    repositoryToCreate.editable
            )
            else -> throw RuntimeException("Unknown auth type: ${repositoryToCreate.credentials.javaClass}")

        }
    }


}

class ModifyPredefinedRepositoryResultUseCase(
        components: BussinesPluginComponents,
        computations: ComputationCache<SavedPredefinedRepository> = components.async.modifyPredefinedRepositoryUseCaseCache
) : ComputationResultUseCase<ModifyPredefinedRepositoryResultRequest, SavedPredefinedRepository>(components, computations)



