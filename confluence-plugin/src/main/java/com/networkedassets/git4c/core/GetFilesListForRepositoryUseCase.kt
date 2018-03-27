package com.networkedassets.git4c.core

import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.GetFilesListForRepositoryQuery
import com.networkedassets.git4c.boundary.GetFilesListForRepositoryResultRequest
import com.networkedassets.git4c.boundary.inbound.RepositoryToGetFiles
import com.networkedassets.git4c.boundary.outbound.FilesList
import com.networkedassets.git4c.core.bussiness.ComputationCache
import com.networkedassets.git4c.core.process.GetFilesProcess
import com.networkedassets.git4c.core.usecase.async.ComputationResultUseCase
import com.networkedassets.git4c.core.usecase.async.MultiThreadAsyncUseCase
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.data.RepositoryWithSshKey
import com.networkedassets.git4c.data.RepositoryWithUsernameAndPassword

class GetFilesListForRepositoryUseCase(
        components: BussinesPluginComponents,
        val getFilesProcess: GetFilesProcess = components.processing.getFilesProcess,
        computations: ComputationCache<FilesList> = components.async.getFilesListForRepositoryUseCaseCache
) : MultiThreadAsyncUseCase<GetFilesListForRepositoryQuery, FilesList>
(components, computations, 2) {

    override fun executedAsync(requestId: String, request: GetFilesListForRepositoryQuery) {
        val repository = detectRepository(request.repositoryToGetFiles)
        val branch = request.repositoryToGetFiles.branch

        try {
            val files = getFilesProcess.getFiles(repository, branch)
            success(requestId, files)
        } catch (e: Exception) {
            error(requestId, e)
        }

    }

    private fun detectRepository(repositoryFromCommand: RepositoryToGetFiles): Repository {
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

class GetFilesListForRepositoryResultUseCase(
        components: BussinesPluginComponents,
        computations: ComputationCache<FilesList> = components.async.getFilesListForRepositoryUseCaseCache
) : ComputationResultUseCase<GetFilesListForRepositoryResultRequest, FilesList>(components, computations)
