package com.networkedassets.git4c.application

import com.networkedassets.git4c.core.bussiness.ConverterPlugin
import com.networkedassets.git4c.core.bussiness.ParserPlugin
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.common.IdentifierGenerator
import com.networkedassets.git4c.core.common.UnifiedDataStore
import com.networkedassets.git4c.core.datastore.MacroSettingsProvider
import com.networkedassets.git4c.core.datastore.cache.DocumentsViewCache
import com.networkedassets.git4c.core.datastore.cache.MacroSettingsCache
import com.networkedassets.git4c.core.datastore.cache.TemporaryIdCache
import com.networkedassets.git4c.core.datastore.encryptors.RepositoryEncryptor
import com.networkedassets.git4c.core.datastore.repositories.*
import com.networkedassets.git4c.core.process.*
import com.networkedassets.git4c.core.usecase.PluginUseCasesProvider
import com.networkedassets.git4c.delivery.executor.UseCasesExecutor
import com.networkedassets.git4c.delivery.executor.execution.BackendDispatcher
import com.networkedassets.git4c.delivery.executor.LocalGateway
import javax.ws.rs.core.Response

class PluginComponents(
        val importer: SourcePlugin,
        val converter: ConverterPlugin,
        val documentsViewCache: DocumentsViewCache,
        val macroSettingsCache: MacroSettingsCache,
        val temporaryIdCache: TemporaryIdCache,
        val macroSettingsDatabase: MacroSettingsDatabase,
        val globsForMacroDatabase: GlobForMacroDatabase,
        val predefinedRepositoryDatabase: PredefinedRepositoryDatabase,
        val encryptedRepositoryDatabase: EncryptedRepositoryDatabase,
        val repositoryEncryptor: RepositoryEncryptor,
        val idGenerator: IdentifierGenerator,
        val predefinedGlobsDatabase: PredefinedGlobsDatabase,
        val parser: ParserPlugin
) {
    val macroSettingsCachableDatabase = MacroSettingsProvider(
            UnifiedDataStore(
                    macroSettingsDatabase,
                    macroSettingsCache
            ),
            macroSettingsDatabase
    )
    val repositoryDatabase: RepositoryDatabase = RepositoryDatabase(repositoryEncryptor, encryptedRepositoryDatabase)
    val refreshProcess = RefreshMacroProcess(documentsViewCache, importer, converter, parser)
    val getBranchesProcess = GetBranchesProcess(importer)
    val getMethodsProcess = GetMethodsProcess(importer, converter, parser)
    val getFilesProcess = GetFilesProcess(importer)
    val getFileProcess = GetFileProcess(importer, converter)
    val executor: UseCasesExecutor = UseCasesExecutor(PluginUseCasesProvider(this))
    val dispatcherHttp: BackendDispatcher<Response, Response> = LocalGateway<Response, Response>(executor)
    val dispatcher: BackendDispatcher<Any, Throwable> = LocalGateway<Any, Throwable>(executor)
}