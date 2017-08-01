package com.networkedassets.git4c.delivery.executor.execution

import com.networkedassets.git4c.delivery.executor.result.BackendPresenter
import com.networkedassets.git4c.delivery.executor.result.BackendRequest
import java.util.concurrent.CompletableFuture

interface BackendDispatcher<REENDER_ANSWER, REENDER_ERROR> {
    fun <ANSWER : Any> sendToExecution(request: BackendRequest<ANSWER>, presenter: BackendPresenter<REENDER_ANSWER, REENDER_ERROR>): CompletableFuture<Any>
}