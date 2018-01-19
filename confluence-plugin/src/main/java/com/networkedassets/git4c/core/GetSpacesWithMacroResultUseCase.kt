package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetSpacesWithMacroResultRequest
import com.networkedassets.git4c.boundary.outbound.Spaces
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.boundary.outbound.exceptions.NotReadyException
import com.networkedassets.git4c.core.business.Computation
import com.networkedassets.git4c.core.datastore.cache.SpacesWithMacroResultCache
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class GetSpacesWithMacroResultUseCase(
        val cache: SpacesWithMacroResultCache
): UseCase<GetSpacesWithMacroResultRequest, Spaces> {

    override fun execute(request: GetSpacesWithMacroResultRequest): Result<Spaces, Exception> {

        val computation = cache.get(request.requestId) ?: return Result.error(NotFoundException(request.transactionInfo, "Cannot find transaction"))

        if (computation.state == Computation.ComputationState.RUNNING) {
            return Result.error(NotReadyException())
        }

        if (computation.state == Computation.ComputationState.FAILED) {
            return Result.error(computation.error!!)
        }

        return Result.of { computation.data!! }

    }

}