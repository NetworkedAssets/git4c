package com.networkedassets.git4c.core.usecase

import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.*
import com.networkedassets.git4c.core.*
import com.networkedassets.git4c.delivery.executor.execution.UseCase
import com.networkedassets.git4c.delivery.executor.execution.UseCasesProvider
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

class PluginUseCasesProvider(
        bussines: BussinesPluginComponents
) : UseCasesProvider {

    val executors: MutableMap<Class<*>, UseCase<*, *>> = mutableMapOf(
            GetDocumentationsMacroViewTemplateQuery::class.java to GetDocumentationsMacroViewTemplateUseCase(bussines),
            GetDocumentationsMacroByDocumentationsMacroIdQuery::class.java to GetDocumentationsMacroByDocumentationsMacroIdUseCase(bussines),
            GetDocumentationsContentTreeByDocumentationsMacroIdQuery::class.java to GetDocumentationsContentTreeByDocumentationsMacroIdUseCase(bussines),
            CreateDocumentationsMacroCommand::class.java to CreateDocumentationsMacroUseCase(bussines),
            CreateDocumentationsMacroResultRequest::class.java to CreateDocumentationsMacroResultUseCase(bussines),
            RefreshDocumentationsMacroCommand::class.java to RefreshDocumentationsMacroUseCase(bussines),
            GetDocumentItemInDocumentationsMacroQuery::class.java to GetDocumentItemInDocumentationsMacroUseCase(bussines),
            GetBranchesByDocumentationsMacroIdQuery::class.java to GetBranchesByDocumentationsMacroIdUseCase(bussines),
            GetBranchesByDocumentationsMacroIdResultRequest::class.java to GetBranchesByDocumentationsMacroIdResultUseCase(bussines),
            CreateTemporaryDocumentationsContentCommand::class.java to CreateTemporaryDocumentationsContentUseCase(bussines),
            RemoveAllDataCommand::class.java to RemoveAllDataUseCase(bussines),
            GetBranchesQuery::class.java to GetBranchesForRepositoryUseCase(bussines),
            GetBranchesResultRequest::class.java to GetBranchesForRepositoryResultUseCase(bussines),
            HealthCheckCommand::class.java to HealthCheckUseCase(bussines),
            CreatePredefinedRepositoryCommand::class.java to CreatePredefinedRepositoryUseCase(bussines),
            CreatePredefinedRepositoryResultRequest::class.java to CreatePredefinedRepositoryResultUseCase(bussines),
            GetAllPredefinedRepositoriesCommand::class.java to GetAllPredefinedRepositoriesUseCase(bussines),
            GetPredefinedRepositoryCommand::class.java to GetPredefinedRepositoryUseCase(bussines),
            GetPredefinedRepositoryResultRequest::class.java to GetPredefinedRepositoryResultUseCase(bussines),
            ModifyPredefinedRepositoryCommand::class.java to ModifyPredefinedRepositoryUseCase(bussines),
            ModifyPredefinedRepositoryResultRequest::class.java to ModifyPredefinedRepositoryResultUseCase(bussines),
            RemovePredefinedRepositoryCommand::class.java to RemovePredefinedRepositoryUseCase(bussines),
            GetFilesListForRepositoryQuery::class.java to GetFilesListForRepositoryUseCase(bussines),
            GetFilesListForRepositoryResultRequest::class.java to GetFilesListForRepositoryResultUseCase(bussines),
            GetFileContentForRepositoryQuery::class.java to GetFileContentForRepositoryUseCase(bussines),
            GetFileContentForRepositoryResultRequest::class.java to GetFileContentForRepositoryResultUseCase(bussines),
            GetMethodsForRepositoryQuery::class.java to GetMethodsForRepositoryUseCase(bussines),
            GetMethodsForRepositoryResultRequest::class.java to GetMethodsForRepositoryResultUseCase(bussines),
            GetMethodsForPredefinedRepositoryQuery::class.java to GetMethodsForPredefinedRepositoryUseCase(bussines),
            GetMethodsForPredefinedRepositoryResultRequest::class.java to GetMethodsForPredefinedRepositoryResultUseCase(bussines),
            GetFilesListForPredefinedRepositoryQuery::class.java to GetFilesListForPredefinedRepositoryUseCase(bussines),
            GetFilesListForPredefinedRepositoryResultRequest::class.java to GetFilesListForPredefinedRepositoryResultUseCase(bussines),
            GetFileContentForPredefinedRepositoryQuery::class.java to GetFileContentForPredefinedRepositoryUseCase(bussines),
            GetFileContentForPredefinedRepositoryResultRequest::class.java to GetFileContentForPredefinedRepositoryResultUseCase(bussines),
            GetPredefinedRepositoryBranchesQuery::class.java to GetPredefinedRepositoryBranchesUseCase(bussines),
            GetPredefinedRepositoryBranchesResultRequest::class.java to GetPredefinedRepositoryBranchesResultUseCase(bussines),
            GetDocumentationsDefaultBranchByDocumentationsMacroIdQuery::class.java to GetDocumentationsDefaultBranchByDocumentationsMacroIdUseCase(bussines),
            GetGlobsByDocumentationsMacroIdQuery::class.java to GetGlobsByDocumentationsMacroIdUseCase(bussines),
            CreatePredefinedGlobCommand::class.java to CreatePredefinedGlobUseCase(bussines),
            DeleteAllPredefinedGlobsCommand::class.java to DeleteAllPredefinedGlobsUseCase(bussines),
            DeletePredefinedGlobByIdCommand::class.java to DeletePredefinedGlobByIdUseCase(bussines),
            GetAllPredefinedGlobsQuery::class.java to GetAllPredefinedGlobsUseCase(bussines),
            RestoreDefaultPredefinedGlobsCommand::class.java to RestorePredefinedGlobsUseCase(bussines),
            GetExtractionDataByDocumentationsMacroIdQuery::class.java to GetExtractionDataByDocumentationsMacroIdUseCase(bussines),
            VerifyRepositoryCommand::class.java to VerifyRepositoryUseCase(bussines),
            VerifyRepositoryResultRequest::class.java to VerifyRepositoryResultUseCase(bussines),
            GetExistingRepositoryBranchesQuery::class.java to GetExistingRepositoryBranchesUseCase(bussines),
            GetExistingRepositoryBranchesResultRequest::class.java to GetExistingRepositoryBranchesResultUseCase(bussines),
            GetFilesListForExistingRepositoryQuery::class.java to GetFilesListForExistingRepositoryUseCase(bussines),
            GetFilesListForExistingRepositoryResultRequest::class.java to GetFilesListForExistingRepositoryResultUseCase(bussines),
            GetFileContentForExistingRepositoryQuery::class.java to GetFileContentForExistingRepositoryUseCase(bussines),
            GetFileContentForExistingRepositoryResultRequest::class.java to GetFileContentForExistingRepositoryResultUseCase(bussines),
            GetMethodsForExistingRepositoryQuery::class.java to GetMethodsForExistingRepositoryUseCase(bussines),
            GetMethodsForExistingRepositoryResultRequest::class.java to GetMethodsForExistingRepositoryResultUseCase(bussines),
            GetLatestRevisionByDocumentationsMacroIdQuery::class.java to GetLatestRevisionByDocumentationsMacroIdUseCase(bussines),
            GetLatestRevisionByDocumentationsMacroIdResultRequest::class.java to GetLatestRevisionByDocumentationsMacroIdResultUseCase(bussines),
            GetSpacesWithMacroQuery::class.java to GetSpacesWithMacroUseCase(bussines),
            GetSpacesWithMacroResultRequest::class.java to GetSpacesWithMacroResultUseCase(bussines),
            GetCommitHistoryForFileByMacroIdQuery::class.java to GetCommitHistoryForFileByMacroIdUseCase(bussines),
            GetCommitHistoryForFileResultRequest::class.java to GetCommitHistoryForFileByMacroIdResultUseCase(bussines),
            VerifyDocumentationMacroByDocumentationsMacroIdQuery::class.java to VerifyDocumentationMacroByDocumentationsMacroIdUseCase(bussines),
            VerifyDocumentationMacroByDocumentationsMacroIdResultRequest::class.java to VerifyDocumentationMacroByDocumentationsMacroIdResultUseCase(bussines),
            RemoveUnusedDataCommand::class.java to RemoveUnusedDataUseCase(bussines),
            ForceUsersToUsePredefinedRepositoriesCommand::class.java to ForceUsersToUsePredefinedRepositoriesUseCase(bussines),
            GetRepositoryUsagesForUserQuery::class.java to GetRepositoryUsagesForUserUseCase(bussines),
            GetForceUsersToUsePredefinedRepositoriesSettingQuery::class.java to GetForceUsersToUsePredefinedRepositoriesSettingUseCase(bussines),
            ViewMacroCommand::class.java to ViewMacroUseCase(bussines),
            PublishFileCommand::class.java to PublishFileUseCase(bussines),
            PublishFileResultRequest::class.java to PublishFileResultUseCase(bussines),
            PreviewFileCommand::class.java to GenerateFilePreviewUseCase(bussines),
            PreviewFileResultRequest::class.java to GenerateFilePreviewResultUseCase(bussines),
            GetExecutorThreadNumbersQuery::class.java to GetExecutorThreadNumbersUseCase(bussines),
            SaveExecutorThreadNumbersQuery::class.java to SaveExecutorThreadNumbersUseCase(bussines),
            RefreshMacroLocationsCommand::class.java to RefreshMacroLocationsUseCase(bussines),
            RefreshMacroLocationsResultCommand::class.java to RefreshMacroLocationsResultUseCase(bussines),
            GetTemporaryEditBranchCommand::class.java to GetTemporaryEditBranchUseCase(bussines),
            GetTemporaryEditBranchResultCommand::class.java to GetTemporaryEditBranchResultUseCase(bussines),
            GetPredefinedGlobByIdQuery::class.java to GetPredefinedGlobByIdUseCase(bussines),
            GetRepositoryInfoForMacroCommand::class.java to GetRepositoryInfoForMacroUseCase(bussines)
    )

    override fun <T : Any, R : BackendRequest<T>> getUseCaseForRequest(request: R): UseCase<R, T> {
        @Suppress("UNCHECKED_CAST")
        return (executors[request.javaClass]
                ?: throw IllegalArgumentException("Request of type ${request.javaClass.simpleName} has no UseCase assigned"))
                as UseCase<R, T>
    }

}