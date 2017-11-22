package com.networkedassets.git4c.core

import com.atlassian.confluence.core.service.NotAuthorizedException
import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.CreateTemporaryDocumentationsContentCommand
import com.networkedassets.git4c.boundary.outbound.Id
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.bussiness.ConverterPlugin
import com.networkedassets.git4c.core.common.IdentifierGenerator
import com.networkedassets.git4c.core.datastore.cache.DocumentsViewCache
import com.networkedassets.git4c.core.datastore.cache.MacroSettingsCache
import com.networkedassets.git4c.core.datastore.cache.TemporaryIdCache
import com.networkedassets.git4c.core.datastore.repositories.ExtractorDataDatabase
import com.networkedassets.git4c.core.datastore.repositories.GlobForMacroDatabase
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.core.exceptions.VerificationException
import com.networkedassets.git4c.core.process.ICheckUserPermissionProcess
import com.networkedassets.git4c.core.process.RefreshMacroProcess
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class CreateTemporaryDocumentationsContentUseCase(
        val settingsCache: MacroSettingsCache,
        val cache: DocumentsViewCache,
        val converter: ConverterPlugin,
        val macroSettingsDatabase: MacroSettingsDatabase,
        val repositoryDatabase: RepositoryDatabase,
        val globForMacroDatabase: GlobForMacroDatabase,
        val extractorDatabase: ExtractorDataDatabase,
        val idGenerator: IdentifierGenerator,
        val temporaryIdCache: TemporaryIdCache,
        val refreshMacroProcess: RefreshMacroProcess,
        val checkUserPermissionProcess: ICheckUserPermissionProcess
) : UseCase<CreateTemporaryDocumentationsContentCommand, Id> {

    override fun execute(request: CreateTemporaryDocumentationsContentCommand): Result<Id, Exception> {

        val macroId = request.macroId
        val user = request.user

        if (checkUserPermissionProcess.userHasPermissionToMacro(macroId, user) == false) {
            return Result.error(NotAuthorizedException("User doesn't have permission to this space"))
        }

        val macroSettings = macroSettingsDatabase.get(request.macroId) ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val key = "${macroSettings.uuid}|||${request.branch}"
        val tempId = temporaryIdCache.get(key) ?: idGenerator.generateNewIdentifier()
        val tempMacroSettings = macroSettingsDatabase.get(tempId) ?: {
            val tempMacroSettings = MacroSettings(tempId, macroSettings.repositoryUuid, request.branch, macroSettings.defaultDocItem, macroSettings.extractorDataUuid)
            settingsCache.insert(tempMacroSettings.uuid, tempMacroSettings)
            temporaryIdCache.insert(key, tempId)
            tempMacroSettings
        }()
        if (macroSettings.repositoryUuid == null) return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val globs = globForMacroDatabase.getByMacro(macroSettings.uuid)
        val repository = repositoryDatabase.get(macroSettings.repositoryUuid) ?: return@execute Result.error(NotFoundException(request.transactionInfo, VerificationStatus.REMOVED))
        val extractor = extractorDatabase.getNullable(macroSettings.extractorDataUuid) ?: return@execute Result.error(NotFoundException(request.transactionInfo, ""))

        try {
            refreshMacroProcess.fetchDataFromSourceThenConvertAndCache(tempMacroSettings, globs, repository, extractor)
            return Result.of { Id(tempMacroSettings.uuid) }
        } catch (e: VerificationException) {
            return@execute Result.error(IllegalArgumentException( e.verification.status.name))
        }
    }

}