package com.networkedassets.git4c.core

import com.atlassian.confluence.core.service.NotAuthorizedException
import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.CreateDocumentationsMacroCommand
import com.networkedassets.git4c.boundary.CreateDocumentationsMacroResultRequest
import com.networkedassets.git4c.boundary.inbound.*
import com.networkedassets.git4c.boundary.outbound.SavedDocumentationsMacro
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.bussiness.ComputationCache
import com.networkedassets.git4c.core.bussiness.ConverterPlugin
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.common.IdentifierGenerator
import com.networkedassets.git4c.core.datastore.MacroSettingsProvider
import com.networkedassets.git4c.core.datastore.extractors.LineNumbersExtractorData
import com.networkedassets.git4c.core.datastore.extractors.MethodExtractorData
import com.networkedassets.git4c.core.datastore.repositories.*
import com.networkedassets.git4c.core.process.CreateMacroProcess
import com.networkedassets.git4c.core.usecase.async.ComputationResultUseCase
import com.networkedassets.git4c.core.usecase.async.MultiThreadAsyncUseCase
import com.networkedassets.git4c.data.*
import java.util.*


typealias inUserNamePassword = com.networkedassets.git4c.boundary.inbound.UsernamePasswordAuthorization
typealias inSshKey = com.networkedassets.git4c.boundary.inbound.SshKeyAuthorization
typealias inNoAuth = com.networkedassets.git4c.boundary.inbound.NoAuth

class CreateDocumentationsMacroUseCase(
        components: BussinesPluginComponents,
        val macroSettingsDatabase: MacroSettingsProvider = components.providers.macroSettingsProvider,
        val repositoryDatabase: RepositoryDatabase = components.providers.repositoryProvider,
        val globForMacroDatabase: GlobForMacroDatabase = components.providers.globsForMacroProvider,
        val predefinedRepositoryDatabase: PredefinedRepositoryDatabase = components.database.predefinedRepositoryDatabase,
        val extractorDataDatabase: ExtractorDataDatabase = components.database.extractorDataDatabase,
        val importer: SourcePlugin = components.macro.importer,
        val converter: ConverterPlugin = components.macro.converter,
        val idGenerator: IdentifierGenerator = components.utils.idGenerator,
        val pluginSettings: PluginSettingsDatabase = components.database.pluginSettings,
        val repositoryUsageDatabase: RepositoryUsageDatabase = components.database.repositoryUsageDatabase,
        val createMacroProcess: CreateMacroProcess = components.processing.createMacroProcess,
        computations: ComputationCache<SavedDocumentationsMacro> = components.async.createDocumentationMacroUseCaseCache
) : MultiThreadAsyncUseCase<CreateDocumentationsMacroCommand, SavedDocumentationsMacro>
(components, computations, 2) {

    override fun executedAsync(requestId: String, request: CreateDocumentationsMacroCommand) {

        val documentationMacroToCreate = request.documentMacroMacroToCreate
        if (!isAllowed(documentationMacroToCreate)) {
            return error(requestId, NotAuthorizedException(request.user))
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
                return success(requestId, SavedDocumentationsMacro(macroSettings.uuid))
            } else {
                return error(requestId, IllegalArgumentException(status.name))
            }
        }
        return error(requestId, NotFoundException(request.transactionInfo, ""))
    }

    private fun extractorConvert(extractor: ExtractorData?): com.networkedassets.git4c.core.datastore.extractors.ExtractorData? {

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
                    false,
                    repository.credentials.username,
                    repository.credentials.password)
            is inSshKey -> return RepositoryWithSshKey(
                    idGenerator.generateNewIdentifier(),
                    repository.url,
                    false,
                    repository.credentials.sshKey)
            is inNoAuth -> return RepositoryWithNoAuthorization(idGenerator.generateNewIdentifier(), repository.url, false)
            else -> throw RuntimeException("Unknown auth type: ${repository.credentials.javaClass}")

        }
    }


    private fun isAllowed(documentationMacroToCreate: DocumentationMacro): Boolean {
        val isForcedPredefined = pluginSettings.getForcePredefinedRepositoriesSetting() ?: false
        return !isForcedPredefined || isPredefined(documentationMacroToCreate) || isExisting(documentationMacroToCreate)
    }
}

class CreateDocumentationsMacroResultUseCase(
        components: BussinesPluginComponents,
        computations: ComputationCache<SavedDocumentationsMacro> = components.async.createDocumentationMacroUseCaseCache
) : ComputationResultUseCase<CreateDocumentationsMacroResultRequest, SavedDocumentationsMacro>(components, computations)