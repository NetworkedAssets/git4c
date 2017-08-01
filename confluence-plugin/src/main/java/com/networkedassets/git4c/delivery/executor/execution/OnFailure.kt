package com.networkedassets.git4c.delivery.executor.execution

interface OnFailure<ERROR> {
    @Throws(Throwable::class)
    fun onFailure(error: Throwable): ERROR
}
