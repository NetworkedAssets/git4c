package com.networkedassets.git4c.infrastructure.database.ao

import com.atlassian.activeobjects.external.ActiveObjects
import com.atlassian.activeobjects.test.TestActiveObjects
import com.networkedassets.git4c.core.datastore.extractors.LineNumbersExtractorData
import com.networkedassets.git4c.core.datastore.extractors.MethodExtractorData
import com.networkedassets.git4c.data.*
import com.networkedassets.git4c.data.encryption.EncryptedRepository
import com.networkedassets.git4c.infrastructure.database.ao.repository.*
import net.java.ao.EntityManager
import net.java.ao.test.converters.NameConverters
import net.java.ao.test.jdbc.Data
import net.java.ao.test.jdbc.DatabaseUpdater
import net.java.ao.test.jdbc.H2Memory
import net.java.ao.test.jdbc.Jdbc
import net.java.ao.test.junit.ActiveObjectsJUnitRunner
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertNotNull

@RunWith(ActiveObjectsJUnitRunner::class)
@Jdbc(H2Memory::class)
@Data(RemoveAllDatabaseTest.TestDatabaseUpdater::class)
@NameConverters
class RemoveAllDatabaseTest {

    private lateinit var entityManager: EntityManager
    private lateinit var ao: ActiveObjects

    private lateinit var extractorData: ConfluenceActiveObjectExtractorData
    private lateinit var globForMacro: ConfluenceActiveObjectGlobForMacro
    private lateinit var macroLocation: ConfluenceActiveObjectMacroLocation
    private lateinit var macroSettings: ConfluenceActiveObjectMacroSettings
    private lateinit var predefinedGlobs: ConfluenceActiveObjectPredefinedGlobs
    private lateinit var predefinedRepository: ConfluenceActiveObjectPredefinedRepository
    private lateinit var repository: ConfluenceActiveObjectRepository
    private lateinit var repositoryUsage: ConfluenceActiveObjectRepositoryUsage
    private lateinit var temporaryEditBranches: ConfluenceActiveObjectTemporaryEditBranches

    @Before
    fun setUp() {
        assertNotNull(entityManager)
        ao = TestActiveObjects(entityManager)
        extractorData = ConfluenceActiveObjectExtractorData(ao)
        globForMacro = ConfluenceActiveObjectGlobForMacro(ao)
        macroLocation = ConfluenceActiveObjectMacroLocation(ao)
        macroSettings = ConfluenceActiveObjectMacroSettings(ao)
        predefinedGlobs = ConfluenceActiveObjectPredefinedGlobs(ao)
        predefinedRepository = ConfluenceActiveObjectPredefinedRepository(ao)
        repository = ConfluenceActiveObjectRepository(ao)
        repositoryUsage = ConfluenceActiveObjectRepositoryUsage(ao)
        temporaryEditBranches = ConfluenceActiveObjectTemporaryEditBranches(ao)
        ao.migrate(MacroSettingsEntity::class.java, RepositoryEntity::class.java, RepositoryWithNoAuthorizationEntity::class.java, RepositoryWithSshKeyEntity::class.java, RepositoryWithUsernameAndPasswordEntity::class.java, PredefinedRepositoryEntity::class.java)
    }

    @Test
    fun testRemoveAll() {
        extractorData.put("1", MethodExtractorData("1", "asd"))
        extractorData.put("2", LineNumbersExtractorData("2", 1,2))
        assertThat(extractorData.getAll()).hasSize(2)

        globForMacro.put("1", GlobForMacro("1", "1", "1"))
        assertThat(globForMacro.getAll()).hasSize(1)

        macroLocation.put("1", MacroLocation("1", "1", "1"))
        assertThat(macroLocation.getAll()).hasSize(1)

        macroSettings.put("1", MacroSettings("1", "1", "1", "1" ,"1", "1"))
        assertThat(macroSettings.getAll()).hasSize(1)

        predefinedGlobs.put("1", PredefinedGlob("1", "1", "1"))
        assertThat(predefinedGlobs.getAll()).hasSize(1)

        predefinedRepository.put("1", PredefinedRepository("1", "1", "1"))
        assertThat(predefinedRepository.getAll()).hasSize(1)

        repository.put("1", EncryptedRepository("1", RepositoryWithNoAuthorization("1", "1", false), "1"))
        assertThat(repository.getAll()).hasSize(1)

        repositoryUsage.put("1", RepositoryUsage("1", "1", "1", "2" ,123L))
        assertThat(repositoryUsage.getAll()).hasSize(1)

        temporaryEditBranches.put("1", TemporaryEditBranch("1", "1"))
        assertThat(temporaryEditBranches.getAll()).hasSize(1)

        extractorData.removeAll()
        globForMacro.removeAll()
        macroLocation.removeAll()
        macroSettings.removeAll()
        predefinedGlobs.removeAll()
        predefinedRepository.removeAll()
        repository.removeAll()
        repositoryUsage.removeAll()
        temporaryEditBranches.removeAll()

        assertThat(extractorData.getAll()).isEmpty()
        assertThat(globForMacro.getAll()).isEmpty()
        assertThat(macroLocation.getAll()).isEmpty()
        assertThat(macroSettings.getAll()).isEmpty()
        assertThat(predefinedGlobs.getAll()).isEmpty()
        assertThat(predefinedRepository.getAll()).isEmpty()
        assertThat(repository.getAll()).isEmpty()
        assertThat(repositoryUsage.getAll()).isEmpty()
        assertThat(temporaryEditBranches.getAll()).isEmpty()

    }

    class TestDatabaseUpdater : DatabaseUpdater {
        private val entities = arrayOf(
                ExtractorEntity::class.java,
                ExtractorLineNumbersEntity::class.java,
                ExtractorMethodEntity::class.java,
                GlobEntity::class.java,
                MacroLocationEntity::class.java,
                MacroSettingsEntity::class.java,
                PredefinedGlobEntity::class.java,
                PredefinedRepositoryEntity::class.java,
                RepositoryEntity::class.java,
                RepositoryUsageEntity::class.java,
                RepositoryWithNoAuthorizationEntity::class.java,
                RepositoryWithSshKeyEntity::class.java,
                RepositoryWithUsernameAndPasswordEntity::class.java,
                TemporaryEditBranchEntity::class.java)
        override fun update(entityManager: EntityManager) {
            entityManager.migrate(*entities)
        }
    }
}

