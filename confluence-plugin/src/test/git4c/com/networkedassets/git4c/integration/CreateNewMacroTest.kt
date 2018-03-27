package com.networkedassets.git4c.integration

import com.jayway.awaitility.Awaitility.await
import com.networkedassets.git4c.boundary.*
import com.networkedassets.git4c.boundary.inbound.*
import com.networkedassets.git4c.boundary.outbound.*
import com.networkedassets.git4c.data.PageAndSpacePermissionsForUser
import com.networkedassets.git4c.utils.InMemoryApplication.application
import com.networkedassets.git4c.utils.InMemoryApplication.execute
import com.networkedassets.git4c.utils.genTransactionId
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.file.Files

class CreateNewMacroTest {

    lateinit var directory: File

    @Before
    fun setUp() {
        directory = Files.createTempDirectory("git4c").toFile()
        val readme = File(directory, "README.md")
        readme.createNewFile()
        readme.writeText("Example README\n")
    }

    @After
    fun cleanup() {
        directory.deleteRecursively()
    }

    @Test
    fun `Created macro should be accessible after page load`() {
        val repositoryToCreate = CustomRepository(directory.absolutePath, NoAuth())
        val repository = RepositoryDetails(repositoryToCreate)
        val macroToCreate = DocumentationMacro(repository, "testRepository", "master", listOf(), "README.md", null, null)
        val user = "test"
        val permission = PageAndSpacePermissionsForUser("", "", "test", true)
        application.cache.pageAndSpacePermissionsForUserCache.put(permission.uuid, permission)

        val request = execute(CreateDocumentationsMacroCommand(macroToCreate, user)).get() as RequestId
        await().until { assertThat(execute(CreateDocumentationsMacroResultRequest(request.requestId)).get()).isInstanceOf(SavedDocumentationsMacro::class.java) }
        val createdMacro = execute(CreateDocumentationsMacroResultRequest(request.requestId)).get() as SavedDocumentationsMacro

        val view =
                execute(ViewMacroCommand(MacroToView(createdMacro.uuid, MacroType.MULTIFILE), PageToView(""), SpaceToView(""))).get() as MacroView

        await().ignoreExceptions().until({
            val macro = execute(GetDocumentationsMacroByDocumentationsMacroIdQuery(view.macroId, user)).get() as DocumentationsMacro
            assertThat(macro.uuid).isEqualTo(view.macroId)
        })
    }

    @Test
    fun `Created macro should be accessible when view page has not been aquired`() {
        val repositoryToCreate = CustomRepository(directory.absolutePath, NoAuth())
        val repository = RepositoryDetails(repositoryToCreate)
        val macroToCreate = DocumentationMacro(repository, "testRepository", "master", listOf(), "README.md", null, null)
        val user = "test"
        val permission = PageAndSpacePermissionsForUser("", "", "test", true)
        application.cache.pageAndSpacePermissionsForUserCache.put(permission.uuid, permission)

        val request = execute(CreateDocumentationsMacroCommand(macroToCreate, user)).get() as RequestId
        await().until { assertThat(execute(CreateDocumentationsMacroResultRequest(request.requestId)).get()).isInstanceOf(SavedDocumentationsMacro::class.java) }
        val createdMacro = execute(CreateDocumentationsMacroResultRequest(request.requestId)).get() as SavedDocumentationsMacro

        await().ignoreExceptions().until({
            val macro = execute(GetDocumentationsMacroByDocumentationsMacroIdQuery(createdMacro.uuid, user)).get() as DocumentationsMacro
            assertThat(macro.uuid).isEqualTo(createdMacro.uuid)
        })
    }

    @Test
    fun `Created macro should have accessible default file content`() {
        val repositoryToCreate = CustomRepository(directory.absolutePath, NoAuth())
        val repository = RepositoryDetails(repositoryToCreate)
        val macroToCreate = DocumentationMacro(repository, "testRepository", "master", listOf(), "README.md", null, null)
        val user = "test"
        val permission = PageAndSpacePermissionsForUser("", "", "test", true)
        application.cache.pageAndSpacePermissionsForUserCache.put(permission.uuid, permission)

        val request = execute(CreateDocumentationsMacroCommand(macroToCreate, user)).get() as RequestId
        await().until { assertThat(execute(CreateDocumentationsMacroResultRequest(request.requestId)).get()).isInstanceOf(SavedDocumentationsMacro::class.java) }
        val createdMacro = execute(CreateDocumentationsMacroResultRequest(request.requestId)).get() as SavedDocumentationsMacro

        await().ignoreExceptions().until({
            val document = execute(GetDocumentItemInDocumentationsMacroQuery(createdMacro.uuid, macroToCreate.defaultDocItem, user)).get() as DocItem
            assertThat(document).isNotNull()
            assertThat(document.content).contains("<span><p>Example README</p> </span>")
        })
    }

