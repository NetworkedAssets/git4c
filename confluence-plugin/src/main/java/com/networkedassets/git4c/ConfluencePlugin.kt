package com.networkedassets.git4c

import com.atlassian.cache.CacheManager
import com.networkedassets.git4c.application.Plugin
import com.networkedassets.git4c.application.PluginComponents
import com.networkedassets.git4c.infrastructure.AtlassianDocumentsViewCache
import com.networkedassets.git4c.infrastructure.UuidIdentifierGenerator
import com.networkedassets.git4c.infrastructure.database.EncryptedDocumentationsMacroSettingsActiveObjectDatabase
import com.networkedassets.git4c.infrastructure.database.ao.EncryptedDocumentationsMacroSettingsDBService
import com.networkedassets.git4c.infrastructure.*
import com.networkedassets.git4c.infrastructure.plugin.converter.markdown.MarkdownConverterPlugin
import com.networkedassets.git4c.infrastructure.git.DefaultGitClient
import com.networkedassets.git4c.infrastructure.plugin.source.git.GitSourcePlugin
import com.networkedassets.git4c.utils.info
import org.slf4j.LoggerFactory

class ConfluencePlugin(val documentationsMacroSettingsDBService: EncryptedDocumentationsMacroSettingsDBService, val cacheManager: CacheManager) : Plugin() {

    private val log = LoggerFactory.getLogger(ConfluencePlugin::class.java)

    override val components: PluginComponents = run {

        log.info { "Starting to initialize Git4C Confluence Plugin components..." }

        val temporaryIdCache = AtlassianTemporaryIdCache(cacheManager)
        val encryptor = SimpleCredentialsEncryptor()
        val documentsViewCache = AtlassianDocumentsViewCache(cacheManager)
        val macroSettingsCache = MacroSettingsCacheEncryptor(AtlassianEncryptedMacroSettingsCache(cacheManager), encryptor)

        val macroSettingsRepository = MacroSettingsProvider(
                UnifiedDataStore(
                        MacroSettingsDatabaseEncryptor(EncryptedDocumentationsMacroSettingsActiveObjectDatabase(documentationsMacroSettingsDBService), encryptor),
                        macroSettingsCache
                )
        )
        val gitClient = DefaultGitClient()
        val importer = GitSourcePlugin(gitClient)
        val converter = MarkdownConverterPlugin()

        val identifierGenerator = UuidIdentifierGenerator()

        log.info { "Initialization of Git4C Confluence Plugin components has been finished." }

        PluginComponents(
                importer,
                converter,
                documentsViewCache,
                macroSettingsCache,
                macroSettingsRepository,
                identifierGenerator,
                temporaryIdCache
        )

    }
}
