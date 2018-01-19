package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetRepositoryUsagesForUserQuery
import com.networkedassets.git4c.boundary.outbound.RepositoryUsages
import com.networkedassets.git4c.core.datastore.repositories.RepositoryUsageDatabase
import com.networkedassets.git4c.data.RepositoryUsage
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class GetRepositoryUsagesForUserUseCase(
        val repositoryUsageDatabase: RepositoryUsageDatabase
) : UseCase<GetRepositoryUsagesForUserQuery, RepositoryUsages> {
    override fun execute(request: GetRepositoryUsagesForUserQuery): Result<RepositoryUsages, Exception> {
        val usages = repositoryUsageDatabase.getByUsername(request.username)
        return Result.of { convertToOutbound(usages) }
    }

    private fun convertToOutbound(usages: List<RepositoryUsage>): RepositoryUsages {
        val outUsages: RepositoryUsages
        if (usages.isEmpty()) {
            outUsages = RepositoryUsages(listOf())
        } else {
            outUsages = RepositoryUsages(usages.map { com.networkedassets.git4c.boundary.outbound.RepositoryUsage(it.repositoryName, it.repositoryUuid) })
        }
        return outUsages
    }

}