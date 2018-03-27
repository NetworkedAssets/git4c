package com.networkedassets.git4c.core.usecase.async

import com.networkedassets.git4c.delivery.executor.monitoring.TransactionInfo
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

abstract class BackendRequestForAsyncResult<out RESULT : Any>
(val requestId: String) : BackendRequest<RESULT>() {

    constructor(requestId: String, transactionInfo: TransactionInfo) : this(requestId) {
        this.transactionInfo = transactionInfo
    }

    override fun toString(): String {
        return this.javaClass.simpleName
    }
}