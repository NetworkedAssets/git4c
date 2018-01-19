package com.networkedassets.git4c.core

import com.atlassian.confluence.core.service.NotAuthorizedException
import com.networkedassets.git4c.application.PluginComponents
import com.networkedassets.git4c.boundary.CreateDocumentationsMacroCommand
import com.networkedassets.git4c.boundary.inbound.*
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.data.PredefinedRepository
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.test.UseCaseTest
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CreateDocumentationsMacroUseCaseTest : UseCaseTest<CreateDocumentationsMacroUseCase>() {

    override fun getUseCase(plugin: PluginComponents): CreateDocumentationsMacroUseCase {
        return CreateDocumentationsMacroUseCase(plugin.macroSettingsDatabase, plugin.repositoryDatabase, plugin.globsForMacroDatabase, plugin.predefinedRepositoryDatabase, plugin.extractorDataDatabase, plugin.importer, plugin.converter, plugin.idGenerator, plugin.pluginSettings, plugin.repositoryUsageDatabase, plugin.createMacroProcess)
    }


    @Test
    fun `Git4C Macro is created when proper Custom Repository is used`() {
        val repositoryToCreate = CustomRepository("src/test/resources", NoAuth())
        val repository = RepositoryDetails(repositoryToCreate)
        val macroToCreate = DocumentationMacro(repository, "testRepository", "master", listOf("glob"), "readme.me", null, null)
        val createMacroCommand = CreateDocumentationsMacroCommand(macroToCreate, "anonymous");

        val answer = useCase.execute(createMacroCommand);

        assertNotNull(answer.component1())
    }

    @Test
    fun `Git4C Macro is not created when wrong data in Custom Repository were used`() {
        val repositoryToCreate = CustomRepository("", NoAuth())
        val repository = RepositoryDetails(repositoryToCreate)
        val macroToCreate = DocumentationMacro(repository, "testRepository", "master", listOf("glob"), "readme.me", null, null)
        val createMacroCommand = CreateDocumentationsMacroCommand(macroToCreate, "anonymous");

        val answer = useCase.execute(createMacroCommand);

        assertNull(answer.component1())
        assertNotNull(answer.component2())
        assertTrue(answer.component2()!!.message == VerificationStatus.SOURCE_NOT_FOUND.name)
    }

    @Test
    fun `Git4C Macro is created when proper Predefined Repository is used`() {
        components.repositoryDatabase.put("1", RepositoryWithNoAuthorization("1", "src/test/resources"))
        components.predefinedRepositoryDatabase.put("1", PredefinedRepository("1", "1", "name_predefine"))
        val repositoryToCreate = PredefinedRepositoryToCreate("1")
        val repository = RepositoryDetails(repositoryToCreate)
        val macroToCreate = DocumentationMacro(repository, "testRepository", "master", listOf("glob"), "readme.me", null, null)
        val createMacroCommand = CreateDocumentationsMacroCommand(macroToCreate, "anonymous");

        val answer = useCase.execute(createMacroCommand);

        assertNotNull(answer.component1())
    }

    @Test
    fun `Git4C Macro is not created when not existing Repository of Predefined Repository is used`() {
        components.predefinedRepositoryDatabase.put("1", PredefinedRepository("1", "1", "name_predefine"))
        val repositoryToCreate = PredefinedRepositoryToCreate("1")
        val repository = RepositoryDetails(repositoryToCreate)
        val macroToCreate = DocumentationMacro(repository, "testRepository", "master", listOf("glob"), "readme.me", null, null)
        val createMacroCommand = CreateDocumentationsMacroCommand(macroToCreate, "anonymous");

        val answer = useCase.execute(createMacroCommand);

        assertNull(answer.component1())
        assertNotNull(answer.component2())
        assertTrue(answer.component2()!!.message == VerificationStatus.REMOVED.name)
    }

    @Test
    fun `Git4C Macro is not created when not existing Predefined Repository is used`() {
        components.repositoryDatabase.put("1", RepositoryWithNoAuthorization("1", "src/test/resources"))
        val repositoryToCreate = PredefinedRepositoryToCreate("1")
        val repository = RepositoryDetails(repositoryToCreate)
        val macroToCreate = DocumentationMacro(repository, "testRepository", "master", listOf("glob"), "readme.me", null, null)
        val createMacroCommand = CreateDocumentationsMacroCommand(macroToCreate, "anonymous");

        val answer = useCase.execute(createMacroCommand);

        assertNull(answer.component1())
        assertNotNull(answer.component2())
        assertTrue(answer.component2()!!.message == VerificationStatus.REMOVED.name)
    }

    @Test
    fun `Git4C Macro is not created when not proper access data in existing Predefined Repository is used`() {
        components.predefinedRepositoryDatabase.put("1", PredefinedRepository("1", "1", "name_predefine"))
        components.repositoryDatabase.put("1", RepositoryWithNoAuthorization("1", ""))
        val repositoryToCreate = PredefinedRepositoryToCreate("1")
        val repository = RepositoryDetails(repositoryToCreate)
        val macroToCreate = DocumentationMacro(repository, "testRepository", "master", listOf("glob"), "readme.me", null, null)
        val createMacroCommand = CreateDocumentationsMacroCommand(macroToCreate, "anonymous");

        val answer = useCase.execute(createMacroCommand);

        assertNull(answer.component1())
        assertNotNull(answer.component2())
        assertTrue(answer.component2()!!.message == VerificationStatus.SOURCE_NOT_FOUND.name)
    }

    @Test
    fun `Git4C Macro is not created with custom Repository when administrator forced users to use predefined repositories`() {
        val repositoryToCreate = CustomRepository("", NoAuth())
        val repository = RepositoryDetails(repositoryToCreate)
        val macroToCreate = DocumentationMacro(repository, "testRepository", "master", listOf("glob"), "readme.me", null, null)
        val createMacroCommand = CreateDocumentationsMacroCommand(macroToCreate, "anonymous");
        components.pluginSettings.setForcePredefinedRepositories(true)

        val answer = useCase.execute(createMacroCommand);

        assertNull(answer.component1())
        assertNotNull(answer.component2())
        assertTrue(answer.component2() is NotAuthorizedException)
    }

    @Test
    fun `Git4C Macro is created when Administrator forces users to use predefined repository and proper Predefined Repository is used`() {
        components.repositoryDatabase.put("1", RepositoryWithNoAuthorization("1", "src/test/resources"))
        components.predefinedRepositoryDatabase.put("1", PredefinedRepository("1", "1", "name_predefine"))
        components.pluginSettings.setForcePredefinedRepositories(true)
        val repositoryToCreate = PredefinedRepositoryToCreate("1")
        val repository = RepositoryDetails(repositoryToCreate)
        val macroToCreate = DocumentationMacro(repository, "testRepository", "master", listOf("glob"), "readme.me", null, null)
        val createMacroCommand = CreateDocumentationsMacroCommand(macroToCreate, "anonymous");

        val answer = useCase.execute(createMacroCommand);

        assertNotNull(answer.component1())
    }

    @Test
    fun `Git4C Macro is created and usage of repository remembered up to 5 repositories`() {

        val repositoryToCreate = CustomRepository("src/test/resources", NoAuth())
        val repository = RepositoryDetails(repositoryToCreate)

        val listOfMacros = ArrayList<DocumentationMacro>()
        listOfMacros.apply {
            for (i in 0..3) {
                listOfMacros.add(DocumentationMacro(repository, "testRepository$i", "master", listOf("glob"), "readme.me", null, null))
            }
        }

        val commandList = ArrayList<CreateDocumentationsMacroCommand>()
        commandList.apply {
            for (i in 0..3) {
                commandList.add(CreateDocumentationsMacroCommand(listOfMacros.get(i), "anonymous"))
            }
        }

        for (i in 0..10) {
            Thread.sleep(10)
            useCase.execute(commandList.get(i % 4))
        }


        val usages = useCase.repositoryUsageDatabase.getByUsername("anonymous")

        assertTrue(usages.size == 5)

        assertTrue(usages.first().repositoryName == "testRepository2")
        assertTrue { usages.last().repositoryName == "testRepository2" }
    }

}