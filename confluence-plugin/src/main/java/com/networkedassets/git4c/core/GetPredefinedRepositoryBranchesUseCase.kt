package com.networkedassets.git4c.core

import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.GetPredefinedRepositoryBranchesQuery
import com.networkedassets.git4c.boundary.GetPredefinedRepositoryBranchesResultRequest
import com.networkedassets.git4c.boundary.outbound.Branches
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.bussiness.ComputationCache
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.datastore.repositories.PredefinedRepositoryDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.usecase.async.ComputationResultUseCase
import com.networkedassets.git4c.core.usecase.async.MultiThreadAsyncUseCase

class GetPredefinedRepositoryBranchesUseCase(
        components: BussinesPluginComponents,
        val predefinedRepositoryDatabase: PredefinedRepositoryDatabase = components.database.predefinedRepositoryDatabase,
        val repositoryDatabase: RepositoryDatabase = components.providers.repositoryProvider,
        val importer: SourcePlugin = components.macro.importer,
        computations: ComputationCache<Branches> = components.async.getPredefinedRepositoryBranchesUseCaseCache
) : MultiThreadAsyncUseCase<GetPredefinedRepositoryBranchesQuery, Branches>
(components, computations, 2) {

    override fun executedAsync(requestId: String, request: GetPredefinedRepositoryBranchesQuery) {
        val predefine = predefinedRepositoryDatabase.get(request.predefinedRepository)
                ?: return error(requestId, NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val repository = repositoryDatabase.get(predefine.repositoryUuid)
                ?: return error(requestId, NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))

        try {
            val branches = importer.getBranches(repository).sorted()
            val answer = Branches("", branches)
            success(requestId, answer)
        } catch (e: Exception) {
            error(requestId, e)
        }
    }

}

class GetPredefinedRepositoryBranchesResultUseCase(
        components: BussinesPluginComponents,
        computations: ComputationCache<Branches> = components.async.getPredefinedRepositoryBranchesUseCaseCache
) : ComputationResultUseCase<GetPredefinedRepositoryBranchesResultRequest, Branches>(components, computations)

