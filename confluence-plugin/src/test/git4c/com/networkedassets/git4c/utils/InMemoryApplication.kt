package com.networkedassets.git4c.utils

import com.atlassian.cache.memory.MemoryCacheManager
import com.networkedassets.git4c.application.PluginComponents
import com.networkedassets.git4c.core.business.*
import com.networkedassets.git4c.core.datastore.repositories.MacroSettingsDatabase
import com.networkedassets.git4c.core.datastore.repositories.PredefinedGlobsDatabase
import com.networkedassets.git4c.delivery.executor.result.BackendRequest
import com.networkedassets.git4c.infrastructure.ConfluencePluginSettingsDatabase
import com.networkedassets.git4c.infrastructure.ConfluenceSpacesWithMacroResultCache
import com.networkedassets.git4c.infrastructure.RepositoryDesEncryptor
import com.networkedassets.git4c.infrastructure.UuidIdentifierGenerator
import com.networkedassets.git4c.infrastructure.cache.*
import com.networkedassets.git4c.infrastructure.mocks.core.DirectorySourcePlugin
import com.networkedassets.git4c.infrastructure.mocks.core.SimpleScheduledExecutorHolder
import com.networkedassets.git4c.infrastructure.mocks.core.cache.InMemoryRevisionCache
import com.networkedassets.git4c.infrastructure.mocks.core.cache.InMemoryTemporaryEditBranchResultCache
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
import com.nhaarman.mockito_kotlin.mock
import org.mockito.Mockito
import java.util.concurrent.CompletableFuture

object InMemoryApplication {

    val application = getComponents()

    fun <ANSWER : Any> execute(query: BackendRequest<ANSWER>): CompletableFuture<*> {
        return sendToExecutionAsync(application.dispatcher, query)
    }

    fun getComponents(): PluginComponents {
        val cacheManager = MemoryCacheManager()
        val macroSettingsDatabaseService = InMemoryMacroSettingsDatabaseService()
        val predefinedRepositoryDatabaseService = InMemoryPredefinedRepositoryDatabaseService()
        val repositoryDatabaseService = InMemoryRepositoryDatabaseService()
        val globsForMacroDatabaseService = InMemoryGlobsForMacroDatabaseService()
        val defaultGlobsDatabase = InMemoryDefaultGlobsDatabase()
        val extractorDatabase = InMemoryExtractorDatabase()
        val repositoryUsageDatabase = InMemoryRepositoryUsageDatabase()
        val macroLocationDatabase = InMemoryMacroLocationDatabase()
        val revisionCache = InMemoryRevisionCache()
        val threadSettingsDatabase = InMemoryThreadSettingsDatabase()
        val temporaryEditBranchesDatabase = InMemoryTemporaryEditBranchesDatabase()
        val globForMacroCache = InMemoryGlobsForMacroCache()
        val temporaryEditBranchResultCache = InMemoryTemporaryEditBranchResultCache()

        return createPlugin(
                macroSettingsDatabaseService,
                cacheManager,
                predefinedRepositoryDatabaseService,
                repositoryDatabaseService,
                globsForMacroDatabaseService,
                defaultGlobsDatabase,
                extractorDatabase,
                repositoryUsageDatabase,
                macroLocationDatabase,
                revisionCache,
                threadSettingsDatabase,
                temporaryEditBranchesDatabase,
                globForMacroCache,
                temporaryEditBranchResultCache
        )
    }


