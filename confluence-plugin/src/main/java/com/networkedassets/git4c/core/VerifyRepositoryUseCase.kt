package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.VerifyRepositoryCommand
import com.networkedassets.git4c.boundary.inbound.NoAuth
import com.networkedassets.git4c.boundary.inbound.RepositoryToVerify
import com.networkedassets.git4c.boundary.inbound.SshKeyAuthorization
import com.networkedassets.git4c.boundary.inbound.UsernamePasswordAuthorization
import com.networkedassets.git4c.boundary.outbound.VerificationInfo
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.data.RepositoryWithSshKey
import com.networkedassets.git4c.data.RepositoryWithUsernameAndPassword
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class VerifyRepositoryUseCase (
        val importer : SourcePlugin
) : UseCase<VerifyRepositoryCommand, VerificationInfo> {
    override fun execute(request: VerifyRepositoryCommand): Result<VerificationInfo, Exception> {
        val repository = inboundToCoreRepository(request.repositoryToVerify)
        importer.verify(repository).apply {
            if (isOk()) {
                return@execute Result.of { VerificationInfo(status) }
            } else {
                return@execute Result.error(IllegalArgumentException(status.name))
            }
        }
        return Result.error(NotFoundException(request.transactionInfo, ""))
    }



    fun inboundToCoreRepository(repository : RepositoryToVerify): Repository{
        return when(repository.credentials){
            is UsernamePasswordAuthorization ->{
                RepositoryWithUsernameAndPassword(
                        "",
                        repository.sourceRepositoryUrl,
                        repository.credentials.username,
                        repository.credentials.password
                )
            }
            is SshKeyAuthorization ->{
                RepositoryWithSshKey(
                        "",
                        repository.sourceRepositoryUrl,
                        repository.credentials.sshKey
                )
            }
            is NoAuth ->{
                RepositoryWithNoAuthorization(
                        "",
                        repository.sourceRepositoryUrl
                )
            }
            else -> throw RuntimeException("Unknown auth type: ${repository.credentials.javaClass}")

        }
    }
}
