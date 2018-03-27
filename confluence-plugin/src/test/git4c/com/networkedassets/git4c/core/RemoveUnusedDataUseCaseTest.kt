package com.networkedassets.git4c.core

import com.networkedassets.git4c.application.PluginComponents
import com.networkedassets.git4c.boundary.RemoveUnusedDataCommand
import com.networkedassets.git4c.core.business.Page
import com.networkedassets.git4c.core.datastore.extractors.LineNumbersExtractorData
import com.networkedassets.git4c.core.datastore.extractors.MethodExtractorData
import com.networkedassets.git4c.data.GlobForMacro
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.data.PredefinedRepository
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.test.UseCaseTest
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RemoveUnusedDataUseCaseTest : UseCaseTest<RemoveUnusedDataUseCase>() {

    override fun getUseCase(plugin: PluginComponents): RemoveUnusedDataUseCase {
        whenever(components.utils.pageManager.getAllPageKeys()).thenReturn(listOf(1))
        // Will give macro with id=ms1 on page in system
        val page = Page("1", "Page1", "url://1",
                "<ac:structured-macro ac:name=\"Git4C Single File\" ac:schema-version=\"1\"\n ac:macro-id=\"ms1\">\n <ac:parameter ac:name=\"uuid\">ms1</ac:parameter>\n</ac:structured-macro>"
        )
        whenever(components.utils.pageManager.getPage(1)).thenReturn(page)
        return RemoveUnusedDataUseCase(plugin.bussines)
    }


    @Test
    fun `Remove MacroSettings that are not in System`() {

        val macroSettings1 = MacroSettings("ms1", null, "", "", null, null)
        val macroSettings2 = MacroSettings("ms2", null, "", "", null, null)

        components.providers.macroSettingsProvider.put("ms1", macroSettings1)
        components.providers.macroSettingsProvider.put("ms2", macroSettings2)

        useCase.execute()

        assertTrue(components.providers.macroSettingsProvider.isAvailable("ms1"))
        assertEquals(macroSettings1, components.providers.macroSettingsProvider.get("ms1"))
        assertFalse(components.providers.macroSettingsProvider.isAvailable("ms2"))
    }

    @Test
    fun `Remove repositories without corresponding MacroSettings`() {

        val repository1 = RepositoryWithNoAuthorization("repo1", "", false)
        val repository2 = RepositoryWithNoAuthorization("repo2", "", false)
        val repository3 = RepositoryWithNoAuthorization("repo3", "", false)

        components.providers.repositoryProvider.put("repo1", repository1)
        components.providers.repositoryProvider.put("repo2", repository2)
        components.providers.repositoryProvider.put("repo3", repository3)

        val macroSettings1 = MacroSettings("ms1", "repo2", "", "", null, null)
        val macroSettings2 = MacroSettings("ms2", "repo3", "", "", null, null)

        components.providers.macroSettingsProvider.put("ms1", macroSettings1)
        components.providers.macroSettingsProvider.put("ms2", macroSettings2)

        useCase.execute()

        assertEquals(1, components.providers.macroSettingsProvider.getAll().size)
        assertEquals(1, components.providers.repositoryProvider.getAll().size)
        assertEquals(repository2, components.providers.repositoryProvider.getAll().first())

    }

    @Test
    fun `Remove repositories without corresponding predefined repository`() {

        val repository1 = RepositoryWithNoAuthorization("repo1", "", false)
        val repository2 = RepositoryWithNoAuthorization("repo2", "", false)
        val repository3 = RepositoryWithNoAuthorization("repo3", "", false)
        val macroSettings1 = MacroSettings("ms1", "repo2", "", "", null, null)
        val predefinedRepo1 = PredefinedRepository("pr1", "repo1", "repository 1")

        components.providers.repositoryProvider.put("repo1", repository1)
        components.providers.repositoryProvider.put("repo2", repository2)
        components.providers.repositoryProvider.put("repo3", repository3)
        components.providers.macroSettingsProvider.put("ms1", macroSettings1)
        components.database.predefinedRepositoryDatabase.put("pr1", predefinedRepo1)

        useCase.execute()

        assertEquals(1, components.providers.macroSettingsProvider.getAll().size)
        assertEquals(2, components.providers.repositoryProvider.getAll().size)
        assertEquals(1, components.database.predefinedRepositoryDatabase.getAll().size)
        assertEquals(setOf(repository1, repository2), components.providers.repositoryProvider.getAll().toSet())

    }

    @Test
    fun `Remove globs without corresponding macroSettings`() {

        val glob1 = GlobForMacro("g1", "ms1", "glob1")
        val glob12 = GlobForMacro("g12", "ms1", "glob2")
        val glob2 = GlobForMacro("g3", "ms2", "glob3")

        val macroSettings1 = MacroSettings("ms1", "", "", "", null, null)

        components.providers.globsForMacroProvider.put("g1", glob1)
        components.providers.globsForMacroProvider.put("gl2", glob12)
        components.providers.globsForMacroProvider.put("g3", glob2)

        components.providers.macroSettingsProvider.put("ms1", macroSettings1)

        useCase.execute()

        assertEquals(1, components.providers.macroSettingsProvider.getAll().size)
        assertEquals(2, components.providers.globsForMacroProvider.getAll().size)

        assertEquals(setOf(glob1, glob12), components.providers.globsForMacroProvider.getAll().toSet())

    }

    @Test
    fun `Remove extractors without corresponding macroSettings`() {

        val extractor1 = LineNumbersExtractorData("ex1", 1, 134)
        val extractor2 = MethodExtractorData("ex2", "method1")
        val extractor3 = LineNumbersExtractorData("ex3", 0, 123)

        val macroSettings = MacroSettings("ms1", "", "", "", "ex3", null)

        components.database.extractorDataDatabase.put("ex1", extractor1)
        components.database.extractorDataDatabase.put("ex2", extractor2)
        components.database.extractorDataDatabase.put("ex3", extractor3)

        components.providers.macroSettingsProvider.put("ms1", macroSettings)

        useCase.execute()

        assertEquals(1, components.providers.macroSettingsProvider.getAll().size)
        assertEquals(1, components.database.extractorDataDatabase.getAll().size)
        assertEquals(extractor3, components.database.extractorDataDatabase.getAll()[0])


    }

    private fun RemoveUnusedDataUseCase.execute() = this.execute(RemoveUnusedDataCommand())

}
