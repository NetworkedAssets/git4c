package com.networkedassets.git4c.core.usecase.async

import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.outbound.RequestId
import com.networkedassets.git4c.core.business.Computation
import com.networkedassets.git4c.core.bussiness.ComputationCache
import com.networkedassets.git4c.delivery.executor.execution.UseCase

abstract class AsyncUseCase<in COMMAND : AsyncBackendRequest, ANSWER : Any>(
        components: BussinesPluginComponents,
        private val computations: ComputationCache<ANSWER>
) : UseCase<COMMAND, RequestId>
(components) {

    abstract fun executedAsync(requestId: String, request: COMMAND)

    protected fun start(operationId: String) {
        computations.put(operationId, Computation(operationId))
    }

    fun temporal(operationId: String, data: ANSWER) {
        computations.put(operationId, Computation(operationId, Computation.ComputationState.RUNNING, data))
    }

    fun data(computationId: String): Computation<ANSWER>? {
        return computations.get(computationId)
    }

    fun success(operationId: String, data: ANSWER) {
        computations.put(operationId, Computation(operationId, Computation.ComputationState.FINISHED, data))
    }

    fun error(operationId: String, error: Exception) {
        computations.put(operationId, Computation(operationId, Computation.ComputationState.FAILED, error = error))
    }
}