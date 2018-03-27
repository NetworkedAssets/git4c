package com.networkedassets.git4c.core

import com.atlassian.confluence.core.service.NotAuthorizedException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.kittinunf.result.Result
import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.PublishFileCommand
import com.networkedassets.git4c.boundary.PublishFileResultRequest
import com.networkedassets.git4c.boundary.outbound.RequestId
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.business.Commit
import com.networkedassets.git4c.core.business.User
import com.networkedassets.git4c.core.business.UserManager
import com.networkedassets.git4c.core.bussiness.ComputationCache
import com.networkedassets.git4c.core.bussiness.Database
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.datastore.cache.DocumentsViewCache
import com.networkedassets.git4c.core.datastore.cache.MacroToBeViewedPrepareLockCache
import com.networkedassets.git4c.core.datastore.repositories.*
import com.networkedassets.git4c.core.exceptions.VerificationException
import com.networkedassets.git4c.core.usecase.async.ComputationResultUseCase
import com.networkedassets.git4c.core.usecase.async.MultiThreadAsyncUseCase
import com.networkedassets.git4c.data.MacroLocation
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.data.TemporaryEditBranch
import com.networkedassets.git4c.utils.genTransactionId
import com.networkedassets.git4c.utils.getLogger
import com.networkedassets.git4c.utils.info
import org.apache.commons.lang3.RandomStringUtils

/**
 * It has to be a single thread due to possibility of duplication of files.
 * TODO: There have to be implemented locking mechanizm at upload per repository.
 */
