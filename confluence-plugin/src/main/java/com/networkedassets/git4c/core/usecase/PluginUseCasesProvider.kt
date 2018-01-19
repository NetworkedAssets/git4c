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
            GetDocumentationsMacroByDocumentationsMacroIdQuery::class.java to GetDocumentationsMacroByDocumentationsMacroIdUseCase(components.extractorDataDatabase, components.checkUserPermissionProcess, components.macroSettingsCachableDatabase, components.repositoryDatabase, components.documentsViewCache, components.macroViewProcess, components.macroViewCache, components.importer),
            GetDocumentationsContentTreeByDocumentationsMacroIdQuery::class.java to GetDocumentationsContentTreeByDocumentationsMacroIdUseCase(components.documentsViewCache, components.checkUserPermissionProcess),
            CreateDocumentationsMacroCommand::class.java to CreateDocumentationsMacroUseCase(components.macroSettingsCachableDatabase, components.repositoryDatabase, components.globsForMacroProvider, components.predefinedRepositoryDatabase, components.extractorDataDatabase, components.importer, components.converter, components.idGenerator, components.pluginSettings, components.repositoryUsageDatabase, components.createMacroProcess),
            RefreshDocumentationsMacroCommand::class.java to RefreshDocumentationsMacroUseCase(components.extractorDataDatabase, components.checkUserPermissionProcess, components.macroSettingsDatabase, components.repositoryDatabase, components.documentsViewCache, components.macroViewProcess, components.macroViewCache, components.importer),
            GetDocumentItemInDocumentationsMacroQuery::class.java to GetDocumentItemInDocumentationsMacroUseCase(components.documentsViewCache, components.checkUserPermissionProcess, components.documentItemCache, components.macroViewProcess, components.macroViewCache, components.macroSettingsCachableDatabase, components.repositoryDatabase, components.documentToBeConvertedLockCache, components.converterAction),
            GetBranchesByDocumentationsMacroIdQuery::class.java to GetBranchesByDocumentationsMacroIdUseCase(components.getBranchesProcess, components.macroSettingsCachableDatabase, components.repositoryDatabase, components.checkUserPermissionProcess),
            CreateTemporaryDocumentationsContentCommand::class.java to CreateTemporaryDocumentationsContentUseCase(components.macroSettingsCache, components.documentsViewCache, components.converter, components.macroSettingsCachableDatabase, components.repositoryDatabase, components.idGenerator, components.temporaryIdCache, components.checkUserPermissionProcess, components.macroViewProcess, components.macroLocationDatabase, components.globsForMacroProvider, components.globForMacroCache),
            RemoveAllDataCommand::class.java to RemoveAllDataUseCase(components.documentsViewCache, components.macroSettingsCachableDatabase, components.globsForMacroProvider, components.repositoryDatabase, components.predefinedRepositoryDatabase, components.predefinedGlobsDatabase, components.idGenerator),
            GetBranchesQuery::class.java to GetBranchesForRepositoryUseCase(components.importer),
            HealthCheckCommand::class.java to HealthCheckUseCase(),
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
            GetDocumentationsDefaultBranchByDocumentationsMacroIdQuery::class.java to GetDocumentationsDefaultBranchByDocumentationsMacroIdUseCase(components.importer, components.documentsViewCache, components.repositoryDatabase, components.macroSettingsCachableDatabase, components.checkUserPermissionProcess, components.macroLocationDatabase, components.temporaryEditBranchesDatabase),
            GetGlobsByDocumentationsMacroIdQuery::class.java to GetGlobsByDocumentationsMacroIdUseCase(components.globsForMacroProvider, components.predefinedGlobsDatabase, components.checkUserPermissionProcess),
            CreatePredefinedGlobCommand::class.java to CreatePredefinedGlobUseCase(components.predefinedGlobsDatabase, components.idGenerator),
            DeleteAllPredefinedGlobsCommand::class.java to DeleteAllPredefinedGlobsUseCase(components.predefinedGlobsDatabase),
            DeletePredefinedGlobByIdCommand::class.java to DeletePredefinedGlobByIdUseCase(components.predefinedGlobsDatabase),
            GetAllPredefinedGlobsQuery::class.java to GetAllPredefinedGlobsUseCase(components.predefinedGlobsDatabase),
            GetPredefinedGlobByIdQuery::class.java to GetPredefinedGlobByIdUseCase(components.predefinedGlobsDatabase),
            RestoreDefaultPredefinedGlobsCommand::class.java to RestorePredefinedGlobsUseCase(components.predefinedGlobsDatabase, components.idGenerator),
            GetExtractionDataByDocumentationsMacroIdQuery::class.java to GetExtractionDataByDocumentationsMacroIdUseCase(components.macroSettingsCachableDatabase, components.extractorDataDatabase, components.checkUserPermissionProcess),
            VerifyRepositoryCommand::class.java to VerifyRepositoryUseCase(components.importer),
            GetExistingRepositoryBranchesQuery::class.java to GetExistingRepositoryBranchesUseCase(components.repositoryDatabase, components.importer),
            GetFilesListForExistingRepositoryQuery::class.java to GetFilesListForExistingRepositoryUseCase(components.getFilesProcess, components.repositoryDatabase),
            GetFileContentForExistingRepositoryQuery::class.java to GetFileContentForExistingRepositoryUseCase(components.getFileProcess, components.repositoryDatabase),
            GetMethodsForExistingRepositoryQuery::class.java to GetMethodsForExistingRepositoryUseCase(components.repositoryDatabase, components.getMethodsProcess),
            GetLatestRevisionByDocumentationsMacroIdQuery::class.java to GetLatestRevisionByDocumentationsMacroIdUseCase(components.macroSettingsDatabase, components.repositoryDatabase, components.importer, components.documentsViewCache, components.macroViewProcess, components.revisionCache),
            GetSpacesWithMacroQuery::class.java to GetSpacesWithMacroUseCase(components.spaceManager, components.pageManager, components.macroSettingsDatabase, components.repositoryDatabase, components.globsForMacroProvider, components.pageMacroExtractor, components.confluenceQueryExecutor, components.spacesWithMacroComputationCache, components.macroLocationDatabase),
            GetSpacesWithMacroResultRequest::class.java to GetSpacesWithMacroResultUseCase(components.spacesWithMacroComputationCache),
            GetCommitHistoryForFileByMacroIdQuery::class.java to GetCommitHistoryForFileByMacroIdUseCase(components.importer, components.repositoryDatabase, components.macroSettingsCachableDatabase, components.checkUserPermissionProcess),
            VerifyDocumentationMacroByDocumentationsMacroIdQuery::class.java to VerifyDocumentationMacroByDocumentationsMacroIdUseCase(components.refreshProcess, components.macroSettingsDatabase, components.globsForMacroProvider, components.repositoryDatabase, components.extractorDataDatabase, components.importer),
            GetCommitHistoryForFileByMacroIdQuery::class.java to GetCommitHistoryForFileByMacroIdUseCase(components.importer, components.repositoryDatabase, components.macroSettingsCachableDatabase, components.checkUserPermissionProcess),
            RemoveUnusedDataCommand::class.java to RemoveUnusedDataUseCase(components.macroSettingsDatabase, components.repositoryDatabase, components.predefinedRepositoryDatabase, components.globsForMacroProvider, components.extractorDataDatabase, components.getAllMacrosInSystemProcess),
            ForceUsersToUsePredefinedRepositoriesCommand::class.java to ForceUsersToUsePredefinedRepositoriesUseCase(components.pluginSettings),
            GetRepositoryUsagesForUserQuery::class.java to GetRepositoryUsagesForUserUseCase(components.repositoryUsageDatabase),
            GetForceUsersToUsePredefinedRepositoriesSettingQuery::class.java to GetForceUsersToUsePredefinedRepositoriesSettingUseCase(components.pluginSettings),
            ViewMacroCommand::class.java to ViewMacroUseCase(components.macroLocationDatabase, components.macroViewProcess, components.macroSettingsCachableDatabase),
            PublishFileCommand::class.java to PublishFileUseCase(components.importer, components.documentsViewCache, components.macroSettingsCachableDatabase, components.repositoryDatabase, components.userManager, components.publishFileComputationCache, components.revisionCheckExecutor, components.temporaryEditBranchesDatabase, components.macroLocationDatabase),
            PublishFileResultRequest::class.java to PublishFileResultUseCase(components.publishFileComputationCache),
            PreviewFileCommand::class.java to GenerateFilePreviewUseCase(components.importer, components.macroSettingsCachableDatabase, components.repositoryDatabase, components.userManager, components.getFileProcess, components.checkUserPermissionProcess),
            GetExecutorThreadNumbersQuery::class.java to GetExecutorThreadNumbersUseCase(components.revisionCheckExecutor, components.repositoryPullExecutor, components.converterExecutor, components.confluenceQueryExecutor),
            SaveExecutorThreadNumbersQuery::class.java to SaveExecutorThreadNumbersUseCase(components.threadSettingsDatabase, components.revisionCheckExecutor, components.repositoryPullExecutor, components.converterExecutor, components.confluenceQueryExecutor),
            RefreshMacroLocationsCommand::class.java to RefreshMacroLocationsUseCase(components.macroLocationDatabase, components.macroSettingsDatabase, components.repositoryDatabase, components.spaceManager, components.pageManager, components.pageMacroExtractor, components.refreshLocationUseCaseCache, components.confluenceQueryExecutor),
            RefreshMacroLocationsResultCommand::class.java to RefreshMacroLocationsResultUseCase(components.refreshLocationUseCaseCache),
            GetTemporaryEditBranchCommand::class.java to GetTemporaryEditBranchUseCase(components.importer, components.documentsViewCache, components.repositoryDatabase, components.macroSettingsDatabase, components.checkUserPermissionProcess, components.macroLocationDatabase, components.temporaryEditBranchesDatabase, components.temporaryEditBranchResultCache),
            GetTemporaryEditBranchResultCommand::class.java to GetTemporaryEditBranchResultUseCase(components.temporaryEditBranchResultCache),
            SetEditingEnabledQuery::class.java to SetEditingEnabledUseCase(components.pluginSettings)
    )

    override fun <T : Any, R : BackendRequest<T>> getUseCaseForRequest(request: R): UseCase<R, T> {
        @Suppress("UNCHECKED_CAST")
        return (executors[request.javaClass]
                ?: throw IllegalArgumentException("Request of type ${request.javaClass.simpleName} has no UseCase assigned"))
                as UseCase<R, T>
    }

}