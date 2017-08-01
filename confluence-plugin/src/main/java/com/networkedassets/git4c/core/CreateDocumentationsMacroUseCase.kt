package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.CreateDocumentationsMacroCommand
import com.networkedassets.git4c.boundary.inbound.DocumentationMacroToCreate
import com.networkedassets.git4c.boundary.outbound.CreatedDocumentationsMacro
import com.networkedassets.git4c.core.bussiness.ConverterPlugin
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.common.IdentifierGenerator
import com.networkedassets.git4c.core.datastore.MacroSettingsRepository
import com.networkedassets.git4c.data.macro.*
import com.networkedassets.git4c.delivery.executor.execution.UseCase
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException


typealias inUserNamePassword = com.networkedassets.git4c.boundary.inbound.UsernamePasswordAuthorization
typealias inSshKey = com.networkedassets.git4c.boundary.inbound.SshKeyAuthorization
typealias inNoAuth = com.networkedassets.git4c.boundary.inbound.NoAuth

class CreateDocumentationsMacroUseCase(
        val macroSettingsRepository: MacroSettingsRepository,
        val importer: SourcePlugin,
        val converter: ConverterPlugin,
        val idGenerator: IdentifierGenerator
) : UseCase<CreateDocumentationsMacroCommand, CreatedDocumentationsMacro> {

    override fun execute(request: CreateDocumentationsMacroCommand): Result<CreatedDocumentationsMacro, Exception> {
        val documentationMacroToCreate = request.documentMacroMacroToCreate
        val repositoryPath = documentationMacroToCreate.sourceRepositoryUrl
        val branch = documentationMacroToCreate.branch
        val credentials = detectCredentials(documentationMacroToCreate)
        val newMacroId = idGenerator.generateNewIdentifier()
        val macroSettings = DocumentationsMacroSettings(newMacroId, repositoryPath, credentials, branch, documentationMacroToCreate.glob, documentationMacroToCreate.defaultDocItem)

        importer.verify(macroSettings).apply {
            if (isOk()) {
                save(macroSettings)
                return@execute Result.of { CreatedDocumentationsMacro(macroSettings.id) }
            } else {
                return@execute Result.error(IllegalArgumentException(status.name))
            }
        }
        return Result.error(NotFoundException(request.transactionInfo, ""))
    }

    private fun detectCredentials(documentationMacroMacroToCreate: DocumentationMacroToCreate): RepositoryAuthorization {
        when (documentationMacroMacroToCreate.credentials) {
            is inUserNamePassword -> return UsernameAndPasswordCredentials(
                    documentationMacroMacroToCreate.credentials.username,
                    documentationMacroMacroToCreate.credentials.password)
            is inSshKey -> return SshKeyCredentials(documentationMacroMacroToCreate.credentials.sshKey)
            is inNoAuth -> return NoAuthCredentials()
            else -> throw RuntimeException("Unknown auth type: ${documentationMacroMacroToCreate.credentials.javaClass}")

        }
    }

    private fun save(documentationsMacroSettings: DocumentationsMacroSettings) {
        macroSettingsRepository.put(
                documentationsMacroSettings.id,
                documentationsMacroSettings
        )
    }
}