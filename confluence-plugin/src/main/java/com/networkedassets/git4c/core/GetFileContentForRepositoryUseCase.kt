package com.networkedassets.git4c.core

import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.GetFileContentForRepositoryQuery
import com.networkedassets.git4c.boundary.GetFileContentForRepositoryResultRequest
import com.networkedassets.git4c.boundary.inbound.RepositoryToGetFile
import com.networkedassets.git4c.boundary.outbound.FileContent
import com.networkedassets.git4c.core.bussiness.ComputationCache
import com.networkedassets.git4c.core.process.GetFileProcess
import com.networkedassets.git4c.core.usecase.async.ComputationResultUseCase
import com.networkedassets.git4c.core.usecase.async.MultiThreadAsyncUseCase
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.data.RepositoryWithSshKey
import com.networkedassets.git4c.data.RepositoryWithUsernameAndPassword

class GetFileContentForRepositoryUseCase(
        components: BussinesPluginComponents,
        val process: GetFileProcess = components.processing.getFileProcess,
        computations: ComputationCache<FileContent> = components.async.getFileContentForRepositoryUseCaseCache
) : MultiThreadAsyncUseCase<GetFileContentForRepositoryQuery, FileContent>
(components, computations, 2) {

    override fun executedAsync(requestId: String, request: GetFileContentForRepositoryQuery) {
        val repository = detectRepository(request.repositoryToGetFile)
        val branch = request.repositoryToGetFile.branch
        try {
            val file = process.getFile(repository, branch, request.repositoryToGetFile.file)
            success(requestId, file)
        } catch (e: Exception) {
            error(requestId, e)
        }
    }

    private fun detectRepository(repositoryFromCommand: RepositoryToGetFile): Repository {
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

class GetFileContentForRepositoryResultUseCase(
        components: BussinesPluginComponents,
        computations: ComputationCache<FileContent> = components.async.getFileContentForRepositoryUseCaseCache
) : ComputationResultUseCase<GetFileContentForRepositoryResultRequest, FileContent>(components, computations)
