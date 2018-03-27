package com.networkedassets.git4c.core

import com.atlassian.confluence.core.service.NotAuthorizedException
import com.jayway.awaitility.Awaitility.await
import com.networkedassets.git4c.application.PluginComponents
import com.networkedassets.git4c.boundary.CreateDocumentationsMacroCommand
import com.networkedassets.git4c.boundary.CreateDocumentationsMacroResultRequest
import com.networkedassets.git4c.boundary.inbound.*
import com.networkedassets.git4c.boundary.outbound.RequestId
import com.networkedassets.git4c.boundary.outbound.SavedDocumentationsMacro
import com.networkedassets.git4c.boundary.outbound.VerificationStatus
import com.networkedassets.git4c.data.PredefinedRepository
import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.test.AsyncUseCaseTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CreateDocumentationsMacroUseCaseTest : AsyncUseCaseTest<CreateDocumentationsMacroUseCase, CreateDocumentationsMacroResultUseCase>() {

    override fun getAnswerUseCase(plugin: PluginComponents): CreateDocumentationsMacroResultUseCase {
        return CreateDocumentationsMacroResultUseCase(plugin.bussines)
    }

    override fun getUseCase(plugin: PluginComponents): CreateDocumentationsMacroUseCase {
        return CreateDocumentationsMacroUseCase(plugin.bussines)
    }


    @Test
    fun `Git4C Macro is created when proper Custom Repository is used`() {
        val repositoryToCreate = CustomRepository("src/test/resources", NoAuth())
        val repository = RepositoryDetails(repositoryToCreate)
        val macroToCreate = DocumentationMacro(repository, "testRepository", "master", listOf("glob"), "readme.me", null, null)
        val createMacroCommand = CreateDocumentationsMacroCommand(macroToCreate, "anonymous");

        val request = useCase.execute(createMacroCommand);
        assertThat(request.component1()).isInstanceOf(RequestId::class.java)

        await().until {
            assertThat(useCaseWithAnswer.execute(CreateDocumentationsMacroResultRequest(request.get().requestId)).component1()).isNotNull()
        }

        val answer = useCaseWithAnswer.execute(CreateDocumentationsMacroResultRequest(request.get().requestId))
        assertNotNull(answer.component1())
        assertThat(answer.component1()).isInstanceOf(SavedDocumentationsMacro::class.java)
    }

    @Test
    fun `Git4C Macro is not created when wrong data in Custom Repository were used`() {
        val repositoryToCreate = CustomRepository("", NoAuth())
        val repository = RepositoryDetails(repositoryToCreate)
        val macroToCreate = DocumentationMacro(repository, "testRepository", "master", listOf("glob"), "readme.me", null, null)
        val createMacroCommand = CreateDocumentationsMacroCommand(macroToCreate, "anonymous");

        val request = useCase.execute(createMacroCommand);
        assertThat(request.component1()).isInstanceOf(RequestId::class.java)

        await().until {
            assertThat(useCaseWithAnswer.execute(CreateDocumentationsMacroResultRequest(request.get().requestId)).component2()).isInstanceOf(IllegalArgumentException::class.java)
        }

        val answer = useCaseWithAnswer.execute(CreateDocumentationsMacroResultRequest(request.get().requestId))

        assertNull(answer.component1())
        assertNotNull(answer.component2())
        assertTrue(answer.component2()!!.message == VerificationStatus.SOURCE_NOT_FOUND.name)
    }

    @Test
    fun `Git4C Macro is created when proper Predefined Repository is used`() {
        components.providers.repositoryProvider.put("1", RepositoryWithNoAuthorization("1", "src/test/resources", false))
        components.database.predefinedRepositoryDatabase.put("1", PredefinedRepository("1", "1", "name_predefine"))
        val repositoryToCreate = PredefinedRepositoryToCreate("1")
        val repository = RepositoryDetails(repositoryToCreate)
        val macroToCreate = DocumentationMacro(repository, "testRepository", "master", listOf("glob"), "readme.me", null, null)
        val createMacroCommand = CreateDocumentationsMacroCommand(macroToCreate, "anonymous");

        val request = useCase.execute(createMacroCommand);
        assertThat(request.component1()).isInstanceOf(RequestId::class.java)

        await().until {
            assertThat(useCaseWithAnswer.execute(CreateDocumentationsMacroResultRequest(request.get().requestId)).component1()).isNotNull()
        }

        val answer = useCaseWithAnswer.execute(CreateDocumentationsMacroResultRequest(request.get().requestId))
        assertNotNull(answer.component1())
        assertThat(answer.component1()).isInstanceOf(SavedDocumentationsMacro::class.java)
    }

    @Test
    fun `Git4C Macro is not created when not existing Repository of Predefined Repository is used`() {
        components.database.predefinedRepositoryDatabase.put("1", PredefinedRepository("1", "1", "name_predefine"))
        val repositoryToCreate = PredefinedRepositoryToCreate("1")
        val repository = RepositoryDetails(repositoryToCreate)
        val macroToCreate = DocumentationMacro(repository, "testRepository", "master", listOf("glob"), "readme.me", null, null)
        val createMacroCommand = CreateDocumentationsMacroCommand(macroToCreate, "anonymous");

        val request = useCase.execute(createMacroCommand);
        assertThat(request.component1()).isInstanceOf(RequestId::class.java)

        await().until {
            assertThat(useCaseWithAnswer.execute(CreateDocumentationsMacroResultRequest(request.get().requestId)).component2()).isInstanceOf(IllegalArgumentException::class.java)
        }

        val answer = useCaseWithAnswer.execute(CreateDocumentationsMacroResultRequest(request.get().requestId))

        assertNull(answer.component1())
        assertNotNull(answer.component2())
        assertTrue(answer.component2()!!.message == VerificationStatus.REMOVED.name)
    }

    @Test
    fun `Git4C Macro is not created when not existing Predefined Repository is used`() {
        components.providers.repositoryProvider.put("1", RepositoryWithNoAuthorization("1", "src/test/resources", false))
        val repositoryToCreate = PredefinedRepositoryToCreate("1")
        val repository = RepositoryDetails(repositoryToCreate)
        val macroToCreate = DocumentationMacro(repository, "testRepository", "master", listOf("glob"), "readme.me", null, null)
        val createMacroCommand = CreateDocumentationsMacroCommand(macroToCreate, "anonymous");

        val request = useCase.execute(createMacroCommand);
        assertThat(request.component1()).isInstanceOf(RequestId::class.java)

        await().until {
            assertThat(useCaseWithAnswer.execute(CreateDocumentationsMacroResultRequest(request.get().requestId)).component2()).isInstanceOf(IllegalArgumentException::class.java)
        }

        val answer = useCaseWithAnswer.execute(CreateDocumentationsMacroResultRequest(request.get().requestId))

        assertNull(answer.component1())
        assertNotNull(answer.component2())
        assertTrue(answer.component2()!!.message == VerificationStatus.REMOVED.name)
    }

    @Test
    fun `Git4C Macro is not created when not proper access data in existing Predefined Repository is used`() {
        components.database.predefinedRepositoryDatabase.put("1", PredefinedRepository("1", "1", "name_predefine"))
        components.providers.repositoryProvider.put("1", RepositoryWithNoAuthorization("1", "", false))
        val repositoryToCreate = PredefinedRepositoryToCreate("1")
        val repository = RepositoryDetails(repositoryToCreate)
        val macroToCreate = DocumentationMacro(repository, "testRepository", "master", listOf("glob"), "readme.me", null, null)
        val createMacroCommand = CreateDocumentationsMacroCommand(macroToCreate, "anonymous");

        val request = useCase.execute(createMacroCommand);
        assertThat(request.component1()).isInstanceOf(RequestId::class.java)

        await().until {
            assertThat(useCaseWithAnswer.execute(CreateDocumentationsMacroResultRequest(request.get().requestId)).component2()).isInstanceOf(IllegalArgumentException::class.java)
        }

        val answer = useCaseWithAnswer.execute(CreateDocumentationsMacroResultRequest(request.get().requestId))

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
        components.database.pluginSettings.setForcePredefinedRepositories(true)

        val request = useCase.execute(createMacroCommand);
        assertThat(request.component1()).isInstanceOf(RequestId::class.java)

        await().until {
            assertThat(useCaseWithAnswer.execute(CreateDocumentationsMacroResultRequest(request.get().requestId)).component2()).isInstanceOf(NotAuthorizedException::class.java)
        }

        val answer = useCaseWithAnswer.execute(CreateDocumentationsMacroResultRequest(request.get().requestId))

        assertNull(answer.component1())
        assertNotNull(answer.component2())
        assertTrue(answer.component2() is NotAuthorizedException)
    }

    @Test
    fun `Git4C Macro is created when Administrator forces users to use predefined repository and proper Predefined Repository is used`() {
        components.providers.repositoryProvider.put("1", RepositoryWithNoAuthorization("1", "src/test/resources", false))
        components.database.predefinedRepositoryDatabase.put("1", PredefinedRepository("1", "1", "name_predefine"))
        components.database.pluginSettings.setForcePredefinedRepositories(true)
        val repositoryToCreate = PredefinedRepositoryToCreate("1")
        val repository = RepositoryDetails(repositoryToCreate)
        val macroToCreate = DocumentationMacro(repository, "testRepository", "master", listOf("glob"), "readme.me", null, null)
        val createMacroCommand = CreateDocumentationsMacroCommand(macroToCreate, "anonymous");

        val request = useCase.execute(createMacroCommand);
        assertThat(request.component1()).isInstanceOf(RequestId::class.java)

        await().until {
            assertThat(useCaseWithAnswer.execute(CreateDocumentationsMacroResultRequest(request.get().requestId)).component1()).isNotNull()
        }

        val answer = useCaseWithAnswer.execute(CreateDocumentationsMacroResultRequest(request.get().requestId))
        assertNotNull(answer.component1())
        assertThat(answer.component1()).isInstanceOf(SavedDocumentationsMacro::class.java)
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

        val list = mutableListOf<RequestId>()
        for (i in 0..10) {
            Thread.sleep(10)
            list.add(useCase.execute(commandList.get(i % 4)).get())
        }

        await().until {
            assertThat(list.size).isEqualTo(11)
            for (i in 0..10) assertThat(useCaseWithAnswer.execute(CreateDocumentationsMacroResultRequest(list.get(i).requestId)).component1()).isNotNull()
        }

        val usages = useCase.repositoryUsageDatabase.getByUsername("anonymous")

        assertThat(usages.size).isEqualTo(5)

        assertThat(usages.first().repositoryName).isEqualTo("testRepository2")
        assertThat(usages.last().repositoryName).isEqualTo("testRepository2")
    }

}