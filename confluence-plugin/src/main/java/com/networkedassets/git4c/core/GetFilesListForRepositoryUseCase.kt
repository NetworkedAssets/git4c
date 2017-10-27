package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetFilesListForRepositoryQuery
import com.networkedassets.git4c.boundary.inbound.RepositoryToGetFiles
import com.networkedassets.git4c.boundary.outbound.FilesList
import com.networkedassets.git4c.core.process.GetFilesProcess
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.data.RepositoryWithSshKey
import com.networkedassets.git4c.data.RepositoryWithUsernameAndPassword
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class GetFilesListForRepositoryUseCase(
        val getFilesProcess: GetFilesProcess
) : UseCase<GetFilesListForRepositoryQuery, FilesList> {

    override fun execute(request: GetFilesListForRepositoryQuery): Result<FilesList, Exception> {
        val repository = detectRepository(request.repositoryToGetFiles)
        val branch = request.repositoryToGetFiles.branch
        return Result.of {
            getFilesProcess.getFiles(repository, branch)
        }
    }

    private fun detectRepository(repositoryFromCommand: RepositoryToGetFiles): Repository {
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