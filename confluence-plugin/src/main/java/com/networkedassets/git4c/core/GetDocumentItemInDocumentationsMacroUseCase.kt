package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetDocumentItemInDocumentationsMacroQuery
import com.networkedassets.git4c.boundary.outbound.DocItem
import com.networkedassets.git4c.core.datastore.DocumentsViewCache
import com.networkedassets.git4c.delivery.executor.execution.UseCase
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException


class GetDocumentItemInDocumentationsMacroUseCase(
        val cache: DocumentsViewCache
) : UseCase<GetDocumentItemInDocumentationsMacroQuery, DocItem> {

    override fun execute(request: GetDocumentItemInDocumentationsMacroQuery): Result<DocItem, Exception> {

        val searchedMacroId = request.macroId
        val searchedDocumentId = request.documentId

        return cache.get(searchedMacroId)
                ?.let { it.files.filter { it.index == searchedDocumentId }.firstOrNull() }
                ?.let { Result.of { DocItem(it) } }
                ?: Result.error(NotFoundException(request.transactionInfo, ""))
    }
}