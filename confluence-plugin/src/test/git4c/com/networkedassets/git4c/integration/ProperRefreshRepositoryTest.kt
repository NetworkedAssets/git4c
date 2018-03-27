package com.networkedassets.git4c.integration

import com.jayway.awaitility.Awaitility.await
import com.jayway.awaitility.core.ConditionFactory
import com.networkedassets.git4c.boundary.*
import com.networkedassets.git4c.boundary.inbound.*
import com.networkedassets.git4c.boundary.outbound.*
import com.networkedassets.git4c.boundary.outbound.exceptions.NotReadyException
import com.networkedassets.git4c.data.PageAndSpacePermissionsForUser
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.infrastructure.git.DefaultGitClient
import com.networkedassets.git4c.infrastructure.plugin.source.git.GitSourcePlugin
import com.networkedassets.git4c.utils.InMemoryApplication
import com.networkedassets.git4c.utils.InMemoryApplication.application
import com.networkedassets.git4c.utils.InMemoryApplication.execute
import com.networkedassets.git4c.utils.TestJGitServer
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.eclipse.jgit.api.Git
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.concurrent.TimeUnit

class ProperRefreshRepositoryTest {

    lateinit var server: Triple<File, Function0<Unit>, Function0<Unit>>
    lateinit var remoteDir: File

    val git4cGitClient = GitSourcePlugin(DefaultGitClient())
    val remoteUrl = "http://127.0.0.1:${TestJGitServer.PORT}/repo"
    val repo = RepositoryWithNoAuthorization("1", remoteUrl, false)
    val user = "test"

    @Before
    fun setUpServer() {
        server = TestJGitServer.create()
        remoteDir = server.first
        Thread {
            server.second()
            server.first.deleteRecursively()
        }.start()
        prepareApp()
    }

    private fun prepareApp() {
        InMemoryApplication.switchToGit()
        val permission = PageAndSpacePermissionsForUser("", "", user, true)
        application.cache.pageAndSpacePermissionsForUserCache.put(permission.uuid, permission)
        application.cache.revisionCache.setTime(2500)
    }

    @After
    fun stopServer() {
        server.third()
        InMemoryApplication.reset()
    }


    @Test
    fun `Currently display Macro should refresh on repository changes after no more then revision cache time seconds`() {
        val createdMacro = createMacro()
        val treeOfCreatedMacro = getTree(createdMacro.uuid)
        assertThat(treeOfCreatedMacro.getChildren()).isEmpty()

        addNewFileToRemoteRepository()
        val revision = getCurrentRevision(createdMacro.uuid)
        assertThat(revision.id).isNotEqualToIgnoringCase(createdMacro.revision)

        await().atMost(3, TimeUnit.SECONDS).until {
            execute(RefreshDocumentationsMacroCommand(createdMacro.uuid, user)).get()
            assertThat(getMacro(createdMacro.uuid).revision).isEqualToIgnoringCase(revision.id);
        }
    }

    @Test
    fun `Display Macro should refresh on repository changes after no more then revision cache time seconds`() {
        val createdMacro = createMacro()
        val revision = createdMacro.revision
        val treeOfCreatedMacro = getTree(createdMacro.uuid)
        assertThat(treeOfCreatedMacro.getChildren()).isEmpty()

        addNewFileToRemoteRepository()
        assertThat(getMacro(createdMacro.uuid).revision).isEqualToIgnoringCase(createdMacro.revision)

        await().atMost(3, TimeUnit.SECONDS).until {
            execute(ViewMacroCommand(MacroToView(createdMacro.uuid, MacroType.MULTIFILE), PageToView(""), SpaceToView(""))).get()
            assertThat(getMacro(createdMacro.uuid).revision).isNotEqualToIgnoringCase(revision);
        }
    }

    @Test
    fun `Display Macro should refresh on repository changes after more then revision cache time`() {
        val createdMacro = createMacro()
        val revision = createdMacro.revision
        val treeOfCreatedMacro = getTree(createdMacro.uuid)
        assertThat(treeOfCreatedMacro.getChildren()).isEmpty()

        addNewFileToRemoteRepository()
        assertThat(getMacro(createdMacro.uuid).revision).isEqualToIgnoringCase(createdMacro.revision)
        execute(ViewMacroCommand(MacroToView(createdMacro.uuid, MacroType.MULTIFILE), PageToView(""), SpaceToView(""))).get()

        Thread.sleep(1500)

        execute(ViewMacroCommand(MacroToView(createdMacro.uuid, MacroType.MULTIFILE), PageToView(""), SpaceToView(""))).get()
        assertThat(getMacro(createdMacro.uuid).revision).isEqualToIgnoringCase(revision);

        await().atMost(4, TimeUnit.SECONDS).until {
            execute(ViewMacroCommand(MacroToView(createdMacro.uuid, MacroType.MULTIFILE), PageToView(""), SpaceToView(""))).get()
            assertThat(getMacro(createdMacro.uuid).revision).isNotEqualToIgnoringCase(revision);
        }
    }

    @Test
    fun `Tree content should be udpated after it has changed on remote`() {
        val createdMacro = createMacro()
        val treeOfCreatedMacro = getTree(createdMacro.uuid)
        assertThat(treeOfCreatedMacro.getChildren()).isEmpty()

        addNewFileToRemoteRepository()
        val revision = getCurrentRevision(createdMacro.uuid)
        assertThat(revision.id).isNotEqualToIgnoringCase(createdMacro.revision)

        await().atMost(3, TimeUnit.SECONDS).until {
            execute(RefreshDocumentationsMacroCommand(createdMacro.uuid, user)).get()
            assertThat(getMacro(createdMacro.uuid).revision).isEqualToIgnoringCase(revision.id);
        }
        assertThat(getTree(createdMacro.uuid).getChildren()).hasSize(1)
    }

