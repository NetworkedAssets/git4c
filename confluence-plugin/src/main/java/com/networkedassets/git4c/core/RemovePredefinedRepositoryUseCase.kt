package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.RemovePredefinedRepositoryCommand
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.datastore.repositories.PredefinedRepositoryDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryUsageDatabase
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class RemovePredefinedRepositoryUseCase(
        components: BussinesPluginComponents,
        val macroSettingsRepository: MacroSettingsDatabase = components.providers.macroSettingsProvider,
        val repositoryDatabase: RepositoryDatabase = components.providers.repositoryProvider,
        val predefinedRepositoryDatabase: PredefinedRepositoryDatabase = components.database.predefinedRepositoryDatabase,
        val repositoryUsageDatabase: RepositoryUsageDatabase = components.database.repositoryUsageDatabase
) : UseCase<RemovePredefinedRepositoryCommand, String>
(components) {

    override fun execute(request: RemovePredefinedRepositoryCommand): Result<String, Exception> {

        val existingPredefinedRepository = predefinedRepositoryDatabase.get(request.repositoryId)
                ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val existingRepository = repositoryDatabase.get(existingPredefinedRepository.repositoryUuid)
                ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val macros = macroSettingsRepository.getByRepository(existingRepository.uuid)

        macros.forEach {
            val updated = MacroSettings(it.uuid, null, it.branch, it.defaultDocItem, it.extractorDataUuid, it.rootDirectory)
            macroSettingsRepository.put(updated.uuid, updated)
        }

        repositoryDatabase.remove(existingRepository.uuid)
        predefinedRepositoryDatabase.remove(existingPredefinedRepository.uuid)

        repositoryUsageDatabase.getByRepositoryUuid(existingRepository.uuid).forEach { repositoryUsageDatabase.remove(it.uuid) }

        return Result.of { "" }
    }

}
