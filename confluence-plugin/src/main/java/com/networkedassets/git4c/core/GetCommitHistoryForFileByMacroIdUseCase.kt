package com.networkedassets.git4c.core

import com.atlassian.confluence.core.service.NotAuthorizedException
import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetCommitHistoryForFileByMacroIdQuery
import com.networkedassets.git4c.boundary.outbound.BasicCommitInfo
import com.networkedassets.git4c.boundary.outbound.Commits
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.process.ICheckUserPermissionProcess
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class GetCommitHistoryForFileByMacroIdUseCase(
        val importer : SourcePlugin,
        val repositoryDatabase: RepositoryDatabase,
        val macroSettingsDatabase: MacroSettingsDatabase,
        val checkUserPermissionProcess: ICheckUserPermissionProcess
): UseCase<GetCommitHistoryForFileByMacroIdQuery, Commits> {
    override fun execute(request: GetCommitHistoryForFileByMacroIdQuery): Result<Commits, Exception> {

        val macroId = request.macroId
        val user = request.user

        if (checkUserPermissionProcess.userHasPermissionToMacro(macroId, user) == false) {
            return Result.error(NotAuthorizedException("User doesn't have permission to this space"))
        }

        val repositoryUuid = macroSettingsDatabase.get(request.macroId)?.repositoryUuid?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val repository = repositoryDatabase.get(repositoryUuid)?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val commits = importer.getCommitsForFile(repository, request.details.branch, request.details.file)
                .map{ it -> BasicCommitInfo(it.id, it.authorName, it.message, it.timeInMs) }
        return Result.of { Commits(commits) }
    }
}