    @Test
    fun `Remove files from repo should change tree items after refresh`() {
        val createdMacro = createMacro()
        val treeOfCreatedMacro = getTree(createdMacro.uuid)
        assertThat(treeOfCreatedMacro.getChildren()).isEmpty()

        addNewFileToRemoteRepository()
        val revision = getCurrentRevision(createdMacro.uuid)
        assertThat(revision.id).isNotEqualToIgnoringCase(createdMacro.revision)
        await().atMost(3, TimeUnit.SECONDS).until {
            execute(RefreshDocumentationsMacroCommand(createdMacro.uuid, user)).get()
            execute(ViewMacroCommand(MacroToView(createdMacro.uuid, MacroType.MULTIFILE), PageToView(""), SpaceToView(""))).get()
            assertThat(getMacro(createdMacro.uuid).revision).isEqualToIgnoringCase(revision.id);
            assertThat(getTree(createdMacro.uuid).getChildren()).asList().hasSize(1)
        }
        val fileName = getTree(createdMacro.uuid).getChildren()[0].name
        await().until {
            assertThat(execute(GetDocumentItemInDocumentationsMacroQuery(createdMacro.uuid, fileName, user)).get()).isNotInstanceOf(NotReadyException::class.java)
        }
        val file = execute(GetDocumentItemInDocumentationsMacroQuery(createdMacro.uuid, fileName, user)).get() as DocItem
        assertThat(file.content).isNotBlank()

        removeFilesAtRepository()
        val revisionDeletedFiles = getCurrentRevision(createdMacro.uuid)
        assertThat(revisionDeletedFiles.id).isNotEqualToIgnoringCase(revision.id)
        await().atMost(3, TimeUnit.SECONDS).until {
            execute(RefreshDocumentationsMacroCommand(createdMacro.uuid, user)).get()
            execute(ViewMacroCommand(MacroToView(createdMacro.uuid, MacroType.MULTIFILE), PageToView(""), SpaceToView(""))).get()
            assertThat(getMacro(createdMacro.uuid).revision).isEqualToIgnoringCase(revisionDeletedFiles.id);
            assertThat(getTree(createdMacro.uuid).getChildren()).asList().hasSize(0)
        }
    }

    // TODO: Test that conversion work after update of repository when document is cached!

    private fun removeFilesAtRepository() {
        val git = Git.open(remoteDir)
        println("${remoteDir.absolutePath}")
        File(remoteDir.absolutePath + File.pathSeparatorChar + "newfile.txt").delete()
        File(remoteDir.absolutePath + File.pathSeparatorChar + "testfile").delete()
        git.rm().addFilepattern("newfile.txt").call()
        git.rm().addFilepattern("testfile").call()
        git.commit().setMessage("Submit a new File").call()
    }

    private fun getMacro(uuid: String): DocumentationsMacro {
        await().until { Assertions.assertThat(execute(GetDocumentationsMacroByDocumentationsMacroIdQuery(uuid, user)).get()).isNotInstanceOf(NotReadyException::class.java) }
        val macro = execute(GetDocumentationsMacroByDocumentationsMacroIdQuery(uuid, user)).get() as DocumentationsMacro
        return macro
    }

    private fun getCurrentRevision(uuid: String): Revision {
        val job = execute(GetLatestRevisionByDocumentationsMacroIdQuery(uuid, user)).get() as RequestId
        await().until { Assertions.assertThat(execute(GetLatestRevisionByDocumentationsMacroIdResultRequest(job.requestId)).get()).isNotInstanceOf(NotReadyException::class.java) }
        val revision = execute(GetLatestRevisionByDocumentationsMacroIdResultRequest(job.requestId)).get() as Revision
        return revision
    }

    private fun createMacro(): DocumentationsMacro {
        val repositoryToCreate = CustomRepository(repo.repositoryPath, NoAuth())
        val repository = RepositoryDetails(repositoryToCreate)
        val macroToCreate = DocumentationMacro(repository, "testRepository", "master", listOf(), "readme.me", null, null)
        val request = execute(CreateDocumentationsMacroCommand(macroToCreate, user)).get() as RequestId
        await().until { Assertions.assertThat(execute(CreateDocumentationsMacroResultRequest(request.requestId)).get()).isInstanceOf(SavedDocumentationsMacro::class.java) }
        val savedMacro = execute(CreateDocumentationsMacroResultRequest(request.requestId)).get() as SavedDocumentationsMacro
        await().until { Assertions.assertThat(execute(GetDocumentationsMacroByDocumentationsMacroIdQuery(savedMacro.uuid, user)).get()).isNotInstanceOf(NotReadyException::class.java) }
        val macro = execute(GetDocumentationsMacroByDocumentationsMacroIdQuery(savedMacro.uuid, user)).get() as DocumentationsMacro
        assertThat(macro.revision).isNotBlank()
        return macro
    }

    private fun getTree(macroId: String): DocumentationsContentTree {
        await().until { Assertions.assertThat(execute(GetDocumentationsContentTreeByDocumentationsMacroIdQuery(macroId, user)).get()).isNotInstanceOf(NotReadyException::class.java) }
        return execute(GetDocumentationsContentTreeByDocumentationsMacroIdQuery(macroId, user)).get() as DocumentationsContentTree
    }

    private fun addNewFileToRemoteRepository() {
        val git = Git.open(remoteDir)
        File(remoteDir, "newfile.txt").writeText("New File")
        git.add().addFilepattern("newfile.txt").call()
        git.commit().setMessage("Submit a new File").call()
    }

    fun awaiting(): ConditionFactory {
        return await().atMost(1, TimeUnit.MINUTES).ignoreExceptions()
    }

}