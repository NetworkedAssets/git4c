package com.networkedassets.git4c.utils

import com.atlassian.cache.memory.MemoryCacheManager
import com.networkedassets.git4c.application.*
import com.networkedassets.git4c.boundary.outbound.*
import com.networkedassets.git4c.core.business.*
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.common.PermissionChecker
import com.networkedassets.git4c.delivery.executor.result.BackendRequest
import com.networkedassets.git4c.infrastructure.AtlassianPageMacroExtractor
import com.networkedassets.git4c.infrastructure.HtmlErrorPageBuilder
import com.networkedassets.git4c.infrastructure.RepositoryDesEncryptor
import com.networkedassets.git4c.infrastructure.UuidIdentifierGenerator
import com.networkedassets.git4c.infrastructure.cache.*
import com.networkedassets.git4c.infrastructure.git.DefaultGitClient
import com.networkedassets.git4c.infrastructure.mocks.core.DirectorySourcePlugin
import com.networkedassets.git4c.infrastructure.mocks.core.database.*
import com.networkedassets.git4c.infrastructure.plugin.converter.ConverterPluginList
import com.networkedassets.git4c.infrastructure.plugin.converter.images.ImageConverterPlugin
import com.networkedassets.git4c.infrastructure.plugin.converter.main.JSoupPostProcessor
import com.networkedassets.git4c.infrastructure.plugin.converter.main.MainConverterPluginList
import com.networkedassets.git4c.infrastructure.plugin.converter.main.asciidoc.AsciidocConverterPlugin
import com.networkedassets.git4c.infrastructure.plugin.converter.main.markdown.MarkdownConverterPlugin
import com.networkedassets.git4c.infrastructure.plugin.converter.plaintext.PlainTextConverterPlugin
import com.networkedassets.git4c.infrastructure.plugin.converter.plantuml.PUMLConverterPlugin
import com.networkedassets.git4c.infrastructure.plugin.converter.prismjs.PrismJSConverterPlugin
import com.networkedassets.git4c.infrastructure.plugin.parser.Parsers
import com.networkedassets.git4c.infrastructure.plugin.source.git.GitSourcePlugin
import org.mockito.Mockito
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

object InMemoryApplication {

    var application = getComponents()

    fun <ANSWER : Any> execute(query: BackendRequest<ANSWER>): CompletableFuture<*> {
        return sendToExecutionAsync(application.dispatching.dispatcher, query)
    }

    fun reset() {
        application = getComponents()
        application.cache.revisionCache.setTime(15000)
    }

    fun switchToGit() {
        application = getComponents(GitSourcePlugin(DefaultGitClient()))
    }

    fun getComponents(source: SourcePlugin = DirectorySourcePlugin()): PluginComponents {

        val cacheManager = MemoryCacheManager()

        val database: DatabasePluginComponents = getDatabaseComponenets()
        val cache: CachePluginComponents = getCacheComponenets(cacheManager)
        val async: ResultsCachePluginComponents = getAsyncComponents(cacheManager)
        val utils: UtilitiesPluginComponents = getUtilsComponents()
        val macro: MacroPluginComponents = getMacroComponents(utils, source)
        val executors: ExecutorsPluginComponents = getExecutorsComponents(database)

        return PluginComponents(
                database,
                cache,
                async,
                macro,
                utils,
                executors
        )

    }


    private fun getExecutorsComponents(database: DatabasePluginComponents): ExecutorsPluginComponents {
        val revisionCheckExecutor = RevisionCheckBaseExecutorHolder(database.threadSettingsDatabase.getRevisionCheckThreadNumber()) { Executors.newScheduledThreadPool(it) }
        val repositoryExecutor = RepositoryPullBaseExecutorHolder(database.threadSettingsDatabase.getRepositoryExecutorThreadNumber()) { Executors.newScheduledThreadPool(it) }
        val converterExecutor = ConverterBaseExecutorHolder(database.threadSettingsDatabase.getConverterExecutorThreadNumber()) { Executors.newScheduledThreadPool(it) }
        val confluenceQueryExecutor = ConfluenceQueryBaseExecutorHolder(database.threadSettingsDatabase.getConfluenceQueryExecutorThreadNumber()) { Executors.newScheduledThreadPool(it) }
        return ExecutorsPluginComponents(
                revisionCheckExecutor,
                repositoryExecutor,
                converterExecutor,
                confluenceQueryExecutor
        )
    }

