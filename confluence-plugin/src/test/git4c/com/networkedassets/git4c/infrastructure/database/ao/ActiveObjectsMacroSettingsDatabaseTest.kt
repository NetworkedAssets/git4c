package com.networkedassets.git4c.ao

import com.atlassian.activeobjects.external.ActiveObjects
import com.atlassian.activeobjects.test.TestActiveObjects
import com.networkedassets.git4c.ao.ActiveObjectsMacroSettingsDatabaseTest.TestDatabaseUpdater
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.infrastructure.database.ao.*
import com.networkedassets.git4c.infrastructure.database.ao.repository.ConfluenceActiveObjectMacroSettings
import net.java.ao.EntityManager
import net.java.ao.test.converters.NameConverters
import net.java.ao.test.jdbc.Data
import net.java.ao.test.jdbc.DatabaseUpdater
import net.java.ao.test.jdbc.H2Memory
import net.java.ao.test.jdbc.Jdbc
import net.java.ao.test.junit.ActiveObjectsJUnitRunner
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertNull

@RunWith(ActiveObjectsJUnitRunner::class)
@Jdbc(H2Memory::class)
@Data(TestDatabaseUpdater::class)
@NameConverters
class ActiveObjectsMacroSettingsDatabaseTest {

    private lateinit var entityManager: EntityManager
    private lateinit var ao: ActiveObjects
    private lateinit var macroSettingsDatabase: ConfluenceActiveObjectMacroSettings

    @Before
    fun setUp() {
        assertNotNull(entityManager)
        ao = TestActiveObjects(entityManager)
        macroSettingsDatabase = ConfluenceActiveObjectMacroSettings(ao)
        ao.migrate(MacroSettingsEntity::class.java, RepositoryEntity::class.java, RepositoryWithNoAuthorizationEntity::class.java, RepositoryWithSshKeyEntity::class.java, RepositoryWithUsernameAndPasswordEntity::class.java, PredefinedRepositoryEntity::class.java)

    }

    @Test
    fun testAdd() {
        assertNull(macroSettingsDatabase.get("1"))
        val settings = MacroSettings("1", "repository", "branch", "item", "")
        macroSettingsDatabase.insert(settings.uuid, settings)
        assertNotNull(macroSettingsDatabase.get("1"))
    }

    @Test
    fun removeTest() {
        val settings = MacroSettings("1", "repository", "branch", "item", "")
        macroSettingsDatabase.insert(settings.uuid, settings)
        assertEquals(1, ao.count(MacroSettingsEntity::class.java))
        macroSettingsDatabase.remove("1")
        assertEquals(0, ao.count(MacroSettingsEntity::class.java))
    }

    @Test
    fun removeAll() {
        testAdd()
        macroSettingsDatabase.removeAll()
        assertEquals(0, ao.count(MacroSettingsEntity::class.java))
    }

    class TestDatabaseUpdater : DatabaseUpdater {
        private val entities = arrayOf(MacroSettingsEntity::class.java, RepositoryWithNoAuthorizationEntity::class.java, RepositoryWithSshKeyEntity::class.java, RepositoryWithUsernameAndPasswordEntity::class.java, GlobEntity::class.java, PredefinedRepositoryEntity::class.java)
        override fun update(entityManager: EntityManager) {
            entityManager.migrate(*entities)
        }
    }
}
