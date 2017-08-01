package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetAllDocumentsByDocumentationsMacroIdQuery
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.datastore.MacroSettingsRepository
import com.networkedassets.git4c.core.process.RefreshMacroProcess
import com.networkedassets.git4c.data.macro.documents.item.DocumentsItem
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class GetAllDocumentsByDocumentationsMacroIdUseCase(
        val refreshMacroProcess: RefreshMacroProcess,
        val macroSettingsRepository: MacroSettingsRepository
): UseCase<GetAllDocumentsByDocumentationsMacroIdQuery, List<DocumentsItem>> {

    override fun execute(request: GetAllDocumentsByDocumentationsMacroIdQuery) =
            macroSettingsRepository.get(request.macroId)
                    ?.let { refreshMacroProcess.fetchDataFromSourceThenConvertAndSave(it) }
                    ?.let { Result.of { it.files } }
                    ?: Result.error(NotFoundException(request.transactionInfo, ""))

}