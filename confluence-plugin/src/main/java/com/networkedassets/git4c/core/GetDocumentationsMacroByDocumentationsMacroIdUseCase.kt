package com.networkedassets.git4c.core

import com.atlassian.plugins.whitelist.NotAuthorizedException
import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetDocumentationsMacroByDocumentationsMacroIdQuery
import com.networkedassets.git4c.boundary.outbound.DocumentationsMacro
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.datastore.repositories.ExtractorDataDatabase
import com.networkedassets.git4c.core.datastore.repositories.GlobForMacroDatabase
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.exceptions.VerificationException
import com.networkedassets.git4c.core.process.ICheckUserPermissionProcess
import com.networkedassets.git4c.core.process.RefreshMacroProcess
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class GetDocumentationsMacroByDocumentationsMacroIdUseCase(
        val refreshMacroProcess: RefreshMacroProcess,
        val macroSettingsRepository: MacroSettingsDatabase,
        val globForMacroDatabase: GlobForMacroDatabase,
        val repositoryDatabase: RepositoryDatabase,
        val extractorDataDatabase: ExtractorDataDatabase,
        val checkUserPermissionProcess: ICheckUserPermissionProcess
) : UseCase<GetDocumentationsMacroByDocumentationsMacroIdQuery, DocumentationsMacro> {

    override fun execute(request: GetDocumentationsMacroByDocumentationsMacroIdQuery): Result<DocumentationsMacro, Exception> {
        val searchedMacroId = request.macroId
        val user = request.user

        if (checkUserPermissionProcess.userHasPermissionToMacro(searchedMacroId, user) == false) {
            return Result.error(NotAuthorizedException("User doesn't have permission to this space"))
        }

        val macroSettings = macroSettingsRepository.get(searchedMacroId) ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        if (macroSettings.repositoryUuid == null) return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val repository = repositoryDatabase.get(macroSettings.repositoryUuid) ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val extractor = extractorDataDatabase.getNullable(macroSettings.extractorDataUuid) ?: return@execute Result.error(NotFoundException(request.transactionInfo, ""))
        val globs = globForMacroDatabase.getByMacro(macroSettings.uuid)
        try {
            val repositoryFetched = refreshMacroProcess.fetchDataFromSourceThenConvertAndCache(macroSettings, globs, repository, extractor)
            return@execute Result.of {
                DocumentationsMacro(repositoryFetched.uuid, macroSettings.repositoryUuid, repositoryFetched.currentBranch, repositoryFetched.revision)
            }
        } catch (e: VerificationException) {
            return@execute Result.error( IllegalArgumentException(e.verification.status.name))
        }
    }

}