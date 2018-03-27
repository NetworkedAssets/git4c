package com.networkedassets.git4c.core

import com.atlassian.confluence.core.service.NotAuthorizedException
import com.github.kittinunf.result.Result
import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.GetRepositoryInfoForMacroCommand
import com.networkedassets.git4c.boundary.outbound.RepositoryInfo
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.datastore.MacroSettingsProvider
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.process.ICheckUserPermissionProcess
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class GetRepositoryInfoForMacroUseCase(
        components: BussinesPluginComponents,
        val macroSettingsProvider: MacroSettingsProvider = components.providers.macroSettingsProvider,
        val repositoryDatabase: RepositoryDatabase = components.providers.repositoryProvider,
        val checkUserPermissionProcess: ICheckUserPermissionProcess = components.processing.checkUserPermissionProcess
) : UseCase<GetRepositoryInfoForMacroCommand, RepositoryInfo>
(components) {

    override fun execute(request: GetRepositoryInfoForMacroCommand): Result<RepositoryInfo, Exception> {

        val macroId = request.macroUuid
        val user = request.user

        if (checkUserPermissionProcess.userHasPermissionToMacro(macroId, user) == false) {
            return Result.error(NotAuthorizedException("User doesn't have permission to this space"))
        }

        val macro = macroSettingsProvider.get(macroId)
                ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val repositoryUuid = macro.repositoryUuid
                ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))

        val repository = repositoryDatabase.get(repositoryUuid)
                ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))

        return Result.of { RepositoryInfo(repository.repositoryPath) }

    }

}