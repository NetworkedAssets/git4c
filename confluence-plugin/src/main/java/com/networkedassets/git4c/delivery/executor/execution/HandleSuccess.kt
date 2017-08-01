package com.networkedassets.git4c.delivery.executor.execution

import com.networkedassets.git4c.delivery.executor.monitoring.TransactionInfo
import com.networkedassets.git4c.delivery.executor.result.BackendPresenter
import org.slf4j.LoggerFactory

class HandleSuccess<ANSWER, ERROR>(private val presenter: BackendPresenter<ANSWER, ERROR>, private val transactionInfo: TransactionInfo) : OnSuccess<ANSWER> {

    private val log = LoggerFactory.getLogger(HandleSuccess::class.java)

    override fun onSuccess(result: Any): ANSWER {
        log.trace("{} << Request - Will generate answer: {}", transactionInfo, result)
        return presenter.render(result)
    }
}