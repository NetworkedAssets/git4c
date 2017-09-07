package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetMethodByDocumentationsMacroIdQuery
import com.networkedassets.git4c.boundary.outbound.SimpleMethod
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class GetMethodByDocumentationsMacroIdUseCase(
        val macroSettingsDatabase: MacroSettingsDatabase
) : UseCase<GetMethodByDocumentationsMacroIdQuery, SimpleMethod> {

    override fun execute(request: GetMethodByDocumentationsMacroIdQuery): Result<SimpleMethod, Exception> {
        val macroId = request.macroId

        val macro = macroSettingsDatabase.get(macroId) ?: return Result.error(NotFoundException(request.transactionInfo, ""))

        return Result.of { SimpleMethod(macro.method) }
    }
}
