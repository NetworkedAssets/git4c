package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetLatestRevisionByDocumentationsMacroIdQuery
import com.networkedassets.git4c.boundary.outbound.Revision
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.datastore.cache.DocumentsViewCache
import com.networkedassets.git4c.core.datastore.cache.RepositoryRevisionCache
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.exceptions.VerificationException
import com.networkedassets.git4c.core.process.MacroViewProcess
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class GetLatestRevisionByDocumentationsMacroIdUseCase(
        val macroSettingsRepository: MacroSettingsDatabase,
        val repositoryDatabase: RepositoryDatabase,
        val importer: SourcePlugin,
        val cache: DocumentsViewCache,
        val macroViewProcess: MacroViewProcess,
        val revisionCache : RepositoryRevisionCache
): UseCase<GetLatestRevisionByDocumentationsMacroIdQuery, Revision> {
    override fun execute(request: GetLatestRevisionByDocumentationsMacroIdQuery): Result<Revision, Exception> {

        val searchedMacroId = request.macroId
        // TODO: Non-blocking-mode

        val macroSettings = macroSettingsRepository.get(searchedMacroId) ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED.name))
        if (macroSettings.repositoryUuid == null) return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED.name))
        val repository = repositoryDatabase.get(macroSettings.repositoryUuid) ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED.name))

        try {
            val revision = Revision(importer.revision(macroSettings, repository).use { it.revision })
            if (cache.get(searchedMacroId)?.revision != revision.id) {
                macroViewProcess.prepareMacroToBeViewed(searchedMacroId)
            }
            return Result.of { revision }
        } catch (e: VerificationException) {
            return@execute Result.error(NotFoundException(request.transactionInfo, e.verification.status.name))
        }
    }
}