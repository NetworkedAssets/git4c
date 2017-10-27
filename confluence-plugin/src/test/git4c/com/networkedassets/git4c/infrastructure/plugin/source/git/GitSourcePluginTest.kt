package com.networkedassets.git4c.infrastructure.plugin.source.git

import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.infrastructure.git.DefaultGitClient
import com.networkedassets.git4c.utils.genTransactionId
import org.junit.Assert.assertTrue
import org.junit.Test

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

        val revision = git.revision(MacroSettings("uuid", repository.uuid, branch, "", null), repository).use { it.revision }

        assertTrue(revision.isNotBlank())
    }

    @Test
    fun `Revision process should get informations when proper Git repository in avarge short time`() {
        val git = GitSourcePlugin(DefaultGitClient())
        val repositoryUrl = "https://github.com/NetworkedAssets/git4c.git"
        val branch = "master"
        val repository = RepositoryWithNoAuthorization(genTransactionId(), repositoryUrl)
        val macro = MacroSettings("uuid", repository.uuid, branch, "", null)
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

}