package com.networkedassets.git4c.core.usecase.async


import com.github.kittinunf.result.Result
import com.networkedassets.git4c.application.BussinesPluginComponents
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.boundary.outbound.exceptions.NotReadyException
import com.networkedassets.git4c.core.business.Computation
import com.networkedassets.git4c.core.bussiness.ComputationCache
import com.networkedassets.git4c.delivery.executor.execution.UseCase

abstract class ComputationResultUseCase<in REQUEST : BackendRequestForAsyncResult<RESULT>, out RESULT : Any>(
        components: BussinesPluginComponents,
        val computations: ComputationCache<out RESULT>
) : UseCase<REQUEST, RESULT>
(components) {

    override fun execute(request: REQUEST): Result<RESULT, Exception> {

        val computation = computations.get(request.requestId)
                ?: return Result.error(NotFoundException(request.transactionInfo, "Cannot find Backend Trancaction for RequestId=${request.requestId}"))

        if (computation.state == Computation.ComputationState.RUNNING) {
            return Result.error(NotReadyException())
        }

        if (computation.state == Computation.ComputationState.FAILED) {
            return Result.error(computation.error!!)
        }

        return Result.of { computation.data!! }
    }
}