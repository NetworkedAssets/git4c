package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetPredefinedRepositoryBranchesQuery
import com.networkedassets.git4c.boundary.outbound.Branches
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.datastore.repositories.PredefinedRepositoryDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class GetPredefinedRepositoryBranchesUseCase(
        val predefinedRepositoryDatabase: PredefinedRepositoryDatabase,
        val repositoryDatabase: RepositoryDatabase,
        val importer: SourcePlugin
) : UseCase<GetPredefinedRepositoryBranchesQuery, Branches> {
    override fun execute(request: GetPredefinedRepositoryBranchesQuery): Result<Branches, Exception> {
        val predefine = predefinedRepositoryDatabase.get(request.predefinedRepository) ?: return@execute Result.error(NotFoundException(request.transactionInfo, "Repository has been deleted."))
        val repository = repositoryDatabase.get(predefine.repositoryUuid) ?: return@execute Result.error(NotFoundException(request.transactionInfo, "Repository has been deleted."))
        return Result.of { Branches("", importer.getBranches(repository)) }

    }

}

