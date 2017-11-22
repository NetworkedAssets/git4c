package com.networkedassets.git4c.core

import com.atlassian.confluence.core.service.NotAuthorizedException
import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.RefreshDocumentationsMacroCommand
import com.networkedassets.git4c.boundary.outbound.DocumentationsMacro
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.datastore.cache.DocumentsViewCache
import com.networkedassets.git4c.core.datastore.repositories.ExtractorDataDatabase
import com.networkedassets.git4c.core.datastore.repositories.GlobForMacroDatabase
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.exceptions.VerificationException
import com.networkedassets.git4c.core.process.ICheckUserPermissionProcess
import com.networkedassets.git4c.core.process.RefreshMacroProcess
import com.networkedassets.git4c.delivery.executor.execution.UseCase


class RefreshDocumentationsMacroUseCase(
        val refreshMacroProcess: RefreshMacroProcess,
        val macroSettingsRepository: MacroSettingsDatabase,
        val globForMacroDatabase: GlobForMacroDatabase,
        val repositoryDatabase: RepositoryDatabase,
        val extractorDataDatabase: ExtractorDataDatabase,
        val cache: DocumentsViewCache,
        val checkUserPermissionProcess: ICheckUserPermissionProcess
) : UseCase<RefreshDocumentationsMacroCommand, DocumentationsMacro> {

    override fun execute(request: RefreshDocumentationsMacroCommand): Result<DocumentationsMacro, Exception> {

        val macroId = request.macroId
        val user = request.user

        if (checkUserPermissionProcess.userHasPermissionToMacro(macroId, user) == false) {
            return Result.error(NotAuthorizedException("User doesn't have permission to this space"))
        }

        val searchedMacroId = request.macroId
        cache.remove(searchedMacroId)
        val macroSettings = macroSettingsRepository.get(searchedMacroId) ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        if (macroSettings.repositoryUuid == null) return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val repository = repositoryDatabase.get(macroSettings.repositoryUuid) ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val globs = globForMacroDatabase.getByMacro(macroSettings.uuid)
        val extractor = extractorDataDatabase.getNullable(macroSettings.extractorDataUuid) ?: return@execute Result.error(NotFoundException(request.transactionInfo, ""))

        try {
            val repositoryFetched = refreshMacroProcess.fetchDataFromSourceThenConvertAndCache(macroSettings, globs, repository, extractor)
            return repositoryFetched.let { Result.of { DocumentationsMacro(it.uuid, macroSettings.repositoryUuid, it.currentBranch, it.revision) } }
        } catch (e: VerificationException) {
            return@execute Result.error(NotFoundException(request.transactionInfo, e.verification.status.name))
        }
    }
}