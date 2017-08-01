package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.RefreshDocumentationsMacroCommand
import com.networkedassets.git4c.boundary.outbound.DocumentationsMacro
import com.networkedassets.git4c.core.datastore.DocumentsViewCache
import com.networkedassets.git4c.core.datastore.MacroSettingsRepository
import com.networkedassets.git4c.core.process.RefreshMacroProcess
import com.networkedassets.git4c.data.macro.DocumentationsMacroSettings
import com.networkedassets.git4c.delivery.executor.execution.UseCase
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException


class RefreshDocumentationsMacroUseCase(
        val refreshMacroProcess: RefreshMacroProcess,
        val macroSettingsRepository: MacroSettingsRepository,
        val cache: DocumentsViewCache
) : UseCase<RefreshDocumentationsMacroCommand, DocumentationsMacro> {

    override fun execute(request: RefreshDocumentationsMacroCommand): Result<DocumentationsMacro, Exception> {
        val searchedMacroId = request.macroId
        cache.remove(searchedMacroId)
        
        return macroSettingsRepository.get(searchedMacroId)
                ?.let { refreshOrGet(it) }
                ?.let { Result.of { DocumentationsMacro(it.uuid, it.currentBranch, it.revision) } }
                ?: Result.error(NotFoundException(request.transactionInfo, ""))
    }

    private fun refreshOrGet(documentationsMacroSettings: DocumentationsMacroSettings) = refreshMacroProcess.fetchDataFromSourceThenConvertAndSave(documentationsMacroSettings)
}