package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.RemoveUnusedDataCommand
import com.networkedassets.git4c.core.datastore.repositories.*
import com.networkedassets.git4c.core.process.IGetAllMacrosInSystem
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class RemoveUnusedDataUseCase(
        components: BussinesPluginComponents,
        val macroSettingsDatabase: MacroSettingsDatabase = components.providers.macroSettingsProvider,
        val repositoryDatabase: RepositoryDatabase = components.providers.repositoryProvider,
        val predefinedRepositoryDatabase: PredefinedRepositoryDatabase = components.database.predefinedRepositoryDatabase,
        val globsDatabase: GlobForMacroDatabase = components.providers.globsForMacroProvider,
        val extractorDataDatabase: ExtractorDataDatabase = components.database.extractorDataDatabase,
        val getAllMacrosInSystemProcess: IGetAllMacrosInSystem = components.processing.getAllMacrosInSystemProcess
) : UseCase<RemoveUnusedDataCommand, Unit>
(components) {

    override fun execute(request: RemoveUnusedDataCommand): Result<Unit, Exception> {

        // TODO: Async! This operation is killer for confluence!
        // TODO: Add new objects

        //Step 1. Remove macrosettings that are no longer in Confluence

        val macrosInSystem = getAllMacrosInSystemProcess.extract().toHashSet()

        macroSettingsDatabase.getAll()
                .filterNot { macrosInSystem.contains(it.uuid) }
                .forEach { macroSettingsDatabase.remove(it.uuid) }

        val extractorsToKeep = macroSettingsDatabase.getAll().mapNotNull { it.extractorDataUuid }

        //Step 2. Remove repositories that are no longer linked to any MacroSettings or any PredefinedRepository

        val repositoriesFromMacros = macroSettingsDatabase.getAll().map { it.repositoryUuid }.toHashSet()
        val repositoriesFromPredefined = predefinedRepositoryDatabase.getAll().map { it.repositoryUuid }.toHashSet()

        val reposToKeep = repositoriesFromMacros + repositoriesFromPredefined

        repositoryDatabase.getAll()
                .filterNot { reposToKeep.contains(it.uuid) }
                .forEach { repositoryDatabase.remove(it.uuid) }

        //Step 3. Remove globs which MacroSettings does no longer exist

        globsDatabase.getAll()
                .filterNot { macrosInSystem.contains(it.macroSettingsUuid) }
                .forEach { globsDatabase.remove(it.uuid) }

        //Step 4. Remove extractors for which MacroSettings does no longer exist

        val existingExtractors = extractorDataDatabase.getAll().map { it.uuid }

        (existingExtractors - extractorsToKeep)
                .forEach { extractorDataDatabase.remove(it) }

        return Result.of { }
    }
}