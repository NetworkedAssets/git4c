package com.networkedassets.git4c.core

import com.atlassian.confluence.core.service.NotAuthorizedException
import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.CreateDocumentationsMacroCommand
import com.networkedassets.git4c.boundary.inbound.*
import com.networkedassets.git4c.boundary.outbound.SavedDocumentationsMacro
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.bussiness.ConverterPlugin
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.common.IdentifierGenerator
import com.networkedassets.git4c.core.datastore.extractors.LineNumbersExtractorData
import com.networkedassets.git4c.core.datastore.extractors.MethodExtractorData
import com.networkedassets.git4c.core.datastore.repositories.*
import com.networkedassets.git4c.core.process.CreateMacroProcess
import com.networkedassets.git4c.data.*
import com.networkedassets.git4c.delivery.executor.execution.UseCase
import java.util.*


typealias inUserNamePassword = com.networkedassets.git4c.boundary.inbound.UsernamePasswordAuthorization
typealias inSshKey = com.networkedassets.git4c.boundary.inbound.SshKeyAuthorization
typealias inNoAuth = com.networkedassets.git4c.boundary.inbound.NoAuth

class CreateDocumentationsMacroUseCase(
        val macroSettingsDatabase: MacroSettingsDatabase,
        val repositoryDatabase: RepositoryDatabase,
        val globForMacroDatabase: GlobForMacroDatabase,
        val predefinedRepositoryDatabase: PredefinedRepositoryDatabase,
        val extractorDataDatabase: ExtractorDataDatabase,
        val importer: SourcePlugin,
        val converter: ConverterPlugin,
        val idGenerator: IdentifierGenerator,
        val pluginSettings: PluginSettingsDatabase,
        val repositoryUsageDatabase: RepositoryUsageDatabase,
        val createMacroProcess: CreateMacroProcess

) : UseCase<CreateDocumentationsMacroCommand, SavedDocumentationsMacro> {

    override fun execute(request: CreateDocumentationsMacroCommand): Result<SavedDocumentationsMacro, Exception> {

        val documentationMacroToCreate = request.documentMacroMacroToCreate

        if (!isAllowed(documentationMacroToCreate)) {
            return Result.error(NotAuthorizedException(request.user))
        }

        val branch = documentationMacroToCreate.branch
        val extractorData = extractorConvert(documentationMacroToCreate.extractor)
        val repository = repositoryConvert(documentationMacroToCreate)
        val newMacroId = idGenerator.generateNewIdentifier()
        val macroSettings = MacroSettings(newMacroId, repository?.uuid, branch, documentationMacroToCreate.defaultDocItem, extractorData?.uuid, documentationMacroToCreate.rootDirectory)

        importer.verify(repository).apply {
            if (isOk()) {
                save(newMacroId, macroSettings, repository!!, extractorData, documentationMacroToCreate, request.user)
                createMacroProcess.fetchDataFromSource(macroSettings, repository);
                return@execute Result.of { SavedDocumentationsMacro(macroSettings.uuid) }
            } else {
                return@execute Result.error(IllegalArgumentException(status.name))
            }
        }
        return Result.error(NotFoundException(request.transactionInfo, ""))
    }

    fun extractorConvert(extractor: ExtractorData?): com.networkedassets.git4c.core.datastore.extractors.ExtractorData? {

        val uuid = idGenerator.generateNewIdentifier()

        return when (extractor) {
            is LineNumbers -> LineNumbersExtractorData(uuid, extractor.start, extractor.end)
            is Method -> MethodExtractorData(uuid, extractor.method)
            else -> {
                if (extractor != null) {
                    //This is just in case developer forgets to add extract type to this method
                    throw RuntimeException("Unknown extractor type: ${extractor::class.java}")
                } else {
                    null
                }
            }
        }

    }

    private fun save(newMacroId: String, macroSettings: MacroSettings, repository: Repository, extractorData: com.networkedassets.git4c.core.datastore.extractors.ExtractorData?, documentationMacroToCreate: DocumentationMacro, user: String) {
        saveMacro(newMacroId, macroSettings)
        if (!isPredefined(documentationMacroToCreate)) saveRepository(repository)
        saveGlobsForMacro(documentationMacroToCreate, newMacroId)
        saveExtractorData(extractorData)
        saveRepositoryUsage(RepositoryUsage(idGenerator.generateNewIdentifier(), user, repository.uuid, documentationMacroToCreate.repositoryName, Date().time))
    }

    private fun saveRepositoryUsage(repositoryUsage: RepositoryUsage) {
        val userUsages = repositoryUsageDatabase.getByUsername(repositoryUsage.username)

        val repositoryUsageToRemove = userUsages
                .find { it.repositoryName == repositoryUsage.repositoryName && it.repositoryUuid == repositoryUsage.repositoryUuid }

        if (repositoryUsageToRemove != null) {
            repositoryUsageDatabase.remove(repositoryUsageToRemove.uuid)
        }

        if (userUsages.size == 5) {
            repositoryUsageDatabase.remove(userUsages.last().uuid)
        }
        repositoryUsageDatabase.put(repositoryUsage.uuid, repositoryUsage)

    }


    private fun saveMacro(newMacroId: String, macroSettings: MacroSettings) {
        macroSettingsDatabase.put(newMacroId, macroSettings)
    }

    private fun saveRepository(repository: Repository) {
        repositoryDatabase.put(repository.uuid, repository)
    }

    private fun saveGlobsForMacro(documentationMacroToCreate: DocumentationMacro, newMacroId: String) {
        documentationMacroToCreate.glob.forEach {
            val glob = GlobForMacro(idGenerator.generateNewIdentifier(), newMacroId, it)
            globForMacroDatabase.put(glob.uuid, glob)
        }
    }

    fun saveExtractorData(extractorData: com.networkedassets.git4c.core.datastore.extractors.ExtractorData?) {
        if (extractorData != null) {
            extractorDataDatabase.put(extractorData.uuid, extractorData)
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


    private fun isAllowed(documentationMacroToCreate: DocumentationMacro): Boolean {
        val isForcedPredefined = pluginSettings.getForcePredefinedRepositoriesSetting() ?: false
        return !isForcedPredefined || isPredefined(documentationMacroToCreate) || isExisting(documentationMacroToCreate)
    }
}

