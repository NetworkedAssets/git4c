package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.RemoveAllDataCommand
import com.networkedassets.git4c.core.datastore.repositories.*
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class RemoveAllDataUseCase(
        components: BussinesPluginComponents,
        val macroSettingsRepository: MacroSettingsDatabase = components.providers.macroSettingsProvider,
        val globForMacroDatabase: GlobForMacroDatabase = components.providers.globsForMacroProvider,
        val predefinedRepositoryDatabase: PredefinedRepositoryDatabase = components.database.predefinedRepositoryDatabase,
        val encryptedRepositoryDatabase: EncryptedRepositoryDatabase = components.database.encryptedRepositoryDatabase,
        val extractorDataDatabase: ExtractorDataDatabase = components.database.extractorDataDatabase,
        val predefinedGlobsDatabase: PredefinedGlobsDatabase = components.database.predefinedGlobsDatabase,
        val macroLocationDatabase: MacroLocationDatabase = components.database.macroLocationDatabase,
        val repositoryUsageDatabase: RepositoryUsageDatabase = components.database.repositoryUsageDatabase,
        val temporaryEditBranchesDatabase: TemporaryEditBranchesDatabase = components.database.temporaryEditBranchesDatabase
) : UseCase<RemoveAllDataCommand, String>
(components) {

    override fun execute(request: RemoveAllDataCommand): Result<String, Exception> {
        macroSettingsRepository.removeAll()
        globForMacroDatabase.removeAll()
        predefinedRepositoryDatabase.removeAll()
        encryptedRepositoryDatabase.removeAll()
        extractorDataDatabase.removeAll()
        predefinedGlobsDatabase.removeAll()
        macroLocationDatabase.removeAll()
        repositoryUsageDatabase.removeAll()
        temporaryEditBranchesDatabase.removeAll()
        return Result.of { "" }
    }
}
