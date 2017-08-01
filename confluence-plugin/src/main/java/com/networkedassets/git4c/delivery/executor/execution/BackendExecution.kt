package com.networkedassets.git4c.delivery.executor.execution


import com.github.kittinunf.result.Result
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

interface BackendExecution {

    fun <ANSWER : Any> executeRequest(request: BackendRequest<ANSWER>): Result<ANSWER, Exception>

}
