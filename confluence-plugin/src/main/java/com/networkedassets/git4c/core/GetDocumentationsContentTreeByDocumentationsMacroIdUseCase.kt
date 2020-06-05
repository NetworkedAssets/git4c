package com.networkedassets.git4c.core

import com.atlassian.confluence.core.service.NotAuthorizedException
import com.github.kittinunf.result.Result
import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.GetDocumentationsContentTreeByDocumentationsMacroIdQuery
import com.networkedassets.git4c.boundary.outbound.DocumentationsContentTree
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.boundary.outbound.exceptions.NotReadyException
import com.networkedassets.git4c.core.bussiness.DocumentsTreeConverter
import com.networkedassets.git4c.core.datastore.cache.DocumentsViewCache
import com.networkedassets.git4c.core.datastore.cache.MacroToBeViewedPrepareLockCache
import com.networkedassets.git4c.core.process.ICheckUserPermissionProcess
import com.networkedassets.git4c.core.process.MacroViewProcess
import com.networkedassets.git4c.data.MacroView
import com.networkedassets.git4c.delivery.executor.execution.UseCase
import com.networkedassets.git4c.utils.debug
import com.networkedassets.git4c.utils.getLogger


class GetDocumentationsContentTreeByDocumentationsMacroIdUseCase(
        components: BussinesPluginComponents,
        val documentsViewCache: DocumentsViewCache = components.cache.documentsViewCache,
        val macroViewCache: MacroToBeViewedPrepareLockCache = components.cache.macroViewCache,
        val macroViewProcess: MacroViewProcess = components.processing.macroViewProcess,
        val checkUserPermissionProcess: ICheckUserPermissionProcess = components.processing.checkUserPermissionProcess
) : UseCase<GetDocumentationsContentTreeByDocumentationsMacroIdQuery, DocumentationsContentTree>
(components) {

    val log = getLogger()

    override fun execute(request: GetDocumentationsContentTreeByDocumentationsMacroIdQuery): Result<DocumentationsContentTree, Exception> {

        val searchedMacroId = request.macroId
        val user = request.user

        if (checkUserPermissionProcess.userHasPermissionToMacro(searchedMacroId, user) == false) {
            return Result.error(NotAuthorizedException("User doesn't have permission to this space"))
        }

        val macroViewStatus = macroViewCache.get(searchedMacroId)

        if (macroViewStatus == null) {
            macroViewProcess.prepareMacroToBeViewed(searchedMacroId)
            log.debug { "Macro=${request.macroId} has no view status present" }
            return@execute Result.error(NotReadyException())
        }

        if (macroViewStatus.macroViewStatus != MacroView.MacroViewStatus.READY) {
            log.debug { "Macro=${request.macroId} has no view status as ready!" }
            return@execute Result.error(NotReadyException())
        }

        return documentsViewCache.get(searchedMacroId)
                ?.let { it.files }
                ?.let { Result.of<DocumentationsContentTree, NotFoundException> { DocumentsTreeConverter.treeify(it) } }
                ?: Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
    }
}