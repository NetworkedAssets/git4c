package com.networkedassets.git4c.core

import com.atlassian.confluence.core.service.NotAuthorizedException
import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetExtractionDataByDocumentationsMacroIdQuery
import com.networkedassets.git4c.boundary.outbound.LineRange
import com.networkedassets.git4c.boundary.outbound.SimpleExtractorData
import com.networkedassets.git4c.boundary.outbound.SimpleMethod
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.datastore.extractors.ExtractorData
import com.networkedassets.git4c.core.datastore.extractors.LineNumbersExtractorData
import com.networkedassets.git4c.core.datastore.extractors.MethodExtractorData
import com.networkedassets.git4c.core.datastore.repositories.ExtractorDataDatabase
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.process.ICheckUserPermissionProcess
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class GetExtractionDataByDocumentationsMacroIdUseCase(
        val macroSettingsDatabase: MacroSettingsDatabase,
        val extractorDatabase: ExtractorDataDatabase,
        val checkUserPermissionProcess: ICheckUserPermissionProcess
) : UseCase<GetExtractionDataByDocumentationsMacroIdQuery, SimpleExtractorData> {

    override fun execute(request: GetExtractionDataByDocumentationsMacroIdQuery): Result<SimpleExtractorData, Exception> {
        val macroId = request.macroId
        val user = request.user

        if (checkUserPermissionProcess.userHasPermissionToMacro(macroId, user) == false) {
            return Result.error(NotAuthorizedException("User doesn't have permission to this space"))
        }

        val macro = macroSettingsDatabase.get(macroId) ?: return Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val extractorData = extractorDatabase.getNullable(macro.extractorDataUuid) ?: return Result.error(NotFoundException(request.transactionInfo, ""))

        return Result.of { convertExtractorDataToOutbound(extractorData) }

    }

    private fun convertExtractorDataToOutbound(data: ExtractorData): SimpleExtractorData{
        return when(data){
            is MethodExtractorData -> {
                SimpleMethod(data.method)
            }
            is LineNumbersExtractorData -> {
                LineRange(data.startLine, data.endLine)
            }
            else -> throw RuntimeException("Unknown extraction data type: ${data.javaClass}")
        }
    }


}
