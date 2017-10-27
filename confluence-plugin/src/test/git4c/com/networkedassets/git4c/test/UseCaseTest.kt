package com.networkedassets.git4c.test

import com.atlassian.activeobjects.external.ActiveObjects
import com.atlassian.activeobjects.test.TestActiveObjects
import com.atlassian.cache.memory.MemoryCacheManager
import com.networkedassets.git4c.application.PluginComponents
import com.networkedassets.git4c.core.business.ErrorPageBuilder
import com.networkedassets.git4c.core.business.PageMacroExtractor
import com.networkedassets.git4c.core.business.PageManager
import com.networkedassets.git4c.core.business.SpaceManager
import com.networkedassets.git4c.core.datastore.repositories.PredefinedGlobsDatabase
import com.networkedassets.git4c.delivery.executor.execution.UseCase
import com.networkedassets.git4c.infrastructure.RepositoryDesEncryptor
import com.networkedassets.git4c.infrastructure.UuidIdentifierGenerator
import com.networkedassets.git4c.infrastructure.cache.AtlassianDocumentsViewCache
import com.networkedassets.git4c.infrastructure.cache.AtlassianMacroSettingsCache
import com.networkedassets.git4c.infrastructure.cache.AtlassianTemporaryIdCache
import com.networkedassets.git4c.infrastructure.database.ao.*
import com.networkedassets.git4c.infrastructure.database.ao.repository.*
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
import com.networkedassets.git4c.infrastructure.plugin.source.directory.DirectorySourcePlugin
import net.java.ao.EntityManager
import net.java.ao.test.jdbc.H2Memory
import net.java.ao.test.jdbc.Jdbc
import net.java.ao.test.junit.ActiveObjectsJUnitRunner
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import kotlin.test.assertNotNull

@RunWith(ActiveObjectsJUnitRunner::class)
@Jdbc(H2Memory::class)
abstract class UseCaseTest<USECASE : UseCase<*, *>> {

    private lateinit var entityManager: EntityManager
    private lateinit var ao: ActiveObjects

    lateinit var components: PluginComponents

    lateinit var builder: Builder

    lateinit var useCase: USECASE

    @Before
    fun setUp() {
        assertNotNull(entityManager)
        ao = TestActiveObjects(entityManager)
        ao.migrate(MacroSettingsEntity::class.java, GlobEntity::class.java, RepositoryEntity::class.java, RepositoryWithNoAuthorizationEntity::class.java, RepositoryWithSshKeyEntity::class.java, RepositoryWithUsernameAndPasswordEntity::class.java, PredefinedRepositoryEntity::class.java, PredefinedGlobEntity::class.java)
        val cacheManager = MemoryCacheManager()
        val macroSettingsDatabaseService = ConfluenceActiveObjectMacroSettings(ao)
        val predefinedRepositoryDatabaseService = ConfluenceActiveObjectPredefinedRepository(ao)
        val repositoryDatabaseService = ConfluenceActiveObjectRepository(ao)
        val globsForMacroDatabaseService = ConfluenceActiveObjectGlobForMacro(ao)
        val defaultGlobsDatabase = ConfluenceActiveObjectPredefinedGlobs(ao)
        val extractorDatabase = ConfluenceActiveObjectExtractorData(ao)
        components = createPlugin(
                macroSettingsDatabaseService,
                cacheManager,
                predefinedRepositoryDatabaseService,
                repositoryDatabaseService,
                globsForMacroDatabaseService,
                defaultGlobsDatabase,
                extractorDatabase
        )
        builder = Builder(components)
        useCase = getUseCase(components)
        builder.state.reset()
    }


    fun createPlugin(macroSettingsDatabase: ConfluenceActiveObjectMacroSettings,
                     cacheManager: MemoryCacheManager,
                     predefinedRepositoryDatabase: ConfluenceActiveObjectPredefinedRepository,
                     encryptedRepositoryDatabase: ConfluenceActiveObjectRepository,
                     globsForMacroDatabase: ConfluenceActiveObjectGlobForMacro,
                     defaultGlobsDatabase: PredefinedGlobsDatabase,
                     extractorData: ConfluenceActiveObjectExtractorData
    ): PluginComponents {

        val temporaryIdCache = AtlassianTemporaryIdCache(cacheManager)
        val documentsViewCache = AtlassianDocumentsViewCache(cacheManager)
        val macroSettingsCache = AtlassianMacroSettingsCache(cacheManager)

        // Important that it uses files from test!!!
        val importer = DirectorySourcePlugin()

        val identifierGenerator = UuidIdentifierGenerator()

        val postProcessor = JSoupPostProcessor(identifierGenerator)

        val mainPlugins = MainConverterPluginList(listOf(AsciidocConverterPlugin(), MarkdownConverterPlugin()), postProcessor)
        val converterPlugins = listOf(mainPlugins, PrismJSConverterPlugin(), ImageConverterPlugin(), PUMLConverterPlugin())
        val converter = ConverterPluginList(converterPlugins, PlainTextConverterPlugin())

        val encryptor = RepositoryDesEncryptor()

        val parser = Parsers()

        return PluginComponents(
                importer,
                converter,
                documentsViewCache,
                macroSettingsCache,
                temporaryIdCache,
                macroSettingsDatabase,
                globsForMacroDatabase,
                predefinedRepositoryDatabase,
                encryptedRepositoryDatabase,
                extractorData,
                encryptor,
                identifierGenerator,
                defaultGlobsDatabase,
                parser,
                mock(ErrorPageBuilder::class.java),
                mock(SpaceManager::class.java),
                mock(PageManager::class.java),
                mock(PageMacroExtractor::class.java)
        )

    }

    abstract fun getUseCase(plugin: PluginComponents): USECASE

}
