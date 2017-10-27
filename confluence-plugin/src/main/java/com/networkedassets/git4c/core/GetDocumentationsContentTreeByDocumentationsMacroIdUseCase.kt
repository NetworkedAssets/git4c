package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetDocumentationsContentTreeByDocumentationsMacroIdQuery
import com.networkedassets.git4c.boundary.outbound.DocumentationsContentTree
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.bussiness.DocumentsTreeConverter
import com.networkedassets.git4c.core.datastore.cache.DocumentsViewCache
import com.networkedassets.git4c.delivery.executor.execution.UseCase


class GetDocumentationsContentTreeByDocumentationsMacroIdUseCase(
        val cache: DocumentsViewCache
) : UseCase<GetDocumentationsContentTreeByDocumentationsMacroIdQuery, DocumentationsContentTree> {

    override fun execute(request: GetDocumentationsContentTreeByDocumentationsMacroIdQuery): Result<DocumentationsContentTree, Exception> {

        val searchedMacroId = request.macroId

        return cache.get(searchedMacroId)
                ?.let { Result.of { DocumentsTreeConverter.treeify(it.files) } }
                ?: Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
    }
}