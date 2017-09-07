package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetDocumentationsDefaultBranchByDocumentationsMacroIdQuery
import com.networkedassets.git4c.boundary.outbound.Branches
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.delivery.executor.execution.UseCase


class GetDocumentationsDefaultBranchByDocumentationsMacroIdUseCase(
        val macroSettingsDatabase: MacroSettingsDatabase
) : UseCase<GetDocumentationsDefaultBranchByDocumentationsMacroIdQuery, Branches> {
    override fun execute(request: GetDocumentationsDefaultBranchByDocumentationsMacroIdQuery): Result<Branches, Exception> {
        return macroSettingsDatabase.get(request.macroId)
                ?.let { Result.of { Branches(it.branch, listOf()) } }
                ?: Result.error(NotFoundException(request.transactionInfo, ""))
    }
}
