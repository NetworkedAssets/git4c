package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetMethodsForPredefinedRepositoryQuery
import com.networkedassets.git4c.boundary.outbound.Methods
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.datastore.repositories.PredefinedRepositoryDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.process.GetMethodsProcess
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class GetMethodsForPredefinedRepositoryUseCase(
        val predefinedRepositoryDatabase: PredefinedRepositoryDatabase,
        val repositoryDatabase: RepositoryDatabase,
        val process: GetMethodsProcess
) : UseCase<GetMethodsForPredefinedRepositoryQuery, Methods> {

    override fun execute(request: GetMethodsForPredefinedRepositoryQuery): Result<Methods, Exception> {

        val details = request.detailsToGetMethods
        val predefine = predefinedRepositoryDatabase.get(request.repository) ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val repository = repositoryDatabase.get(predefine.repositoryUuid) ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))

        return Result.of {
            process.getMethods(repository, details.branch, details.file)
        }
    }
}