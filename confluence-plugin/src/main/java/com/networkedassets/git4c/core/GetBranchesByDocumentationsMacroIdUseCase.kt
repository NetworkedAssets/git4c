package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetBranchesByDocumentationsMacroIdQuery
import com.networkedassets.git4c.boundary.outbound.Branches
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.process.GetBranchesProcess
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class GetBranchesByDocumentationsMacroIdUseCase(
        val getBranchesProcess: GetBranchesProcess,
        val macroSettingsDatabase: MacroSettingsDatabase,
        val repositoryDatabase: RepositoryDatabase
) : UseCase<GetBranchesByDocumentationsMacroIdQuery, Branches> {

    override fun execute(request: GetBranchesByDocumentationsMacroIdQuery): Result<Branches, Exception> {
        val macroSettings = macroSettingsDatabase.get(request.macroId) ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        if (macroSettings.repositoryUuid == null) return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val repository = repositoryDatabase.get(macroSettings.repositoryUuid) ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        return Result.of { getBranches(macroSettings, repository) }
    }

    private fun getBranches(macroSettings: MacroSettings, repository: Repository) = getBranchesProcess.fetchBranchList(macroSettings, repository)
}