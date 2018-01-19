package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.GetTemporaryEditBranchResultCommand
import com.networkedassets.git4c.boundary.outbound.TemporaryBranch
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import com.networkedassets.git4c.boundary.outbound.exceptions.NotReadyException
import com.networkedassets.git4c.core.business.Computation
import com.networkedassets.git4c.core.datastore.cache.TemporaryEditBranchResultCache
import com.networkedassets.git4c.delivery.executor.execution.UseCase

class GetTemporaryEditBranchResultUseCase(
        val temporaryEditBranchResultCache: TemporaryEditBranchResultCache
):UseCase<GetTemporaryEditBranchResultCommand, TemporaryBranch> {

    override fun execute(request: GetTemporaryEditBranchResultCommand): Result<TemporaryBranch, Exception> {

        val cache = temporaryEditBranchResultCache

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