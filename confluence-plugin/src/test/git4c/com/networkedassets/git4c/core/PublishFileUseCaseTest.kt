package com.networkedassets.git4c.core

import com.atlassian.confluence.core.service.NotAuthorizedException
import com.jayway.awaitility.Awaitility.await
import com.networkedassets.git4c.application.PluginComponents
import com.networkedassets.git4c.boundary.PublishFileCommand
import com.networkedassets.git4c.boundary.PublishFileResultRequest
import com.networkedassets.git4c.boundary.inbound.FileToSave
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.business.User
import com.networkedassets.git4c.data.MacroLocation
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.test.AsyncUseCaseTest
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class PublishFileUseCaseTest : AsyncUseCaseTest<PublishFileUseCase, PublishFileResultUseCase>() {

    override fun getAnswerUseCase(plugin: PluginComponents): PublishFileResultUseCase {
        return PublishFileResultUseCase(plugin.bussines)
    }

    override fun getUseCase(plugin: PluginComponents): PublishFileUseCase {
        return PublishFileUseCase(plugin.bussines)
    }

    @Test
    fun `Anonymous user cannot save file`() {
        val result = useCase.execute(PublishFileCommand(null, "", FileToSave("", "", "", null)))

        assertThat(result.component1()).isNull()
        assertThat(result.component2()).isNotNull().isOfAnyClassIn(NotAuthorizedException::class.java)
    }


    @Test
    fun `When macro doesn't exists exception is thrown`() {
        val macroSettingsId = "msId"
        components.providers.macroSettingsProvider.remove(macroSettingsId)
        val requestId = useCase.execute(PublishFileCommand("user", macroSettingsId, FileToSave("myfile.txt", "this is content", "", null)))

        await().until {
            assertThat(useCaseWithAnswer.execute(PublishFileResultRequest(requestId.get().requestId)).component2()).isOfAnyClassIn(NotFoundException::class.java)
        }

        val result = useCaseWithAnswer.execute(PublishFileResultRequest(requestId.get().requestId))
        assertThat(result.component1()).isNull()
        assertThat(result.component2()).isNotNull().isOfAnyClassIn(NotFoundException::class.java)
    }

    @Test
    fun `When macro and repository exists file is published`() {

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

        val requestId = useCase.execute(PublishFileCommand("user", macroSettingsId, fileToSave))

        await().until {
            assertThat(useCaseWithAnswer.execute(PublishFileResultRequest(requestId.get().requestId)).component1()).isNotNull()
        }

        val result = useCaseWithAnswer.execute(PublishFileResultRequest(requestId.get().requestId))

        assertThat(result.component1()).isNotNull()
        assertThat(result.component2()).isNull()
    }

    @Test
    fun `When repository is not editable error is returned`() {

        val macroSettingsId = "msId"
        val repositoryId = "rId"
        val macroSettings = MacroSettings(macroSettingsId, repositoryId, "branch", "", null, null)
        val repositorySettings = RepositoryWithNoAuthorization(repositoryId, "path", false)
        val fileToSave = FileToSave("file.txt", "old content\nnew content", "Edit file.txt", null)

        components.providers.macroSettingsProvider.put(macroSettingsId, macroSettings)
        components.providers.repositoryProvider.put(repositoryId, repositorySettings)

        whenever(components.utils.userManager.getUser("user")).thenReturn(User("User", "user@user.com"))

        val requestId = useCase.execute(PublishFileCommand("user", macroSettingsId, fileToSave))

        await().until {
            assertThat(useCaseWithAnswer.execute(PublishFileResultRequest(requestId.get().requestId)).component2()).isInstanceOf(NotFoundException::class.java)
        }

        val result = useCaseWithAnswer.execute(PublishFileResultRequest(requestId.get().requestId))

        assertThat(result.component1()).isNull()
        assertThat(result.component2()).isNotNull()

    }

    /**

    @Test
    fun `When user want to create new branch it is created and file is edited at that branch`() {

    val macroSettingsId = "msId"
    val repositoryId = "rId"
    val macroSettings = MacroSettings(macroSettingsId, repositoryId, "branch", "", null, null)
    val repositorySettings = RepositoryWithNoAuthorization(repositoryId, "path")
    val fileToSave = FileToSave("file.txt", "old content\nnew content", "Edit file.txt", "newbranch")

    whenever(macroSettingsRepository.get(macroSettingsId)).thenReturn(macroSettings)
    whenever(repositoryDatabase.get(repositoryId)).thenReturn(repositorySettings)
    whenever(userManager.getUser("user")).thenReturn(User("User", "user@user.com"))

    val result = publishFileUseCase.execute(PublishFileCommand("user", macroSettingsId, fileToSave))

    assertThat(result.component1()).isNotNull()
    assertThat(result.component2()).isNull()
    verify(importer).createNewBranch(repositorySettings, "branch", "newbranch")
    verify(importer).updateFile(repositorySettings, "newbranch", "file.txt", "old content\nnew content", Commit("User", "user@user.com", "Edit file.txt"))
    verify(importer).pushLocalBranch(repositorySettings, "newbranch")

    val transactionId = result.component1()!!.requestId
    val transaction = transactionCache.get(transactionId)

    assertThat(transaction).isNotNull()
    transaction!!

    assertThat(transaction.state).isEqualTo(Computation.ComputationState.FINISHED)
    assertThat(transaction.data).isEqualTo(Unit)
    }

    @Test
    fun `When user wants to push to branch that is read only, new branch is created and push is repeated`() {

    val macroSettingsId = "msId"
    val repositoryId = "rId"
    val macroSettings = MacroSettings(macroSettingsId, repositoryId, "branch", "", null, null)
    val repositorySettings = RepositoryWithNoAuthorization(repositoryId, "path")
    val fileToSave = FileToSave("file.txt", "old content\nnew content", "Edit file.txt", null)

    whenever(macroSettingsRepository.get(macroSettingsId)).thenReturn(macroSettings)
    whenever(repositoryDatabase.get(repositoryId)).thenReturn(repositorySettings)
    whenever(userManager.getUser("user")).thenReturn(User("User", "user@user.com"))

    whenever(importer.pushLocalBranch(repositorySettings, "branch")).thenThrow(VerificationException(VerificationInfo(VerificationStatus.WRONG_CREDENTIALS)))

    var userBranchName = ""

    whenever(importer.createNewBranch(eq(repositorySettings), eq("branch"), any())).thenAnswer {
    userBranchName = it.arguments[2] as String
    Unit
    }

    val result = publishFileUseCase.execute(PublishFileCommand("user", macroSettingsId, fileToSave))

    assertThat(result.component1()).isNotNull()
    assertThat(result.component2()).isNull()

    verify(importer).updateFile(repositorySettings, "branch", "file.txt", "old content\nnew content", Commit("User", "user@user.com", "Edit file.txt"))
    verify(importer).pushLocalBranch(repositorySettings, "branch")

    verify(importer).createNewBranch(repositorySettings, "branch", userBranchName)
    verify(importer).pushLocalBranch(repositorySettings, userBranchName)

    val transactionId = result.component1()!!.requestId
    val transaction = transactionCache.get(transactionId)

    assertThat(transaction).isNotNull()
    assertThat(transaction!!.state).isEqualTo(Computation.ComputationState.FAILED)
    assertThat(transaction.error).hasMessageContaining("ANOTHER_BRANCH")

    }

    @Test
    fun `When second push fails error about read only branch is returned`() {

    val macroSettingsId = "msId"
    val repositoryId = "rId"
    val macroSettings = MacroSettings(macroSettingsId, repositoryId, "branch", "", null, null)
    val repositorySettings = RepositoryWithNoAuthorization(repositoryId, "path")
    val fileToSave = FileToSave("file.txt", "old content\nnew content", "Edit file.txt", null)

    whenever(macroSettingsRepository.get(macroSettingsId)).thenReturn(macroSettings)
    whenever(repositoryDatabase.get(repositoryId)).thenReturn(repositorySettings)
    whenever(userManager.getUser("user")).thenReturn(User("User", "user@user.com"))

    var userBranchName = ""

    whenever(importer.createNewBranch(eq(repositorySettings), eq("branch"), any())).thenAnswer {
    userBranchName = it.arguments[2] as String
    Unit
    }

    whenever(importer.pushLocalBranch(eq(repositorySettings), any())).thenThrow(VerificationException(VerificationInfo(VerificationStatus.WRONG_CREDENTIALS)))
    whenever(importer.getLocation(repositorySettings)).thenReturn(File("."))

    val result = publishFileUseCase.execute(PublishFileCommand("user", macroSettingsId, fileToSave))

    assertThat(result.component1()).isNotNull()
    assertThat(result.component2()).isNull()

    verify(importer).updateFile(repositorySettings, "branch", "file.txt", "old content\nnew content", Commit("User", "user@user.com", "Edit file.txt"))
    verify(importer).pushLocalBranch(repositorySettings, "branch")

    verify(importer).createNewBranch(repositorySettings, "branch", userBranchName)
    verify(importer).pushLocalBranch(repositorySettings, userBranchName)

    val transactionId = result.component1()!!.requestId
    val transaction = transactionCache.get(transactionId)

    assertThat(transaction).isNotNull()
    assertThat(transaction!!.state).isEqualTo(Computation.ComputationState.FAILED)
    assertThat(transaction.error).hasMessageContaining("READ_ONLY_REPO")

    }

     */
}