    private fun createPlugin(macroSettingsDatabase: MacroSettingsDatabase,
                             cacheManager: MemoryCacheManager,
                             predefinedRepositoryDatabase: InMemoryPredefinedRepositoryDatabaseService,
                             encryptedRepositoryDatabase: InMemoryRepositoryDatabaseService,
                             globsForMacroDatabase: InMemoryGlobsForMacroDatabaseService,
                             defaultGlobsDatabase: PredefinedGlobsDatabase,
                             extractorData: InMemoryExtractorDatabase,
                             repositoryUsageDatabase: InMemoryRepositoryUsageDatabase,
                             macroLocationDatabase: InMemoryMacroLocationDatabase,
                             revisionCache: InMemoryRevisionCache,
                             threadSettingsDatabase: InMemoryThreadSettingsDatabase,
                             temporaryEditBranchesDatabase: InMemoryTemporaryEditBranchesDatabase,
                             globForMacroCache: InMemoryGlobsForMacroCache,
                             temporaryEditBranchResultCache: InMemoryTemporaryEditBranchResultCache
    ): PluginComponents {

        val temporaryIdCache = AtlassianTemporaryIdCache(cacheManager)
        val documentsViewCache = AtlassianDocumentsViewCache(cacheManager)
        val documentItemCache = AtlassianDocumentItemCache(cacheManager)
        val macroSettingsCache = AtlassianMacroSettingsCache(cacheManager)
        val macroViewCache = AtlassianMacroViewCache(cacheManager)
        val pageAndSpacePermissionsForUserCache = AtlassianPageAndSpacePermissionsForUserCache(cacheManager)
        val documentConvertionLock = AtlassianDocumentConvertionCache(cacheManager)
        val publishFileComputationCache = AtlassianComputationCache(cacheManager)
        val spacesWithMacroComputationCache = ConfluenceSpacesWithMacroResultCache(cacheManager)
        val refreshLocationUseCaseCache = AtlassianRefreshLocationUseCaseCache(cacheManager)

        // Important that it uses files from test!!!
        val importer = DirectorySourcePlugin()

        val identifierGenerator = UuidIdentifierGenerator()

        val postProcessor = JSoupPostProcessor(identifierGenerator)

        val mainPlugins = MainConverterPluginList(listOf(AsciidocConverterPlugin(), MarkdownConverterPlugin()), postProcessor)
        val converterPlugins = listOf(mainPlugins, PrismJSConverterPlugin(), ImageConverterPlugin(), PUMLConverterPlugin())
        val converter = ConverterPluginList(converterPlugins, PlainTextConverterPlugin())

        val encryptor = RepositoryDesEncryptor()

        val parser = Parsers()

        val pluginSettingsDatabase = ConfluencePluginSettingsDatabase(object : PluginSettings {
            val hashmap = HashMap<String, String>()
            override fun put(key: String, setting: String) {
                hashmap.put(key, setting)
            }

            override fun get(key: String): String? {
                return hashmap.get(key)
            }

            override fun remove(key: String) {
                hashmap.remove(key)
            }
        })

        return PluginComponents(
                importer,
                converter,
                documentsViewCache,
                documentItemCache,
                macroSettingsCache,
                temporaryIdCache,
                macroViewCache,
                revisionCache,
                pageAndSpacePermissionsForUserCache,
                documentConvertionLock,
                refreshLocationUseCaseCache,
                macroSettingsDatabase,
                globsForMacroDatabase,
                predefinedRepositoryDatabase,
                encryptedRepositoryDatabase,
                extractorData,
                encryptor,
                identifierGenerator,
                defaultGlobsDatabase,
                parser,
                Mockito.mock(ErrorPageBuilder::class.java),
                Mockito.mock(SpaceManager::class.java),
                Mockito.mock(PageManager::class.java),
                Mockito.mock(PageMacroExtractor::class.java),
                mock(),
                macroLocationDatabase,
                mock(),
                pluginSettingsDatabase,
                repositoryUsageDatabase,
                SimpleScheduledExecutorHolder(),
                SimpleScheduledExecutorHolder(),
                SimpleScheduledExecutorHolder(),
                SimpleScheduledExecutorHolder(),
                publishFileComputationCache,
                threadSettingsDatabase,
                spacesWithMacroComputationCache,
                temporaryEditBranchesDatabase,
                globForMacroCache,
                temporaryEditBranchResultCache
        )

    }

}

