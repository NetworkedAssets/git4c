package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.RemoveAllDataCommand
import com.networkedassets.git4c.core.common.IdentifierGenerator
import com.networkedassets.git4c.core.datastore.cache.DocumentsViewCache
import com.networkedassets.git4c.core.datastore.repositories.*
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class RemoveAllDataUseCase(
        val documentsViewCache: DocumentsViewCache,
        val macroSettingsRepository: MacroSettingsDatabase,
        val globForMacroDatabase: GlobForMacroDatabase,
        val repositoryDatabase: RepositoryDatabase,
        val predefinedRepositoryDatabase: PredefinedRepositoryDatabase,
        val predefinedGlobsDatabase: PredefinedGlobsDatabase,
        val idGenerator: IdentifierGenerator
) : UseCase<RemoveAllDataCommand, String> {

    override fun execute(request: RemoveAllDataCommand): Result<String, Exception> {
        // TODO: Async! This operation is killer for confluence!
        // TODO: Add new objects
        documentsViewCache.removeAll()
        macroSettingsRepository.removeAll()
        globForMacroDatabase.removeAll()
        repositoryDatabase.removeAll()
        predefinedRepositoryDatabase.removeAll()
        predefinedGlobsDatabase.removeAll()
        return Result.of { "" }
    }
}
