package com.networkedassets.git4c.delivery.executor.execution


import com.networkedassets.git4c.delivery.executor.result.BackendRequest

interface UseCasesProvider {

    fun <T : Any, R : BackendRequest<T>> getUseCaseForRequest(request: R): UseCase<R, T>

}
