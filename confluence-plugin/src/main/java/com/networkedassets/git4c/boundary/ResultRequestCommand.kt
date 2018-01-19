package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.delivery.executor.result.BackendRequest

abstract class ResultRequestCommand<out T> (
        val requestId: String
): BackendRequest<T>()