package com.networkedassets.git4c.standalone

import com.networkedassets.git4c.application.Plugin
import com.networkedassets.git4c.application.PluginComponents
import com.networkedassets.git4c.core.datastore.TemporaryIdCache
import com.networkedassets.git4c.data.macro.MacroSettings
import com.networkedassets.git4c.data.macro.NoAuthCredentials
import com.networkedassets.git4c.infrastructure.MacroSettingsProvider
import com.networkedassets.git4c.infrastructure.UnifiedDataStore
import com.networkedassets.git4c.infrastructure.UuidIdentifierGenerator
import com.networkedassets.git4c.infrastructure.plugin.converter.main.markdown.MarkdownConverterPlugin
import com.networkedassets.git4c.infrastructure.plugin.source.directory.DirectorySourcePlugin
import com.networkedassets.git4c.standalone.infrastructure.DocumentsCache
import com.networkedassets.git4c.standalone.infrastructure.HashMapCache
import com.networkedassets.git4c.standalone.infrastructure.SettingsCache
import com.networkedassets.git4c.standalone.infrastructure.SettingsDB

class StandalonePlugin : Plugin() {
    override val components: PluginComponents = run {

        val temporaryIdCache = object : HashMapCache<String>(), TemporaryIdCache {}
        val documentsViewCache = DocumentsCache()
        val macroSettingsCache = SettingsCache()
        val macroSettingsDB = SettingsDB()
        val macroSettingsRepository = MacroSettingsProvider(
                UnifiedDataStore(
                        macroSettingsDB,
                        macroSettingsCache
                )
        )

        macroSettingsDB.put("1", MacroSettings("1", "standalone/src/main/resources/exampleDocumentation", NoAuthCredentials(), "master", "", ""))

        val importer = DirectorySourcePlugin()
        val converter = MarkdownConverterPlugin()

        val identifierGenerator = UuidIdentifierGenerator()

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