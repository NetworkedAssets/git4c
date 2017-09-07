package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetMethodsForRepositoryQuery
import com.networkedassets.git4c.boundary.inbound.RepositoryToGetMethods
import com.networkedassets.git4c.boundary.outbound.Method
import com.networkedassets.git4c.boundary.outbound.Methods
import com.networkedassets.git4c.core.bussiness.ConverterPlugin
import com.networkedassets.git4c.core.bussiness.ParserPlugin
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.process.GetMethodsProcess
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.data.RepositoryWithSshKey
import com.networkedassets.git4c.data.RepositoryWithUsernameAndPassword
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class GetMethodsForRepositoryUseCase(
        val process: GetMethodsProcess
) : UseCase<GetMethodsForRepositoryQuery, Methods> {

    override fun execute(request: GetMethodsForRepositoryQuery): Result<Methods, Exception> {
        val repository = detectRepository(request.repositoryToGetMethods)
        val branch = request.repositoryToGetMethods.branch
        val file = request.repositoryToGetMethods.file
        return Result.of {
            process.getMethods(repository, branch, file)
        }
    }

    private fun detectRepository(repositoryFromCommand: RepositoryToGetMethods): Repository {
        when (repositoryFromCommand.credentials) {
            is inUserNamePassword -> return RepositoryWithUsernameAndPassword(
                    "",
                    repositoryFromCommand.sourceRepositoryUrl,
                    repositoryFromCommand.credentials.username,
                    repositoryFromCommand.credentials.password)
            is inSshKey -> return RepositoryWithSshKey(
                    "",
                    repositoryFromCommand.sourceRepositoryUrl,
                    repositoryFromCommand.credentials.sshKey)
            is inNoAuth -> return RepositoryWithNoAuthorization(
                    "",
                    repositoryFromCommand.sourceRepositoryUrl
            )
            else -> throw RuntimeException("Unknown auth type: ${repositoryFromCommand.credentials.javaClass}")

        }
    }
}