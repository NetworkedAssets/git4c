package com.networkedassets.git4c

import com.github.kittinunf.result.Result
import com.vodafone.backend.commons.core.execution.UseCase
import com.vodafone.backend.commons.core.execution.UseCasesProvider
import com.vodafone.backend.commons.delivery.BackendRequest
import com.vodafone.backend.commons.delivery.LocalGateway
import com.vodafone.backend.commons.util.sendToExecution
import com.vodafone.backend.template.delivery.execution.UseCasesExecutor
import org.junit.Test


class LocalGatewayTest {

    @Test
    fun check() {

        val useCase = object : UseCase<BackendRequest<String>, String> {
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

