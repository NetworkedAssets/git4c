package com.networkedassets.git4c.core.usecase.async

import com.networkedassets.git4c.boundary.outbound.RequestId
import com.networkedassets.git4c.delivery.executor.monitoring.TransactionInfo
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

abstract class AsyncBackendRequest() : BackendRequest<RequestId>() {

    constructor(transactionInfo: TransactionInfo) : this() {
        this.transactionInfo = transactionInfo
    }

    override fun toString(): String {
        return this.javaClass.simpleName
    }
}