package com.networkedassets.git4c.ao.upgrade

import com.atlassian.activeobjects.external.ActiveObjects
import com.atlassian.activeobjects.external.ModelVersion
import com.atlassian.activeobjects.test.TestActiveObjects
import com.networkedassets.git4c.ao.upgrade.DocumentationsMacroSettingsUpgradeTask004Test.TestDatabaseUpdater
import com.networkedassets.git4c.infrastructure.database.ao.upgrade.v4.*
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@RunWith(ActiveObjectsJUnitRunner::class)
@Jdbc(H2Memory::class)
@Data(TestDatabaseUpdater::class)
@NameConverters
class DocumentationsMacroSettingsUpgradeTask004Test {

    private lateinit var entityManager: EntityManager

    private lateinit var ao: ActiveObjects


    private val migration = MacroSettingsUpgradeTask004()

    private val modelVersion = ModelVersion.valueOf("3")

    @Before
    fun setUp() {
        Assert.assertNotNull(entityManager)
        ao = TestActiveObjects(entityManager)
    }

    @Test
    fun emptyDBMigrationTest() {
        migration.upgrade(modelVersion, ao)
    }

    @Test
    fun DBWithDataTest() {

        val settings1 = ao.create(DocumentationsMacroSettingsBefore::class.java)
        settings1.uuid = "1"
        settings1.save()

        val settings2 = ao.create(DocumentationsMacroSettingsBefore::class.java)
        settings2.uuid = "2"
        settings2.save()

        val glob1 = ao.create(GlobEntityBefore::class.java)
        glob1.glob = "glob1"
        glob1.macroSettings = settings1
        glob1.save()

        val glob2 = ao.create(GlobEntityBefore::class.java)
        glob2.glob = "glob2"
        glob2.macroSettings = settings1
        glob2.save()

        val glob3 = ao.create(GlobEntityBefore::class.java)
        glob3.glob = "glob3"
        glob3.save()

        migration.upgrade(modelVersion, ao)

        ao.migrate(DocumentationsMacroSettingsAfter::class.java, GlobEntityAfter::class.java)

        val s1 = ao.find(DocumentationsMacroSettingsAfter::class.java).find { it.uuid == "1" }!!
        val s2 = ao.find(DocumentationsMacroSettingsAfter::class.java).find { it.uuid == "2" }!!

        val gl1 = ao.find(GlobEntityAfter::class.java).find { it.glob == "glob1" }!!
        val gl2 = ao.find(GlobEntityAfter::class.java).find { it.glob == "glob2" }!!
        val gl3 = ao.find(GlobEntityAfter::class.java).find { it.glob == "glob3" }

        assertNotNull(s1)
        assertNotNull(s2)
        assertNotNull(gl1)
        assertNotNull(gl2)
        assertNull(gl3)

        assertNotNull(gl1.uuid)
        assertNotNull(gl2.uuid)

        assertEquals("1", gl1.macro)
        assertEquals("1", gl2.macro)

    }

    @Test
    fun removeDuplicatedGlobsTest() {

        ao.delete(*ao.find(DocumentationsMacroSettingsBefore::class.java))
        ao.delete(*ao.find(GlobEntityBefore::class.java))

        val settings1 = ao.create(DocumentationsMacroSettingsBefore::class.java)
        settings1.uuid = "1"
        settings1.save()

        val glob1 = ao.create(GlobEntityBefore::class.java)
        glob1.glob = "glob1"
        glob1.macroSettings = settings1
        glob1.save()

        val glob2 = ao.create(GlobEntityBefore::class.java)
        glob2.glob = "glob1"
        glob2.macroSettings = settings1
        glob2.save()

        migration.upgrade(modelVersion, ao)

        ao.migrate(DocumentationsMacroSettingsAfter::class.java, GlobEntityAfter::class.java)

        assertEquals(1, ao.find(GlobEntityAfter::class.java).size)

    }

    class TestDatabaseUpdater : DatabaseUpdater {
        private val entities = arrayOf(GlobEntityBefore::class.java, DocumentationsMacroSettingsBefore::class.java)
        override fun update(entityManager: EntityManager) {
            entityManager.migrate(*entities)
        }
    }

}
