package com.networkedassets.git4c.infrastructure.database.ao.upgrade

import com.atlassian.activeobjects.external.ActiveObjects
import com.atlassian.activeobjects.external.ModelVersion
import com.atlassian.activeobjects.test.TestActiveObjects
import com.networkedassets.git4c.infrastructure.database.ao.upgrade.v8.*
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
import java.util.*
import kotlin.test.assertTrue

@RunWith(ActiveObjectsJUnitRunner::class)
@Jdbc(H2Memory::class)
@Data
@NameConverters
class PredefinedRepositoriesUpgradeTask008Test {

    private lateinit var entityManager: EntityManager

    private lateinit var ao: ActiveObjects

    private val migration = PredefinedRepositoryUpgradeTask008()

    private val modelVersion = ModelVersion.valueOf("7")

    @Before
    fun setUp() {
        Assert.assertNotNull(entityManager)
        ao = TestActiveObjects(entityManager)
        ao.migrate(PredefinedRepositoryEntityBefore::class.java)
        ao.migrate(RepositoryWithUsernameAndPasswordEntity::class.java)
        ao.migrate(RepositoryWithSshKeyEntity::class.java)
        ao.migrate(RepositoryWithNoAuthorizationEntity::class.java)
    }

    @Test
    fun `All predefined repositories have names`() {
        // given
        val existingRepository = ao.create(RepositoryWithNoAuthorizationEntity::class.java)
        existingRepository.uuid = UUID.randomUUID().toString()
        existingRepository.path = UUID.randomUUID().toString()
        existingRepository.securityKey = UUID.randomUUID().toString()
        existingRepository.save()
        val existingPredefinedRepository = ao.create(PredefinedRepositoryEntityBefore::class.java)
        existingPredefinedRepository.uuid = UUID.randomUUID().toString()
        existingPredefinedRepository.repository = existingRepository.uuid
        existingPredefinedRepository.save()

        // when
        migration.upgrade(modelVersion, ao)

        // then
        val newPrededine = ao.find(PredefinedRepositoryEntityAfter::class.java)[0]
        assertTrue(newPrededine.name == existingRepository.path)

    }
}