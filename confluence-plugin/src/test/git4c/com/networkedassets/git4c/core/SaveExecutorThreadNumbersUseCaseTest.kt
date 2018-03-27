package com.networkedassets.git4c.core

import com.networkedassets.git4c.application.PluginComponents
import com.networkedassets.git4c.boundary.SaveExecutorThreadNumbersQuery
import com.networkedassets.git4c.boundary.inbound.ExecutorThreadNumbersIn
import com.networkedassets.git4c.test.UseCaseTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class SaveExecutorThreadNumbersUseCaseTest : UseCaseTest<SaveExecutorThreadNumbersUseCase>() {

    override fun getUseCase(plugin: PluginComponents): SaveExecutorThreadNumbersUseCase {
        return SaveExecutorThreadNumbersUseCase(plugin.bussines)
    }


    @Test
    fun `Use case returns number of threads in holder`() {
        val result = useCase.execute(SaveExecutorThreadNumbersQuery(
                ExecutorThreadNumbersIn(
                        1,
                        2,
                        3,
                        4
                )
        ))

        assertThat(result.component1()).isNotNull()
        assertThat(result.component2()).isNull()


        assertThat(components.executors.revisionCheckExecutor.getThreadNumber()).isEqualTo(1)
        assertThat(components.executors.repositoryPullExecutor.getThreadNumber()).isEqualTo(2)
        assertThat(components.executors.converterExecutor.getThreadNumber()).isEqualTo(3)
        assertThat(components.executors.confluenceQueryExecutor.getThreadNumber()).isEqualTo(4)

    }

}