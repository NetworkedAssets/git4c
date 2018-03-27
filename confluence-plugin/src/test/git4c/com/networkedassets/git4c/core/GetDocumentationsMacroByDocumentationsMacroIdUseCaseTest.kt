package com.networkedassets.git4c.core

import com.jayway.awaitility.Awaitility.await
import com.networkedassets.git4c.application.PluginComponents
import com.networkedassets.git4c.boundary.GetDocumentationsMacroByDocumentationsMacroIdQuery
import com.networkedassets.git4c.boundary.outbound.DocumentationsMacro
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.test.UseCaseTest
import org.junit.Test
import kotlin.test.assertTrue

class GetDocumentationsMacroByDocumentationsMacroIdUseCaseTest : UseCaseTest<GetDocumentationsMacroByDocumentationsMacroIdUseCase>() {


    override fun getUseCase(plugin: PluginComponents): GetDocumentationsMacroByDocumentationsMacroIdUseCase {
        return GetDocumentationsMacroByDocumentationsMacroIdUseCase(plugin.bussines)
    }

    @Test
    fun `Not Found Exception when there is no macro with such an Macro Id present`() {
        val result = useCase.execute(GetDocumentationsMacroByDocumentationsMacroIdQuery("abc", null))

        assertTrue { result.component1() == null }
        assertTrue { result.component2() is NotFoundException }
    }

    @Test
    fun `Macro is return when present in database`() {
        val repository = RepositoryWithNoAuthorization("1", "src/test/resources", false)
        val settings = MacroSettings("1", repository.uuid, "master", "item", null, null)
        components.providers.macroSettingsProvider.put("1", settings)
        components.providers.repositoryProvider.put("1", repository)
        components.processing.macroViewProcess.prepareMacroToBeViewed("1")

        await().until {
            val result = useCase.execute(GetDocumentationsMacroByDocumentationsMacroIdQuery("1", null))

            assertTrue { result.component1() is DocumentationsMacro }
            assertTrue { result.component2() == null }
        }
    }
}