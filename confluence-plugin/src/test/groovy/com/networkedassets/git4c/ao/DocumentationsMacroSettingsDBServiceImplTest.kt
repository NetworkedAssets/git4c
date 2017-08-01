package com.networkedassets.git4c.ao

import com.atlassian.activeobjects.external.ActiveObjects
import net.java.ao.test.junit.ActiveObjectsJUnitRunner
import org.junit.runner.RunWith
import com.atlassian.activeobjects.test.TestActiveObjects
import com.networkedassets.git4c.data.macro.*
import com.networkedassets.git4c.infrastructure.database.ao.*
import org.junit.Assert.*
import net.java.ao.EntityManager
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue


@RunWith(ActiveObjectsJUnitRunner::class)
class DocumentationsMacroSettingsDBServiceImplTest {

    private lateinit var entityManager: EntityManager

    private lateinit var ao: ActiveObjects

    private lateinit var macroSettingsDBService: ConfluenceActiveObjectDocumentationsMacroSettings

    @Before
    fun setUp() {
        assertNotNull(entityManager)
        ao = TestActiveObjects(entityManager)
        macroSettingsDBService = ConfluenceActiveObjectDocumentationsMacroSettings(ao)
        ao.migrate(DocumentationsMacroSettingsEntity::class.java, AuthEntity::class.java, NoAuthEntity::class.java, SSHAuthEntity::class.java, UsernamePasswordAuthEntity::class.java)
    }

    @Test
    fun testAdd() {

        assertNull(macroSettingsDBService.getSettings("1"))
        assertTrue(ao.count(NoAuthEntity::class.java) == 0)
        assertTrue(ao.count(SSHAuthEntity::class.java) == 0)
        assertTrue(ao.count(UsernamePasswordAuthEntity::class.java) == 0)

        val settings = EncryptedDocumentationsMacroSettings("1", "", NoAuthCredentials(), "", "", "", "")

        macroSettingsDBService.add(settings)

        assertNotNull(macroSettingsDBService.getSettings("1"))
        assertTrue(ao.count(NoAuthEntity::class.java) == 1)
        assertTrue(ao.count(SSHAuthEntity::class.java) == 0)
        assertTrue(ao.count(UsernamePasswordAuthEntity::class.java) == 0)

        val s = macroSettingsDBService.getSettings("1")
        assertNotNull(s)

        val s2 = ao.find(NoAuthEntity::class.java)[0].macroSettings
        assertNotNull(s2)
    }

    @Test
    fun removeTest() {

        macroSettingsDBService.add(EncryptedDocumentationsMacroSettings("1", "", NoAuthCredentials(), "", "", "", ""))

        assertEquals(1, ao.count(DocumentationsMacroSettingsEntity::class.java))
        assertEquals(1, ao.count(NoAuthEntity::class.java))

        macroSettingsDBService.remove("1")

        assertEquals(0, ao.count(DocumentationsMacroSettingsEntity::class.java))
        assertEquals(0, ao.count(NoAuthEntity::class.java))

    }

    @Test
    fun removeAll() {

        macroSettingsDBService.add(EncryptedDocumentationsMacroSettings("2", "", NoAuthCredentials(), "", "", "", ""))
        macroSettingsDBService.add(EncryptedDocumentationsMacroSettings("2", "", SshKeyCredentials("asd"), "", "", "", ""))
        macroSettingsDBService.add(EncryptedDocumentationsMacroSettings("3", "", UsernameAndPasswordCredentials("1", "2"), "", "", "", ""))

        assertTrue(ao.count(DocumentationsMacroSettingsEntity::class.java) == 3)
        assertTrue(ao.count(NoAuthEntity::class.java) == 1)
        assertTrue(ao.count(SSHAuthEntity::class.java) == 1)
        assertTrue(ao.count(UsernamePasswordAuthEntity::class.java) == 1)

        macroSettingsDBService.removeAll()

        assertEquals(0, ao.count(DocumentationsMacroSettingsEntity::class.java))
        assertEquals(0, ao.count(NoAuthEntity::class.java))
        assertEquals(0, ao.count(SSHAuthEntity::class.java))
        assertEquals(0, ao.count(UsernamePasswordAuthEntity::class.java))
    }

    @Test
    fun deserializationTest() {
        macroSettingsDBService.add(EncryptedDocumentationsMacroSettings("1", "", NoAuthCredentials(), "", "", "", ""))
        macroSettingsDBService.add(EncryptedDocumentationsMacroSettings("2", "", SshKeyCredentials("asd"), "", "", "", ""))
        macroSettingsDBService.add(EncryptedDocumentationsMacroSettings("3", "", UsernameAndPasswordCredentials("1", "2"), "", "", "", ""))

        assertNotNull(macroSettingsDBService.getSettings("1"))
        assertNotNull(macroSettingsDBService.getSettings("2"))
        assertNotNull(macroSettingsDBService.getSettings("3"))
    }
}
