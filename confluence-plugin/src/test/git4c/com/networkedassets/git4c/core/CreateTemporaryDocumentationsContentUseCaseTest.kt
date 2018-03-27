package com.networkedassets.git4c.core

import com.networkedassets.git4c.application.PluginComponents
import com.networkedassets.git4c.boundary.CreateTemporaryDocumentationsContentCommand
import com.networkedassets.git4c.boundary.outbound.Id
import com.networkedassets.git4c.data.MacroLocation
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.data.PageAndSpacePermissionsForUser
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.test.UseCaseTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import kotlin.test.assertNotNull

class CreateTemporaryDocumentationsContentUseCaseTest : UseCaseTest<CreateTemporaryDocumentationsContentUseCase>() {


    override fun getUseCase(plugin: PluginComponents): CreateTemporaryDocumentationsContentUseCase {
        return CreateTemporaryDocumentationsContentUseCase(plugin.bussines)
    }

    @Test
    fun `Temporary macro should be created`() {
        components.database.macroLocationDatabase.put("macro_1", MacroLocation("macro_1", "page_1", "space_2"))
        components.providers.macroSettingsProvider.put("macro_1", MacroSettings("macro_1", "repository_1", "master", "default_file", null, null))
        components.providers.repositoryProvider.put("repository_1", RepositoryWithNoAuthorization("repository_1", "src/test/resources", false))
        val permission = PageAndSpacePermissionsForUser("page_1", "space_2", "test", true)
        components.cache.pageAndSpacePermissionsForUserCache.put(permission.uuid, permission)

        val answer = useCase.execute(CreateTemporaryDocumentationsContentCommand("macro_1", "master", "test"))
        assertNotNull(answer.component1())
        assertThat(answer.get()).isInstanceOf(Id::class.java)
    }

}