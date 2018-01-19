package com.networkedassets.git4c.core

import com.networkedassets.git4c.boundary.PublishFileResultRequest
import com.networkedassets.git4c.boundary.outbound.exceptions.NotReadyException
import com.networkedassets.git4c.core.business.Computation
import com.networkedassets.git4c.core.business.Computation.ComputationState.*
import com.networkedassets.git4c.core.datastore.cache.PublishFileComputationCache
import com.networkedassets.git4c.utils.genTransactionId
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class PublishFileResultUseCaseTest {

    @Mock
    lateinit var cache: PublishFileComputationCache

    lateinit var usecase: PublishFileResultUseCase

    val requestId = "request_id"

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        usecase = PublishFileResultUseCase(cache)
    }

    @Test
    fun `When result is not in cache error is returned`() {
        whenever(cache.get(requestId)).thenReturn(null)

        val result = usecase.execute(PublishFileResultRequest(requestId))

        assertThat(result.component1()).isNull()
        assertThat(result.component2()).isNotNull()
    }

    @Test
    fun `When computation is not done 202 is returned`() {

        val res = Computation<Unit>(
                genTransactionId(),
                state = RUNNING
        )

        whenever(cache.get(requestId)).thenReturn(res)

        val result = usecase.execute(PublishFileResultRequest(requestId))

        assertThat(result.component1()).isNull()
        assertThat(result.component2()).isNotNull()
        assertThat(result.component2()).isExactlyInstanceOf(NotReadyException::class.java)

    }

    @Test
    fun `When computation errored exception is returned`() {

        val exception = Exception()

        val res = Computation<Unit>(
                genTransactionId(),
                state = FAILED,
                error = exception
        )

        whenever(cache.get(requestId)).thenReturn(res)

        val result = usecase.execute(PublishFileResultRequest(requestId))

        assertThat(result.component1()).isNull()
        assertThat(result.component2()).isNotNull()
        assertThat(result.component2()).isSameAs(exception)
    }

    @Test
    fun `When computation succeed result is returned`() {

        val res = Computation(
                genTransactionId(),
                data = Unit,
                state = FINISHED
        )

        whenever(cache.get(requestId)).thenReturn(res)

        val result = usecase.execute(PublishFileResultRequest(requestId))

        assertThat(result.component1()).isNotNull()
        assertThat(result.component1()).isSameAs(Unit)
        assertThat(result.component2()).isNull()
    }
}
