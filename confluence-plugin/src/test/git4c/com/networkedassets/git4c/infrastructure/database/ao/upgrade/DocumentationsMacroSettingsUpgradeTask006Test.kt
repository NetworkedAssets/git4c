package com.networkedassets.git4c.ao.upgrade

import com.atlassian.activeobjects.external.ActiveObjects
import com.atlassian.activeobjects.external.ModelVersion
import com.atlassian.activeobjects.test.TestActiveObjects
import com.networkedassets.git4c.infrastructure.database.ao.upgrade.v6.*
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
import kotlin.test.assertNull

@RunWith(ActiveObjectsJUnitRunner::class)
@Jdbc(H2Memory::class)
@Data(DocumentationsMacroSettingsUpgradeTask006Test.TestDatabaseUpdater::class)
@NameConverters
class DocumentationsMacroSettingsUpgradeTask006Test {

    private lateinit var entityManager: EntityManager

    private lateinit var ao: ActiveObjects

    @Before
    fun setUp() {
        Assert.assertNotNull(entityManager)
        ao = TestActiveObjects(entityManager)
    }

    @Test
    fun migrationTest() {

        val settingsWGlob = ao.create(MacroSettingsEntityBefore::class.java)
        settingsWGlob.uuid = "1"
        settingsWGlob.save()

        MacroSettingsUpgradeTask006().upgrade(ModelVersion.valueOf("1"), ao)

        assertEquals(1, ao.find(MacroSettingsEntityAfter::class.java).size)
        assertNull(ao.find(MacroSettingsEntityAfter::class.java).first().method)

    }

    class TestDatabaseUpdater : DatabaseUpdater {
        private val entities = arrayOf(MacroSettingsEntityBefore::class.java)
        override fun update(entityManager: EntityManager) {
            entityManager.migrate(*entities)
        }
    }

}
