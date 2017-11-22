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
import com.networkedassets.git4c.core.datastore.repositories.*
import com.networkedassets.git4c.infrastructure.*
import com.networkedassets.git4c.infrastructure.cache.AtlassianDocumentsViewCache
import com.networkedassets.git4c.infrastructure.cache.AtlassianMacroSettingsCache
import com.networkedassets.git4c.infrastructure.cache.AtlassianTemporaryIdCache
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
import com.networkedassets.git4c.utils.info
import org.slf4j.LoggerFactory

class ConfluencePlugin(
        val macroSettingsDatabase: MacroSettingsDatabase,
        val predefinedRepositoryDatabase: PredefinedRepositoryDatabase,
        val encryptedRepositoryDatabase: EncryptedRepositoryDatabase,
        val globsForMacroDatabase: GlobForMacroDatabase,
        val predefinedGlobsDatabase: PredefinedGlobsDatabase,
        val extractorDataDatabase: ExtractorDataDatabase,
        val cacheManager: CacheManager,
        val spaceManager: SpaceManager,
        val pageManager: PageManager,
        val transationTemplate: TransactionTemplate,
        val pluginSettingsFactory: PluginSettingsFactory,
        val permissionManager: PermissionManager,
        val userManager: UserManager
) : Plugin() {

    private val log = LoggerFactory.getLogger(ConfluencePlugin::class.java)

    override val components: PluginComponents = run {

        log.info { "Starting to initialize Git4C Confluence Plugin components..." }

        val temporaryIdCache = AtlassianTemporaryIdCache(cacheManager)
        val documentsViewCache = AtlassianDocumentsViewCache(cacheManager)
        val macroSettingsCache = AtlassianMacroSettingsCache(cacheManager)

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

        val pluginSettingsDatabase = ConfluencePluginSettingsDatabase(ConfluencePluginSettings(pluginSettingsFactory))

        val permissionChecker = AtlassianPermissionChecker(permissionManager, pageManager, userManager, spaceManager, transationTemplate)

        val macroIdToSpaceAndPageDatabase = HashmapMacroIdToSpaceAndPageDatabase()

        log.info { "Initialization of Git4C Confluence Plugin components has been finished." }

        PluginComponents(
                importer,
                converter,
                documentsViewCache,
                macroSettingsCache,
                temporaryIdCache,
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
                pluginSettingsDatabase,
                permissionChecker,
                macroIdToSpaceAndPageDatabase
        )
    }
}
