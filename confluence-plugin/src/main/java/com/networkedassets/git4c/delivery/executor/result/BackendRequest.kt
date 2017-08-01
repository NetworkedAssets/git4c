package com.networkedassets.git4c.delivery.executor.result

import com.networkedassets.git4c.delivery.executor.monitoring.TransactionInfo
import java.io.Serializable

abstract class BackendRequest<out ANSWER>() : Serializable {

    var transactionInfo: TransactionInfo = TransactionInfo(this.javaClass)
        private set

    constructor(transactionInfo: TransactionInfo) : this() {
        this.transactionInfo = transactionInfo
    }

    protected val created = System.currentTimeMillis()

    override fun toString(): String {
        return this.javaClass.simpleName
    }

}
