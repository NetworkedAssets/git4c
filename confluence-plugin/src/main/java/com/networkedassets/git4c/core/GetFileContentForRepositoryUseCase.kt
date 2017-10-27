package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetFileContentForRepositoryQuery
import com.networkedassets.git4c.boundary.inbound.RepositoryToGetFile
import com.networkedassets.git4c.boundary.outbound.FileContent
import com.networkedassets.git4c.core.process.GetFileProcess
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.data.RepositoryWithSshKey
import com.networkedassets.git4c.data.RepositoryWithUsernameAndPassword
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class GetFileContentForRepositoryUseCase(
        val process: GetFileProcess
) : UseCase<GetFileContentForRepositoryQuery, FileContent> {

    override fun execute(request: GetFileContentForRepositoryQuery): Result<FileContent, Exception> {
        val repository = detectRepository(request.repositoryToGetFile)
        val branch = request.repositoryToGetFile.branch
        return Result.of {
                process.getFile(repository, branch, request.repositoryToGetFile.file)
            }
        }

    private fun detectRepository(repositoryFromCommand: RepositoryToGetFile): Repository {
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