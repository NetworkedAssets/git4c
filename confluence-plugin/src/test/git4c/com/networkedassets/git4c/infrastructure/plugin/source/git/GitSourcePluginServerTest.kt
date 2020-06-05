package com.networkedassets.git4c.infrastructure.plugin.source.git

import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.infrastructure.git.DefaultGitClient
import com.networkedassets.git4c.utils.TestJGitServer
import io.kotlintest.properties.Gen
import org.assertj.core.api.Assertions.assertThat
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ResetCommand
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.*
import kotlin.test.assertEquals


class GitSourcePluginServerTest {

    //remoteRepositoryDir, startServer(), stopServer()
    lateinit var server: Triple<File, Function0<Unit>, Function0<Unit>>

    @Before
    fun setUpServer() {
        server = TestJGitServer.create()

        val thread = Thread {
            server.second()
            server.first.deleteRecursively()
        }
        thread.start()
    }

    @After
    fun stopServer() {
        server.third()
    }


    @Test
    fun `Git4c retries push and deletes cache when exception`() {

        val remoteDir = server.first
        val git4cGitClient = GitSourcePlugin(DefaultGitClient())
        val remoteUrl = "http://127.0.0.1:${TestJGitServer.PORT}/repo"
        val repo = RepositoryWithNoAuthorization("1", remoteUrl, false)

        val testText1 = "1\n 2\n 3\n 4\n 5\n 6\n 7\n 8\n 9\n 0"
        val testText2 = "Q\n W\n E\n R\n T\n 6\n 7\n 8\n 9\n 0"
        val testText3 = "Q\n W\n E\n A\n S\n D\n F\n 8\n 9\n 0"

        val branch = "master"
        val fileName = "broken.md"

        git4cGitClient.pull(repo, branch).use { it }

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

        val importedFiles1 = git4cGitClient.pull(repo, branch).use { it }

        assertEquals(1, importedFiles1.imported.size)

        brokenFile.writeText(testText2)
        localRepository.add().addFilepattern(fileName).call()
        localRepository.commit().setMessage("Submit broken file").call()
        localRepository.push().setRemote(remoteDir.absolutePath).call()

        val importedFiles2 = git4cGitClient.pull(repo, branch).use { it }

        assertEquals(1, importedFiles2.imported.size)

        localRepository.reset().setMode(ResetCommand.ResetType.HARD).setRef("HEAD~1").call()
        brokenFile.writeText(testText3)
        localRepository.add().addFilepattern(fileName).call()
        localRepository.commit().setMessage("Submit second broken file").call()
        localRepository.push().setForce(true).setRemote(remoteDir.absolutePath).call()
        localRepository.pull().call()


        git4cGitClient.pull(repo, branch).use { it }
        git4cGitClient.pull(repo, branch).use { it }

        // TODO: Check content of a file
    }

    @Test
    fun `For merged branches isBranchMerged returns true`() {

        val remoteDir = server.first
        val git4cGitClient = GitSourcePlugin(DefaultGitClient())
        val remoteUrl = "http://127.0.0.1:${TestJGitServer.PORT}/repo"
        val repo = RepositoryWithNoAuthorization("1", remoteUrl, false)

        val git = Git.open(remoteDir)

        File(remoteDir, "file.txt").writeText("My file")
        git.add().addFilepattern("file.txt").call()
        git.commit().setMessage("Submit file").call()


        git.branchCreate().setName("branch1").call()
        git.branchCreate().setName("branch2").call()

        assertThat(git4cGitClient.isBranchMerged(repo, "branch1", "branch2")).isTrue()

    }

    @Test
    fun `For non merged branches isBranchMerged returns false`() {

        val remoteDir = server.first
        val git4cGitClient = GitSourcePlugin(DefaultGitClient())
        val remoteUrl = "http://127.0.0.1:${TestJGitServer.PORT}/repo"
        val repo = RepositoryWithNoAuthorization("1", remoteUrl, false)

        val git = Git.open(remoteDir)

        File(remoteDir, "file.txt").writeText("My file")
        git.add().addFilepattern("file.txt").call()
        git.commit().setMessage("Submit file 1").call()

        git.branchCreate().setName("branch1").call()

        git.checkout().setCreateBranch(true).setName("branch2").call()

        File(remoteDir, "file2.txt").writeText("My file")
        git.add().addFilepattern("file.txt").call()
        git.commit().setMessage("Submit file 2").call()

        //BRANCH1 --COMMIT-- BRANCH2

        assertThat(git4cGitClient.isBranchMerged(repo, "branch2", "branch1")).isFalse()
        assertThat(git4cGitClient.isBranchMerged(repo, "branch1", "branch2")).isTrue()
    }

    @Test
    fun `Moving between branches and tags works fine`() {

        val remoteDir = server.first
        val git4cGitClient = GitSourcePlugin(DefaultGitClient())
        val remoteUrl = "http://127.0.0.1:${TestJGitServer.PORT}/repo"
        val repo = RepositoryWithNoAuthorization("1", remoteUrl, false)

        val git = Git.open(remoteDir)

        File(remoteDir, "testfile").delete()
        File(remoteDir, "myfile.txt").writeText("branch1")
        git.add().addFilepattern("myfile.txt").call()
        git.add().addFilepattern("testfile").call()
        git.commit().setMessage("Submit file 1").call()
        git.tag().setName("v0.0.1").call()

        git.checkout().setCreateBranch(true).setName("branch1").call()
        git.checkout().setCreateBranch(true).setName("branch2").call()

        File(remoteDir, "myfile.txt").writeText("branch2")
        git.add().addFilepattern("myfile.txt").call()
        git.commit().setMessage("Submit file 2").call()
        git.tag().setName("v0.0.2").call()

        val branches = listOf(
                "master" to "branch1",
                "branch1" to "branch1",
                "v0.0.1" to "branch1",
                "branch2" to "branch2",
                "v0.0.2" to "branch2"
        )

        forAll(Gen.random(branches), 30) { (branch, fileContent) ->

            git4cGitClient.pull(repo, branch).use { it.imported }.let { imported ->
                assertThat(imported).hasSize(1)
                assertThat(String(imported.first().content())).isEqualTo(fileContent)
            }

        }

    }

    private fun <A> forAll(gena: Gen<A>, iter: Int, fn: (a: A) -> Unit): Unit {
        for (k in 0..iter) {
            fn(gena.next())
        }
    }

    private fun <A> Gen.Companion.random(list: List<A>) = create {
        list[Random().nextInt(list.size)]
    }

}
