package com.networkedassets.git4c.infrastructure.plugin.source.git

import com.google.common.io.Files
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.core.business.Commit
import com.networkedassets.git4c.core.exceptions.VerificationException
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.infrastructure.git.DefaultGitClient
import com.networkedassets.git4c.utils.genTransactionId
import org.assertj.core.api.Assertions.assertThat
import org.eclipse.jgit.api.Git
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.Timeout
import java.io.File
import java.nio.file.Paths
import kotlin.test.fail

class GitSourcePluginTest {

    @Rule
    @JvmField
    public var globalTimeout = Timeout.seconds(60)

    @Test
    fun `Verify process should pass when proper Git repository`() {
        val git = GitSourcePlugin(DefaultGitClient())
        val repositoryUrl = "https://github.com/github/gitignore.git"
        val repository = RepositoryWithNoAuthorization(genTransactionId(), repositoryUrl, false)

        val verify = git.verify(repository)

        assertTrue(verify.isOk())
    }

    @Test
    fun `Revision process should get informations when proper Git repository`() {
        val git = GitSourcePlugin(DefaultGitClient())
        val repositoryUrl = "https://github.com/github/gitignore.git"
        val branch = "master"
        val repository = RepositoryWithNoAuthorization(genTransactionId(), repositoryUrl, false)

        val revision = git.revision(MacroSettings("uuid", repository.uuid, branch, "", null, null), repository).use { it.revision }

        assertTrue(revision.isNotBlank())
    }
    @Test
    fun `Pull process should be done when proper Git repository`() {
        val git = GitSourcePlugin(DefaultGitClient())
        val repositoryUrl = "https://github.com/github/gitignore.git"
        val branch = "master"
        val repository = RepositoryWithNoAuthorization(genTransactionId(), repositoryUrl, false)

        val pull = git.pull(repository, branch).use { it.imported }
        assertTrue(pull.isNotEmpty())
    }

    @Test
    fun `Get Files process should be done when proper Git repository`() {
        val git = GitSourcePlugin(DefaultGitClient())
        val repositoryUrl = "https://github.com/github/gitignore.git"
        val branch = "master"
        val repository = RepositoryWithNoAuthorization(genTransactionId(), repositoryUrl, false)
        val pull = git.pull(repository, branch).use { it.imported }

        val get = git.get(repository, branch).use { it.imported }
        assertTrue(get.isNotEmpty())
        assertTrue(get.size == pull.size)
    }

    @Test
    fun `Get Files process should be done when proper Git repository for tags`() {
        val git = GitSourcePlugin(DefaultGitClient())
        val repositoryUrl = "https://github.com/stevemao/left-pad.git"
        val branch = "v1.1.2"
        val repository = RepositoryWithNoAuthorization(genTransactionId(), repositoryUrl, false)
        val pull = git.pull(repository, branch).use { it.imported }

        val get = git.get(repository, branch).use { it.imported }
        assertThat(pull).isNotEmpty
        assertThat(get).isNotEmpty
        assertThat(get).hasSameSizeAs(pull)
    }

    @Test
    fun `updateAndPushFile on read only repository throws exception with WRONG_CREDENTIALS`() {
        val git = GitSourcePlugin(DefaultGitClient())
        val repositoryUrl = "https://github.com/github/gitignore.git"
        val branch = "master"

        val repository = RepositoryWithNoAuthorization(genTransactionId(), repositoryUrl, false)
        git.pull(repository, branch).close()

        try {
            git.updateFile(repository, "master", "newfile", "content", Commit("", "", ""))
            git.pushLocalBranch(repository, "master")
            fail()
        } catch (e: VerificationException) {
            assertThat(e.verification.status).isEqualTo(VerificationStatus.WRONG_CREDENTIALS)
        }

    }

