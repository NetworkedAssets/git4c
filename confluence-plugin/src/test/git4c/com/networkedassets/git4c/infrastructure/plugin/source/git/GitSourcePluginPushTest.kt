package com.networkedassets.git4c.infrastructure.plugin.source.git

import com.google.common.io.Files
import com.networkedassets.git4c.core.business.Commit
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.data.RepositoryWithSshKey
import com.networkedassets.git4c.infrastructure.git.DefaultGitClient
import com.networkedassets.git4c.infrastructure.git.DefaultGitClient.Companion.addAuthData
import org.assertj.core.api.Assertions.assertThat
import org.eclipse.jgit.api.CreateBranchCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.RefSpec
import org.junit.*
import java.io.File
import java.util.*

/**
 * Remote repository requirement: master branch with one file: empty README
 */
@Ignore
class GitSourcePluginPushTest {

    lateinit var git: SourcePlugin

    lateinit var repository: Repository

    lateinit var repoDir: File

    lateinit var localGit: Git

    @Before
    fun setup() {
        val repositoryurl = (System.getenv("REPOSITORY_URL") ?: "").trim()
        val keyLocation = (System.getenv("KEY_LOCATION") ?: "").trim()

        Assume.assumeTrue(repositoryurl.isNotEmpty())
        Assume.assumeTrue(keyLocation.isNotEmpty())

        val jGitClient = DefaultGitClient()

        git = GitSourcePlugin(jGitClient)

        repository = RepositoryWithSshKey("123", repositoryurl, File(keyLocation).readText())

        val repoDir = Files.createTempDir()

        this.repoDir = repoDir

        localGit = jGitClient.clone(repository, "master", repoDir.toPath())

    }

    @After
    fun cleanup() {
        localGit.close()
        repoDir.deleteRecursively()
    }


    @Test
    fun `All branches should be returned`() {

        val branchName = "test/${UUID.randomUUID()}"

        val initialBranches = git.getBranches(repository)

        assertThat(initialBranches).contains("master")

        localGit.branchCreate().setName(branchName).call()

        localGit.push().addAuthData(repository).setRefSpecs(RefSpec("$branchName:$branchName")).call()

        val afterBranchAdd = git.getBranches(repository)

        assertThat(afterBranchAdd).containsExactlyInAnyOrder(*initialBranches.toTypedArray(), branchName)

        localGit.branchDelete().setBranchNames(branchName).call()

        //delete branch 'branchToDelete' on remote 'origin'
        val refSpec = RefSpec()
                .setSource(null)
                .setDestination("refs/heads/$branchName")

        localGit.push().addAuthData(repository).setRefSpecs(refSpec).setRemote("origin").call()

        val afterBranchRemove = git.getBranches(repository)

        assertThat(afterBranchRemove).containsExactlyInAnyOrder(*initialBranches.toTypedArray())

    }

    @Test
    fun `Editing file indeed replaced it remotely`() {

        val branchName = "test/${UUID.randomUUID()}"

        localGit.branchCreate().setName(branchName).call()

        localGit.push().addAuthData(repository).setRefSpecs(RefSpec("$branchName:$branchName")).call()

        localGit.checkout()
                .setName(branchName)
                .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                .setStartPoint("origin/$branchName")
                .call()

        git.updateFile(repository, branchName, "README", "New exciting content", Commit("test user", "user@test.com", "Message from tests"))
        git.pushLocalBranch(repository, branchName)

        localGit.pull().addAuthData(repository).call()

        val readmeContent = File(repoDir, "README")

        assertThat(readmeContent).hasContent("New exciting content")

        val commits = localGit.log().call()

        val lastCommit = commits.first()

        assertThat(lastCommit.authorIdent.emailAddress).isEqualTo("user@test.com")
        assertThat(lastCommit.authorIdent.name).isEqualTo("test user")
        assertThat(lastCommit.fullMessage).isEqualTo("Message from tests")

    }

    @Test
    fun `Creating new branch creates new branch from given existing branch`() {

        val oldBranch = "test/${UUID.randomUUID()}"
        val newBranch = "test/${UUID.randomUUID()}"

        git.createNewBranch(repository, "master", oldBranch)

        git.updateFile(repository, oldBranch, "README", "Even newer content", Commit("test user", "user@test.com", "Message from tests"))
        git.pushLocalBranch(repository, oldBranch)

        git.createNewBranch(repository, oldBranch, newBranch)
        git.pushLocalBranch(repository, newBranch)

        localGit.pull().addAuthData(repository).call()

        localGit.checkout()
                .setCreateBranch(true)
                .setName(newBranch)
                .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                .setStartPoint("origin/$newBranch")
                .call()


        val readmeContent = File(repoDir, "README")

        assertThat(readmeContent).hasContent("Even newer content")

    }

    @Test
    fun `updateAndPublishFile can create new files`() {

        val branch = "test/${UUID.randomUUID()}"

        git.createNewBranch(repository, "master", branch)

        git.updateFile(repository, branch, "README2", "New readme", Commit("test user", "user@test.com", "Message from tests"))
        git.pushLocalBranch(repository, branch)

        localGit.pull().addAuthData(repository).call()

        localGit.checkout()
                .setCreateBranch(true)
                .setName(branch)
                .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                .setStartPoint("origin/$branch")
                .call()

        val readme = File(repoDir, "README")
        val readme2 = File(repoDir, "README2")

        assertThat(readme).hasContent("")
        assertThat(readme2).hasContent("New readme")

    }

    @Test
    fun `createNewBranch doesn't throw error when branch already exists`() {
        val branch = "test/${UUID.randomUUID()}"
        git.createNewBranch(repository, "master", branch)
        git.createNewBranch(repository, "master", branch)
    }


    @Test
    fun `RemoveLocalBranch removes branch`() {
        val git = GitSourcePlugin(DefaultGitClient())
        val repositoryUrl = "https://github.com/NetworkedAssets/git4c.git"
        val branch = "master"
        val repository = RepositoryWithNoAuthorization("", repositoryUrl)

        git.pull(repository, "master")

        assertThat(git.getLocalBranches(repository)).containsExactlyInAnyOrder("master")

        git.createNewBranch(repository, "master", "master2")

        assertThat(git.getLocalBranches(repository)).containsExactlyInAnyOrder("master", "master2")

        git.removeBranch(repository, "master2")

        assertThat(git.getLocalBranches(repository)).containsExactlyInAnyOrder("master")

    }

    @Test
    fun `RemoveLocalBranch doesn't throw exception if branch doesn't exist`() {

        val git = GitSourcePlugin(DefaultGitClient())
        val repositoryUrl = "https://github.com/NetworkedAssets/git4c.git"
        val branch = "master"
        val repository = RepositoryWithNoAuthorization("", repositoryUrl)

        git.pull(repository, "master")

        assertThat(git.getLocalBranches(repository)).containsExactlyInAnyOrder("master")

        git.removeBranch(repository, "master2")

    }

}