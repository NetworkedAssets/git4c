package com.networkedassets.git4c.core

import com.networkedassets.git4c.application.PluginComponents
import com.networkedassets.git4c.boundary.PublishFileCommand
import com.networkedassets.git4c.boundary.PublishFileResultRequest
import com.networkedassets.git4c.boundary.inbound.FileToSave
import com.networkedassets.git4c.core.business.User
import com.networkedassets.git4c.core.bussiness.ComputationCache
import com.networkedassets.git4c.data.MacroLocation
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.test.AsyncResultUseCaseTest
import com.nhaarman.mockito_kotlin.whenever

class PublishFileResultUseCaseTest : AsyncResultUseCaseTest<PublishFileUseCase, PublishFileResultUseCase, PublishFileCommand, Unit, PublishFileResultRequest>() {

    override fun getAnswerCache(): ComputationCache<Unit> {
        return components.async.publishFileComputationCache
    }

    override fun getResultRequest(requestId: String): PublishFileResultRequest {
        return PublishFileResultRequest(requestId)
    }

    override fun getExpectedProperAnswer() {
        return Unit
    }

    override fun getCommandForProperAnswer(): PublishFileCommand {
        val macroSettingsId = "msId"
        val repositoryId = "rId"
        val macroSettings = MacroSettings(macroSettingsId, repositoryId, "branch", "", null, null)
        val repositorySettings = RepositoryWithNoAuthorization(repositoryId, "path", true)
        val fileToSave = FileToSave("file.txt", "old content\nnew content", "Edit file.txt", null)
        val location = MacroLocation(macroSettingsId, "page_1", "space_2")
        components.providers.macroSettingsProvider.put(macroSettingsId, macroSettings)
        components.providers.repositoryProvider.put(repositoryId, repositorySettings)
        components.database.macroLocationDatabase.put(location.uuid, location)
        // TODO: Don't use mockito!!! Use mocked interfaces imlementation!
        whenever(components.utils.userManager.getUser("user")).thenReturn(User("User", "user@user.com"))
        return PublishFileCommand("user", macroSettingsId, fileToSave)
    }

    override fun getAnswerUseCase(plugin: PluginComponents): PublishFileResultUseCase {
        return PublishFileResultUseCase(plugin.bussines)
    }

    override fun getUseCase(plugin: PluginComponents): PublishFileUseCase {
        return PublishFileUseCase(plugin.bussines)
    }

}
