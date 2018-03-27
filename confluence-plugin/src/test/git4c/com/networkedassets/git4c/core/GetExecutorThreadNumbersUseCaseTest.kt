package com.networkedassets.git4c.core

import com.networkedassets.git4c.application.PluginComponents
import com.networkedassets.git4c.boundary.GetExecutorThreadNumbersQuery
import com.networkedassets.git4c.boundary.outbound.ExecutorThreadNumbers
import com.networkedassets.git4c.test.UseCaseTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class GetExecutorThreadNumbersUseCaseTest : UseCaseTest<GetExecutorThreadNumbersUseCase>() {

    override fun getUseCase(plugin: PluginComponents): GetExecutorThreadNumbersUseCase {
        return GetExecutorThreadNumbersUseCase(plugin.bussines)
    }

    @Test
    fun `Use case returns number of threads in holder`() {
        val result = useCase.execute(GetExecutorThreadNumbersQuery())
        assertThat(result.component1()).isNotNull()
        assertThat(result.component2()).isNull()
        assertThat(result.component1()).isEqualTo(ExecutorThreadNumbers(8, 2, 1, 2))

    }

}