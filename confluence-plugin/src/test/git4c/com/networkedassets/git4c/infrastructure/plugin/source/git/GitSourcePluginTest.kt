package com.networkedassets.git4c.infrastructure.plugin.source.git

import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.infrastructure.git.DefaultGitClient
import com.networkedassets.git4c.utils.genTransactionId
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ResetCommand
import org.junit.*
import org.junit.Assert.assertTrue
import java.io.File
import kotlin.test.assertEquals


class GitSourcePluginTest {

    //remoteRepositoryDir, startServer(), stopServer()
    lateinit var server:  Triple<File, Function0<Unit>, Function0<Unit>>

    @Before
    fun setUpServer(){
        server = TestJGitServer.create()

        val thread =Thread {
            server.second()
            server.first.deleteRecursively()
        }
        thread.start()
    }

    @After
    fun stopServer(){
        server.third()
    }

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


    @Test
    fun `Git4c retries push and deletes cache when exception`() {

        val remoteDir = server.first
        val git4cGitClient = GitSourcePlugin(DefaultGitClient())
        val remoteUrl = "http://127.0.0.1:${TestJGitServer.PORT}/repo"
        val repo = RepositoryWithNoAuthorization("1", remoteUrl)


        val testText1 = "1\n 2\n 3\n 4\n 5\n 6\n 7\n 8\n 9\n 0"
        val testText2 = "Q\n W\n E\n R\n T\n 6\n 7\n 8\n 9\n 0"
        val testText3 = "Q\n W\n E\n A\n S\n D\n F\n 8\n 9\n 0"

        val branch = "master"
        val fileName = "broken.md"

        git4cGitClient.pull(repo, branch)

        val localRepoPath = File.createTempFile("LocalRepository", "")
        localRepoPath.delete()
        localRepoPath.mkdirs()

        val localRepository = Git.cloneRepository().setURI(remoteUrl).setDirectory(localRepoPath)
                .setBranch(branch).call()
        localRepository.pull()
        localRepository.pull()

        val brokenFile = File(localRepoPath, fileName)

        brokenFile.createNewFile()
        brokenFile.writeText(testText1)
        localRepository.add().addFilepattern(fileName).call()
        localRepository.commit().setMessage("Submit broken file").call()
        localRepository.push().setRemote(remoteDir.absolutePath).call()

        val importedFiles1 = git4cGitClient.pull(repo, branch)

        assertEquals(2, importedFiles1.imported.size)

        brokenFile.writeText(testText2)
        localRepository.add().addFilepattern(fileName).call()
        localRepository.commit().setMessage("Submit broken file").call()
        localRepository.push().setRemote(remoteDir.absolutePath).call()

        val importedFiles2 = git4cGitClient.pull(repo, branch)

        assertEquals(2, importedFiles2.imported.size)

        localRepository.reset().setMode(ResetCommand.ResetType.HARD).setRef("HEAD~1").call()
        brokenFile.writeText(testText3)
        localRepository.add().addFilepattern(fileName).call()
        localRepository.commit().setMessage("Submit second broken file").call()
        localRepository.push().setForce(true).setRemote(remoteDir.absolutePath).call()
        localRepository.pull().call()


        git4cGitClient.pull(repo, branch)
        val importedFiles3 = git4cGitClient.pull(repo, branch)


        assertTrue(importedFiles3.imported[0].contentString.replace("\r\n","\n").contains(testText3))
    }




}