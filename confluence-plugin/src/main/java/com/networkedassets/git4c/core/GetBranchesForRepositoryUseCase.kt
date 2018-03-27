package com.networkedassets.git4c.core

import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.GetBranchesQuery
import com.networkedassets.git4c.boundary.GetBranchesResultRequest
import com.networkedassets.git4c.boundary.inbound.RepositoryToGetBranches
import com.networkedassets.git4c.boundary.outbound.Branches
import com.networkedassets.git4c.core.bussiness.ComputationCache
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.usecase.async.ComputationResultUseCase
import com.networkedassets.git4c.core.usecase.async.MultiThreadAsyncUseCase
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.data.RepositoryWithSshKey
import com.networkedassets.git4c.data.RepositoryWithUsernameAndPassword


class GetBranchesForRepositoryUseCase(
        components: BussinesPluginComponents,
        val importer: SourcePlugin = components.macro.importer,
        computations: ComputationCache<Branches> = components.async.getBranchesForRepositoryUseCaseCache
) : MultiThreadAsyncUseCase<GetBranchesQuery, Branches>
(components, computations, 2) {

    override fun executedAsync(requestId: String, request: GetBranchesQuery) {

        val repository = detectRepository(request.repositoryToGetBranches)

        val status = importer.verify(repository)

        if (status.isOk()) {
            getBranches(requestId, repository)
        } else {
            error(requestId, IllegalArgumentException(status.status.name))
        }
    }

    private fun getBranches(requestId: String, repository: Repository) {
        try {
            val branches = importer.getBranches(repository).sorted()
            val answer = Branches(null, branches)
            success(requestId, answer)
        } catch (e: Exception) {
            error(requestId, e)
        }
    }

    private fun detectRepository(repositoryToGetBranches: RepositoryToGetBranches) =
            when (repositoryToGetBranches.credentials) {
                is inUserNamePassword -> RepositoryWithUsernameAndPassword(
                        "",
                        repositoryToGetBranches.sourceRepositoryUrl,
                        false,
                        repositoryToGetBranches.credentials.username,
                        repositoryToGetBranches.credentials.password)
                is inSshKey -> RepositoryWithSshKey(
                        "",
                        repositoryToGetBranches.sourceRepositoryUrl,
                        false,
                        repositoryToGetBranches.credentials.sshKey)
                is inNoAuth -> RepositoryWithNoAuthorization(
                        "",
                        repositoryToGetBranches.sourceRepositoryUrl,
                        false
                )
                else -> throw RuntimeException("Unknown auth type: ${repositoryToGetBranches.credentials.javaClass}")
            }
}

class GetBranchesForRepositoryResultUseCase(
        components: BussinesPluginComponents,
        computations: ComputationCache<Branches> = components.async.getBranchesForRepositoryUseCaseCache
) : ComputationResultUseCase<GetBranchesResultRequest, Branches>(components, computations)