package com.networkedassets.git4c.application

import com.networkedassets.git4c.core.business.*
import com.networkedassets.git4c.core.bussiness.ConverterPlugin
import com.networkedassets.git4c.core.bussiness.ParserPlugin
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.common.IdentifierGenerator
import com.networkedassets.git4c.core.common.PermissionChecker
import com.networkedassets.git4c.core.common.UnifiedDataStore
import com.networkedassets.git4c.core.datastore.GlobForMacroProvider
import com.networkedassets.git4c.core.datastore.MacroSettingsProvider
import com.networkedassets.git4c.core.datastore.cache.*
import com.networkedassets.git4c.core.datastore.encryptors.RepositoryEncryptor
import com.networkedassets.git4c.core.datastore.repositories.*
import com.networkedassets.git4c.core.process.*
import com.networkedassets.git4c.core.process.action.*
import com.networkedassets.git4c.core.usecase.PluginUseCasesProvider
import com.networkedassets.git4c.delivery.executor.LocalGateway
import com.networkedassets.git4c.delivery.executor.UseCasesExecutor
import com.networkedassets.git4c.delivery.executor.execution.BackendDispatcher
import javax.ws.rs.core.Response

class PluginComponents(
        val importer: SourcePlugin,
        val converter: ConverterPlugin,
        val documentsViewCache: DocumentsViewCache,
        val documentItemCache: DocumentItemCache,
        val macroSettingsCache: MacroSettingsCache,
        val temporaryIdCache: TemporaryIdCache,
        val macroViewCache: MacroToBeViewedPrepareLockCache,
        val revisionCache: RepositoryRevisionCache,
        val pageAndSpacePermissionsForUserCache: PageAndSpacePermissionsForUserCache,
        val documentToBeConvertedLockCache: DocumentToBeConvertedLockCache,
        val refreshLocationUseCaseCache: RefreshLocationUseCaseCache,
        val macroSettingsDatabase: MacroSettingsDatabase,
        val globsForMacroDatabase: GlobForMacroDatabase,
        val predefinedRepositoryDatabase: PredefinedRepositoryDatabase,
        val encryptedRepositoryDatabase: EncryptedRepositoryDatabase,
        val extractorDataDatabase: ExtractorDataDatabase,
        val repositoryEncryptor: RepositoryEncryptor,
        val idGenerator: IdentifierGenerator,
        val predefinedGlobsDatabase: PredefinedGlobsDatabase,
        val parser: ParserPlugin,
        val pageBuilder: ErrorPageBuilder,
        val spaceManager: SpaceManager,
        val pageManager: PageManager,
        val pageMacroExtractor: PageMacroExtractor,
        val permissionChecker: PermissionChecker,
        val macroLocationDatabase: MacroLocationDatabase,
        val userManager: UserManager,
        val pluginSettings: PluginSettingsDatabase,
        val repositoryUsageDatabase: RepositoryUsageDatabase,
        val revisionCheckExecutor: RevisionCheckExecutorHolder,
        val repositoryPullExecutor: RepositoryPullExecutorHolder,
        val converterExecutor: ConverterExecutorHolder,
        val confluenceQueryExecutor: ConfluenceQueryExecutorHolder,
        val publishFileComputationCache: PublishFileComputationCache,
        val threadSettingsDatabase: ThreadSettingsDatabase,
        val spacesWithMacroComputationCache: SpacesWithMacroResultCache,
        val temporaryEditBranchesDatabase: TemporaryEditBranchesDatabase,
        val globForMacroCache: GlobForMacroCache,
        val temporaryEditBranchResultCache: TemporaryEditBranchResultCache
) {
    val macroSettingsCachableDatabase = MacroSettingsProvider(
            UnifiedDataStore(
                    macroSettingsDatabase,
                    macroSettingsCache
            ),
            macroSettingsDatabase
    )

    val globsForMacroProvider = GlobForMacroProvider(globsForMacroDatabase, globForMacroCache)

    val repositoryDatabase: RepositoryDatabase = RepositoryDatabase(repositoryEncryptor, encryptedRepositoryDatabase)
    val extractContentProcess = ExtractContentProcess(parser)
    val revisionCheckAction = RevisionCheckAction(revisionCheckExecutor, importer, revisionCache, macroSettingsCachableDatabase, repositoryDatabase, globsForMacroProvider, documentsViewCache)
    val indexDocumentAction = IndexDocumentsAction(documentItemCache)
    val repositoryPullAction = PullRepositoryAction(repositoryPullExecutor, macroSettingsCachableDatabase, repositoryDatabase, globsForMacroProvider, extractorDataDatabase, importer, indexDocumentAction, documentsViewCache)
    val converterAction = ConvertDocumentItemAction(importer, macroSettingsCachableDatabase, repositoryDatabase, documentToBeConvertedLockCache, converterExecutor, documentItemCache, pageBuilder, extractContentProcess, extractorDataDatabase, converter)
    val refreshProcess = RefreshMacroAction(documentsViewCache, importer, revisionCache, globsForMacroProvider, macroSettingsCachableDatabase, repositoryDatabase, revisionCheckAction, repositoryPullAction)
    val createMacroProcess = CreateMacroProcess(macroViewCache, importer, repositoryPullExecutor)
    val macroViewProcess = MacroViewProcess(macroViewCache, refreshProcess, macroSettingsCachableDatabase, repositoryDatabase, documentsViewCache)
    val getBranchesProcess = GetBranchesProcess(importer)
    val getMethodsProcess = GetMethodsProcess(importer, converter, parser, extractContentProcess)
    val getFilesProcess = GetFilesProcess(importer)
    val getFileProcess = GetFileProcess(importer, converter, extractContentProcess)
    val checkUserPermissionProcess = CheckUserPermissionProcess(macroLocationDatabase, permissionChecker, pageAndSpacePermissionsForUserCache)
    val getAllMacrosInSystemProcess = GetAllMacrosInSystem(pageManager, pageMacroExtractor)
    val executor: UseCasesExecutor = UseCasesExecutor(PluginUseCasesProvider(this))
    val dispatcherHttp: BackendDispatcher<Response, Response> = LocalGateway<Response, Response>(executor)
    val dispatcher: BackendDispatcher<Any, Throwable> = LocalGateway<Any, Throwable>(executor)
}