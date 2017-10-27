package com.networkedassets.git4c.core.usecase

import com.networkedassets.git4c.application.PluginComponents
import com.networkedassets.git4c.boundary.*
import com.networkedassets.git4c.core.*
import com.networkedassets.git4c.delivery.executor.execution.UseCase
import com.networkedassets.git4c.delivery.executor.execution.UseCasesProvider
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

class PluginUseCasesProvider(val components: PluginComponents) : UseCasesProvider {

    val executors: MutableMap<Class<*>, UseCase<*, *>> = mutableMapOf(
            GetDocumentationsMacroViewTemplateQuery::class.java to GetDocumentationsMacroViewTemplateUseCase(),
            GetDocumentationsMacroByDocumentationsMacroIdQuery::class.java to GetDocumentationsMacroByDocumentationsMacroIdUseCase(components.refreshProcess, components.macroSettingsCachableDatabase, components.globsForMacroDatabase, components.repositoryDatabase, components.extractorDataDatabase),
            GetDocumentationsContentTreeByDocumentationsMacroIdQuery::class.java to GetDocumentationsContentTreeByDocumentationsMacroIdUseCase(components.documentsViewCache),
            CreateDocumentationsMacroCommand::class.java to CreateDocumentationsMacroUseCase(components.macroSettingsCachableDatabase, components.repositoryDatabase, components.globsForMacroDatabase, components.predefinedRepositoryDatabase, components.extractorDataDatabase, components.importer, components.converter, components.idGenerator),
            RefreshDocumentationsMacroCommand::class.java to RefreshDocumentationsMacroUseCase(components.refreshProcess, components.macroSettingsCachableDatabase, components.globsForMacroDatabase, components.repositoryDatabase, components.extractorDataDatabase, components.documentsViewCache),
            GetDocumentItemInDocumentationsMacroQuery::class.java to GetDocumentItemInDocumentationsMacroUseCase(components.documentsViewCache),
            GetBranchesByDocumentationsMacroIdQuery::class.java to GetBranchesByDocumentationsMacroIdUseCase(components.getBranchesProcess, components.macroSettingsCachableDatabase, components.repositoryDatabase),
            CreateTemporaryDocumentationsContentCommand::class.java to CreateTemporaryDocumentationsContentUseCase(components.macroSettingsCache, components.documentsViewCache, components.converter, components.macroSettingsCachableDatabase, components.repositoryDatabase, components.globsForMacroDatabase, components.extractorDataDatabase, components.idGenerator, components.temporaryIdCache, components.refreshProcess),
            RemoveAllDataCommand::class.java to RemoveAllDataUseCase(components.documentsViewCache, components.macroSettingsCachableDatabase, components.globsForMacroDatabase, components.repositoryDatabase, components.predefinedRepositoryDatabase, components.predefinedGlobsDatabase, components.idGenerator),
            GetBranchesQuery::class.java to GetBranchesForRepositoryUseCase(components.importer),
            HealthCheckCommand::class.java to HealthCheckUseCase(),
            GetAllDocumentsByDocumentationsMacroIdQuery::class.java to GetAllDocumentsByDocumentationsMacroIdUseCase(components.refreshProcess, components.macroSettingsCachableDatabase, components.repositoryDatabase, components.globsForMacroDatabase, components.extractorDataDatabase),
            CreatePredefinedRepositoryCommand::class.java to CreatePredefinedRepositoryUseCase(components.predefinedRepositoryDatabase, components.repositoryDatabase, components.importer, components.idGenerator),
            GetAllPredefinedRepositoriesCommand::class.java to GetAllPredefinedRepositoriesUseCase(components.predefinedRepositoryDatabase, components.repositoryDatabase),
            GetPredefinedRepositoryCommand::class.java to GetPredefinedRepositoryUseCase(components.predefinedRepositoryDatabase, components.repositoryDatabase, components.importer),
            ModifyPredefinedRepositoryCommand::class.java to ModifyPredefinedRepositoryUseCase(components.predefinedRepositoryDatabase, components.repositoryDatabase, components.importer),
            RemovePredefinedRepositoryCommand::class.java to RemovePredefinedRepositoryUseCase(components.macroSettingsCachableDatabase, components.repositoryDatabase, components.predefinedRepositoryDatabase),
            GetFilesListForRepositoryQuery::class.java to GetFilesListForRepositoryUseCase(components.getFilesProcess),
            GetFileContentForRepositoryQuery::class.java to GetFileContentForRepositoryUseCase(components.getFileProcess),
            GetMethodsForRepositoryQuery::class.java to GetMethodsForRepositoryUseCase(components.getMethodsProcess),
            GetMethodsForPredefinedRepositoryQuery::class.java to GetMethodsForPredefinedRepositoryUseCase(components.predefinedRepositoryDatabase, components.repositoryDatabase, components.getMethodsProcess),
            GetFilesListForPredefinedRepositoryQuery::class.java to GetFilesListForPredefinedRepositoryUseCase(components.getFilesProcess, components.predefinedRepositoryDatabase, components.repositoryDatabase),
            GetFileContentForPredefinedRepositoryQuery::class.java to GetFileContentForPredefinedRepositoryUseCase(components.getFileProcess, components.predefinedRepositoryDatabase, components.repositoryDatabase),
            GetPredefinedRepositoryBranchesQuery::class.java to GetPredefinedRepositoryBranchesUseCase(components.predefinedRepositoryDatabase, components.repositoryDatabase, components.importer),
            GetDocumentationsDefaultBranchByDocumentationsMacroIdQuery::class.java to GetDocumentationsDefaultBranchByDocumentationsMacroIdUseCase(components.macroSettingsCachableDatabase),
            GetGlobsByDocumentationsMacroIdQuery::class.java to GetGlobsByDocumentationsMacroIdUseCase(components.globsForMacroDatabase, components.predefinedGlobsDatabase),
            CreatePredefinedGlobCommand::class.java to CreatePredefinedGlobUseCase(components.predefinedGlobsDatabase, components.idGenerator),
            DeleteAllPredefinedGlobsCommand::class.java to DeleteAllPredefinedGlobsUseCase(components.predefinedGlobsDatabase),
            DeletePredefinedGlobByIdCommand::class.java to DeletePredefinedGlobByIdUseCase(components.predefinedGlobsDatabase),
            GetAllPredefinedGlobsQuery::class.java to GetAllPredefinedGlobsUseCase(components.predefinedGlobsDatabase),
            GetPredefinedGlobByIdQuery::class.java to GetPredefinedGlobByIdUseCase(components.predefinedGlobsDatabase),
            RestoreDefaultPredefinedGlobsCommand::class.java to RestorePredefinedGlobsUseCase(components.predefinedGlobsDatabase, components.idGenerator),
            GetExtractionDataByDocumentationsMacroIdQuery::class.java to GetExtractionDataByDocumentationsMacroIdUseCase(components.macroSettingsCachableDatabase, components.extractorDataDatabase),
            VerifyRepositoryCommand::class.java to VerifyRepositoryUseCase(components.importer),
            GetExistingRepositoryBranchesQuery::class.java to GetExistingRepositoryBranchesUseCase(components.repositoryDatabase, components.importer),
            GetFilesListForExistingRepositoryQuery::class.java to GetFilesListForExistingRepositoryUseCase(components.getFilesProcess, components.repositoryDatabase),
            GetFileContentForExistingRepositoryQuery::class.java to GetFileContentForExistingRepositoryUseCase(components.getFileProcess, components.repositoryDatabase),
            GetMethodsForExistingRepositoryQuery::class.java to GetMethodsForExistingRepositoryUseCase(components.repositoryDatabase, components.getMethodsProcess),
            GetSpacesWithMacroQuery::class.java to GetSpacesWithMacroUseCase(components.spaceManager, components.pageManager, components.macroSettingsDatabase, components.repositoryDatabase, components.globsForMacroDatabase, components.pageMacroExtractor),
            GetCommitHistoryForFileByMacroIdQuery::class.java to GetCommitHistoryForFileByMacroIdUseCase(components.importer, components.repositoryDatabase, components.macroSettingsCachableDatabase),
            VerifyDocumentationMacroByDocumentationsMacroIdQuery::class.java to VerifyDocumentationMacroByDocumentationsMacroIdUseCase(components.refreshProcess, components.macroSettingsDatabase, components.globsForMacroDatabase, components.repositoryDatabase, components.extractorDataDatabase, components.importer),
            GetCommitHistoryForFileByMacroIdQuery::class.java to GetCommitHistoryForFileByMacroIdUseCase(components.importer, components.repositoryDatabase, components.macroSettingsCachableDatabase),
            RemoveUnusedDataCommand::class.java to RemoveUnusedDataUseCase(components.macroSettingsDatabase, components.repositoryDatabase, components.predefinedRepositoryDatabase, components.globsForMacroDatabase, components.extractorDataDatabase, components.getAllMacrosInSystemProcess)
    )

    override fun <T : Any, R : BackendRequest<T>> getUseCaseForRequest(request: R): UseCase<R, T> {
        @Suppress("UNCHECKED_CAST")
        return (executors[request.javaClass]
                ?: throw IllegalArgumentException("Request of type ${request.javaClass.simpleName} has no UseCase assigned"))
                as UseCase<R, T>
    }

}