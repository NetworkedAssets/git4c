package com.networkedassets.git4c.core

import com.networkedassets.git4c.application.PluginComponents
import com.networkedassets.git4c.boundary.GetDocumentationsMacroByDocumentationsMacroIdQuery
import com.networkedassets.git4c.boundary.outbound.DocumentationsMacro
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.data.RepositoryWithUsernameAndPassword
import com.networkedassets.git4c.test.UseCaseTest
import org.junit.Test
import kotlin.test.assertTrue

class GetDocumentationsMacroByDocumentationsMacroIdUseCaseTest : UseCaseTest<GetDocumentationsMacroByDocumentationsMacroIdUseCase>() {

    override fun getUseCase(plugin: PluginComponents): GetDocumentationsMacroByDocumentationsMacroIdUseCase {
        return GetDocumentationsMacroByDocumentationsMacroIdUseCase(plugin.refreshProcess, plugin.macroSettingsCachableDatabase, plugin.globsForMacroDatabase, plugin.repositoryDatabase)
    }

    @Test
    fun `Not Found Exception when there is no macro with such an Macro Id present`() {
        var result = useCase.execute(GetDocumentationsMacroByDocumentationsMacroIdQuery("abc"))

        assertTrue { result.component1() == null }
        assertTrue { result.component2() is NotFoundException }
    }

    @Test
    fun `Macro is return when present in database`() {
        val repository = RepositoryWithUsernameAndPassword("1", "src/test/resources", "user", "pass123")
        val settings = MacroSettings("1", repository.uuid, "master", "item", null)
        components.macroSettingsCachableDatabase.insert("abc", settings)
        components.repositoryDatabase.insert("1", repository)

        var result = useCase.execute(GetDocumentationsMacroByDocumentationsMacroIdQuery("abc"))

        assertTrue { result.component1() is DocumentationsMacro }
        assertTrue { result.component2() == null }
    }
}