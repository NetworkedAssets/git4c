package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.CreateDocumentationsMacroCommand
import com.networkedassets.git4c.boundary.inbound.CustomRepository
import com.networkedassets.git4c.boundary.inbound.DocumentationMacro
import com.networkedassets.git4c.boundary.inbound.ExistingRepository
import com.networkedassets.git4c.boundary.inbound.PredefinedRepositoryToCreate
import com.networkedassets.git4c.boundary.outbound.SavedDocumentationsMacro
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.bussiness.ConverterPlugin
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.common.IdentifierGenerator
import com.networkedassets.git4c.core.datastore.repositories.GlobForMacroDatabase
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.datastore.repositories.PredefinedRepositoryDatabase
import com.networkedassets.git4c.core.datastore.repositories.RepositoryDatabase
import com.networkedassets.git4c.data.*
import com.networkedassets.git4c.delivery.executor.execution.UseCase


typealias inUserNamePassword = com.networkedassets.git4c.boundary.inbound.UsernamePasswordAuthorization
typealias inSshKey = com.networkedassets.git4c.boundary.inbound.SshKeyAuthorization
typealias inNoAuth = com.networkedassets.git4c.boundary.inbound.NoAuth

class CreateDocumentationsMacroUseCase(
        val macroSettingsDatabase: MacroSettingsDatabase,
        val repositoryDatabase: RepositoryDatabase,
        val globForMacroDatabase: GlobForMacroDatabase,
        val predefinedRepositoryDatabase: PredefinedRepositoryDatabase,
        val importer: SourcePlugin,
        val converter: ConverterPlugin,
        val idGenerator: IdentifierGenerator
) : UseCase<CreateDocumentationsMacroCommand, SavedDocumentationsMacro> {

    override fun execute(request: CreateDocumentationsMacroCommand): Result<SavedDocumentationsMacro, Exception> {
        val documentationMacroToCreate = request.documentMacroMacroToCreate
        val branch = documentationMacroToCreate.branch
        val method = documentationMacroToCreate.method
        val repository = repositoryConvert(documentationMacroToCreate)
        val newMacroId = idGenerator.generateNewIdentifier()
        val macroSettings = MacroSettings(newMacroId, repository?.uuid, branch, documentationMacroToCreate.defaultDocItem, method)

        importer.verify(repository).apply {
            if (isOk()) {
                save(newMacroId, macroSettings, repository!!, documentationMacroToCreate)
                return@execute Result.of { SavedDocumentationsMacro(macroSettings.uuid) }
            } else {
                return@execute Result.error(IllegalArgumentException(status.name))
            }
        }
        return Result.error(NotFoundException(request.transactionInfo, ""))
    }

    private fun save(newMacroId: String, macroSettings: MacroSettings, repository: Repository, documentationMacroToCreate: DocumentationMacro) {
        saveMacro(newMacroId, macroSettings)
        if (!isPredefined(documentationMacroToCreate)) saveRepository(repository)
        saveGlobsForMacro(documentationMacroToCreate, newMacroId)
    }

    private fun saveMacro(newMacroId: String, macroSettings: MacroSettings) {
        macroSettingsDatabase.insert(newMacroId, macroSettings)
    }

    private fun saveRepository(repository: Repository) {
        repositoryDatabase.insert(repository.uuid, repository)
    }

    private fun saveGlobsForMacro(documentationMacroToCreate: DocumentationMacro, newMacroId: String) {
        documentationMacroToCreate.glob.forEach {
            val glob = GlobForMacro(idGenerator.generateNewIdentifier(), newMacroId, it)
            globForMacroDatabase.insert(glob.uuid, glob)
        }
    }

    private fun repositoryConvert(documentationMacroMacroToCreate: DocumentationMacro): Repository? {
        if (isPredefined(documentationMacroMacroToCreate)) {
            return convertFromPredefinedRepository(documentationMacroMacroToCreate.repositoryDetails.repository as PredefinedRepositoryToCreate)
        } else if (isExisting(documentationMacroMacroToCreate)) {
            return convertFromExistingRepository(documentationMacroMacroToCreate.repositoryDetails.repository as ExistingRepository)
        } else {
            return convertFromCustomRepository(documentationMacroMacroToCreate.repositoryDetails.repository as CustomRepository)
        }
    }

    private fun isPredefined(documentationMacroToCreate: DocumentationMacro) = when (documentationMacroToCreate.repositoryDetails.repository) {
        is CustomRepository -> false
        is PredefinedRepositoryToCreate -> true
        else -> false
    }

    private fun isExisting(documentationMacroToCreate: DocumentationMacro) = when (documentationMacroToCreate.repositoryDetails.repository) {
        is CustomRepository -> false
        is PredefinedRepositoryToCreate -> false
        is ExistingRepository -> true
        else -> false
    }

    private fun convertFromPredefinedRepository(repository: PredefinedRepositoryToCreate): Repository? {
        val predefined = predefinedRepositoryDatabase.get(repository.uuid) ?: return null
        return repositoryDatabase.get(predefined.repositoryUuid)
    }

    private fun convertFromExistingRepository(repository: ExistingRepository): Repository? {
        return repositoryDatabase.get(repository.uuid)
    }

    private fun convertFromCustomRepository(repository: CustomRepository): Repository? {
        when (repository.credentials) {
            is inUserNamePassword -> return RepositoryWithUsernameAndPassword(
                    idGenerator.generateNewIdentifier(),
                    repository.url,
                    repository.credentials.username,
                    repository.credentials.password)
            is inSshKey -> return RepositoryWithSshKey(
                    idGenerator.generateNewIdentifier(),
                    repository.url,
                    repository.credentials.sshKey)
            is inNoAuth -> return RepositoryWithNoAuthorization(idGenerator.generateNewIdentifier(), repository.url)
            else -> throw RuntimeException("Unknown auth type: ${repository.credentials.javaClass}")

        }
    }
}

