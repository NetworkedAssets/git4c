package com.networkedassets.git4c.core

import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.GetMethodsForRepositoryQuery
import com.networkedassets.git4c.boundary.GetMethodsForRepositoryResultRequest
import com.networkedassets.git4c.boundary.inbound.RepositoryToGetMethods
import com.networkedassets.git4c.boundary.outbound.Methods
import com.networkedassets.git4c.core.bussiness.ComputationCache
import com.networkedassets.git4c.core.process.GetMethodsProcess
import com.networkedassets.git4c.core.usecase.async.ComputationResultUseCase
import com.networkedassets.git4c.core.usecase.async.MultiThreadAsyncUseCase
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.data.RepositoryWithSshKey
import com.networkedassets.git4c.data.RepositoryWithUsernameAndPassword

class GetMethodsForRepositoryUseCase(
        components: BussinesPluginComponents,
        val process: GetMethodsProcess = components.processing.getMethodsProcess,
        computations: ComputationCache<Methods> = components.async.getMethodsForRepositoryUseCaseCache
) : MultiThreadAsyncUseCase<GetMethodsForRepositoryQuery, Methods>
(components, computations, 2) {

    override fun executedAsync(requestId: String, request: GetMethodsForRepositoryQuery) {
        val repository = detectRepository(request.repositoryToGetMethods)
        val branch = request.repositoryToGetMethods.branch
        val file = request.repositoryToGetMethods.file

        try {
            val methods = process.getMethods(repository, branch, file)
            success(requestId, methods)
        } catch (e: Exception) {
            error(requestId, e)
        }
    }

    private fun detectRepository(repositoryFromCommand: RepositoryToGetMethods): Repository {
        when (repositoryFromCommand.credentials) {
            is inUserNamePassword -> return RepositoryWithUsernameAndPassword(
                    "",
                    repositoryFromCommand.sourceRepositoryUrl,
                    false,
                    repositoryFromCommand.credentials.username,
                    repositoryFromCommand.credentials.password)
            is inSshKey -> return RepositoryWithSshKey(
                    "",
                    repositoryFromCommand.sourceRepositoryUrl,
                    false,
                    repositoryFromCommand.credentials.sshKey)
            is inNoAuth -> return RepositoryWithNoAuthorization(
                    "",
                    repositoryFromCommand.sourceRepositoryUrl,
                    false
            )
            else -> throw RuntimeException("Unknown auth type: ${repositoryFromCommand.credentials.javaClass}")

        }
    }
}

class GetMethodsForRepositoryResultUseCase(
        components: BussinesPluginComponents,
        computations: ComputationCache<Methods> = components.async.getMethodsForRepositoryUseCaseCache
) : ComputationResultUseCase<GetMethodsForRepositoryResultRequest, Methods>(components, computations)
