package com.networkedassets.git4c.core

import com.networkedassets.git4c.boundary.GetExistingRepositoryBranchesQuery
import com.networkedassets.git4c.boundary.outbound.Branches
import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class GetExistingRepositoryBranchesUseCase(
        val repositoryDatabase: RepositoryDatabase,
        val importer: SourcePlugin
) : UseCase<GetExistingRepositoryBranchesQuery, Branches> {
    override fun execute(request: GetExistingRepositoryBranchesQuery): Result<Branches, Exception> {
        val repository = repositoryDatabase.get(request.repositoryUuid) ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        return Result.of { Branches("", importer.getBranches(repository).sorted()) }

    }

}

