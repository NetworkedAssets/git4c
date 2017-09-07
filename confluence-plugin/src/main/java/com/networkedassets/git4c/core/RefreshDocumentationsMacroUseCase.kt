package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.RefreshDocumentationsMacroCommand
import com.networkedassets.git4c.boundary.outbound.DocumentationsMacro
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.datastore.cache.DocumentsViewCache
import com.networkedassets.git4c.core.datastore.repositories.GlobForMacroDatabase
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.exceptions.VerificationException
import com.networkedassets.git4c.core.process.RefreshMacroProcess
import com.networkedassets.git4c.delivery.executor.execution.UseCase


class RefreshDocumentationsMacroUseCase(
        val refreshMacroProcess: RefreshMacroProcess,
        val macroSettingsRepository: MacroSettingsDatabase,
        val globForMacroDatabase: GlobForMacroDatabase,
        val repositoryDatabase: RepositoryDatabase,
        val cache: DocumentsViewCache
) : UseCase<RefreshDocumentationsMacroCommand, DocumentationsMacro> {

    override fun execute(request: RefreshDocumentationsMacroCommand): Result<DocumentationsMacro, Exception> {
        val searchedMacroId = request.macroId
        cache.remove(searchedMacroId)
        val macroSettings = macroSettingsRepository.get(searchedMacroId) ?: return@execute Result.error(NotFoundException(request.transactionInfo, ""))
        if (macroSettings.repositoryUuid == null) return@execute Result.error(NotFoundException(request.transactionInfo, ""))
        val repository = repositoryDatabase.get(macroSettings.repositoryUuid) ?: return@execute Result.error(NotFoundException(request.transactionInfo, ""))
        val globs = globForMacroDatabase.getByMacro(macroSettings.uuid)

        try {
            val repositoryFetched = refreshMacroProcess.fetchDataFromSourceThenConvertAndCache(macroSettings, globs, repository)
            return repositoryFetched.let { Result.of { DocumentationsMacro(it.uuid, macroSettings.repositoryUuid, it.currentBranch, it.revision) } }
        } catch (e: VerificationException) {
            return@execute Result.error(NotFoundException(request.transactionInfo, e.verification.status.name))
        }
    }
}