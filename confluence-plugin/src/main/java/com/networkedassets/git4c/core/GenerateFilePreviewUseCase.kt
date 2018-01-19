package com.networkedassets.git4c.core

import com.atlassian.confluence.core.service.NotAuthorizedException
import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.PreviewFileCommand
import com.networkedassets.git4c.boundary.outbound.FileContent
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.business.Commit
import com.networkedassets.git4c.core.business.UserManager
import com.networkedassets.git4c.core.bussiness.Database
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.process.GetFileProcess
import com.networkedassets.git4c.core.process.ICheckUserPermissionProcess
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.delivery.executor.execution.UseCase
import java.util.*

class GenerateFilePreviewUseCase(
        val importer: SourcePlugin,
        val macroSettingsRepository: MacroSettingsDatabase,
        val repositoryDatabase: Database<Repository>,
        val userManager: UserManager,
        val getFileProcess: GetFileProcess,
        val checkUserPermissionProcess: ICheckUserPermissionProcess
) : UseCase<PreviewFileCommand, FileContent> {

    override fun execute(request: PreviewFileCommand): Result<FileContent, Exception> {
        // TODO: Pull and non-blocking!
        val user = request.user
        val macroId = request.macroId
        val file = request.fileToGeneratePreview.file
        val content = request.fileToGeneratePreview.content

        if (checkUserPermissionProcess.userHasPermissionToMacro(macroId, user) == false) {
            return Result.error(NotAuthorizedException("User doesn't have permission to this space"))
        }

        val macro = macroSettingsRepository.get(macroId) ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val repositoryId = macro.repositoryUuid
        val repository = repositoryDatabase.get(repositoryId!!)!!
        val branch = macro.branch

        val branchName = "temppreview/${UUID.randomUUID()}"

        return Result.of {
            importer.createNewBranch(repository, branch, branchName)
            importer.updateFile(repository, branchName, file, content, Commit("a", "a", "a"))
            val fileContent = getFileProcess.getFile(repository, branchName, file)
            importer.removeBranch(repository, branchName)
            return@of fileContent
        }

    }

}