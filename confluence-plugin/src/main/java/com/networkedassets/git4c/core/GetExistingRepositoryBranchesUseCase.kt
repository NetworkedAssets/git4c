package com.networkedassets.git4c.core

import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.GetExistingRepositoryBranchesQuery
import com.networkedassets.git4c.boundary.GetExistingRepositoryBranchesResultRequest
import com.networkedassets.git4c.boundary.outbound.Branches
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.bussiness.ComputationCache
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.usecase.async.ComputationResultUseCase
import com.networkedassets.git4c.core.usecase.async.MultiThreadAsyncUseCase

class GetExistingRepositoryBranchesUseCase(
        components: BussinesPluginComponents,
        val repositoryDatabase: RepositoryDatabase = components.providers.repositoryProvider,
        val importer: SourcePlugin = components.macro.importer,
        computations: ComputationCache<Branches> = components.async.getExistingRepositoryBranchesUseCaseCache
) : MultiThreadAsyncUseCase<GetExistingRepositoryBranchesQuery, Branches>
(components, computations, 2) {

    override fun executedAsync(requestId: String, request: GetExistingRepositoryBranchesQuery) {

        val repository = repositoryDatabase.get(request.repositoryUuid)
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


class GetExistingRepositoryBranchesResultUseCase(
        components: BussinesPluginComponents,
        computations: ComputationCache<Branches> = components.async.getExistingRepositoryBranchesUseCaseCache
) : ComputationResultUseCase<GetExistingRepositoryBranchesResultRequest, Branches>(components, computations)

