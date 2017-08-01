package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetDocumentationsMacroByDocumentationsMacroIdQuery
import com.networkedassets.git4c.boundary.outbound.DocumentationsMacro
import com.networkedassets.git4c.core.datastore.MacroSettingsRepository
import com.networkedassets.git4c.core.process.RefreshMacroProcess
import com.networkedassets.git4c.data.macro.DocumentationsMacroSettings
import com.networkedassets.git4c.delivery.executor.execution.UseCase
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException


class GetDocumentationsMacroByDocumentationsMacroIdUseCase(
        val refreshMacroProcess: RefreshMacroProcess,
        val macroSettingsRepository: MacroSettingsRepository
) : UseCase<GetDocumentationsMacroByDocumentationsMacroIdQuery, DocumentationsMacro> {

    override fun execute(request: GetDocumentationsMacroByDocumentationsMacroIdQuery): Result<DocumentationsMacro, Exception> {
        val searchedMacroId = request.macroId
        return macroSettingsRepository.get(searchedMacroId)
                ?.let { refreshOrGet(it) }
                ?.let { Result.of { DocumentationsMacro(it.uuid, it.currentBranch, it.revision) } }
                ?: Result.error(NotFoundException(request.transactionInfo, ""))
    }

    private fun refreshOrGet(documentationsMacroSettings: DocumentationsMacroSettings) = refreshMacroProcess.fetchDataFromSourceThenConvertAndSave(documentationsMacroSettings)
}