package com.networkedassets.git4c

import com.atlassian.cache.CacheManager
import com.atlassian.confluence.pages.PageManager
import com.atlassian.confluence.security.PermissionManager
import com.atlassian.confluence.spaces.SpaceManager
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
import com.atlassian.sal.api.transaction.TransactionTemplate
import com.atlassian.user.UserManager
import com.networkedassets.git4c.application.Plugin
import com.networkedassets.git4c.application.PluginComponents
import com.networkedassets.git4c.core.business.ConfluenceQueryBaseExecutorHolder
import com.networkedassets.git4c.core.business.ConverterBaseExecutorHolder
import com.networkedassets.git4c.core.business.RepositoryPullBaseExecutorHolder
import com.networkedassets.git4c.core.business.RevisionCheckBaseExecutorHolder
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

        log.info { "Starting to initialize Git4C Confluence Plugin components..." }

        val temporaryIdCache = AtlassianTemporaryIdCache(cacheManager)
        val documentsViewCache = AtlassianDocumentsViewCache(cacheManager)
        val documentItemCache = AtlassianDocumentItemCache(cacheManager)
        val macroSettingsCache = AtlassianMacroSettingsCache(cacheManager)
        val macroViewCache = AtlassianMacroViewCache(cacheManager)
        val revisionCache = AtlassianRepositoryRevisionCache(cacheManager)
        val documentsConvertionLockCache = AtlassianDocumentConvertionCache(cacheManager)
        val publishFileComputationCache = AtlassianComputationCache(cacheManager)
        val refreshLocationUseCaseCache = AtlassianRefreshLocationUseCaseCache(cacheManager)
        val pageAndSpacePermissionsForUserCache = AtlassianPageAndSpacePermissionsForUserCache(cacheManager)

        val gitClient = DefaultGitClient()
        val importer = GitSourcePlugin(gitClient)

        val identifierGenerator = UuidIdentifierGenerator()

        val postProcessor = JSoupPostProcessor(identifierGenerator)

        val mainPlugins = MainConverterPluginList(listOf(AsciidocConverterPlugin(), MarkdownConverterPlugin()), postProcessor)
        val converterPlugins = listOf(mainPlugins, PrismJSConverterPlugin(), ImageConverterPlugin(), PUMLConverterPlugin())
        val converter = ConverterPluginList(converterPlugins, PlainTextConverterPlugin())

        val encryptor = RepositoryDesEncryptor()

        val parser = Parsers()

        val atlassianPageManager = AtlassianPageManager(spaceManager, pageManager, transationTemplate)

        val atlassianSpaceManager = AtlassianSpaceManager(spaceManager, transationTemplate)

        val pageBuilder = HtmlErrorPageBuilder()

        val pageMacroExtractor = AtlassianPageMacroExtractor()

        val pluginSettings = ConfluencePluginSettings(pluginSettingsFactory)

        val pluginSettingsDatabase = ConfluencePluginSettingsDatabase(pluginSettings)

        val permissionChecker = AtlassianPermissionChecker(permissionManager, pageManager, userManager, spaceManager, transationTemplate)

        val git4cUserManager = AtlassianUserManager(userManager, transationTemplate)

        val threadSettingsDatabase = ConfluenceThreadSettingsDatabase(pluginSettings)

        val spacesWithMacroComputationCache = ConfluenceSpacesWithMacroResultCache(cacheManager)
        val temporaryEditBranchComputationCache = ConfluenceTemporaryEditBranchResultCache(cacheManager)

        val revisionCheckExecutor = RevisionCheckBaseExecutorHolder(threadSettingsDatabase.getRevisionCheckThreadNumber()) { Executors.newScheduledThreadPool(it) }
        val repositoryExecutor = RepositoryPullBaseExecutorHolder(threadSettingsDatabase.getRepositoryExecutorThreadNumber()) { Executors.newScheduledThreadPool(it) }
        val converterExecutor = ConverterBaseExecutorHolder(threadSettingsDatabase.getConverterExecutorThreadNumber()) { Executors.newScheduledThreadPool(it) }
        val confluenceQueryExecutor = ConfluenceQueryBaseExecutorHolder(threadSettingsDatabase.getConfluenceQueryExecutorThreadNumber()) { Executors.newScheduledThreadPool(it) }

        val globForMacroCache = AtlassianGlobForMacroCache(cacheManager)

        log.info { "Initialization of Git4C Confluence Plugin components has been finished." }

        PluginComponents(
                importer,
                converter,
                documentsViewCache,
                documentItemCache,
                macroSettingsCache,
                temporaryIdCache,
                macroViewCache,
                revisionCache,
                pageAndSpacePermissionsForUserCache,
                documentsConvertionLockCache,
                refreshLocationUseCaseCache,
                macroSettingsDatabase,
                globsForMacroDatabase,
                predefinedRepositoryDatabase,
                encryptedRepositoryDatabase,
                extractorDataDatabase,
                encryptor,
                identifierGenerator,
                predefinedGlobsDatabase,
                parser,
                pageBuilder,
                atlassianSpaceManager,
                atlassianPageManager,
                pageMacroExtractor,
                permissionChecker,
                macroLocationDatabase,
                git4cUserManager,
                pluginSettingsDatabase,
                repositoryUsageDatabase,
                revisionCheckExecutor,
                repositoryExecutor,
                converterExecutor,
                confluenceQueryExecutor,
                publishFileComputationCache,
                threadSettingsDatabase,
                spacesWithMacroComputationCache,
                temporaryEditBranchesDatabase,
                globForMacroCache,
                temporaryEditBranchComputationCache
        )
    }
}