    @Test
    fun `Display of multiple macros with the same repository on different branch should allow to see file`() {

        // Setup
        val macros = mutableListOf<String>()
        val user = genTransactionId()
        val permission = PageAndSpacePermissionsForUser("", "", user, true)
        application.cache.pageAndSpacePermissionsForUserCache.put(permission.uuid, permission)

        // Given
        val macrosNumber = 10
        val documentName = "README.md"

        // When: Creating multiple macros
        for (times in 1..macrosNumber) Thread {
            val repositoryToCreate = CustomRepository(directory.absolutePath, NoAuth())
            val repository = RepositoryDetails(repositoryToCreate)
            val macroToCreate = DocumentationMacro(repository, genTransactionId(), genTransactionId(), listOf(), documentName, null, null)
            val request = execute(CreateDocumentationsMacroCommand(macroToCreate, user)).get() as RequestId
            await().until { assertThat(execute(CreateDocumentationsMacroResultRequest(request.requestId)).get()).isInstanceOf(SavedDocumentationsMacro::class.java) }
            val createdMacro = execute(CreateDocumentationsMacroResultRequest(request.requestId)).get() as SavedDocumentationsMacro
            macros.add(createdMacro.uuid)
        }.run()

        // Then
        await().ignoreExceptions().until({
            assertThat(macros).size().isEqualTo(macrosNumber)
        })

        macros.forEach { macro ->
            execute(GetDocumentItemInDocumentationsMacroQuery(macro, documentName, user)).get()
        }

        await().ignoreExceptions().until({
            macros.forEach { macro ->
                val document = execute(GetDocumentItemInDocumentationsMacroQuery(macro, documentName, user)).get() as DocItem
                assertThat(document).isNotNull()
                assertThat(document.content).contains("<span><p>Example README</p> </span>")
            }
        })
    }

    @Test
    fun `Created Single File macro should have only one file in index`() {
        val repositoryToCreate = CustomRepository(directory.absolutePath, NoAuth())
        val repository = RepositoryDetails(repositoryToCreate)
        val macroToCreate = DocumentationMacro(repository, "testRepository", "master", listOf("README.md"), "README.md", null, null)
        val user = "test"
        val permission = PageAndSpacePermissionsForUser("", "", "test", true)
        application.cache.pageAndSpacePermissionsForUserCache.put(permission.uuid, permission)

        val request = execute(CreateDocumentationsMacroCommand(macroToCreate, user)).get() as RequestId
        await().until { assertThat(execute(CreateDocumentationsMacroResultRequest(request.requestId)).get()).isInstanceOf(SavedDocumentationsMacro::class.java) }
        val createdMacro = execute(CreateDocumentationsMacroResultRequest(request.requestId)).get() as SavedDocumentationsMacro

        val view =
                execute(ViewMacroCommand(MacroToView(createdMacro.uuid, MacroType.SINGLEFILE), PageToView(""), SpaceToView(""))).get() as MacroView

        await().ignoreExceptions().until({
            val macro = execute(GetDocumentationsMacroByDocumentationsMacroIdQuery(view.macroId, user)).get() as DocumentationsMacro
            assertThat(macro.uuid).isEqualTo(view.macroId)
        })

        val macroFiles = execute(GetDocumentationsContentTreeByDocumentationsMacroIdQuery(view.macroId, user)).get() as DocumentationsContentTree
        assertThat(macroFiles.getChildren()).hasSize(1)
        assertThat(macroFiles.getChildren().get(0).name).isEqualTo("README.md")
        assertThat(macroFiles.getChildren().get(0).type).isEqualTo(DocumentationsContentTree.NodeType.DOCITEM)
    }

}

