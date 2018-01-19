package com.networkedassets.git4c.test

import com.atlassian.activeobjects.external.ActiveObjects
import com.networkedassets.git4c.application.PluginComponents
import com.networkedassets.git4c.boundary.RemoveAllDataCommand
import com.networkedassets.git4c.boundary.RestoreDefaultPredefinedGlobsCommand
import com.networkedassets.git4c.boundary.outbound.PredefinedGlobsData
import com.networkedassets.git4c.core.business.DefaultGlobsMap
import com.networkedassets.git4c.delivery.executor.execution.BackendDispatcher
import com.networkedassets.git4c.delivery.executor.execution.UseCase
import com.networkedassets.git4c.utils.InMemoryApplication.getComponents
import com.networkedassets.git4c.utils.sendToExecution
import net.java.ao.EntityManager
import net.java.ao.test.jdbc.H2Memory
import net.java.ao.test.jdbc.Jdbc
import net.java.ao.test.junit.ActiveObjectsJUnitRunner
import org.junit.Before
import org.junit.runner.RunWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

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
    val executor = UseCaseExecutor(components.dispatcher, commands, queries)
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
        val predefinedGlobsInDatabase = components.predefinedGlobsDatabase.getAll()
        assertTrue(predefinedGlobsInDatabase.size == 5)

        defaultGlobsMap.forEach { globFromDefault ->
            assertNotNull(predefinedGlobsInDatabase.firstOrNull { it.name == globFromDefault.key && it.glob.contains(globFromDefault.value) })
        }
    }

    fun thereIsNoDataInDatabase() {
        assertTrue(components.macroSettingsDatabase.getAll().isEmpty())
        assertTrue(components.predefinedGlobsDatabase.getAll().isEmpty())
        assertTrue(components.repositoryDatabase.getAll().isEmpty())
        assertTrue(components.predefinedRepositoryDatabase.getAll().isEmpty())
        assertTrue(components.globsForMacroDatabase.getAll().isEmpty())
        assertTrue(components.encryptedRepositoryDatabase.getAll().isEmpty())
        assertTrue(components.macroSettingsCachableDatabase.getAll().isEmpty())
        assertTrue(components.macroSettingsCache.getAll().isEmpty())
        assertTrue(components.temporaryIdCache.getAll().isEmpty())
    }
}
