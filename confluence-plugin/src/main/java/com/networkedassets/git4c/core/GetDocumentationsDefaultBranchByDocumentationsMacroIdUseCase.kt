package com.networkedassets.git4c.core

import com.atlassian.confluence.core.service.NotAuthorizedException
import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetDocumentationsDefaultBranchByDocumentationsMacroIdQuery
import com.networkedassets.git4c.boundary.outbound.Branches
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.process.ICheckUserPermissionProcess
import com.networkedassets.git4c.delivery.executor.execution.UseCase


class GetDocumentationsDefaultBranchByDocumentationsMacroIdUseCase(
        val macroSettingsDatabase: MacroSettingsDatabase,
        val checkUserPermissionProcess: ICheckUserPermissionProcess
) : UseCase<GetDocumentationsDefaultBranchByDocumentationsMacroIdQuery, Branches> {
    override fun execute(request: GetDocumentationsDefaultBranchByDocumentationsMacroIdQuery): Result<Branches, Exception> {

        val macroId = request.macroId
        val user = request.user

        if (checkUserPermissionProcess.userHasPermissionToMacro(macroId, user) == false) {
            return Result.error(NotAuthorizedException("User doesn't have permission to this space"))
        }

        return macroSettingsDatabase.get(macroId)
                ?.let { Result.of { Branches(it.branch, listOf()) } }
                ?: Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
    }
}
