package com.networkedassets.git4c.ao.upgrade

import com.atlassian.activeobjects.external.ActiveObjects
import com.atlassian.activeobjects.external.ModelVersion
import com.atlassian.activeobjects.test.TestActiveObjects
import com.networkedassets.git4c.infrastructure.database.ao.upgrade.v7.PredefinedGlobEntity
import com.networkedassets.git4c.infrastructure.database.ao.upgrade.v7.PredefinedGlobsUpgradeTask007
import net.java.ao.EntityManager
import net.java.ao.test.converters.NameConverters
import net.java.ao.test.jdbc.Data
import net.java.ao.test.jdbc.H2Memory
import net.java.ao.test.jdbc.Jdbc
import net.java.ao.test.junit.ActiveObjectsJUnitRunner
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(ActiveObjectsJUnitRunner::class)
@Jdbc(H2Memory::class)
@Data
@NameConverters
class PredefinedGlobsUpgradeTask007Test {

    private lateinit var entityManager: EntityManager

    private lateinit var ao: ActiveObjects

    private val migration = PredefinedGlobsUpgradeTask007()

    private val modelVersion = ModelVersion.valueOf("6")

    @Before
    fun setUp() {
        Assert.assertNotNull(entityManager)
        ao = TestActiveObjects(entityManager)
    }

    @Test
    fun emptyDatabaseMigrationTest() {
        // when
        migration.upgrade(modelVersion, ao)

        // then
        val globs = ao.find(PredefinedGlobEntity::class.java)
        assertTrue(globs.size == 5)
        migration.globsMap.forEach { globFromMigration ->
            assertNotNull(globs.firstOrNull { it.uuid != null && it.name == globFromMigration.key && it.glob.contains(globFromMigration.value) })
        }
    }
}