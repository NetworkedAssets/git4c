package com.networkedassets.git4c.core

import com.networkedassets.git4c.application.PluginComponents
import com.networkedassets.git4c.boundary.CreateTemporaryDocumentationsContentCommand
import com.networkedassets.git4c.core.process.ICheckUserPermissionProcess
import com.networkedassets.git4c.data.MacroLocation
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.test.UseCaseTest
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import kotlin.test.assertNotNull

class CreateTemporaryDocumentationsContentUseCaseTest : UseCaseTest<CreateTemporaryDocumentationsContentUseCase>() {

    val checkUserPermissionProcess: ICheckUserPermissionProcess = mock()

    override fun getUseCase(plugin: PluginComponents): CreateTemporaryDocumentationsContentUseCase {
        whenever(checkUserPermissionProcess.userHasPermissionToMacro(any(), any())).thenReturn(true)
        return CreateTemporaryDocumentationsContentUseCase(plugin.macroSettingsCache, plugin.documentsViewCache, plugin.converter, plugin.macroSettingsDatabase, plugin.repositoryDatabase, plugin.idGenerator, plugin.temporaryIdCache, checkUserPermissionProcess, plugin.macroViewProcess, plugin.macroLocationDatabase, plugin.globsForMacroDatabase, plugin.globForMacroCache)
    }

    @Test
    fun `Temporary macro should be created`() {
        components.macroLocationDatabase.put("macro_1", MacroLocation("macro_1", "page_1", "space_2"))
        components.macroSettingsDatabase.put("macro_1", MacroSettings("macro_1", "repository_1", "master", "default_file", null, null))
        components.repositoryDatabase.put("repository_1", RepositoryWithNoAuthorization("repository_1", "src/test/resources"))

        val answer = useCase.execute(CreateTemporaryDocumentationsContentCommand("macro_1", "master", ""))
        assertNotNull(answer.component1())
    }
}