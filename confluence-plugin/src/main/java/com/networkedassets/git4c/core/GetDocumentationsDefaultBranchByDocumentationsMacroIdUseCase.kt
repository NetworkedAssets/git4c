package com.networkedassets.git4c.core

import com.atlassian.confluence.core.service.NotAuthorizedException
import com.github.kittinunf.result.Result
import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.GetDocumentationsDefaultBranchByDocumentationsMacroIdQuery
import com.networkedassets.git4c.boundary.outbound.Branches
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.datastore.repositories.*
import com.networkedassets.git4c.core.process.ICheckUserPermissionProcess
import com.networkedassets.git4c.delivery.executor.execution.UseCase


class GetDocumentationsDefaultBranchByDocumentationsMacroIdUseCase(
        components: BussinesPluginComponents,
        val importer: SourcePlugin = components.macro.importer,
        val repositoryDatabase: RepositoryDatabase = components.providers.repositoryProvider,
        val macroSettingsDatabase: MacroSettingsDatabase = components.providers.macroSettingsProvider,
        val checkUserPermissionProcess: ICheckUserPermissionProcess = components.processing.checkUserPermissionProcess,
        val macroLocationDatabase: MacroLocationDatabase = components.database.macroLocationDatabase,
        val temporaryEditBranchesDatabase: TemporaryEditBranchesDatabase = components.database.temporaryEditBranchesDatabase
) : UseCase<GetDocumentationsDefaultBranchByDocumentationsMacroIdQuery, Branches>
(components) {
    override fun execute(request: GetDocumentationsDefaultBranchByDocumentationsMacroIdQuery): Result<Branches, Exception> {

        val macroId = request.macroId
        val user = request.user

        if (checkUserPermissionProcess.userHasPermissionToMacro(macroId, user) == false) {
            return Result.error(NotAuthorizedException("User doesn't have permission to this space"))
        }

        val macro = macroSettingsDatabase.get(macroId)
                ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))

        val macroLocation = macroLocationDatabase.get(macroId)
                ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val repositoryId = macro.repositoryUuid
                ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val pageId = macroLocation.pageId

        val defaultBranch = temporaryEditBranchesDatabase.get(repositoryId, pageId)?.name
                ?: macroSettingsDatabase.get(macroId)?.branch
                ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))

        return Result.of { Branches(defaultBranch, listOf()) }

    }
}
