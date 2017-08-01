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
            GetDocumentationsMacroByDocumentationsMacroIdQuery::class.java to GetDocumentationsMacroByDocumentationsMacroIdUseCase(components.refreshProcess, components.macroSettingsRepository),
            GetDocumentationsContentTreeByDocumentationsMacroIdQuery::class.java to GetDocumentationsContentTreeByDocumentationsMacroIdUseCase(components.documentsViewCache),
            CreateDocumentationsMacroCommand::class.java to CreateDocumentationsMacroUseCase(components.macroSettingsRepository, components.importer, components.converter, components.idGenerator),
            RefreshDocumentationsMacroCommand::class.java to RefreshDocumentationsMacroUseCase(components.refreshProcess, components.macroSettingsRepository, components.documentsViewCache),
            GetDocumentItemInDocumentationsMacroQuery::class.java to GetDocumentItemInDocumentationsMacroUseCase(components.documentsViewCache),
            GetBranchesByDocumentationsMacroIdQuery::class.java to GetBranchesByDocumentationsMacroIdUseCase(components.getBranchesProcess, components.macroSettingsRepository),
            CreateTemporaryDocumentationsContentCommand::class.java to CreateTemporaryDocumentationsContentUseCase(components.macroSettingsCache, components.documentsViewCache, components.converter, components.macroSettingsRepository, components.idGenerator, components.temporaryIdCache, components.refreshProcess),
            RemoveAllDataCommand::class.java to RemoveAllDataUseCase(components.macroSettingsRepository, components.documentsViewCache),
            GetBranchesQuery::class.java to GetBranchesUseCase(components.importer),
            HealthCheckCommand::class.java to HealthCheckUseCase(),
            GetAllDocumentsByDocumentationsMacroIdQuery::class.java to GetAllDocumentsByDocumentationsMacroIdUseCase(components.refreshProcess, components.macroSettingsRepository)
    )

    override fun <T : Any, R : BackendRequest<T>> getUseCaseForRequest(request: R): UseCase<R, T> {
        @Suppress("UNCHECKED_CAST")
        return (executors[request.javaClass]
                ?: throw IllegalArgumentException("Request of type ${request.javaClass.simpleName} has no UseCase assigned"))
                as UseCase<R, T>
    }

}