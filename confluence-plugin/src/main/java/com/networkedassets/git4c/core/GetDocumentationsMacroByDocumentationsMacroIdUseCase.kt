package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetDocumentationsMacroByDocumentationsMacroIdQuery
import com.networkedassets.git4c.boundary.outbound.DocumentationsMacro
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.datastore.repositories.GlobForMacroDatabase
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.exceptions.VerificationException
import com.networkedassets.git4c.core.process.RefreshMacroProcess
import com.networkedassets.git4c.delivery.executor.execution.UseCase


class GetDocumentationsMacroByDocumentationsMacroIdUseCase(
        val refreshMacroProcess: RefreshMacroProcess,
        val macroSettingsRepository: MacroSettingsDatabase,
        val globForMacroDatabase: GlobForMacroDatabase,
        val repositoryDatabase: RepositoryDatabase
) : UseCase<GetDocumentationsMacroByDocumentationsMacroIdQuery, DocumentationsMacro> {

    override fun execute(request: GetDocumentationsMacroByDocumentationsMacroIdQuery): Result<DocumentationsMacro, Exception> {
        val searchedMacroId = request.macroId
        val macroSettings = macroSettingsRepository.get(searchedMacroId) ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED.name))
        if (macroSettings.repositoryUuid == null) return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED.name))
        val repository = repositoryDatabase.get(macroSettings.repositoryUuid) ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED.name))
        val globs = globForMacroDatabase.getByMacro(macroSettings.uuid)
        try {
            val repositoryFetched = refreshMacroProcess.fetchDataFromSourceThenConvertAndCache(macroSettings, globs, repository)
            return@execute Result.of {
                DocumentationsMacro(repositoryFetched.uuid, macroSettings.repositoryUuid, repositoryFetched.currentBranch, repositoryFetched.revision)
            }
        } catch (e: VerificationException) {
            return@execute Result.error(NotFoundException(request.transactionInfo, e.verification.status.name))
        }
    }

}