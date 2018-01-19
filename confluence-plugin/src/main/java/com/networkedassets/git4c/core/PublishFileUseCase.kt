package com.networkedassets.git4c.core

import com.atlassian.confluence.core.service.NotAuthorizedException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.PublishFileCommand
import com.networkedassets.git4c.boundary.outbound.RequestId
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.business.Commit
import com.networkedassets.git4c.core.business.Computation
import com.networkedassets.git4c.core.business.ExecutorHolder
import com.networkedassets.git4c.core.business.UserManager
import com.networkedassets.git4c.core.bussiness.Database
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.datastore.cache.DocumentsViewCache
import com.networkedassets.git4c.core.datastore.cache.PublishFileComputationCache
import com.networkedassets.git4c.core.datastore.repositories.*
import com.networkedassets.git4c.core.exceptions.VerificationException
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.data.TemporaryEditBranch
import com.networkedassets.git4c.delivery.executor.execution.UseCase
import com.networkedassets.git4c.utils.genTransactionId
import org.apache.commons.lang3.RandomStringUtils
import java.util.*
import java.util.concurrent.ScheduledExecutorService

class PublishFileUseCase(
        val importer: SourcePlugin,
        val cache: DocumentsViewCache,
        val macroSettingsRepository: MacroSettingsDatabase,
        val repositoryDatabase: Database<Repository>,
        val userManager: UserManager,
        val transactionCache: PublishFileComputationCache,
        val executorHolder: ExecutorHolder<ScheduledExecutorService>,
        val temporaryEditBranchesDatabase: TemporaryEditBranchesDatabase,
        val macroLocationDatabase: MacroLocationDatabase
) : UseCase<PublishFileCommand, RequestId> {

    val lock = java.lang.Object()

    override fun execute(request: PublishFileCommand): Result<RequestId, Exception> {

        val user = request.user
        val macroId = request.macroId
        val file = request.fileToSave.file
        val content = request.fileToSave.content
        val commitMessage = request.fileToSave.commitMessage
//        val destBranch = request.fileToSave.branch

        if (user == null) {
            return Result.error(NotAuthorizedException("Anonymous users cannot save files"))
        }

        val macro = macroSettingsRepository.get(macroId) ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val repositoryId = macro.repositoryUuid
        val repository = repositoryDatabase.get(repositoryId!!)!!
        val location = macroLocationDatabase.get(macro.uuid) ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))

        val pageId = location.pageId

        val branch = macro.branch

        val commit: Commit

        val userObj = userManager.getUser(user)

        if (userObj == null) {
            return Result.error(RuntimeException("Cannot find user: $user"))
        } else {
            commit = Commit(userObj.fullName, userObj.email, commitMessage)
        }

        val requestId = UUID.randomUUID().toString()

        transactionCache.put(requestId, Computation(requestId))

        executorHolder.getExecutor().submit {

            /**
             * 3 cases when uploading file
             * 1. Target branch is writable
             * 2. Target branch is not writable, but temporary one is
             * 3. No branches are writable (read-only credentials)
             */

//        val gitTargetBranch = destBranch ?: branch
            val gitTargetBranch = branch
            val gitSourceBranch = branch

            var newBranch = false

            if (!importer.getBranches(repository).contains(gitTargetBranch)) {
                importer.createNewBranch(repository, gitSourceBranch, gitTargetBranch)
                newBranch = true
            }

            importer.updateFile(repository, gitTargetBranch, file, content, commit)

            cache.remove(macroId)

            try {
                importer.pushLocalBranch(repository, gitTargetBranch)
                //Case 1
                transactionCache.put(requestId, Computation(requestId, Computation.ComputationState.FINISHED, data = Unit))
            } catch (e: VerificationException) {

                if (newBranch) {
                    importer.removeBranch(repository, gitTargetBranch)
                } else {
                    //So the change made by importer.updateFile would be removed
                    importer.resetBranch(repository, gitTargetBranch)
                }

                val tempBranch = synchronized(lock) {

                    val branchToEdit = temporaryEditBranchesDatabase.get(repositoryId, pageId)

                    if (branchToEdit == null) {

                        //Branch doesn't exist yet, let's create it

                        val branchName = "Git4C_temporary_${RandomStringUtils.randomAlphanumeric(8)}"

                        val newBranchToEdit = TemporaryEditBranch(
                                genTransactionId(),
                                branchName
                        )

                        temporaryEditBranchesDatabase.put(repositoryId, pageId, newBranchToEdit)

                        newBranchToEdit

                    } else {

                        if (!importer.getBranches(repository).contains(branchToEdit.name)) {

                            val branchName = "Git4C_temporary_${RandomStringUtils.randomAlphanumeric(8)}"

                            val newBranchToEdit = TemporaryEditBranch(
                                    genTransactionId(),
                                    branchName
                            )

                            temporaryEditBranchesDatabase.put(repositoryId, pageId, newBranchToEdit)

                            newBranchToEdit

                        } else if (importer.isBranchMerged(repository, branchToEdit.name, macro.branch)) {
                            //Branch was merged, change its name

                            val branchName = "Git4C_temporary_${RandomStringUtils.randomAlphanumeric(8)}"

                            val newBranchToEdit = TemporaryEditBranch(
                                    genTransactionId(),
                                    branchName
                            )

                            temporaryEditBranchesDatabase.put(repositoryId, pageId, newBranchToEdit)

                            newBranchToEdit

                        } else {
                            branchToEdit
                        }

                    }

                }

                val tempBranchName = tempBranch.name

                importer.createNewBranch(repository, gitSourceBranch, tempBranchName)
                importer.updateFile(repository, tempBranchName, file, content, commit)

                try {
                    importer.pushLocalBranch(repository, tempBranchName)
                    //Case 2
                    val ex = RuntimeException(jacksonObjectMapper().writeValueAsString(AnotherBranch(tempBranchName)))
                    transactionCache.put(requestId, Computation(requestId, Computation.ComputationState.FAILED, error = ex))
                } catch (e: VerificationException) {
                    //Case 3
                    val ex = RuntimeException(jacksonObjectMapper().writeValueAsString(ReadOnlyRepo(tempBranchName, importer.getLocation(repository).absolutePath)))
                    transactionCache.put(requestId, Computation(requestId, Computation.ComputationState.FAILED, error = ex))
                }
            }


        }

        return Result.of { RequestId(requestId) }

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

}