class PublishFileUseCase(
        components: BussinesPluginComponents,
        val importer: SourcePlugin = components.macro.importer,
        val documentsViewCache: DocumentsViewCache = components.cache.documentsViewCache,
        val macroSettingsRepository: MacroSettingsDatabase = components.providers.macroSettingsProvider,
        val repositoryDatabase: Database<Repository> = components.providers.repositoryProvider,
        val userManager: UserManager = components.utils.userManager,
        val temporaryEditBranchesDatabase: TemporaryEditBranchesDatabase = components.database.temporaryEditBranchesDatabase,
        val macroLocationDatabase: MacroLocationDatabase = components.database.macroLocationDatabase,
        val macroViewCache: MacroToBeViewedPrepareLockCache = components.cache.macroViewCache,
        val computation: ComputationCache<Unit> = components.async.publishFileComputationCache
) : MultiThreadAsyncUseCase<PublishFileCommand, Unit>
(components, computation, 1) {

    val log = getLogger()

    override fun execute(request: PublishFileCommand): Result<RequestId, Exception> {
        request.user ?: return Result.error(NotAuthorizedException("Anonymous users cannot save files"))
        return super.execute(request)
    }

    /**
     * There are 3 cases when uploading file
     *
     * 1. Target branch is writable
     * 2. Target branch is not writable, but temporary one is
     * 3. No branches are writable (read-only credentials)
     *
     */
    override fun executedAsync(requestId: String, request: PublishFileCommand) {
        val macroId = request.macroId
        val macro = macroSettingsRepository.get(macroId)
        if (macro == null) {
            log.info { "Could not find macro with Macro=${macroId}" }
            return error(requestId, NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        }

        val repositoryId = macro.repositoryUuid
        val repository = repositoryDatabase.get(repositoryId!!)
        if (repository == null) {
            log.info { "Could not find repository with RepositoryId=${repositoryId}" }
            return error(requestId, NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        }

        if (!repository.isEditable) {
            log.info { "MacroId=$macroId is not editable" }
            return error(requestId, NotFoundException(request.transactionInfo, VerificationStatus.ACCESS_DENIED))
        }

        val location = macroLocationDatabase.get(macro.uuid)
        if (location == null) {
            log.info { "Could not find macro location Macro=${macroId}" }
            return error(requestId, NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        }


        val userObj = userManager.getUser(request.user!!)
        if (userObj == null) {
            log.info { "Could not find User ${request.user}" }
            return error(requestId, RuntimeException("Cannot find user: ${request.user}"))
        }


        saveFile(request, location, macro, userObj, repository, macroId, requestId, repositoryId)
    }

    private fun saveFile(request: PublishFileCommand, location: MacroLocation, macro: MacroSettings, userObj: User, repository: Repository, macroId: String, requestId: String, repositoryId: String) {
        val file = request.fileToSave.file
        val content = request.fileToSave.content
        val commitMessage = request.fileToSave.commitMessage
        val pageId = location.pageId
        val branch = macro.branch
        val commit = Commit(userObj.fullName, userObj.email, commitMessage)
        val gitTargetBranch = branch
        val gitSourceBranch = branch
        var newBranch = false

        if (!importer.getBranches(repository).contains(gitTargetBranch)) {
            importer.createNewBranch(repository, gitSourceBranch, gitTargetBranch)
            newBranch = true
        }
        importer.updateFile(repository, gitTargetBranch, file, content, commit)
        macroViewCache.remove(macroId)
        documentsViewCache.remove(macroId)
        pushFile(repository, gitTargetBranch, requestId, newBranch, repositoryId, pageId, macro, gitSourceBranch, file, content, commit)
    }

    private fun pushFile(repository: Repository, gitTargetBranch: String, requestId: String, newBranch: Boolean, repositoryId: String, pageId: String, macro: MacroSettings, gitSourceBranch: String, file: String, content: String, commit: Commit) {
        try {
            importer.pushLocalBranch(repository, gitTargetBranch)
            //Case 1
            success(requestId, Unit)
        } catch (e: VerificationException) {
            createNewBranchAndTryToPush(newBranch, repository, gitTargetBranch, repositoryId, pageId, macro, gitSourceBranch, file, content, commit, requestId)
        }
    }

    private fun createNewBranchAndTryToPush(newBranch: Boolean, repository: Repository, gitTargetBranch: String, repositoryId: String, pageId: String, macro: MacroSettings, gitSourceBranch: String, file: String, content: String, commit: Commit, requestId: String) {
        if (newBranch) {
            importer.removeBranch(repository, gitTargetBranch)
        } else {
            //So the change made by importer.updateFile would be removed
            importer.resetBranch(repository, gitTargetBranch)
        }

        val tempBranch = temporaryEditBranch(repositoryId, pageId, repository, macro)

        val tempBranchName = tempBranch.name
        importer.createNewBranch(repository, gitSourceBranch, tempBranchName)
        importer.updateFile(repository, tempBranchName, file, content, commit)

        pushToLocalBranch(repository, tempBranchName, requestId)
    }

    private fun temporaryEditBranch(repositoryId: String, pageId: String, repository: Repository, macro: MacroSettings): TemporaryEditBranch {
        val branchToEdit = temporaryEditBranchesDatabase.get(repositoryId, pageId)
        if (branchToEdit == null) {
            //Branch doesn't exist yet, let's create it
            return notExistingBranch(repositoryId, pageId)
        } else {
            return existingBranchOrNew(repository, branchToEdit, repositoryId, pageId, macro)
        }
    }

    private fun existingBranchOrNew(repository: Repository, branchToEdit: TemporaryEditBranch, repositoryId: String, pageId: String, macro: MacroSettings): TemporaryEditBranch {
        if (!importer.getBranches(repository).contains(branchToEdit.name)) {
            return notExistingBranch(repositoryId, pageId)
        } else if (importer.isBranchMerged(repository, branchToEdit.name, macro.branch)) {
            //Branch was merged, change its name
            return notExistingBranch(repositoryId, pageId)
        } else return branchToEdit
    }

    private fun notExistingBranch(repositoryId: String, pageId: String): TemporaryEditBranch {
        val branchName = "Git4C_temporary_${RandomStringUtils.randomAlphanumeric(8)}"
        val newBranchToEdit = TemporaryEditBranch(
                genTransactionId(),
                branchName
        )
        temporaryEditBranchesDatabase.put(repositoryId, pageId, newBranchToEdit)
        return newBranchToEdit
    }

    private fun pushToLocalBranch(repository: Repository, tempBranchName: String, requestId: String) {
        try {
            importer.pushLocalBranch(repository, tempBranchName)
            //Case 2
            val ex = RuntimeException(jacksonObjectMapper().writeValueAsString(AnotherBranch(tempBranchName)))
            error(requestId, ex)
        } catch (e: VerificationException) {
            //Case 3
            val ex = RuntimeException(jacksonObjectMapper().writeValueAsString(ReadOnlyRepo(tempBranchName, importer.getLocation(repository).absolutePath)))
            error(requestId, ex)
        }
    }
}

data class ReadOnlyRepo(
        val branch: String,
        val repoLocation: String
) {
    val type: String = "READ_ONLY_REPO"
}

data class AnotherBranch(
        val branch: String
) {
    val type: String = "ANOTHER_BRANCH"
}

class PublishFileResultUseCase(
        components: BussinesPluginComponents,
        computations: ComputationCache<Unit> = components.async.publishFileComputationCache
) : ComputationResultUseCase<PublishFileResultRequest, Unit>
(components, computations)