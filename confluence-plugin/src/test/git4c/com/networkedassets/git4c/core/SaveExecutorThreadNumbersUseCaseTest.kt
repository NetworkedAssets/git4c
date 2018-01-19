package com.networkedassets.git4c.core

import com.networkedassets.git4c.boundary.SaveExecutorThreadNumbersQuery
import com.networkedassets.git4c.boundary.inbound.ExecutorThreadNumbersIn
import com.networkedassets.git4c.core.business.ConfluenceQueryExecutorHolder
import com.networkedassets.git4c.core.business.ConverterExecutorHolder
import com.networkedassets.git4c.core.business.RepositoryPullExecutorHolder
import com.networkedassets.git4c.core.business.RevisionCheckExecutorHolder
import com.networkedassets.git4c.core.datastore.repositories.ThreadSettingsDatabase
import com.nhaarman.mockito_kotlin.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class SaveExecutorThreadNumbersUseCaseTest {

    @Mock
    lateinit var threadSettingsDatabase: ThreadSettingsDatabase

    @Mock
    lateinit var revisionCheckExecutorHolder: RevisionCheckExecutorHolder

    @Mock
    lateinit var repositoryPullExecutorHolder: RepositoryPullExecutorHolder

    @Mock
    lateinit var converterExecutorHolder: ConverterExecutorHolder

    @Mock
    lateinit var confluenceQueryExecutorHolder: ConfluenceQueryExecutorHolder

    lateinit var useCase: SaveExecutorThreadNumbersUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        useCase = SaveExecutorThreadNumbersUseCase(
                threadSettingsDatabase,
                revisionCheckExecutorHolder,
                repositoryPullExecutorHolder,
                converterExecutorHolder,
                confluenceQueryExecutorHolder
        )
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

        verify(threadSettingsDatabase).setRevisionCheckThreadNumber(1)
        verify(threadSettingsDatabase).setRepositoryExecutorThreadNumber(2)
        verify(threadSettingsDatabase).setConverterExecutorThreadNumber(3)
        verify(threadSettingsDatabase).setConfluenceQueryExecutorThreadNumber(4)

        verify(revisionCheckExecutorHolder).reset(1)
        verify(repositoryPullExecutorHolder).reset(2)
        verify(converterExecutorHolder).reset(3)
        verify(confluenceQueryExecutorHolder).reset(4)

    }

}