package com.networkedassets.git4c.core

import com.networkedassets.git4c.application.PluginComponents
import com.networkedassets.git4c.boundary.GetDocumentationsMacroByDocumentationsMacroIdQuery
import com.networkedassets.git4c.boundary.outbound.DocumentationsMacro
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.process.ICheckUserPermissionProcess
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.data.RepositoryWithUsernameAndPassword
import com.networkedassets.git4c.test.UseCaseTest
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import kotlin.test.assertTrue

class GetDocumentationsMacroByDocumentationsMacroIdUseCaseTest : UseCaseTest<GetDocumentationsMacroByDocumentationsMacroIdUseCase>() {

    val checkUserPermissionProcess: ICheckUserPermissionProcess = mock()

    override fun getUseCase(plugin: PluginComponents): GetDocumentationsMacroByDocumentationsMacroIdUseCase {
        whenever(checkUserPermissionProcess.userHasPermissionToMacro(any(), any())).thenReturn(true)
        return GetDocumentationsMacroByDocumentationsMacroIdUseCase(plugin.refreshProcess, plugin.macroSettingsCachableDatabase, plugin.globsForMacroDatabase, plugin.repositoryDatabase, plugin.extractorDataDatabase, checkUserPermissionProcess)
    }

    @Test
    fun `Not Found Exception when there is no macro with such an Macro Id present`() {
        val result = useCase.execute(GetDocumentationsMacroByDocumentationsMacroIdQuery("abc", null))

        assertTrue { result.component1() == null }
        assertTrue { result.component2() is NotFoundException }
    }

    @Test
    fun `Macro is return when present in database`() {
        val repository = RepositoryWithUsernameAndPassword("1", "src/test/resources", "user", "pass123")
        val settings = MacroSettings("1", repository.uuid, "master", "item", null)
        components.macroSettingsCachableDatabase.insert("abc", settings)
        components.repositoryDatabase.insert("1", repository)

        val result = useCase.execute(GetDocumentationsMacroByDocumentationsMacroIdQuery("abc", null))

        assertTrue { result.component1() is DocumentationsMacro }
        assertTrue { result.component2() == null }
    }
}