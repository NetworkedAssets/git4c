package com.networkedassets.git4c.core

import com.atlassian.confluence.core.service.NotAuthorizedException
import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetDocumentItemInDocumentationsMacroQuery
import com.networkedassets.git4c.boundary.outbound.DocItem
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.datastore.cache.DocumentsViewCache
import com.networkedassets.git4c.core.process.ICheckUserPermissionProcess
import com.networkedassets.git4c.delivery.executor.execution.UseCase


class GetDocumentItemInDocumentationsMacroUseCase(
        val cache: DocumentsViewCache,
        val checkUserPermissionProcess: ICheckUserPermissionProcess
) : UseCase<GetDocumentItemInDocumentationsMacroQuery, DocItem> {

    override fun execute(request: GetDocumentItemInDocumentationsMacroQuery): Result<DocItem, Exception> {

        val searchedMacroId = request.macroId
        val user = request.user
        val searchedDocumentId = request.documentId

        if (checkUserPermissionProcess.userHasPermissionToMacro(searchedMacroId, user) == false) {
            return Result.error(NotAuthorizedException("User doesn't have permission to this space"))
        }

        return cache.get(searchedMacroId)
                ?.let { it.files.filter { it.index == searchedDocumentId }.firstOrNull() }
                ?.let { Result.of { DocItem(it) } }
                ?: Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
    }
}