    private fun getUtilsComponents(): UtilitiesPluginComponents {
        val identifierGenerator = UuidIdentifierGenerator()
        val repositoryEncryptor = RepositoryDesEncryptor()
        val git4cSpaceManager = Mockito.mock(SpaceManager::class.java)
        val git4cPageManager = Mockito.mock(PageManager::class.java)
        val git4cPermissionChecker = Mockito.mock(PermissionChecker::class.java)
        val git4cUserManager = Mockito.mock(UserManager::class.java)
        return UtilitiesPluginComponents(
                repositoryEncryptor,
                identifierGenerator,
                git4cSpaceManager,
                git4cPageManager,
                git4cPermissionChecker,
                git4cUserManager
        )
    }

    private fun getMacroComponents(utils: UtilitiesPluginComponents, sourcePlugin: SourcePlugin): MacroPluginComponents {
        val importer = sourcePlugin
        val postProcessor = JSoupPostProcessor(utils.idGenerator)
        val mainPlugins = MainConverterPluginList(listOf(AsciidocConverterPlugin(), MarkdownConverterPlugin()), postProcessor)
        val converterPlugins = listOf(mainPlugins, PrismJSConverterPlugin(), ImageConverterPlugin(), PUMLConverterPlugin())
        val converter = ConverterPluginList(converterPlugins, PlainTextConverterPlugin())
        val parser = Parsers()
        val pageBuilder = HtmlErrorPageBuilder()
        val pageMacroExtractor = AtlassianPageMacroExtractor()
        return MacroPluginComponents(
                importer,
                converter,
                parser,
                pageBuilder,
                pageMacroExtractor
        )
    }

