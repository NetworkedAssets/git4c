package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.ModifyPredefinedRepositoryCommand
import com.networkedassets.git4c.boundary.inbound.PredefinedRepository
import com.networkedassets.git4c.boundary.outbound.SavedPredefinedRepository
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.datastore.repositories.PredefinedRepositoryDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.data.RepositoryWithSshKey
import com.networkedassets.git4c.data.RepositoryWithUsernameAndPassword
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class ModifyPredefinedRepositoryUseCase(
        val predefinedRepositoryDatabase: PredefinedRepositoryDatabase,
        val repositoryDatabase: RepositoryDatabase,
        val importer: SourcePlugin
) : UseCase<ModifyPredefinedRepositoryCommand, SavedPredefinedRepository> {

    override fun execute(request: ModifyPredefinedRepositoryCommand): Result<SavedPredefinedRepository, Exception> {


        val existingPredefinedRepository = predefinedRepositoryDatabase.get(request.repositoryId) ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val existingRepository = repositoryDatabase.get(existingPredefinedRepository.repositoryUuid) ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))


        val repository = inboundRepositoryToCore(request.predefinedRepositoryToModify, existingRepository.uuid)

        val predefinedRepository = com.networkedassets.git4c.data.PredefinedRepository(request.repositoryId, repository.uuid, request.predefinedRepositoryToModify.repositoryName)

        importer.verify(repository).apply {
            if (isOk()) {
                repositoryDatabase.remove(repository.uuid)
                repositoryDatabase.put(repository.uuid, repository)
                predefinedRepositoryDatabase.put(predefinedRepository.uuid, predefinedRepository)
                return@execute Result.of { SavedPredefinedRepository(predefinedRepository.uuid) }
            } else {
                return@execute Result.error(IllegalArgumentException(status.name))
            }
        }
        return Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
    }


    private fun inboundRepositoryToCore(repositoryToCreate: PredefinedRepository, uuid: String): Repository {
        when (repositoryToCreate.credentials) {
            is inUserNamePassword -> return RepositoryWithUsernameAndPassword(
                    uuid,
                    repositoryToCreate.sourceRepositoryUrl,
                    repositoryToCreate.credentials.username,
                    repositoryToCreate.credentials.password)
            is inSshKey -> return RepositoryWithSshKey(
                    uuid,
                    repositoryToCreate.sourceRepositoryUrl,
                    repositoryToCreate.credentials.sshKey)
            is inNoAuth -> return RepositoryWithNoAuthorization(
                    uuid,
                    repositoryToCreate.sourceRepositoryUrl
            )
            else -> throw RuntimeException("Unknown auth type: ${repositoryToCreate.credentials.javaClass}")

        }
    }


}
