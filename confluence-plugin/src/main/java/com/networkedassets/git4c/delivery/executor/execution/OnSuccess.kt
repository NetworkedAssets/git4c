package com.networkedassets.git4c.delivery.executor.execution

interface OnSuccess<ANSWER> {
    @Throws(Throwable::class)
    fun onSuccess(result: Any): ANSWER
}
