package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.RemovePredefinedRepositoryCommand
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.datastore.repositories.PredefinedRepositoryDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class RemovePredefinedRepositoryUseCase(
        val macroSettingsRepository: MacroSettingsDatabase,
        val repositoryDatabase: RepositoryDatabase,
        val predefinedRepositoryDatabase: PredefinedRepositoryDatabase) : UseCase<RemovePredefinedRepositoryCommand, String> {

    override fun execute(request: RemovePredefinedRepositoryCommand): Result<String, Exception> {

        val existingPredefinedRepository = predefinedRepositoryDatabase.get(request.repositoryId) ?: return@execute Result.error(NotFoundException(request.transactionInfo, ""))
        val existingRepository = repositoryDatabase.get(existingPredefinedRepository.repositoryUuid) ?: return@execute Result.error(NotFoundException(request.transactionInfo, ""))
        val macros = macroSettingsRepository.getByRepository(existingRepository.uuid)

        macros.forEach {
            val updated = MacroSettings(it.uuid, null, it.branch, it.defaultDocItem, it.method)
            macroSettingsRepository.update(updated.uuid, updated)
        }

        repositoryDatabase.remove(existingRepository.uuid)
        predefinedRepositoryDatabase.remove(existingPredefinedRepository.uuid)

        return Result.of { "" }
    }

}
