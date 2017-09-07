package com.networkedassets.git4c.delivery.executor

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.delivery.executor.execution.BackendDispatcher
import com.networkedassets.git4c.delivery.executor.execution.BackendExecution
import com.networkedassets.git4c.delivery.executor.execution.HandleFailure
import com.networkedassets.git4c.delivery.executor.execution.HandleSuccess
import com.networkedassets.git4c.delivery.executor.result.BackendPresenter
import com.networkedassets.git4c.delivery.executor.result.BackendRequest
import com.networkedassets.git4c.utils.Autocloser
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.function.Supplier

@Suppress("UNCHECKED_CAST")
class LocalGateway<GENERIC_ANSWER, ERROR>(private val executor: BackendExecution) : BackendDispatcher<GENERIC_ANSWER, ERROR> {

    private val log = LoggerFactory.getLogger(LocalGateway::class.java)
    val DEFAULT_EXECUTOR_SERVICE: ExecutorService = Executors.newCachedThreadPool()

    init {
        Autocloser.atShutdownDo { DEFAULT_EXECUTOR_SERVICE.shutdown() }
    }

    override fun <ANSWER : Any> sendToExecution(request: BackendRequest<ANSWER>, presenter: BackendPresenter<GENERIC_ANSWER, ERROR>): CompletableFuture<Any> {
        log.trace("{} >> Request", request.transactionInfo, request.javaClass.simpleName)


        return CompletableFuture.supplyAsync(Supplier<Result<ANSWER, Exception>> { executor.executeRequest(request) }, DEFAULT_EXECUTOR_SERVICE)
                .handle { result, throwable ->
                    if (throwable == null) {
                        return@handle HandleSuccess(presenter, request.transactionInfo).onSuccess(result) as ANSWER
                    } else {
                        return@handle HandleFailure(presenter, request.transactionInfo).onFailure(throwable)
                    }
                }
    }

}