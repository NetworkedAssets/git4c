package com.networkedassets.git4c.ao.upgrade

import com.atlassian.activeobjects.external.ActiveObjects
import com.atlassian.activeobjects.external.ModelVersion
import com.atlassian.activeobjects.test.TestActiveObjects
import com.networkedassets.git4c.infrastructure.database.ao.upgrade.v5.MacroSettingsUpgradeTask005
import com.networkedassets.git4c.infrastructure.database.ao.upgrade.v5.after.DocumentationsMacroSettingsAfter
import com.networkedassets.git4c.infrastructure.database.ao.upgrade.v5.after.NoAuthEntityAfter
import com.networkedassets.git4c.infrastructure.database.ao.upgrade.v5.after.SSHAuthEntityAfter
import com.networkedassets.git4c.infrastructure.database.ao.upgrade.v5.after.UsernamePasswordAuthEntityAfter
import com.networkedassets.git4c.infrastructure.database.ao.upgrade.v5.before.DocumentationsMacroSettingsBefore
import com.networkedassets.git4c.infrastructure.database.ao.upgrade.v5.before.NoAuthEntityBefore
import com.networkedassets.git4c.infrastructure.database.ao.upgrade.v5.before.SSHAuthEntityBefore
import com.networkedassets.git4c.infrastructure.database.ao.upgrade.v5.before.UsernamePasswordAuthEntityBefore
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
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@RunWith(ActiveObjectsJUnitRunner::class)
@Jdbc(H2Memory::class)
@Data
@NameConverters
class DocumentationsMacroSettingsUpgradeTask005Test {

    private lateinit var entityManager: EntityManager

    private lateinit var ao: ActiveObjects

    private val entities = arrayOf(DocumentationsMacroSettingsBefore::class.java, NoAuthEntityBefore::class.java, SSHAuthEntityBefore::class.java, UsernamePasswordAuthEntityBefore::class.java)

    private val migration = MacroSettingsUpgradeTask005()

    private val modelVersion = ModelVersion.valueOf("4")

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
        ao.migrate(*entities)

        entities.forEach {
            ao.delete(*ao.find(it))
        }

        val noauth = ao.create(NoAuthEntityBefore::class.java)
        noauth.save()

        val sshkey = ao.create(SSHAuthEntityBefore::class.java)
        sshkey.key = "KEY"
        sshkey.save()

        val usernamepassword = ao.create(UsernamePasswordAuthEntityBefore::class.java)
        usernamepassword.username = "LOGIN"
        usernamepassword.password = "PASS"
        usernamepassword.save()

        val s1 = ao.create(DocumentationsMacroSettingsBefore::class.java)
        s1.securityKey = "KEY1"
        s1.path = "PATH1"
        s1.uuid = "1"
        s1.auth = noauth
        s1.save()

        val s2 = ao.create(DocumentationsMacroSettingsBefore::class.java)
        s2.securityKey = "KEY2"
        s2.path = "PATH2"
        s2.uuid = "2"
        s2.auth = sshkey
        s2.save()

        val s3 = ao.create(DocumentationsMacroSettingsBefore::class.java)
        s3.securityKey = "KEY3"
        s3.path = "PATH3"
        s3.uuid = "3"
        s3.auth = usernamepassword
        s3.save()

        migration.upgrade(modelVersion, ao)

        val ns1 = ao.find(DocumentationsMacroSettingsAfter::class.java).first { it.uuid == "1" }!!
        val ns2 = ao.find(DocumentationsMacroSettingsAfter::class.java).first { it.uuid == "2" }!!
        val ns3 = ao.find(DocumentationsMacroSettingsAfter::class.java).first { it.uuid == "3" }!!

        val nnoauth = ao.find(NoAuthEntityAfter::class.java)[0]
        val nsshkey = ao.find(SSHAuthEntityAfter::class.java)[0]
        val nusernamepassword = ao.find(UsernamePasswordAuthEntityAfter::class.java)[0]

        assertNotNull(ns1.repository)
        assertNotNull(ns2.repository)
        assertNotNull(ns3.repository)

        assertNotNull(nnoauth.uuid)
        assertNotNull(nsshkey.uuid)
        assertNotNull(nusernamepassword.uuid)

        assertNotNull(nnoauth.path)
        assertNotNull(nsshkey.path)
        assertNotNull(nusernamepassword.path)

        assertNotNull(nnoauth.securityKey)
        assertNotNull(nsshkey.securityKey)
        assertNotNull(nusernamepassword.securityKey)


        assertEquals(ns1.repository, nnoauth.uuid)
        assertEquals(ns2.repository, nsshkey.uuid)
        assertEquals(ns3.repository, nusernamepassword.uuid)

        assertEquals("PATH1", nnoauth.path)
        assertEquals("PATH2", nsshkey.path)
        assertEquals("PATH3", nusernamepassword.path)

        assertEquals("KEY1", nnoauth.securityKey)
        assertEquals("KEY2", nsshkey.securityKey)
        assertEquals("KEY3", nusernamepassword.securityKey)
    }
}
