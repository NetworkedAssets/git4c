package com.networkedassets.git4c.infrastructure.database.ao.upgrade

import com.atlassian.activeobjects.external.ActiveObjects
import com.atlassian.activeobjects.external.ModelVersion
import com.atlassian.activeobjects.test.TestActiveObjects
import com.networkedassets.git4c.infrastructure.database.ao.upgrade.v10.*
import net.java.ao.EntityManager
import net.java.ao.test.converters.NameConverters
import net.java.ao.test.jdbc.Data
import net.java.ao.test.jdbc.DatabaseUpdater
import net.java.ao.test.jdbc.H2Memory
import net.java.ao.test.jdbc.Jdbc
import net.java.ao.test.junit.ActiveObjectsJUnitRunner
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(ActiveObjectsJUnitRunner::class)
@Jdbc(H2Memory::class)
@Data(MacroSettingsUpgradeTask010Test.TestDatabaseUpdater::class)
@NameConverters
class MacroSettingsUpgradeTask010Test {

    private lateinit var entityManager: EntityManager

    private lateinit var ao: ActiveObjects

    private val migration = MacroSettingsUpgradeTask010()

    private val modelVersion = ModelVersion.valueOf("9")

    @Before
    fun setUp() {
        Assert.assertNotNull(entityManager)
        ao = TestActiveObjects(entityManager)
    }

    @Test
    fun migrationTest() {

        val r1 = ao.create(RepositoryWithNoAuthorizationEntityBefore::class.java)
        val r2 = ao.create(RepositoryWithUsernameAndPasswordEntityBefore::class.java)
        val r3 = ao.create(RepositoryWithSshKeyEntityBefore::class.java)

        r1.save()
        r2.save()
        r3.save()

        assertThat(ao.find(RepositoryWithNoAuthorizationEntityBefore::class.java)).hasSize(1)
        assertThat(ao.find(RepositoryWithUsernameAndPasswordEntityBefore::class.java)).hasSize(1)
        assertThat(ao.find(RepositoryWithSshKeyEntityBefore::class.java)).hasSize(1)

        migration.upgrade(modelVersion, ao)

        assertThat(ao.find(RepositoryWithNoAuthorizationEntityAfter::class.java)).hasSize(1)
        assertThat(ao.find(RepositoryWithUsernameAndPasswordEntityAfter::class.java)).hasSize(1)
        assertThat(ao.find(RepositoryWithSshKeyEntityAfter::class.java)).hasSize(1)

        assertThat(ao.find(RepositoryWithNoAuthorizationEntityAfter::class.java)[0].editable).isFalse()
        assertThat(ao.find(RepositoryWithUsernameAndPasswordEntityAfter::class.java)[0].editable).isFalse()
        assertThat(ao.find(RepositoryWithSshKeyEntityAfter::class.java)[0].editable).isFalse()

    }

    class TestDatabaseUpdater : DatabaseUpdater {
        private val entities = arrayOf(RepositoryWithNoAuthorizationEntityBefore::class.java,
                RepositoryWithUsernameAndPasswordEntityBefore::class.java,
                RepositoryWithSshKeyEntityBefore::class.java)
        override fun update(entityManager: EntityManager) {
            entityManager.migrate(*entities)
        }
    }

}
