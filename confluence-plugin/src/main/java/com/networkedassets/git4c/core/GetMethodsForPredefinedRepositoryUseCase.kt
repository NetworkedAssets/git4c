package com.networkedassets.git4c.core

import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.GetMethodsForPredefinedRepositoryQuery
import com.networkedassets.git4c.boundary.GetMethodsForPredefinedRepositoryResultRequest
import com.networkedassets.git4c.boundary.outbound.Methods
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.bussiness.ComputationCache
import com.networkedassets.git4c.core.datastore.repositories.PredefinedRepositoryDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.process.GetMethodsProcess
import com.networkedassets.git4c.core.usecase.async.ComputationResultUseCase
import com.networkedassets.git4c.core.usecase.async.MultiThreadAsyncUseCase

class GetMethodsForPredefinedRepositoryUseCase(
        components: BussinesPluginComponents,
        val predefinedRepositoryDatabase: PredefinedRepositoryDatabase = components.database.predefinedRepositoryDatabase,
        val repositoryDatabase: RepositoryDatabase = components.providers.repositoryProvider,
        val process: GetMethodsProcess = components.processing.getMethodsProcess,
        computations: ComputationCache<Methods> = components.async.getMethodsForPredefinedRepositoryUseCaseCache
) : MultiThreadAsyncUseCase<GetMethodsForPredefinedRepositoryQuery, Methods>
(components, computations, 2) {

    override fun executedAsync(requestId: String, request: GetMethodsForPredefinedRepositoryQuery) {
        val details = request.detailsToGetMethods
        val predefine = predefinedRepositoryDatabase.get(request.repository)
                ?: return error(requestId, NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val repository = repositoryDatabase.get(predefine.repositoryUuid)
                ?: return error(requestId, NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))

        try {
            val methods = process.getMethods(repository, details.branch, details.file)
            success(requestId, methods)
        } catch (e: Exception) {
            error(requestId, e)
        }
    }
}

class GetMethodsForPredefinedRepositoryResultUseCase(
        components: BussinesPluginComponents,
        computations: ComputationCache<Methods> = components.async.getMethodsForPredefinedRepositoryUseCaseCache
) : ComputationResultUseCase<GetMethodsForPredefinedRepositoryResultRequest, Methods>(components, computations)
