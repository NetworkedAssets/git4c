package com.networkedassets.git4c.application

import com.networkedassets.git4c.core.bussiness.ConverterPlugin
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.common.IdentifierGenerator
import com.networkedassets.git4c.core.datastore.DocumentsViewCache
import com.networkedassets.git4c.core.datastore.MacroSettingsCache
import com.networkedassets.git4c.core.datastore.MacroSettingsRepository
import com.networkedassets.git4c.core.datastore.TemporaryIdCache
import com.networkedassets.git4c.core.process.GetBranchesProcess
import com.networkedassets.git4c.core.process.RefreshMacroProcess
import com.networkedassets.git4c.core.usecase.PluginUseCasesProvider
import com.networkedassets.git4c.delivery.executor.execution.BackendDispatcher
import com.networkedassets.git4c.delivery.executor.result.LocalGateway
import com.networkedassets.git4c.delivery.executor.UseCasesExecutor
import javax.ws.rs.core.Response

class PluginComponents(
        val importer: SourcePlugin,
        val converter: ConverterPlugin,
        val documentsViewCache: DocumentsViewCache,
        val macroSettingsCache: MacroSettingsCache,
        val macroSettingsRepository: MacroSettingsRepository,
        val idGenerator: IdentifierGenerator,
        val temporaryIdCache: TemporaryIdCache
) {
    val refreshProcess = RefreshMacroProcess(documentsViewCache, importer, converter, macroSettingsRepository)
    val getBranchesProcess = GetBranchesProcess(importer)
    val executor: UseCasesExecutor = UseCasesExecutor(PluginUseCasesProvider(this))
    val dispatcherHttp: BackendDispatcher<Response, Response> = LocalGateway<Response, Response>(executor)
    val dispatcher: BackendDispatcher<Any, Throwable> = LocalGateway<Any, Throwable>(executor)
}