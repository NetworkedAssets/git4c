package com.networkedassets.git4c.core.usecase.async

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.outbound.RequestId
import com.networkedassets.git4c.core.bussiness.ComputationCache
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

abstract class MultiThreadAsyncUseCase<in COMMAND : AsyncBackendRequest, ANSWER : Any>(
        components: BussinesPluginComponents,
        computations: ComputationCache<ANSWER>,
        concurrency: Int

) : AsyncUseCase<COMMAND, ANSWER>
(components, computations) {

    val executor = Executors.newScheduledThreadPool(concurrency)

    override fun execute(request: COMMAND): Result<RequestId, Exception> {
        val id = UUID.randomUUID().toString()
        start(id)
        // For testing purposes you may manipulate the time of schedule. The default value should be set to the 10 MILISECONDS. The best testing result is set it to 10 SECONDS so that short delay may be observed for testing 202 responses.
        executor.schedule(
                { execution(id, request) },
                5, TimeUnit.MILLISECONDS
        )
        return Result.of { RequestId(id) }
    }

    private fun execution(id: String, request: COMMAND) {
        try {
            executedAsync(id, request)
        } catch (e: Exception) {
            error(id, e)
        }
    }
}