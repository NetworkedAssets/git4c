package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetAllDocumentsByDocumentationsMacroIdQuery
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
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
        val globForMacroDatabase: GlobForMacroDatabase
) : UseCase<GetAllDocumentsByDocumentationsMacroIdQuery, List<DocumentsItem>> {

    override fun execute(request: GetAllDocumentsByDocumentationsMacroIdQuery): Result<List<DocumentsItem>, Exception> {
        val macroSettings = macroSettingsDatabase.get(request.macroId) ?: return@execute Result.error(NotFoundException(request.transactionInfo, ""))
        if (macroSettings.repositoryUuid == null) return@execute Result.error(NotFoundException(request.transactionInfo, ""))
        val repository = repositoryDatabase.get(macroSettings.repositoryUuid) ?: return@execute Result.error(NotFoundException(request.transactionInfo, ""))
        val globs = globForMacroDatabase.getByMacro(macroSettings.uuid)
        val data = refreshMacroProcess.fetchDataFromSourceThenConvertAndCache(macroSettings, globs, repository)
        return Result.of { data.files }
    }
}
