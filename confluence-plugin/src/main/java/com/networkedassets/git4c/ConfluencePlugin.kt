package com.networkedassets.git4c

import com.atlassian.cache.CacheManager
import com.networkedassets.git4c.application.Plugin
import com.networkedassets.git4c.application.PluginComponents
import com.networkedassets.git4c.core.datastore.repositories.*
import com.networkedassets.git4c.infrastructure.*
import com.networkedassets.git4c.infrastructure.cache.AtlassianDocumentsViewCache
import com.networkedassets.git4c.infrastructure.cache.AtlassianMacroSettingsCache
import com.networkedassets.git4c.infrastructure.cache.AtlassianTemporaryIdCache
import com.networkedassets.git4c.infrastructure.git.DefaultGitClient
import com.networkedassets.git4c.infrastructure.plugin.parser.Parsers
import com.networkedassets.git4c.infrastructure.plugin.converter.ConverterPluginList
import com.networkedassets.git4c.infrastructure.plugin.converter.images.ImageConverterPlugin
import com.networkedassets.git4c.infrastructure.plugin.converter.markdown.MarkdownConverterPlugin
import com.networkedassets.git4c.infrastructure.plugin.converter.plaintext.PlainTextConverterPlugin
import com.networkedassets.git4c.infrastructure.plugin.converter.plantuml.PUMLConverterPlugin
import com.networkedassets.git4c.infrastructure.plugin.converter.prismjs.PrismJSConverterPlugin
import com.networkedassets.git4c.infrastructure.plugin.source.git.GitSourcePlugin
import com.networkedassets.git4c.utils.info
import org.slf4j.LoggerFactory

class ConfluencePlugin(
        val macroSettingsDatabase: MacroSettingsDatabase,
        val predefinedRepositoryDatabase: PredefinedRepositoryDatabase,
        val encryptedRepositoryDatabase: EncryptedRepositoryDatabase,
        val globsForMacroDatabase: GlobForMacroDatabase,
        val predefinedGlobsDatabase: PredefinedGlobsDatabase,
        val cacheManager: CacheManager
) : Plugin() {

    private val log = LoggerFactory.getLogger(ConfluencePlugin::class.java)

    override val components: PluginComponents = run {

        log.info { "Starting to initialize Git4C Confluence Plugin components..." }

        val temporaryIdCache = AtlassianTemporaryIdCache(cacheManager)
        val documentsViewCache = AtlassianDocumentsViewCache(cacheManager)
        val macroSettingsCache = AtlassianMacroSettingsCache(cacheManager)

        val gitClient = DefaultGitClient()
        val importer = GitSourcePlugin(gitClient)

        val converterPlugins = listOf(MarkdownConverterPlugin(), PrismJSConverterPlugin(), ImageConverterPlugin(), PUMLConverterPlugin())
        val converter = ConverterPluginList(converterPlugins, PlainTextConverterPlugin())

        val identifierGenerator = UuidIdentifierGenerator()

        val encryptor = RepositoryDesEncryptor()

        val parser = Parsers()

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
                encryptor,
                identifierGenerator,
                predefinedGlobsDatabase,
                parser
        )
    }
}
