package com.networkedassets.git4c.core

import com.networkedassets.git4c.application.PluginComponents
import com.networkedassets.git4c.boundary.CreateTemporaryDocumentationsContentCommand
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.test.UseCaseTest
import org.junit.Test
import kotlin.test.assertNotNull

class CreateTemporaryDocumentationsContentUseCaseTest : UseCaseTest<CreateTemporaryDocumentationsContentUseCase>() {

    override fun getUseCase(plugin: PluginComponents): CreateTemporaryDocumentationsContentUseCase {
        return CreateTemporaryDocumentationsContentUseCase(plugin.macroSettingsCache, plugin.documentsViewCache, plugin.converter, plugin.macroSettingsDatabase, plugin.repositoryDatabase, plugin.globsForMacroDatabase, plugin.idGenerator, plugin.temporaryIdCache, plugin.refreshProcess)
    }

    @Test
    fun `Temporary macro should be created`() {
        components.macroSettingsDatabase.insert("macro_1", MacroSettings("macro_1", "repository_1", "master", "default_file", null))
        components.repositoryDatabase.insert("repository_1", RepositoryWithNoAuthorization("repository_1", "src/test/resources"))

        val answer = useCase.execute(CreateTemporaryDocumentationsContentCommand("macro_1", "master"))

        assertNotNull(answer.component1())
    }
}