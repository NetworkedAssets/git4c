package com.networkedassets.git4c.delivery.executor.execution

import com.networkedassets.git4c.delivery.executor.monitoring.TransactionInfo
import com.networkedassets.git4c.delivery.executor.result.BackendPresenter
import org.slf4j.LoggerFactory

class HandleFailure<ANSWER, ERROR>(private val presenter: BackendPresenter<ANSWER, ERROR>, private val transactionInfo: TransactionInfo) : OnFailure<ERROR> {

    private val log = LoggerFactory.getLogger(HandleFailure::class.java)

    override fun onFailure(error: Throwable): ERROR {
        log.info("{} << Request - error received - {}", transactionInfo, error.javaClass.simpleName)
        return presenter.error(error)
    }
}