package com.networkedassets.git4c.core

import com.atlassian.confluence.core.service.NotAuthorizedException
import com.networkedassets.git4c.application.PluginComponents
import com.networkedassets.git4c.boundary.PublishFileCommand
import com.networkedassets.git4c.boundary.inbound.FileToSave
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.core.business.Commit
import com.networkedassets.git4c.core.business.Computation
import com.networkedassets.git4c.core.business.User
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.test.UseCaseTest
import com.networkedassets.git4c.utils.InMemoryApplication.application
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Ignore
import org.junit.Test

class PublishFileUseCaseTest : UseCaseTest<PublishFileUseCase>() {

    override fun getUseCase(plugin: PluginComponents): PublishFileUseCase {
        return PublishFileUseCase(plugin.importer, plugin.documentsViewCache, plugin.macroSettingsDatabase, plugin.repositoryDatabase, plugin.userManager, plugin.publishFileComputationCache, plugin.converterExecutor, plugin.temporaryEditBranchesDatabase, plugin.macroLocationDatabase)
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
        application.macroSettingsDatabase.remove(macroSettingsId)

        val result = useCase.execute(PublishFileCommand("user", macroSettingsId, FileToSave("myfile.txt", "this is content", "", null)))
        assertThat(result.component1()).isNull()
        assertThat(result.component2()).isNotNull().isOfAnyClassIn(NotFoundException::class.java)
    }

    @Test
    @Ignore
    fun `When macro and repository exists file is published`() {

        val macroSettingsId = "msId"
        val repositoryId = "rId"
        val macroSettings = MacroSettings(macroSettingsId, repositoryId, "branch", "", null, null)
        val repositorySettings = RepositoryWithNoAuthorization(repositoryId, "path")
        val fileToSave = FileToSave("file.txt", "old content\nnew content", "Edit file.txt", null)

        application.macroSettingsDatabase.put(macroSettingsId, macroSettings)
        application.repositoryDatabase.put(repositoryId, repositorySettings)

        // TODO: Don't use mockito!!! Use mocked interfaces imlementation!
        whenever(application.userManager.getUser("user")).thenReturn(User("User", "user@user.com"))

        val result = useCase.execute(PublishFileCommand("user", macroSettingsId, fileToSave))

        assertThat(result.component1()).isNotNull()
        assertThat(result.component2()).isNull()
        verify(application.importer).updateFile(repositorySettings, "branch", "file.txt", "old content\nnew content", Commit("User", "user@user.com", "Edit file.txt"))
        verify(application.importer).pushLocalBranch(repositorySettings, "branch")

        val transactionId = result.component1()!!.requestId
        val transaction = application.publishFileComputationCache .get(transactionId)

        assertThat(transaction).isNotNull()
        assertThat(transaction!!.state).isEqualTo(Computation.ComputationState.FINISHED)
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
