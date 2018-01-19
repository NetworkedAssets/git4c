package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.PublishFileResultRequest
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.boundary.outbound.exceptions.NotReadyException
import com.networkedassets.git4c.core.business.Computation
import com.networkedassets.git4c.core.datastore.cache.PublishFileComputationCache
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class PublishFileResultUseCase(
        private val transactionCache: PublishFileComputationCache
): UseCase<PublishFileResultRequest, Unit> {

    override fun execute(request: PublishFileResultRequest): Result<Unit, Exception> {

        //TODO: Make transaction per user???
        val computation = transactionCache.get(request.requestId) ?: return Result.error(NotFoundException(request.transactionInfo, "Cannot find transaction"))

        if (computation.state == Computation.ComputationState.RUNNING) {
            return Result.error(NotReadyException())
        }

        if (computation.state == Computation.ComputationState.FAILED) {
            return Result.error(computation.error!!)
        }

        return Result.of { computation.data!! }

    }
}