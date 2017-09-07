package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetFilesForRepositoryQuery
import com.networkedassets.git4c.boundary.inbound.RepositoryToGetFiles
import com.networkedassets.git4c.boundary.outbound.Files
import com.networkedassets.git4c.core.bussiness.DocumentsTreeConverter
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.process.GetFilesProcess
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.data.RepositoryWithSshKey
import com.networkedassets.git4c.data.RepositoryWithUsernameAndPassword
import com.networkedassets.git4c.data.macro.documents.item.DocumentsItem
import com.networkedassets.git4c.data.macro.documents.item.TableOfContents
import com.networkedassets.git4c.delivery.executor.execution.UseCase
import java.util.*

class GetFilesForRepositoryUseCase(
        val getFilesProcess: GetFilesProcess
) : UseCase<GetFilesForRepositoryQuery, Files> {

    override fun execute(request: GetFilesForRepositoryQuery): Result<Files, Exception> {
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