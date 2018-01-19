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
import org.junit.Ignore
import org.junit.Test
import java.io.File
import java.nio.file.Paths
import kotlin.test.fail

@Ignore
class GitSourcePluginTest {

    @Test
    fun `Verify process should pass when proper Git repository`() {
        val git = GitSourcePlugin(DefaultGitClient())
        val repositoryUrl = "https://github.com/NetworkedAssets/git4c.git"
        val repository = RepositoryWithNoAuthorization(genTransactionId(), repositoryUrl)

        val verify = git.verify(repository)

        assertTrue(verify.isOk())
    }

    @Test
    fun `Verify process should pass when proper Git repository in avarge short time`() {
        val git = GitSourcePlugin(DefaultGitClient())
        val repositoryUrl = "https://github.com/NetworkedAssets/git4c.git"
        val repository = RepositoryWithNoAuthorization(genTransactionId(), repositoryUrl)
        git.verify(repository)

        val times = arrayListOf<Long>()

        for (i in 1..10) {
            val time = System.currentTimeMillis()
            val verify = git.verify(repository)
            val runtime = System.currentTimeMillis() - time
            assertTrue(verify.isOk())
            times.add(runtime)
        }

        assert(times.average() < 2000)
    }

    @Test
    fun `Revision process should get informations when proper Git repository`() {
        val git = GitSourcePlugin(DefaultGitClient())
        val repositoryUrl = "https://github.com/NetworkedAssets/git4c.git"
        val branch = "master"
        val repository = RepositoryWithNoAuthorization(genTransactionId(), repositoryUrl)

        val revision = git.revision(MacroSettings("uuid", repository.uuid, branch, "", null, null), repository).use { it.revision }

        assertTrue(revision.isNotBlank())
    }

    @Test
    fun `Revision process should get informations when proper Git repository in avarge short time`() {
        val git = GitSourcePlugin(DefaultGitClient())
        val repositoryUrl = "https://github.com/NetworkedAssets/git4c.git"
        val branch = "master"
        val repository = RepositoryWithNoAuthorization(genTransactionId(), repositoryUrl)
        val macro = MacroSettings("uuid", repository.uuid, branch, "", null, null)
        git.revision(macro, repository)

        val times = arrayListOf<Long>()

        for (i in 1..10) {
            val time = System.currentTimeMillis()
            val revision = git.revision(macro, repository).use { it.revision }
            val runtime = System.currentTimeMillis() - time
            assertTrue(revision.isNotBlank())
            times.add(runtime)
        }

        assert(times.average() < 2000)
    }

    @Test
    fun `Pull process should be done when proper Git repository`() {
        val git = GitSourcePlugin(DefaultGitClient())
        val repositoryUrl = "https://github.com/NetworkedAssets/git4c.git"
        val branch = "master"
        val repository = RepositoryWithNoAuthorization(genTransactionId(), repositoryUrl)

        val pull = git.pull(repository, branch).use { it.imported }
        assertTrue(pull.isNotEmpty())
    }

    @Test
    fun `Pull process should be done when proper Git repository in avarge short time`() {
        val git = GitSourcePlugin(DefaultGitClient())
        val repositoryUrl = "https://github.com/NetworkedAssets/git4c.git"
        val branch = "master"
        val repository = RepositoryWithNoAuthorization(genTransactionId(), repositoryUrl)
        git.pull(repository, branch)

        val times = arrayListOf<Long>()

        for (i in 1..10) {
            val time = System.currentTimeMillis()
            val pull = git.pull(repository, branch).use { it.imported }
            assertTrue(pull.isNotEmpty())
            val runtime = System.currentTimeMillis() - time
            assertTrue(pull.isNotEmpty())
            times.add(runtime)
        }

        assert(times.average() < 2000)
    }

    @Test
    fun `Get Files process should be done when proper Git repository`() {
        val git = GitSourcePlugin(DefaultGitClient())
        val repositoryUrl = "https://github.com/NetworkedAssets/git4c.git"
        val branch = "master"
        val repository = RepositoryWithNoAuthorization(genTransactionId(), repositoryUrl)
        val pull = git.pull(repository, branch)

        val get = git.get(repository, branch).use { it.imported }
        assertTrue(get.isNotEmpty())
        assertTrue(get.size == pull.imported.size)
    }

