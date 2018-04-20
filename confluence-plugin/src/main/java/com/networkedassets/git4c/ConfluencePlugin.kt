package com.networkedassets.git4c

import com.atlassian.cache.CacheManager
import com.atlassian.confluence.pages.PageManager
import com.atlassian.confluence.security.PermissionManager
import com.atlassian.confluence.spaces.SpaceManager
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
import com.atlassian.sal.api.transaction.TransactionTemplate
import com.atlassian.user.UserManager
import com.networkedassets.git4c.application.*
import com.networkedassets.git4c.boundary.outbound.*
import com.networkedassets.git4c.core.business.*
import com.networkedassets.git4c.core.datastore.repositories.*
import com.networkedassets.git4c.infrastructure.*
import com.networkedassets.git4c.infrastructure.cache.*
import com.networkedassets.git4c.infrastructure.git.DefaultGitClient
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
import com.networkedassets.git4c.utils.getLogger
import com.networkedassets.git4c.utils.info
import java.util.concurrent.Executors

class ConfluencePlugin(
        val macroSettingsDatabase: MacroSettingsDatabase,
        val predefinedRepositoryDatabase: PredefinedRepositoryDatabase,
        val encryptedRepositoryDatabase: EncryptedRepositoryDatabase,
        val globsForMacroDatabase: GlobForMacroDatabase,
        val predefinedGlobsDatabase: PredefinedGlobsDatabase,
        val extractorDataDatabase: ExtractorDataDatabase,
        val macroLocationDatabase: MacroLocationDatabase,
        val repositoryUsageDatabase: RepositoryUsageDatabase,
        val temporaryEditBranchesDatabase: TemporaryEditBranchesDatabase,
        val cacheManager: CacheManager,
        val spaceManager: SpaceManager,
        val pageManager: PageManager,
        val transationTemplate: TransactionTemplate,
        val pluginSettingsFactory: PluginSettingsFactory,
        val permissionManager: PermissionManager,
        val userManager: UserManager
) : Plugin() {

    private val log = getLogger()

    override val components: PluginComponents = run {

        println("Starting to initialize Git4C Confluence Plugin components...")
        log.info { "Starting to initialize Git4C Confluence Plugin components..." }

        val database: DatabasePluginComponents = getDatabaseComponenets()
        val cache: CachePluginComponents = getCacheComponenets()
        val async: ResultsCachePluginComponents = getAsyncComponents()
        val utils: UtilitiesPluginComponents = getUtilsComponents()
        val macro: MacroPluginComponents = getMacroComponents(utils)
        val executors: ExecutorsPluginComponents = getExecutorsComponents(database)

        println("Initialization of Git4C Confluence Plugin components has been finished.")
        log.info { "Initialization of Git4C Confluence Plugin components has been finished." }

        PluginComponents(
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
        val git4cSpaceManager = AtlassianSpaceManager(spaceManager, transationTemplate)
        val git4cPageManager = AtlassianPageManager(spaceManager, pageManager, transationTemplate)
        val git4cPermissionChecker = AtlassianPermissionChecker(permissionManager, pageManager, userManager, spaceManager, transationTemplate)
        val git4cUserManager = AtlassianUserManager(userManager, transationTemplate)
        return UtilitiesPluginComponents(
                repositoryEncryptor,
                identifierGenerator,
                git4cSpaceManager,
                git4cPageManager,
                git4cPermissionChecker,
                git4cUserManager
        )
    }

    private fun getMacroComponents(utils: UtilitiesPluginComponents): MacroPluginComponents {
        val gitClient = DefaultGitClient()
        val importer = GitSourcePlugin(gitClient)
        val postProcessor = JSoupPostProcessor(utils.idGenerator)
        val mainPlugins = MainConverterPluginList(listOf(AsciidocConverterPlugin.get(true), MarkdownConverterPlugin()), postProcessor)
        val converterPlugins = listOf(mainPlugins, PrismJSConverterPlugin(), ImageConverterPlugin(), PUMLConverterPlugin())
        val converter = ConverterPluginList(converterPlugins, PlainTextConverterPlugin())
        val fileIgnorer = FileIgnorerList(AsciidocConverterPlugin.get(true))
        val parser = Parsers()
        val pageBuilder = HtmlErrorPageBuilder()
        val pageMacroExtractor = AtlassianPageMacroExtractor()
        return MacroPluginComponents(
                importer,
                converter,
                fileIgnorer,
                parser,
                pageBuilder,
                pageMacroExtractor
        )
    }

    private fun getAsyncComponents(): ResultsCachePluginComponents {
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

    private fun getCacheComponenets(): CachePluginComponents {
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
        val pluginSettings = ConfluencePluginSettings(pluginSettingsFactory)
        val pluginSettingsDatabase = ConfluencePluginSettingsDatabase(pluginSettings)
        val threadSettingsDatabase = ConfluenceThreadSettingsDatabase(pluginSettings)
        return DatabasePluginComponents(
                macroSettingsDatabase,
                globsForMacroDatabase,
                predefinedRepositoryDatabase,
                encryptedRepositoryDatabase,
                extractorDataDatabase,
                predefinedGlobsDatabase,
                macroLocationDatabase,
                pluginSettingsDatabase,
                repositoryUsageDatabase,
                threadSettingsDatabase,
                temporaryEditBranchesDatabase
        )
    }


}
