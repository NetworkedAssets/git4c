package com.networkedassets.git4c.core

import com.networkedassets.git4c.boundary.RemoveUnusedDataCommand
import com.networkedassets.git4c.core.datastore.extractors.ExtractorData
import com.networkedassets.git4c.core.datastore.extractors.LineNumbersExtractorData
import com.networkedassets.git4c.core.datastore.extractors.MethodExtractorData
import com.networkedassets.git4c.core.datastore.repositories.*
import com.networkedassets.git4c.core.process.IGetAllMacrosInSystem
import com.networkedassets.git4c.data.GlobForMacro
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.data.PredefinedRepository
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.data.encryption.EncryptedRepository
import com.networkedassets.git4c.infrastructure.mocks.infrastructure.MapDatabase
import com.networkedassets.git4c.infrastructure.mocks.infrastructure.SimpleEncryptor
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RemoveUnusedDataUseCaseTest {

    private val globsDatabase = object : MapDatabase<GlobForMacro>(), GlobForMacroDatabase {
        override fun getByMacro(macroUuid: String) = getAll().filter { it.macroSettingsUuid == macroUuid }
    }
    private val predefinedRepositoryDatabase = object : MapDatabase<PredefinedRepository>(), PredefinedRepositoryDatabase {}
    private val encryptedRepositoryDatabase = object : MapDatabase<EncryptedRepository>(), EncryptedRepositoryDatabase {}
    private val macroSettingsDatabase = object : MapDatabase<MacroSettings>(), MacroSettingsDatabase {
        override fun getByRepository(uuid: String) = getAll().filter { it.repositoryUuid == uuid }
    }

    private val extractorDataDatabase = object : MapDatabase<ExtractorData>(), ExtractorDataDatabase {}

    private val repositoryDatabase = RepositoryDatabase(SimpleEncryptor(), encryptedRepositoryDatabase)

    private val process = mock<IGetAllMacrosInSystem>()

    var useCase = RemoveUnusedDataUseCase(macroSettingsDatabase, repositoryDatabase, predefinedRepositoryDatabase,
            globsDatabase, extractorDataDatabase, process)

    @Before
    fun setup() {
        globsDatabase.removeAll()
        predefinedRepositoryDatabase.removeAll()
        encryptedRepositoryDatabase.removeAll()
        macroSettingsDatabase.removeAll()
        repositoryDatabase.removeAll()
    }

    @Test
    fun `Remove MacroSettings that are not in System`() {

        whenever(process.extract()).thenReturn(listOf("ms1"))

        val macroSettings1 = MacroSettings("ms1", null, "", "", null, null)
        val macroSettings2 = MacroSettings("ms2", null, "", "", null, null)

        macroSettingsDatabase.put("ms1", macroSettings1)
        macroSettingsDatabase.put("ms2", macroSettings2)

        useCase.execute()

        assertTrue(macroSettingsDatabase.isAvailable("ms1"))
        assertEquals(macroSettings1, macroSettingsDatabase.get("ms1"))
        assertFalse(macroSettingsDatabase.isAvailable("ms2"))
    }

    @Test
    fun `Remove repositories without corresponding MacroSettings`() {

        whenever(process.extract()).thenReturn(listOf("ms1"))

        val repository1 = object : Repository("repo1", "") {}
        val repository2 = object : Repository("repo2", "") {}
        val repository3 = object : Repository("repo3", "") {}

        repositoryDatabase.put("repo1", repository1)
        repositoryDatabase.put("repo2", repository2)
        repositoryDatabase.put("repo3", repository3)

        val macroSettings1 = MacroSettings("ms1", "repo2", "", "", null, null)
        val macroSettings2 = MacroSettings("ms2", "repo3", "", "", null, null)

        macroSettingsDatabase.put("ms1", macroSettings1)
        macroSettingsDatabase.put("ms2", macroSettings2)

        useCase.execute()

        assertEquals(1, macroSettingsDatabase.getAll().size)
        assertEquals(1, repositoryDatabase.getAll().size)
        assertEquals(repository2, repositoryDatabase.getAll().first())

    }

    @Test
    fun `Remove repositories without corresponding predefined repository`() {

        whenever(process.extract()).thenReturn(listOf("ms1"))

        val repository1 = object : Repository("repo1", "") {}
        val repository2 = object : Repository("repo2", "") {}
        val repository3 = object : Repository("repo3", "") {}
        val macroSettings1 = MacroSettings("ms1", "repo2", "", "", null, null)
        val predefinedRepo1 = PredefinedRepository("pr1", "repo1", "repository 1")

        repositoryDatabase.put("repo1", repository1)
        repositoryDatabase.put("repo2", repository2)
        repositoryDatabase.put("repo3", repository3)
        macroSettingsDatabase.put("ms1", macroSettings1)
        predefinedRepositoryDatabase.put("pr1", predefinedRepo1)

        useCase.execute()

        assertEquals(1, macroSettingsDatabase.getAll().size)
        assertEquals(2, repositoryDatabase.getAll().size)
        assertEquals(1, predefinedRepositoryDatabase.getAll().size)
        assertEquals(setOf(repository1, repository2), repositoryDatabase.getAll().toSet())

    }

    @Test
    fun `Remove globs without corresponding macroSettings`() {

        whenever(process.extract()).thenReturn(listOf("ms1"))

        val glob1 = GlobForMacro("g1", "ms1", "glob1")
        val glob12 = GlobForMacro("g12", "ms1", "glob2")
        val glob2 = GlobForMacro("g3", "ms2", "glob3")

        val macroSettings1 = MacroSettings("ms1", "", "", "", null, null)

        globsDatabase.put("g1", glob1)
        globsDatabase.put("gl2", glob12)
        globsDatabase.put("g3", glob2)

        macroSettingsDatabase.put("ms1", macroSettings1)

        useCase.execute()

        assertEquals(1, macroSettingsDatabase.getAll().size)
        assertEquals(2, globsDatabase.getAll().size)

        assertEquals(setOf(glob1, glob12), globsDatabase.getAll().toSet())

    }

    @Test
    fun `Remove extractors without corresponding macroSettings`() {

        whenever(process.extract()).thenReturn(listOf("ms1"))

        val extractor1 = LineNumbersExtractorData("ex1", 1, 134)
        val extractor2 = MethodExtractorData("ex2", "method1")
        val extractor3 = LineNumbersExtractorData("ex3", 0, 123)

        val macroSettings = MacroSettings("ms1", "", "", "", "ex3", null)

        extractorDataDatabase.put("ex1", extractor1)
        extractorDataDatabase.put("ex2", extractor2)
        extractorDataDatabase.put("ex3", extractor3)

        macroSettingsDatabase.put("ms1", macroSettings)

        useCase.execute()

        assertEquals(1, macroSettingsDatabase.getAll().size)
        assertEquals(1, extractorDataDatabase.getAll().size)
        assertEquals(extractor3, extractorDataDatabase.getAll()[0])


    }

    private fun RemoveUnusedDataUseCase.execute() = this.execute(RemoveUnusedDataCommand())

}
