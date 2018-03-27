package com.networkedassets.git4c.core

import com.atlassian.confluence.core.service.NotAuthorizedException
import com.github.kittinunf.result.Result
import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.CreateTemporaryDocumentationsContentCommand
import com.networkedassets.git4c.boundary.outbound.Id
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.bussiness.ConverterPlugin
import com.networkedassets.git4c.core.common.IdentifierGenerator
import com.networkedassets.git4c.core.datastore.GlobForMacroProvider
import com.networkedassets.git4c.core.datastore.MacroSettingsProvider
import com.networkedassets.git4c.core.datastore.cache.GlobForMacroCache
import com.networkedassets.git4c.core.datastore.cache.MacroSettingsCache
import com.networkedassets.git4c.core.datastore.cache.TemporaryIdCache
import com.networkedassets.git4c.core.datastore.repositories.MacroLocationDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.process.ICheckUserPermissionProcess
import com.networkedassets.git4c.core.process.MacroViewProcess
import com.networkedassets.git4c.data.GlobForMacro
import com.networkedassets.git4c.data.MacroLocation
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class CreateTemporaryDocumentationsContentUseCase(
        components: BussinesPluginComponents,
        val settingsCache: MacroSettingsCache = components.cache.macroSettingsCache,
        val converter: ConverterPlugin = components.macro.converter,
        val macroSettingsDatabase: MacroSettingsProvider = components.providers.macroSettingsProvider,
        val repositoryDatabase: RepositoryDatabase = components.providers.repositoryProvider,
        val idGenerator: IdentifierGenerator = components.utils.idGenerator,
        val temporaryIdCache: TemporaryIdCache = components.cache.temporaryIdCache,
        val checkUserPermissionProcess: ICheckUserPermissionProcess = components.processing.checkUserPermissionProcess,
        val macroViewProcess: MacroViewProcess = components.processing.macroViewProcess,
        val macroLocationDatabase: MacroLocationDatabase = components.database.macroLocationDatabase,
        val globForMacroDatabase: GlobForMacroProvider = components.providers.globsForMacroProvider,
        val globForMacroCache: GlobForMacroCache = components.cache.globForMacroCache
) : UseCase<CreateTemporaryDocumentationsContentCommand, Id>
(components) {

    override fun execute(request: CreateTemporaryDocumentationsContentCommand): Result<Id, Exception> {

        val macroId = request.macroId
        val user = request.user

        if (checkUserPermissionProcess.userHasPermissionToMacro(macroId, user) == false) {
            return Result.error(NotAuthorizedException("User doesn't have permission to this space"))
        }

        val macroSettings = macroSettingsDatabase.get(request.macroId)
                ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val key = "${macroSettings.uuid}|||${request.branch}"
        val tempId = temporaryIdCache.get(key) ?: idGenerator.generateNewIdentifier()
        val tempMacroSettings = macroSettingsDatabase.get(tempId) ?: {
            val tempMacroSettings = MacroSettings(tempId, macroSettings.repositoryUuid, request.branch, macroSettings.defaultDocItem, macroSettings.extractorDataUuid, macroSettings.rootDirectory)
            settingsCache.put(tempMacroSettings.uuid, tempMacroSettings)
            temporaryIdCache.put(key, tempId)
            tempMacroSettings
        }()

        val originGlobs = globForMacroDatabase.getByMacro(macroId)
        val destGlobs = globForMacroDatabase.getByMacro(tempId)

        if (destGlobs.isEmpty() && !originGlobs.isEmpty()) {
            originGlobs.forEach {
                val id = idGenerator.generateNewIdentifier()
                globForMacroCache.put(id, GlobForMacro(id, tempId, it.glob))
            }
        }

        macroSettings.repositoryUuid
                ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        repositoryDatabase.get(macroSettings.repositoryUuid)
                ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))

        val existingMacroLocation = macroLocationDatabase.get(request.macroId)
        existingMacroLocation
                ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))

        macroLocationDatabase.put(tempMacroSettings.uuid,
                MacroLocation(tempMacroSettings.uuid, existingMacroLocation.pageId, existingMacroLocation.spaceKey)
        )
        macroViewProcess.prepareMacroToBeViewed(tempMacroSettings.uuid);

        return Result.of { Id(tempMacroSettings.uuid) }
    }

}