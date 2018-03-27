package com.networkedassets.git4c.core

import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.GetTemporaryEditBranchCommand
import com.networkedassets.git4c.boundary.GetTemporaryEditBranchResultCommand
import com.networkedassets.git4c.boundary.outbound.TemporaryBranch
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.bussiness.ComputationCache
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.datastore.cache.DocumentsViewCache
import com.networkedassets.git4c.core.datastore.cache.MacroToBeViewedPrepareLockCache
import com.networkedassets.git4c.core.datastore.repositories.*
import com.networkedassets.git4c.core.process.ICheckUserPermissionProcess
import com.networkedassets.git4c.core.usecase.async.ComputationResultUseCase
import com.networkedassets.git4c.core.usecase.async.MultiThreadAsyncUseCase
import com.networkedassets.git4c.delivery.executor.monitoring.TransactionInfo
import com.networkedassets.git4c.utils.error
import com.networkedassets.git4c.utils.getLogger
import java.util.concurrent.TimeUnit

class GetTemporaryEditBranchUseCase(
        components: BussinesPluginComponents,
        val importer: SourcePlugin = components.macro.importer,
        val documentsViewCache: DocumentsViewCache = components.cache.documentsViewCache,
        val repositoryDatabase: RepositoryDatabase = components.providers.repositoryProvider,
        val macroSettingsDatabase: MacroSettingsDatabase = components.providers.macroSettingsProvider,
        val checkUserPermissionProcess: ICheckUserPermissionProcess = components.processing.checkUserPermissionProcess,
        val macroLocationDatabase: MacroLocationDatabase = components.database.macroLocationDatabase,
        val temporaryEditBranchesDatabase: TemporaryEditBranchesDatabase = components.database.temporaryEditBranchesDatabase,
        val macroViewCache: MacroToBeViewedPrepareLockCache = components.cache.macroViewCache,
        computations: ComputationCache<TemporaryBranch> = components.async.temporaryEditBranchResultCache
) : MultiThreadAsyncUseCase<GetTemporaryEditBranchCommand, TemporaryBranch>
(components, computations, 2) {

    val log = getLogger()

    override fun executedAsync(requestId: String, request: GetTemporaryEditBranchCommand) {
        process(requestId, request.macroId, request.transactionInfo)
    }

    fun process(requestId: String, macroId: String, transactionInfo: TransactionInfo) {

        try {

            val macroLocation = macroLocationDatabase.get(macroId)
            val macro = macroSettingsDatabase.get(macroId)
                    ?: throw NotFoundException(transactionInfo, VerificationStatus.REMOVED)
            val repositoryId = macro.repositoryUuid
                    ?: throw NotFoundException(transactionInfo, VerificationStatus.REMOVED)
            val repository = repositoryDatabase.get(repositoryId)
                    ?: throw NotFoundException(transactionInfo, VerificationStatus.REMOVED)

            if (importer.isLocked(repository.repositoryPath)) {
                executor.schedule({ process(requestId, macroId, transactionInfo) }, 1, TimeUnit.SECONDS)
                return
            }

            var temporaryBranch = macroLocation.withNotNull { temporaryEditBranchesDatabase.get(repositoryId, this.pageId) }

            if (temporaryBranch != null &&
                    (!importer.getBranches(repository).contains(temporaryBranch.name)
                            || importer.isBranchMerged(repository, temporaryBranch.name, macro.branch))) {

                temporaryEditBranchesDatabase.remove(temporaryBranch.uuid)
                macroViewCache.remove(macroId)
                documentsViewCache.remove(macroId)
                temporaryBranch = null
            }

            success(requestId, TemporaryBranch(temporaryBranch?.name))
        } catch (e: Exception) {
            log.error(e) { "Execution failed" }
            error(requestId, e)
        }


    }

    infix inline fun <T : Any, R : Any> T?.withNotNull(thenDo: T.() -> R?): R? = if (this == null) null else this.thenDo()

}

class GetTemporaryEditBranchResultUseCase(
        components: BussinesPluginComponents,
        computations: ComputationCache<TemporaryBranch> = components.async.temporaryEditBranchResultCache
) : ComputationResultUseCase<GetTemporaryEditBranchResultCommand, TemporaryBranch>
(components, computations)