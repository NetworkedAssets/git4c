package com.networkedassets.git4c.test

import com.networkedassets.git4c.application.PluginComponents
import com.networkedassets.git4c.boundary.RemoveAllDataCommand
import com.networkedassets.git4c.boundary.RestoreDefaultPredefinedGlobsCommand
import com.networkedassets.git4c.boundary.outbound.PredefinedGlobsData
import com.networkedassets.git4c.delivery.executor.execution.BackendDispatcher
import com.networkedassets.git4c.utils.sendToExecution

class Builder(components: PluginComponents) {
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

