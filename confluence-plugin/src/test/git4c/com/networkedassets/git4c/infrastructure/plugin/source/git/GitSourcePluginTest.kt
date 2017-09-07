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
        val time = System.currentTimeMillis()
        val verify = git.verify(repository)
        val runtime = System.currentTimeMillis() - time
        println("Time of verification of git repository: " + runtime)
        assertTrue(verify.isOk())
    }

    @Test
    fun `Revision process should pass when proper Git repository`() {
        val git = GitSourcePlugin(DefaultGitClient())
        val repositoryUrl = "https://github.com/NetworkedAssets/git4c.git"
        val repository = RepositoryWithNoAuthorization(genTransactionId(), repositoryUrl)
        val time = System.currentTimeMillis()
        val verify = git.revision(MacroSettings("uuid", repository.uuid, "master", "", null), repository).use { it.revision }
        val runtime = System.currentTimeMillis() - time
        println("Time of verification of git repository: " + runtime)

        assertTrue(verify.isNotBlank())
        val time2 = System.currentTimeMillis()
        val verify2 = git.revision(MacroSettings("uuid", repository.uuid, "master", "", null), repository).use { it.revision }
        val runtime2 = System.currentTimeMillis() - time2
        println("Time of verification of git repository: " + runtime2)
        assertTrue(verify2.isNotBlank())
    }

    @Test
    fun `Pull process should pass when proper Git repository`() {
        val git = GitSourcePlugin(DefaultGitClient())
        val repositoryUrl = "https://github.com/NetworkedAssets/git4c.git"
        val repository = RepositoryWithNoAuthorization(genTransactionId(), repositoryUrl)
        val time = System.currentTimeMillis()
        val verify = git.revision(MacroSettings("uuid", repository.uuid, "master", "", null), repository).use { it.revision }
        val runtime = System.currentTimeMillis() - time
        println("Time of verification of git repository: " + runtime)

        assertTrue(verify.isNotBlank())
        val time2 = System.currentTimeMillis()
        val verify2 = git.pull(repository, "master").use { it.imported }
        val runtime2 = System.currentTimeMillis() - time2
        println("Pull of git repository: " + runtime2)
        assertTrue(verify2.isNotEmpty())
    }

}