    @Test
    fun `pushLocalBranch on read only repository throws exception with WRONG_CREDENTIALS`() {

        val git = GitSourcePlugin(DefaultGitClient())
        val repositoryUrl = "https://github.com/github/gitignore.git"
        val branch = "master"

        val repository = RepositoryWithNoAuthorization(genTransactionId(), repositoryUrl, false)
        git.pull(repository, branch).close()

        try {
            git.createNewBranch(repository, "master", "mybranch")
            git.pushLocalBranch(repository, "mybranch")
            fail()
        } catch (e: VerificationException) {
            assertThat(e.verification.status).isEqualTo(VerificationStatus.WRONG_CREDENTIALS)
        }

    }

    @Test
    fun `resetBranch replaces local branch with remote one`() {

        val git = GitSourcePlugin(DefaultGitClient())
        val repositoryUrl = "https://github.com/github/gitignore.git"
        val branch = "master"
        val file = "Actionscript.gitignore"
        val repository = RepositoryWithNoAuthorization(genTransactionId(), repositoryUrl, false)

        val repositoryLocation = git.getLocation(repository).absolutePath
        val jGit = Git.open(File(repositoryLocation))

        val commits = jGit.log().add(jGit.repository.resolve("refs/heads/master")).call().map { it.id.name }

        git.pull(repository, branch).close()

        val oldContent = Paths.get(repositoryLocation, file).toFile().readText()

        git.updateFile(repository, "master", file, "New content", Commit("user", "user@user.user", "user"))

        assertThat(Paths.get(repositoryLocation, file).toFile()).hasContent("New content")

        git.resetBranch(repository, "master")

        assertThat(Paths.get(repositoryLocation, file).toFile()).hasContent(oldContent)

        assertThat(jGit.log().add(jGit.repository.resolve("refs/heads/master")).call().map { it.id.name }).isEqualTo(commits)

        assertThat(jGit.status().call().hasUncommittedChanges()).isFalse()

    }

    @Test
    fun `resetLastChange removes new files`() {

        val git = GitSourcePlugin(DefaultGitClient())
        val repositoryUrl = "https://github.com/github/gitignore.git"
        val branch = "master"
        val repository = RepositoryWithNoAuthorization(genTransactionId(), repositoryUrl, false)

        val repositoryLocation = git.getLocation(repository).absolutePath
        val jGit = Git.open(File(repositoryLocation))

        val commits = jGit.log().add(jGit.repository.resolve("refs/heads/master")).call().map { it.id.name }

        assertThat(Paths.get(repositoryLocation, "README_new.md").toFile()).doesNotExist()

        git.pull(repository, branch).close()

        git.updateFile(repository, "master", "README_new.md", "New content", Commit("user", "user@user.user", "user"))

        assertThat(Paths.get(repositoryLocation, "README_new.md").toFile()).hasContent("New content")

        git.resetBranch(repository, "master")

        assertThat(Paths.get(repositoryLocation, "README_new.md").toFile()).doesNotExist()

        assertThat(jGit.log().add(jGit.repository.resolve("refs/heads/master")).call().map { it.id.name }).isEqualTo(commits)

        assertThat(jGit.status().call().hasUncommittedChanges()).isFalse()

    }

    @Test
    fun `updateFile doesn't allow files that aren't in repository`() {

        val git = GitSourcePlugin(DefaultGitClient())
        val repositoryUrl = "https://github.com/github/gitignore.git"
        val repository = RepositoryWithNoAuthorization(genTransactionId(), repositoryUrl, false)

        val tempLoc = Files.createTempDir()

        val rougeFile = File(tempLoc, "rougeFile.txt")

        try {
            git.updateFile(repository, "master", rougeFile.absolutePath, "New content", Commit("user", "user@user.user", "user"))
            fail()
        } catch (e: Exception) {
            assertThat(e).hasMessageContaining("Unauthorized file write")
        }

        assertThat(rougeFile).doesNotExist()

        tempLoc.deleteRecursively()

    }

}