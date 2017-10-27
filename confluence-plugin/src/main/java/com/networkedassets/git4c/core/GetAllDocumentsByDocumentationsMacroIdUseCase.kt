package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetAllDocumentsByDocumentationsMacroIdQuery
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.datastore.repositories.ExtractorDataDatabase
import com.networkedassets.git4c.core.datastore.repositories.GlobForMacroDatabase
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.process.RefreshMacroProcess
import com.networkedassets.git4c.data.macro.documents.item.DocumentsItem
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class GetAllDocumentsByDocumentationsMacroIdUseCase(
        val refreshMacroProcess: RefreshMacroProcess,
        val macroSettingsDatabase: MacroSettingsDatabase,
        val repositoryDatabase: RepositoryDatabase,
        val globForMacroDatabase: GlobForMacroDatabase,
        val extractorDataDatabase: ExtractorDataDatabase
) : UseCase<GetAllDocumentsByDocumentationsMacroIdQuery, List<DocumentsItem>> {

    override fun execute(request: GetAllDocumentsByDocumentationsMacroIdQuery): Result<List<DocumentsItem>, Exception> {
        val macroSettings = macroSettingsDatabase.get(request.macroId) ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        if (macroSettings.repositoryUuid == null) return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val repository = repositoryDatabase.get(macroSettings.repositoryUuid) ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val globs = globForMacroDatabase.getByMacro(macroSettings.uuid)
        val extractor = extractorDataDatabase.getNullable(macroSettings.extractorDataUuid)
        val data = refreshMacroProcess.fetchDataFromSourceThenConvertAndCache(macroSettings, globs, repository, extractor)
        return Result.of { data.files }
    }
}
