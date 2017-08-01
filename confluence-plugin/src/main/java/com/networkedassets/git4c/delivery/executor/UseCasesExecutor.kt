package com.networkedassets.git4c.delivery.executor

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.delivery.executor.execution.BackendExecution
import com.networkedassets.git4c.delivery.executor.execution.UseCasesProvider
import com.networkedassets.git4c.delivery.executor.result.BackendRequest
import org.slf4j.LoggerFactory
import java.util.Objects.isNull

class UseCasesExecutor(private val provider: UseCasesProvider) : BackendExecution {

    private val log = LoggerFactory.getLogger(UseCasesExecutor::class.java)

    override fun <ANSWER : Any> executeRequest(request: BackendRequest<ANSWER>): Result<ANSWER, Exception> {
        val useCase = provider.getUseCaseForRequest(request)

        if (isNull(useCase)) {
            log.error("{} Couldn't find use case for request: {}", request.transactionInfo, request.javaClass)
            throw RuntimeException()
        }

        return useCase.execute(request)
    }

}
