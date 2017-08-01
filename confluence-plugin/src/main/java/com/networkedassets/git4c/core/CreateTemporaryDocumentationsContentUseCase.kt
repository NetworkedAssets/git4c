package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.CreateTemporaryDocumentationsContentCommand
import com.networkedassets.git4c.boundary.outbound.Id
import com.networkedassets.git4c.core.bussiness.ConverterPlugin
import com.networkedassets.git4c.core.common.IdentifierGenerator
import com.networkedassets.git4c.core.datastore.DocumentsViewCache
import com.networkedassets.git4c.core.datastore.MacroSettingsCache
import com.networkedassets.git4c.core.datastore.MacroSettingsRepository
import com.networkedassets.git4c.core.datastore.TemporaryIdCache
import com.networkedassets.git4c.core.process.RefreshMacroProcess
import com.networkedassets.git4c.data.macro.DocumentationsMacroSettings
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class CreateTemporaryDocumentationsContentUseCase(
        val settingsCache: MacroSettingsCache,
        val cache: DocumentsViewCache,
        val converter: ConverterPlugin,
        val repository: MacroSettingsRepository,
        val idGenerator: IdentifierGenerator,
        val temporaryIdCache: TemporaryIdCache,
        val refreshMacroProcess: RefreshMacroProcess
) : UseCase<CreateTemporaryDocumentationsContentCommand, Id> {

    override fun execute(request: CreateTemporaryDocumentationsContentCommand): Result<Id, Exception> {
        val macroSettings = repository.get(request.macroId) ?: throw RuntimeException("Id not found")
        val key = "${macroSettings.id}|||${request.branch}"

        val tempId = temporaryIdCache.get(key) ?:idGenerator.generateNewIdentifier()
        val tempMacroSettings = repository.get(tempId) ?: {
            val tempMacroSettings = DocumentationsMacroSettings(tempId, macroSettings.repositoryPath, macroSettings.credentials, request.branch, macroSettings.glob, macroSettings.defaultDocItem)
            settingsCache.put(tempMacroSettings.id, tempMacroSettings)
            temporaryIdCache.put(key, tempId)
            tempMacroSettings
        }()

        refreshMacroProcess.fetchDataFromSourceThenConvertAndSave(tempMacroSettings, save = false)

        return Result.of { Id(tempMacroSettings.id) }
    }

}