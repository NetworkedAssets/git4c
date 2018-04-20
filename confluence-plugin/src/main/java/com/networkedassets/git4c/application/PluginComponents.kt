package com.networkedassets.git4c.application

import com.networkedassets.git4c.boundary.outbound.*
import com.networkedassets.git4c.core.business.*
import com.networkedassets.git4c.core.bussiness.ComputationCache
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
        val database: DatabasePluginComponents,
        val cache: CachePluginComponents,
        val async: ResultsCachePluginComponents,
        val macro: MacroPluginComponents,
        val utils: UtilitiesPluginComponents,
        val executors: ExecutorsPluginComponents,
        val providers: ProvidersPluginComponents = ProvidersPluginComponents(database, cache, utils),
        val processing: ProcessesPluginComponents = ProcessesPluginComponents(database, cache, async, macro, utils, executors, providers),
        val bussines: BussinesPluginComponents = BussinesPluginComponents(database, cache, async, macro, utils, executors, providers, processing),
        val dispatching: DispatchingPluginComponents = DispatchingPluginComponents(bussines)
)

class DatabasePluginComponents(
        val macroSettingsDatabase: MacroSettingsDatabase,
        val globsForMacroDatabase: GlobForMacroDatabase,
        val predefinedRepositoryDatabase: PredefinedRepositoryDatabase,
        val encryptedRepositoryDatabase: EncryptedRepositoryDatabase,
        val extractorDataDatabase: ExtractorDataDatabase,
        val predefinedGlobsDatabase: PredefinedGlobsDatabase,
        val macroLocationDatabase: MacroLocationDatabase,
        val pluginSettings: PluginSettingsDatabase,
        val repositoryUsageDatabase: RepositoryUsageDatabase,
        val threadSettingsDatabase: ThreadSettingsDatabase,
        val temporaryEditBranchesDatabase: TemporaryEditBranchesDatabase
)

class CachePluginComponents(
        val documentsViewCache: DocumentsViewCache,
        val documentItemCache: DocumentItemCache,
        val macroSettingsCache: MacroSettingsCache,
        val temporaryIdCache: TemporaryIdCache,
        val macroViewCache: MacroToBeViewedPrepareLockCache,
        val revisionCache: RepositoryRevisionCache,
        val pageAndSpacePermissionsForUserCache: PageAndSpacePermissionsForUserCache,
        val globForMacroCache: GlobForMacroCache
)


class ResultsCachePluginComponents(
        val documentToBeConvertedLockCache: DocumentToBeConvertedLockCache,
        val refreshLocationUseCaseCache: ComputationCache<Unit>,
        val publishFileComputationCache: ComputationCache<Unit>,
        val spacesWithMacroComputationCache: ComputationCache<Spaces>,
        val temporaryEditBranchResultCache: ComputationCache<TemporaryBranch>,
        val createDocumentationMacroUseCaseCache: ComputationCache<SavedDocumentationsMacro>,
        val createPredefinedRepositoryUseCaseCache: ComputationCache<SavedPredefinedRepository>,
        val generateFilePreviewUseCaseCache: ComputationCache<FileContent>,
        val getBranchesByDocumentationsMacroIdUseCaseCache: ComputationCache<Branches>,
        val getBranchesForRepositoryUseCaseCache: ComputationCache<Branches>,
        val getCommitHistoryForFileByMacroIdUseCaseCache: ComputationCache<Commits>,
        val getExistingRepositoryBranchesUseCaseCache: ComputationCache<Branches>,
        val getFileContentForExistingRepositoryUseCaseCache: ComputationCache<FileContent>,
        val getFileContentForPredefinedRepositoryUseCaseCache: ComputationCache<FileContent>,
        val getFileContentForRepositoryUseCaseCache: ComputationCache<FileContent>,
        val getFilesListForExistingRepositoryUseCaseCache: ComputationCache<FilesList>,
        val getFilesListForPredefinedRepositoryUseCaseCache: ComputationCache<FilesList>,
        val getFilesListForRepositoryUseCaseCache: ComputationCache<FilesList>,
        val getLatestRevisionByDocumentationsMacroIdUseCaseCache: ComputationCache<Revision>,
        val getMethodsForExistingRepositoryUseCaseCache: ComputationCache<Methods>,
        val getMethodsForPredefinedRepositoryUseCaseCache: ComputationCache<Methods>,
        val getMethodsForRepositoryUseCaseCache: ComputationCache<Methods>,
        val getPredefinedRepositoryBranchesUseCaseCache: ComputationCache<Branches>,
        val getPredefinedRepositoryUseCaseCache: ComputationCache<PredefinedRepository>,
        val modifyPredefinedRepositoryUseCaseCache: ComputationCache<SavedPredefinedRepository>,
        val verifyDocumentationMacroByDocumentationsMacroIdUseCaseCache: ComputationCache<String>,
        val verifyRepositoryUseCaseCache: ComputationCache<VerificationInfo>
)

class MacroPluginComponents(
        val importer: SourcePlugin,
        val converter: ConverterPlugin,
        val fileIgnorer: FileIgnorer,
        val parser: ParserPlugin,
        val pageBuilder: ErrorPageBuilder,
        val pageMacroExtractor: PageMacroExtractor
)

