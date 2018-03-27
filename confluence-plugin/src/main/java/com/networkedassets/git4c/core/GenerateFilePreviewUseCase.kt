package com.networkedassets.git4c.core

import com.atlassian.confluence.core.service.NotAuthorizedException
import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.PreviewFileCommand
import com.networkedassets.git4c.boundary.PreviewFileResultRequest
import com.networkedassets.git4c.boundary.outbound.FileContent
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.business.Commit
import com.networkedassets.git4c.core.business.UserManager
import com.networkedassets.git4c.core.bussiness.ComputationCache
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.process.GetFileProcess
import com.networkedassets.git4c.core.process.ICheckUserPermissionProcess
import com.networkedassets.git4c.core.usecase.async.ComputationResultUseCase
import com.networkedassets.git4c.core.usecase.async.MultiThreadAsyncUseCase
import java.util.*

class GenerateFilePreviewUseCase(
        components: BussinesPluginComponents,
        val importer: SourcePlugin = components.macro.importer,
        val macroSettingsRepository: MacroSettingsDatabase = components.providers.macroSettingsProvider,
        val repositoryDatabase: RepositoryDatabase = components.providers.repositoryProvider,
        val userManager: UserManager = components.utils.userManager,
        val getFileProcess: GetFileProcess = components.processing.getFileProcess,
        val checkUserPermissionProcess: ICheckUserPermissionProcess = components.processing.checkUserPermissionProcess,
        computations: ComputationCache<FileContent> = components.async.generateFilePreviewUseCaseCache
) : MultiThreadAsyncUseCase<PreviewFileCommand, FileContent>
(components, computations, 1) {

    override fun executedAsync(requestId: String, request: PreviewFileCommand) {
        val user = request.user
        val macroId = request.macroId
        val file = request.fileToGeneratePreview.file
        val content = request.fileToGeneratePreview.content

        if (checkUserPermissionProcess.userHasPermissionToMacro(macroId, user) == false) {
            return error(requestId, NotAuthorizedException("User doesn't have permission to this space"))
        }

        val macro = macroSettingsRepository.get(macroId)
                ?: return error(requestId, NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val repositoryId = macro.repositoryUuid
        val repository = repositoryDatabase.get(repositoryId!!)!!
        val branch = macro.branch

        val branchName = "temppreview/${UUID.randomUUID()}"

        try {
            importer.createNewBranch(repository, branch, branchName)
            importer.updateFile(repository, branchName, file, content, Commit("a", "a", "a"))
            val fileContent = getFileProcess.getFile(repository, branchName, file)
            importer.removeBranch(repository, branchName)
            success(requestId, fileContent)
        } catch (e: Exception) {
            error(requestId, e)
        }
    }

}

class GenerateFilePreviewResultUseCase(
        components: BussinesPluginComponents,
        computations: ComputationCache<FileContent> = components.async.generateFilePreviewUseCaseCache
) : ComputationResultUseCase<PreviewFileResultRequest, FileContent>(components, computations)