    private fun getAsyncComponents(cacheManager: MemoryCacheManager): ResultsCachePluginComponents {
        val documentToBeConvertedLockCache = AtlassianDocumentConvertionCache(cacheManager)
        val refreshLocationUseCaseCache = AtlassianComputationCache<Unit>(cacheManager)
        val publishFileComputationCache = AtlassianComputationCache<Unit>(cacheManager)
        val spacesWithMacroComputationCache = AtlassianComputationCache<Spaces>(cacheManager)
        val temporaryEditBranchResultCache = AtlassianComputationCache<TemporaryBranch>(cacheManager)
        val createDocumentationMacroUseCaseCache = AtlassianComputationCache<SavedDocumentationsMacro>(cacheManager)
        val createPredefinedRepositoryUseCaseCache = AtlassianComputationCache<SavedPredefinedRepository>(cacheManager)
        val generateFilePreviewUseCaseCache = AtlassianComputationCache<FileContent>(cacheManager)
        val getBranchesByDocumentationsMacroIdUseCaseCache = AtlassianComputationCache<Branches>(cacheManager)
        val getBranchesForRepositoryUseCaseCache = AtlassianComputationCache<Branches>(cacheManager)
        val getCommitHistoryForFileByMacroIdUseCaseCache = AtlassianComputationCache<Commits>(cacheManager)
        val getExistingRepositoryBranchesUseCaseCache = AtlassianComputationCache<Branches>(cacheManager)
        val getFileContentForExistingRepositoryUseCaseCache = AtlassianComputationCache<FileContent>(cacheManager)
        val getFileContentForPredefinedRepositoryUseCaseCache = AtlassianComputationCache<FileContent>(cacheManager)
        val getFileContentForRepositoryUseCaseCache = AtlassianComputationCache<FileContent>(cacheManager)
        val getFilesListForExistingRepositoryUseCaseCache = AtlassianComputationCache<FilesList>(cacheManager)
        val getFilesListForPredefinedRepositoryUseCaseCache = AtlassianComputationCache<FilesList>(cacheManager)
        val getFilesListForRepositoryUseCaseCache = AtlassianComputationCache<FilesList>(cacheManager)
        val getLatestRevisionByDocumentationsMacroIdUseCaseCache = AtlassianComputationCache<Revision>(cacheManager)
        val getMethodsForExistingRepositoryUseCaseCache = AtlassianComputationCache<Methods>(cacheManager)
        val getMethodsForPredefinedRepositoryUseCaseCache = AtlassianComputationCache<Methods>(cacheManager)
        val getMethodsForRepositoryUseCaseCache = AtlassianComputationCache<Methods>(cacheManager)
        val getPredefinedRepositoryBranchesUseCaseCache = AtlassianComputationCache<Branches>(cacheManager)
        val getPredefinedRepositoryUseCaseCache = AtlassianComputationCache<PredefinedRepository>(cacheManager)
        val modifyPredefinedRepositoryUseCaseCache = AtlassianComputationCache<SavedPredefinedRepository>(cacheManager)
        val verifyDocumentationMacroByDocumentationsMacroIdUseCaseCache = AtlassianComputationCache<String>(cacheManager)
        val verifyRepositoryUseCaseCache = AtlassianComputationCache<VerificationInfo>(cacheManager)
        return ResultsCachePluginComponents(
                documentToBeConvertedLockCache,
                refreshLocationUseCaseCache,
                publishFileComputationCache,
                spacesWithMacroComputationCache,
                temporaryEditBranchResultCache,
                createDocumentationMacroUseCaseCache,
                createPredefinedRepositoryUseCaseCache,
                generateFilePreviewUseCaseCache,
                getBranchesByDocumentationsMacroIdUseCaseCache,
                getBranchesForRepositoryUseCaseCache,
                getCommitHistoryForFileByMacroIdUseCaseCache,
                getExistingRepositoryBranchesUseCaseCache,
                getFileContentForExistingRepositoryUseCaseCache,
                getFileContentForPredefinedRepositoryUseCaseCache,
                getFileContentForRepositoryUseCaseCache,
                getFilesListForExistingRepositoryUseCaseCache,
                getFilesListForPredefinedRepositoryUseCaseCache,
                getFilesListForRepositoryUseCaseCache,
                getLatestRevisionByDocumentationsMacroIdUseCaseCache,
                getMethodsForExistingRepositoryUseCaseCache,
                getMethodsForPredefinedRepositoryUseCaseCache,
                getMethodsForRepositoryUseCaseCache,
                getPredefinedRepositoryBranchesUseCaseCache,
                getPredefinedRepositoryUseCaseCache,
                modifyPredefinedRepositoryUseCaseCache,
                verifyDocumentationMacroByDocumentationsMacroIdUseCaseCache,
                verifyRepositoryUseCaseCache
        )
    }

    private fun getCacheComponenets(cacheManager: MemoryCacheManager): CachePluginComponents {
        val documentsViewCache = AtlassianDocumentsViewCache(cacheManager)
        val documentItemCache = AtlassianDocumentItemCache(cacheManager)
        val macroSettingsCache = AtlassianMacroSettingsCache(cacheManager)
        val temporaryIdCache = AtlassianTemporaryIdCache(cacheManager)
        val macroViewCache = AtlassianMacroViewCache(cacheManager)
        val revisionCache = AtlassianRepositoryRevisionCache(cacheManager)
        val pageAndSpacePermissionsForUserCache = AtlassianPageAndSpacePermissionsForUserCache(cacheManager)
        val globForMacroCache = AtlassianGlobForMacroCache(cacheManager)
        return CachePluginComponents(
                documentsViewCache,
                documentItemCache,
                macroSettingsCache,
                temporaryIdCache,
                macroViewCache,
                revisionCache,
                pageAndSpacePermissionsForUserCache,
                globForMacroCache
        )
    }

    private fun getDatabaseComponenets(): DatabasePluginComponents {
        return DatabasePluginComponents(
                InMemoryMacroSettingsDatabaseService(),
                InMemoryGlobsForMacroDatabaseService(),
                InMemoryPredefinedRepositoryDatabaseService(),
                InMemoryRepositoryDatabaseService(),
                InMemoryExtractorDatabase(),
                InMemoryDefaultGlobsDatabase(),
                InMemoryMacroLocationDatabase(),
                InMemoryPluginSettingsDatabaseService(),
                InMemoryRepositoryUsageDatabase(),
                InMemoryThreadSettingsDatabase(),
                InMemoryTemporaryEditBranchesDatabase()
        )
    }
}

