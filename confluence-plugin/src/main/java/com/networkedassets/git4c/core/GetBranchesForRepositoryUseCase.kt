package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetBranchesQuery
import com.networkedassets.git4c.boundary.inbound.RepositoryToGetBranches
import com.networkedassets.git4c.boundary.outbound.Branches
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.data.RepositoryWithSshKey
import com.networkedassets.git4c.data.RepositoryWithUsernameAndPassword
import com.networkedassets.git4c.delivery.executor.execution.UseCase


class GetBranchesForRepositoryUseCase(val importer: SourcePlugin) : UseCase<GetBranchesQuery, Branches> {

    override fun execute(request: GetBranchesQuery): Result<Branches, Exception> {

        val repository = detectRepository(request.repositoryToGetBranches)

        val status = importer.verify(repository)

        return if (status.isOk()) {
            Result.of { Branches(null, importer.getBranches(repository).sorted()) }
        } else {
            Result.error(IllegalArgumentException(status.status.name))
        }
    }

    private fun detectRepository(repositoryToGetBranches: RepositoryToGetBranches) =
            when (repositoryToGetBranches.credentials) {
                is inUserNamePassword -> RepositoryWithUsernameAndPassword(
                        "",
                        repositoryToGetBranches.sourceRepositoryUrl,
                        repositoryToGetBranches.credentials.username,
                        repositoryToGetBranches.credentials.password)
                is inSshKey -> RepositoryWithSshKey(
                        "",
                        repositoryToGetBranches.sourceRepositoryUrl,
                        repositoryToGetBranches.credentials.sshKey)
                is inNoAuth -> RepositoryWithNoAuthorization(
                        "",
                        repositoryToGetBranches.sourceRepositoryUrl
                )
                else -> throw RuntimeException("Unknown auth type: ${repositoryToGetBranches.credentials.javaClass}")
            }
}