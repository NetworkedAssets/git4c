package com.networkedassets.git4c.delivery.executor.execution


import com.github.kittinunf.result.Result
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

interface UseCase<in REQUEST : BackendRequest<RESULT>, out RESULT : Any> {

    fun execute(request: REQUEST): Result<RESULT, Exception>

}