    @Test
    fun `Get Files process should be done when proper Git repository in avarge short time`() {
        val git = GitSourcePlugin(DefaultGitClient())
        val repositoryUrl = "https://github.com/NetworkedAssets/git4c.git"
        val branch = "master"
        val repository = RepositoryWithNoAuthorization(genTransactionId(), repositoryUrl)
        val pull = git.pull(repository, branch)

        val times = arrayListOf<Long>()

        for (i in 1..10) {
            val time = System.currentTimeMillis()
            val get = git.get(repository, branch).use { it.imported }
            assertTrue(get.size == pull.imported.size)
            val runtime = System.currentTimeMillis() - time
            times.add(runtime)
        }

        assert(times.average() < 300)
    }


    @Test
    fun `updateAndPushFile on read only repository throws exception with WRONG_CREDENTIALS`() {


        val git = GitSourcePlugin(DefaultGitClient())
        val repositoryUrl = "https://github.com/github/gitignore.git"
        val branch = "master"

        val repository = RepositoryWithNoAuthorization(genTransactionId(), repositoryUrl)
        git.pull(repository, branch)

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

        val repository = RepositoryWithNoAuthorization(genTransactionId(), repositoryUrl)
        git.pull(repository, branch)

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
        val repository = RepositoryWithNoAuthorization(genTransactionId(), repositoryUrl)

        val repositoryLocation = git.getLocation(repository)
        val jGit = Git.open(repositoryLocation)

        val commits = Git.open(repositoryLocation).log().add(jGit.repository.resolve("refs/heads/master")).call().map { it.id.name }

        git.pull(repository, branch)

        val oldContent = Paths.get(repositoryLocation.parent, "README.md").toFile().readText()

        git.updateFile(repository, "master", "README.md", "New content", Commit("user", "user@user.user", "user"))

        assertThat(Paths.get(repositoryLocation.parent, "README.md").toFile()).hasContent("New content")

        git.resetBranch(repository, "master")

        assertThat(Paths.get(repositoryLocation.parent, "README.md").toFile()).hasContent(oldContent)

        assertThat(Git.open(repositoryLocation).log().add(jGit.repository.resolve("refs/heads/master")).call().map { it.id.name }).isEqualTo(commits)

        assertThat(jGit.status().call().hasUncommittedChanges()).isFalse()

    }

    @Test
    fun `resetLastChange removes new files`() {

        val git = GitSourcePlugin(DefaultGitClient())
        val repositoryUrl = "https://github.com/github/gitignore.git"
        val branch = "master"
        val repository = RepositoryWithNoAuthorization(genTransactionId(), repositoryUrl)

        val repositoryLocation = git.getLocation(repository)
        val jGit = Git.open(repositoryLocation)

        val commits = Git.open(repositoryLocation).log().add(jGit.repository.resolve("refs/heads/master")).call().map { it.id.name }

        assertThat(Paths.get(repositoryLocation.parent, "README_new.md").toFile()).doesNotExist()

        git.pull(repository, branch)

        git.updateFile(repository, "master", "README_new.md", "New content", Commit("user", "user@user.user", "user"))

        assertThat(Paths.get(repositoryLocation.parent, "README_new.md").toFile()).hasContent("New content")

        git.resetBranch(repository, "master")

        assertThat(Paths.get(repositoryLocation.parent, "README_new.md").toFile()).doesNotExist()

        assertThat(Git.open(repositoryLocation).log().add(jGit.repository.resolve("refs/heads/master")).call().map { it.id.name }).isEqualTo(commits)

        assertThat(jGit.status().call().hasUncommittedChanges()).isFalse()

    }

    @Test
    fun `updateFile doesn't allow files that aren't in repository`() {

        val git = GitSourcePlugin(DefaultGitClient())
        val repositoryUrl = "https://github.com/github/gitignore.git"
        val repository = RepositoryWithNoAuthorization(genTransactionId(), repositoryUrl)

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