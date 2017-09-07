package com.networkedassets.git4c.test

import com.networkedassets.git4c.application.PluginComponents
import com.networkedassets.git4c.core.business.DefaultGlobsMap
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class UseCaseAssertions(val components: PluginComponents) {

    val defaultGlobsMap = DefaultGlobsMap().defaultGlobs

    fun thereAreOnlyDefultGlobsInDatabase() {
        val predefinedGlobsInDatabase = components.predefinedGlobsDatabase.getAll()
        assertTrue(predefinedGlobsInDatabase.size == 5)

        defaultGlobsMap.forEach { globFromDefault ->
            assertNotNull(predefinedGlobsInDatabase.firstOrNull { it.name == globFromDefault.key && it.glob.contains(globFromDefault.value) })
        }
    }

    fun thereIsNoDataInDatabase() {
        assertTrue(components.macroSettingsDatabase.getAll().isEmpty())
        assertTrue(components.predefinedGlobsDatabase.getAll().isEmpty())
        assertTrue(components.repositoryDatabase.getAll().isEmpty())
        assertTrue(components.predefinedRepositoryDatabase.getAll().isEmpty())
        assertTrue(components.globsForMacroDatabase.getAll().isEmpty())
        assertTrue(components.encryptedRepositoryDatabase.getAll().isEmpty())
        assertTrue(components.macroSettingsCachableDatabase.getAll().isEmpty())
        assertTrue(components.macroSettingsCache.getAll().isEmpty())
        assertTrue(components.temporaryIdCache.getAll().isEmpty())
    }
}