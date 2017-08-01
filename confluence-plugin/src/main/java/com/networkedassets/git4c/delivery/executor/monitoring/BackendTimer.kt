package com.networkedassets.git4c.delivery.executor.monitoring

import com.networkedassets.git4c.utils.info
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

class BackendTimer {
    val startTime: Long = System.currentTimeMillis()

    fun start(transactionInfo: TransactionInfo): Stopwatch {
        return Stopwatch(this, transactionInfo)
    }

    class Stopwatch(val timer: BackendTimer, val transactionInfo: TransactionInfo) {
        private val log = LoggerFactory.getLogger(Stopwatch::class.java)

        fun stopAndLog(response: Int) {
            val duration = System.currentTimeMillis() - timer.startTime
            if (response != 0) {
                log.info { "$transactionInfo ExecutionTime=${TimeUnit.NANOSECONDS.toMillis(duration)} ms, HttpResponse=$response " }
            } else {
                log.info { "$transactionInfo ExecutionTime=${TimeUnit.NANOSECONDS.toMillis(duration)} ms" }
            }
        }

        fun stopAndLog(exception: Throwable, response: Int) {
            stopAndLog(response)
            if (exception is IllegalArgumentException) {
                log.info { "$transactionInfo During processing: ${getErrorMessage(exception)}" }
            } else {
                log.error("$transactionInfo Error during processing: ${getErrorMessage(exception)}", exception)
            }
        }

        private fun getErrorMessage(exception: Throwable): String? {
            return if (exception.message?.isNotBlank() ?: false) exception.message else ""
        }
    }
}