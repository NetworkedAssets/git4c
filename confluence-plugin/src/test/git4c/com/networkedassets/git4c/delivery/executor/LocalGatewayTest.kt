package com.networkedassets.git4c.delivery.executor

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.delivery.executor.execution.UseCase
import com.networkedassets.git4c.delivery.executor.execution.UseCasesProvider
import com.networkedassets.git4c.delivery.executor.result.BackendRequest
import com.networkedassets.git4c.utils.InMemoryApplication.application
import com.networkedassets.git4c.utils.sendToExecution
import org.junit.Test


class LocalGatewayTest {

    @Test
    fun check() {

        val useCase = object : UseCase<BackendRequest<String>, String>(application.bussines) {
            override fun execute(request: BackendRequest<String>): Result<String, Exception> {
                return Result.of { "" }
            }

        }

        val list = ArrayList<UseCase<*, *>>()
        list.add(useCase)
        val provider = object : UseCasesProvider {
            override fun <T : Any, R : BackendRequest<T>> getUseCaseForRequest(request: R): UseCase<R, T> {
                return list[0] as UseCase<R, T>
            }
        }
        val executor = UseCasesExecutor(provider)
        val gateway = LocalGateway<Any, Throwable>(executor)

        val request = object : BackendRequest<String>() {}
        val answer = sendToExecution(gateway, request)
        assert(answer.equals(""))
    }

}