class UtilitiesPluginComponents(
        val repositoryEncryptor: RepositoryEncryptor,
        val idGenerator: IdentifierGenerator,
        val spaceManager: SpaceManager,
        val pageManager: PageManager,
        val permissionChecker: PermissionChecker,
        val userManager: UserManager
)

class ExecutorsPluginComponents(
        val revisionCheckExecutor: RevisionCheckExecutorHolder,
        val repositoryPullExecutor: RepositoryPullExecutorHolder,
        val converterExecutor: ConverterExecutorHolder,
        val confluenceQueryExecutor: ConfluenceQueryExecutorHolder
)


class ProvidersPluginComponents(
        database: DatabasePluginComponents,
        cache: CachePluginComponents,
        utils: UtilitiesPluginComponents,
        val macroSettingsProvider: MacroSettingsProvider = MacroSettingsProvider(
                UnifiedDataStore(database.macroSettingsDatabase, cache.macroSettingsCache),
                database.macroSettingsDatabase
        ),
        val globsForMacroProvider: GlobForMacroProvider = GlobForMacroProvider(database.globsForMacroDatabase, cache.globForMacroCache),
        val repositoryProvider: RepositoryDatabase = RepositoryDatabase(utils.repositoryEncryptor, database.encryptedRepositoryDatabase)
)

class BussinesPluginComponents(
        val database: DatabasePluginComponents,
        val cache: CachePluginComponents,
        val async: ResultsCachePluginComponents,
        val macro: MacroPluginComponents,
        val utils: UtilitiesPluginComponents,
        val executors: ExecutorsPluginComponents,
        val providers: ProvidersPluginComponents = ProvidersPluginComponents(database, cache, utils),
        val processing: ProcessesPluginComponents = ProcessesPluginComponents(database, cache, async, macro, utils, executors, providers)
)

class ProcessesPluginComponents(
        database: DatabasePluginComponents,
        cache: CachePluginComponents,
        async: ResultsCachePluginComponents,
        macro: MacroPluginComponents,
        utils: UtilitiesPluginComponents,
        executors: ExecutorsPluginComponents,
        providers: ProvidersPluginComponents,
        val extractContentProcess: ExtractContentProcess = ExtractContentProcess(macro.parser),
        val revisionCheckAction: RevisionCheckAction = RevisionCheckAction(executors.revisionCheckExecutor, macro.importer, cache.revisionCache, providers.macroSettingsProvider, providers.repositoryProvider, providers.globsForMacroProvider, cache.documentsViewCache),
        val indexDocumentAction: IndexDocumentsAction = IndexDocumentsAction(cache.documentItemCache, macro.fileIgnorer),
        val repositoryPullAction: PullRepositoryAction = PullRepositoryAction(executors.repositoryPullExecutor, providers.macroSettingsProvider, providers.repositoryProvider, providers.globsForMacroProvider, database.extractorDataDatabase, macro.importer, indexDocumentAction, cache.documentsViewCache, cache.revisionCache),
        val converterAction: ConvertDocumentItemAction = ConvertDocumentItemAction(macro.importer, providers.macroSettingsProvider, providers.repositoryProvider, async.documentToBeConvertedLockCache, executors.converterExecutor, cache.documentItemCache, macro.pageBuilder, extractContentProcess, database.extractorDataDatabase, macro.converter),
        val refreshProcess: RefreshMacroAction = RefreshMacroAction(cache.documentsViewCache, macro.importer, cache.revisionCache, providers.globsForMacroProvider, providers.macroSettingsProvider, providers.repositoryProvider, revisionCheckAction, repositoryPullAction),
        val createMacroProcess: CreateMacroProcess = CreateMacroProcess(cache.macroViewCache, macro.importer, executors.repositoryPullExecutor),
        val macroViewProcess: MacroViewProcess = MacroViewProcess(cache.macroViewCache, refreshProcess, providers.macroSettingsProvider, providers.repositoryProvider, cache.documentsViewCache),
        val getBranchesProcess: GetBranchesProcess = GetBranchesProcess(macro.importer),
        val getMethodsProcess: GetMethodsProcess = GetMethodsProcess(macro.importer, macro.converter, macro.parser, extractContentProcess),
        val getFilesProcess: GetFilesProcess = GetFilesProcess(macro.importer, macro.fileIgnorer),
        val getFileProcess: GetFileProcess = GetFileProcess(macro.importer, macro.converter, extractContentProcess),
        val checkUserPermissionProcess: CheckUserPermissionProcess = CheckUserPermissionProcess(database.macroLocationDatabase, utils.permissionChecker, cache.pageAndSpacePermissionsForUserCache),
        val getAllMacrosInSystemProcess: GetAllMacrosInSystem = GetAllMacrosInSystem(utils.pageManager, macro.pageMacroExtractor)
)

class DispatchingPluginComponents(
        bussines: BussinesPluginComponents,
        val pluginUseCases: PluginUseCasesProvider = PluginUseCasesProvider(bussines),
        val executor: UseCasesExecutor = UseCasesExecutor(pluginUseCases),
        val dispatcher: BackendDispatcher<Any, Throwable> = LocalGateway<Any, Throwable>(executor),
        val dispatcherHttp: BackendDispatcher<Response, Response> = LocalGateway<Response, Response>(executor)
)