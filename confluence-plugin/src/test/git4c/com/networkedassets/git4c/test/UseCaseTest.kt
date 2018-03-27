package com.networkedassets.git4c.test

import com.atlassian.activeobjects.external.ActiveObjects
import com.jayway.awaitility.Awaitility.await
import com.networkedassets.git4c.application.PluginComponents
import com.networkedassets.git4c.boundary.RemoveAllDataCommand
import com.networkedassets.git4c.boundary.RestoreDefaultPredefinedGlobsCommand
import com.networkedassets.git4c.boundary.outbound.PredefinedGlobsData
import com.networkedassets.git4c.boundary.outbound.exceptions.NotReadyException
import com.networkedassets.git4c.core.business.Computation
import com.networkedassets.git4c.core.business.DefaultGlobsMap
import com.networkedassets.git4c.core.bussiness.ComputationCache
import com.networkedassets.git4c.core.usecase.async.AsyncBackendRequest
import com.networkedassets.git4c.core.usecase.async.AsyncUseCase
import com.networkedassets.git4c.core.usecase.async.BackendRequestForAsyncResult
import com.networkedassets.git4c.core.usecase.async.ComputationResultUseCase
import com.networkedassets.git4c.delivery.executor.execution.BackendDispatcher
import com.networkedassets.git4c.delivery.executor.execution.UseCase
import com.networkedassets.git4c.utils.InMemoryApplication.getComponents
import com.networkedassets.git4c.utils.genTransactionId
import com.networkedassets.git4c.utils.sendToExecution
import net.java.ao.EntityManager
import net.java.ao.test.jdbc.H2Memory
import net.java.ao.test.jdbc.Jdbc
import net.java.ao.test.junit.ActiveObjectsJUnitRunner
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


abstract class AsyncResultUseCaseTest<USECASE : AsyncUseCase<REQUEST, RESULT>, ANSWER_USECASE : ComputationResultUseCase<REQUEST_FOR_ANSWER, RESULT>, REQUEST : AsyncBackendRequest, RESULT : Any, REQUEST_FOR_ANSWER : BackendRequestForAsyncResult<RESULT>>
    : AsyncUseCaseTest<USECASE, ANSWER_USECASE>() {

    lateinit var requestId: String

    abstract fun getAnswerCache(): ComputationCache<RESULT>

    abstract fun getResultRequest(requestId: String): REQUEST_FOR_ANSWER

    abstract fun getExpectedProperAnswer(): RESULT

    abstract fun getCommandForProperAnswer(): REQUEST

    @Before
    fun setUpResult() {
        useCaseWithAnswer = getAnswerUseCase(components)
        requestId = components.utils.idGenerator.generateNewIdentifier()
    }


    @Test
    fun `When result is not in cache error is returned`() {
        getAnswerCache().remove(requestId)
        val result = useCaseWithAnswer.execute(getResultRequest(requestId))

        assertThat(result.component1()).isNull()
        assertThat(result.component2()).isNotNull()
    }

    @Test
    fun `When computation is not done 202 is returned`() {

        val res = Computation<Nothing>(
                genTransactionId(),
                state = Computation.ComputationState.RUNNING
        )
        getAnswerCache().put(requestId, res)

        val result = useCaseWithAnswer.execute(getResultRequest(requestId))

        assertThat(result.component1()).isNull()
        assertThat(result.component2()).isNotNull()
        assertThat(result.component2()).isExactlyInstanceOf(NotReadyException::class.java)

    }

    @Test
    fun `When computation errored exception is returned`() {

        val exception = Exception()

        val res = Computation<Nothing>(
                genTransactionId(),
                state = Computation.ComputationState.FAILED,
                error = exception
        )

        components.async.publishFileComputationCache.put(requestId, res)

        val result = useCaseWithAnswer.execute(getResultRequest(requestId))

        assertThat(result.component1()).isNull()
        assertThat(result.component2()).isNotNull()
        assertThat(result.component2()).isSameAs(exception)
    }

    @Test
    fun `When computation succeed result is returned`() {

        val request = useCase.execute(getCommandForProperAnswer()).component1()!!.requestId

        await().until { assertThat(useCaseWithAnswer.execute(getResultRequest(request)).component1()).isNotNull() }

        val result = useCaseWithAnswer.execute(getResultRequest(request))

        assertThat(result.component1()).isNotNull()
        assertThat(result.component1()).isSameAs(getExpectedProperAnswer())
        assertThat(result.component2()).isNull()
        assertThat(getAnswerCache().get(request)!!.state).isEqualTo(Computation.ComputationState.FINISHED)
    }
}

