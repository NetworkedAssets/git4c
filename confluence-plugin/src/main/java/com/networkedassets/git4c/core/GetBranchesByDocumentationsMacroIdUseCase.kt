package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetBranchesByDocumentationsMacroIdQuery
import com.networkedassets.git4c.boundary.outbound.Branches
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.datastore.MacroSettingsRepository
import com.networkedassets.git4c.core.process.GetBranchesProcess
import com.networkedassets.git4c.data.macro.DocumentationsMacroSettings
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class GetBranchesByDocumentationsMacroIdUseCase(
        val getBranchesProcess: GetBranchesProcess,
        val macroSettingsRepository: MacroSettingsRepository
) : UseCase<GetBranchesByDocumentationsMacroIdQuery, Branches> {

    override fun execute(request: GetBranchesByDocumentationsMacroIdQuery): Result<Branches, Exception> {
        val soughtMacroId = request.macroId
        return macroSettingsRepository.get(soughtMacroId)
                ?.let { Result.of { getBranches(it) } }
                ?: Result.error(NotFoundException(request.transactionInfo, ""))
    }

    private fun getBranches(documentationsMacroSettings: DocumentationsMacroSettings) = getBranchesProcess.fetchBranchList(documentationsMacroSettings)
}