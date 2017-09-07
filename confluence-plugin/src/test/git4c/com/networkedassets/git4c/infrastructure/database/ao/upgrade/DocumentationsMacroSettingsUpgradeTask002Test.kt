package com.networkedassets.git4c.ao.upgrade

import com.atlassian.activeobjects.external.ActiveObjects
import com.atlassian.activeobjects.external.ModelVersion
import com.atlassian.activeobjects.test.TestActiveObjects
import com.networkedassets.git4c.infrastructure.database.ao.upgrade.v2.DocumentationsMacroSettingsAfter
import com.networkedassets.git4c.infrastructure.database.ao.upgrade.v2.DocumentationsMacroSettingsBefore
import com.networkedassets.git4c.infrastructure.database.ao.upgrade.v2.GlobEntity
import com.networkedassets.git4c.infrastructure.database.ao.upgrade.v2.MacroSettingsUpgradeTask002
import net.java.ao.EntityManager
import net.java.ao.test.converters.NameConverters
import net.java.ao.test.jdbc.Data
import net.java.ao.test.jdbc.DatabaseUpdater
import net.java.ao.test.jdbc.H2Memory
import net.java.ao.test.jdbc.Jdbc
import net.java.ao.test.junit.ActiveObjectsJUnitRunner
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@RunWith(ActiveObjectsJUnitRunner::class)
@Jdbc(H2Memory::class)
@Data(DocumentationsMacroSettingsUpgradeTask002Test.TestDatabaseUpdater::class)
@NameConverters
class DocumentationsMacroSettingsUpgradeTask002Test {

    private lateinit var entityManager: EntityManager

    private lateinit var ao: ActiveObjects

    private val entities = arrayOf(DocumentationsMacroSettingsBefore::class.java)

    @Before
    fun setUp() {
        Assert.assertNotNull(entityManager)
        ao = TestActiveObjects(entityManager)
    }

    @Test
    fun migrationTest() {

        val settingsWGlob = ao.create(DocumentationsMacroSettingsBefore::class.java)
        settingsWGlob.uuid = "1"
        settingsWGlob.glob = "**/*.kt"
        settingsWGlob.save()

        val settingsWOGlob = ao.create(DocumentationsMacroSettingsBefore::class.java)
        settingsWOGlob.uuid = "2"
        settingsWOGlob.glob = ""
        settingsWOGlob.save()

        MacroSettingsUpgradeTask002().upgrade(ModelVersion.valueOf("1"), ao)

        ao.migrate(DocumentationsMacroSettingsAfter::class.java)

        assertEquals(1, ao.find(GlobEntity::class.java).size)

        val settings = ao.find(DocumentationsMacroSettingsAfter::class.java)
        assertEquals(2, settings.size)

        val WGlob = settings.first { it.uuid == "1" }
        val WOGlob = settings.first { it.uuid == "2" }

        assertEquals(0, WOGlob.globs.size)
        assertEquals(1, WGlob.globs.size)
        assertEquals("**/*.kt", WGlob.globs[0].glob)

        ao.delete(settingsWGlob, settingsWOGlob)
    }

    class TestDatabaseUpdater : DatabaseUpdater {
        private val entities = arrayOf(DocumentationsMacroSettingsBefore::class.java)
        override fun update(entityManager: EntityManager) {
            entityManager.migrate(*entities)
        }
    }

}
