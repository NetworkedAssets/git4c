package com.networkedassets.git4c.core

import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.GetPredefinedRepositoryCommand
import com.networkedassets.git4c.boundary.GetPredefinedRepositoryResultRequest
import com.networkedassets.git4c.boundary.outbound.PredefinedRepository
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

class GetPredefinedRepositoryUseCase(
        components: BussinesPluginComponents,
        val predefinedRepositoryDatabase: PredefinedRepositoryDatabase = components.database.predefinedRepositoryDatabase,
        val repositoryDatabase: RepositoryDatabase = components.providers.repositoryProvider,
        val importer: SourcePlugin = components.macro.importer,
        computations: ComputationCache<PredefinedRepository> = components.async.getPredefinedRepositoryUseCaseCache
) : MultiThreadAsyncUseCase<GetPredefinedRepositoryCommand, PredefinedRepository>
(components, computations, 2) {

    override fun executedAsync(requestId: String, request: GetPredefinedRepositoryCommand) {
        val predefinedRepository = predefinedRepositoryDatabase.get(request.repositoryId)
                ?: return error(requestId, NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val repository = repositoryDatabase.get(predefinedRepository.repositoryUuid)
                ?: return error(requestId, NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))

        importer.verify(repository).apply {
            if (isOk()) {
                return success(requestId, PredefinedRepository(predefinedRepository.uuid, repository.repositoryPath, authType(repository), predefinedRepository.name, repository.isEditable))
            } else {
                return error(requestId, IllegalArgumentException(status.name))
            }
        }
        return error(requestId, NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
    }

    private fun authType(repository: Repository) = when (repository) {
        is RepositoryWithNoAuthorization -> "NOAUTH"
        is RepositoryWithUsernameAndPassword -> "USERNAMEANDPASSWORD"
        is RepositoryWithSshKey -> "SSHKEY"
        else -> throw RuntimeException("Unknown auth type: ${repository.javaClass}")

    }
}

class GetPredefinedRepositoryResultUseCase(
        components: BussinesPluginComponents,
        computations: ComputationCache<PredefinedRepository> = components.async.getPredefinedRepositoryUseCaseCache
) : ComputationResultUseCase<GetPredefinedRepositoryResultRequest, PredefinedRepository>(components, computations)