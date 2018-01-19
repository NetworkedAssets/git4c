package com.networkedassets.git4c.core

import com.networkedassets.git4c.boundary.GetExecutorThreadNumbersQuery
import com.networkedassets.git4c.boundary.outbound.ExecutorThreadNumbers
import com.networkedassets.git4c.core.business.ConfluenceQueryExecutorHolder
import com.networkedassets.git4c.core.business.ConverterExecutorHolder
import com.networkedassets.git4c.core.business.RepositoryPullExecutorHolder
import com.networkedassets.git4c.core.business.RevisionCheckExecutorHolder
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class GetExecutorThreadNumbersUseCaseTest {

    @Mock
    lateinit var revisionCheckExecutorHolder: RevisionCheckExecutorHolder

    @Mock
    lateinit var repositoryPullExecutorHolder: RepositoryPullExecutorHolder

    @Mock
    lateinit var converterExecutorHolder: ConverterExecutorHolder

    @Mock
    lateinit var confluenceQueryExecutorHolder: ConfluenceQueryExecutorHolder

    lateinit var useCase: GetExecutorThreadNumbersUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        useCase = GetExecutorThreadNumbersUseCase(
                revisionCheckExecutorHolder,
                repositoryPullExecutorHolder,
                converterExecutorHolder,
                confluenceQueryExecutorHolder
        )
    }

    @Test
    fun `Use case returns number of threads in holder`() {
        whenever(revisionCheckExecutorHolder.getThreadNumber()).thenReturn(1)
        whenever(repositoryPullExecutorHolder.getThreadNumber()).thenReturn(2)
        whenever(converterExecutorHolder.getThreadNumber()).thenReturn(3)
        whenever(confluenceQueryExecutorHolder.getThreadNumber()).thenReturn(4)

        val result = useCase.execute(GetExecutorThreadNumbersQuery())
        assertThat(result.component1()).isNotNull()
        assertThat(result.component2()).isNull()
        assertThat(result.component1()).isEqualTo(ExecutorThreadNumbers(1,2,3, 4))

    }

}