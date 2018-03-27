package com.networkedassets.git4c.core

import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.GetMethodsForExistingRepositoryQuery
import com.networkedassets.git4c.boundary.GetMethodsForExistingRepositoryResultRequest
import com.networkedassets.git4c.boundary.outbound.Methods
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.bussiness.ComputationCache
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.process.GetMethodsProcess
import com.networkedassets.git4c.core.usecase.async.ComputationResultUseCase
import com.networkedassets.git4c.core.usecase.async.MultiThreadAsyncUseCase


class GetMethodsForExistingRepositoryUseCase(
        components: BussinesPluginComponents,
        val repositoryDatabase: RepositoryDatabase = components.providers.repositoryProvider,
        val process: GetMethodsProcess = components.processing.getMethodsProcess,
        computations: ComputationCache<Methods> = components.async.getMethodsForExistingRepositoryUseCaseCache
) : MultiThreadAsyncUseCase<GetMethodsForExistingRepositoryQuery, Methods>
(components, computations, 2) {

    override fun executedAsync(requestId: String, request: GetMethodsForExistingRepositoryQuery) {
        val details = request.detailsToGetMethods
        val repository = repositoryDatabase.get(request.repository)
                ?: return error(requestId, NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))

        try {
            val methods = process.getMethods(repository, details.branch, details.file)
            success(requestId, methods)
        } catch (e: Exception) {
            error(requestId, e)
        }
    }
}

class GetMethodsForExistingRepositoryResultUseCase(
        components: BussinesPluginComponents,
        computations: ComputationCache<Methods> = components.async.getMethodsForExistingRepositoryUseCaseCache
) : ComputationResultUseCase<GetMethodsForExistingRepositoryResultRequest, Methods>(components, computations)