abstract class AsyncUseCaseTest<USECASE : AsyncUseCase<*, *>, ANSWER_USECASE : ComputationResultUseCase<*, *>>
    : UseCaseTest<USECASE>() {

    abstract fun getAnswerUseCase(plugin: PluginComponents): ANSWER_USECASE

    lateinit var useCaseWithAnswer: ANSWER_USECASE

    @Before
    fun setUpAsync() {
        useCaseWithAnswer = getAnswerUseCase(components)
    }
}

@RunWith(ActiveObjectsJUnitRunner::class)
@Jdbc(H2Memory::class)
abstract class UseCaseTest<USECASE : UseCase<*, *>> {

    private lateinit var entityManager: EntityManager
    private lateinit var ao: ActiveObjects

    lateinit var components: PluginComponents

    lateinit var builder: MockPluginBuilder

    lateinit var useCase: USECASE

    @Before
    fun setUp() {
        components = getComponents()
        builder = MockPluginBuilder(components)
        useCase = getUseCase(components)
        builder.state.reset()
    }

    abstract fun getUseCase(plugin: PluginComponents): USECASE
}

class MockPluginBuilder(components: PluginComponents) {
    val commands = CommandBuilder()
    val queries = QueryBuilder()
    val executor = UseCaseExecutor(components.dispatching.dispatcher, commands, queries)
    val state = StateBuilder(components, commands, queries, executor)
}

class StateBuilder(components: PluginComponents, val commands: CommandBuilder, val queries: QueryBuilder, val executor: UseCaseExecutor) {

    val assertions = UseCaseAssertions(components)

    fun reset() {
        removeAllData()
        restoreDefaultPredefinedGlobs()
    }

    private fun restoreDefaultPredefinedGlobs() {
        executor.restoreDefaultPredefinedGlobs()
        assertions.thereAreOnlyDefultGlobsInDatabase()
    }

    private fun removeAllData() {
        executor.removeAllDataUseCase()
        assertions.thereIsNoDataInDatabase()
    }
}

class CommandBuilder {

    fun removeAllDataCommand(): RemoveAllDataCommand {
        return RemoveAllDataCommand()
    }

    fun restoreDefaultPredefinedGlobsCommand(): RestoreDefaultPredefinedGlobsCommand {
        return RestoreDefaultPredefinedGlobsCommand()
    }

}

class QueryBuilder {
}

class UseCaseExecutor(val dispatcher: BackendDispatcher<Any, Throwable>, val commands: CommandBuilder, val queries: QueryBuilder) {

    fun removeAllDataUseCase(): String {
        return sendToExecution(dispatcher, commands.removeAllDataCommand())
    }

    fun restoreDefaultPredefinedGlobs(): PredefinedGlobsData {
        return sendToExecution(dispatcher, commands.restoreDefaultPredefinedGlobsCommand())
    }

}

class UseCaseAssertions(val components: PluginComponents) {

    val defaultGlobsMap = DefaultGlobsMap().defaultGlobs

    fun thereAreOnlyDefultGlobsInDatabase() {
        val predefinedGlobsInDatabase = components.database.predefinedGlobsDatabase.getAll()
        assertTrue(predefinedGlobsInDatabase.size == 5)

        defaultGlobsMap.forEach { globFromDefault ->
            assertNotNull(predefinedGlobsInDatabase.firstOrNull { it.name == globFromDefault.key && it.glob.contains(globFromDefault.value) })
        }
    }

    fun thereIsNoDataInDatabase() {
        assertTrue(components.providers.macroSettingsProvider.getAll().isEmpty())
        assertTrue(components.database.macroSettingsDatabase.getAll().isEmpty())
        assertTrue(components.database.predefinedGlobsDatabase.getAll().isEmpty())
        assertTrue(components.providers.repositoryProvider.getAll().isEmpty())
        assertTrue(components.database.predefinedRepositoryDatabase.getAll().isEmpty())
        assertTrue(components.database.globsForMacroDatabase.getAll().isEmpty())
        assertTrue(components.database.encryptedRepositoryDatabase.getAll().isEmpty())
        assertTrue(components.database.macroSettingsDatabase.getAll().isEmpty())
        assertTrue(components.cache.macroSettingsCache.getAll().isEmpty())
        assertTrue(components.cache.temporaryIdCache.getAll().isEmpty())
    }
}
