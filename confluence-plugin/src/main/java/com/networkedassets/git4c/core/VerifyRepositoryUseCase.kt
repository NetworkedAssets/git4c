package com.networkedassets.git4c.core

import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.VerifyRepositoryCommand
import com.networkedassets.git4c.boundary.VerifyRepositoryResultRequest
import com.networkedassets.git4c.boundary.inbound.NoAuth
import com.networkedassets.git4c.boundary.inbound.RepositoryToVerify
import com.networkedassets.git4c.boundary.inbound.SshKeyAuthorization
import com.networkedassets.git4c.boundary.inbound.UsernamePasswordAuthorization
import com.networkedassets.git4c.boundary.outbound.VerificationInfo
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.bussiness.ComputationCache
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.usecase.async.ComputationResultUseCase
import com.networkedassets.git4c.core.usecase.async.MultiThreadAsyncUseCase
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.data.RepositoryWithSshKey
import com.networkedassets.git4c.data.RepositoryWithUsernameAndPassword

class VerifyRepositoryUseCase(
        components: BussinesPluginComponents,
        val importer: SourcePlugin = components.macro.importer,
        computations: ComputationCache<VerificationInfo> = components.async.verifyRepositoryUseCaseCache
) : MultiThreadAsyncUseCase<VerifyRepositoryCommand, VerificationInfo>
(components, computations, 2) {

    override fun executedAsync(requestId: String, request: VerifyRepositoryCommand) {
        val repository = inboundToCoreRepository(request.repositoryToVerify)
        importer.verify(repository).apply {
            if (isOk()) {
                return success(requestId, VerificationInfo(status))
            } else {
                return error(requestId, IllegalArgumentException(status.name))
            }
        }
        return error(requestId, NotFoundException(request.transactionInfo, ""))
    }


    fun inboundToCoreRepository(repository: RepositoryToVerify): Repository {
        return when (repository.credentials) {
            is UsernamePasswordAuthorization -> {
                RepositoryWithUsernameAndPassword(
                        "",
                        repository.sourceRepositoryUrl,
                        false,
                        repository.credentials.username,
                        repository.credentials.password
                )
            }
            is SshKeyAuthorization -> {
                RepositoryWithSshKey(
                        "",
                        repository.sourceRepositoryUrl,
                        false,
                        repository.credentials.sshKey
                )
            }
            is NoAuth -> {
                RepositoryWithNoAuthorization(
                        "",
                        repository.sourceRepositoryUrl,
                        false
                )
            }
            else -> throw RuntimeException("Unknown auth type: ${repository.credentials.javaClass}")

        }
    }
}

class VerifyRepositoryResultUseCase(
        components: BussinesPluginComponents,
        computations: ComputationCache<VerificationInfo> = components.async.verifyRepositoryUseCaseCache
) : ComputationResultUseCase<VerifyRepositoryResultRequest, VerificationInfo>(components, computations)

