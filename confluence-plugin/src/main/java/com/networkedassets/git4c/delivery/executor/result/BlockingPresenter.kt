package com.networkedassets.git4c.delivery.executor.result

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.delivery.executor.monitoring.TransactionInfo
import com.networkedassets.git4c.delivery.executor.monitoring.BackendTimer
import java.util.*
import java.util.Objects.isNull

class BlockingPresenter() : BackendPresenter<Any, Throwable> {

    private var stopwatch: Optional<BackendTimer.Stopwatch> = Optional.empty<BackendTimer.Stopwatch>()

    override fun render(result: Any): Any {
        if (result is Result<*, Exception>) {
            return render(result)
        } else {
            return error(IllegalArgumentException())
        }
    }

    override fun error(exception: Throwable): Throwable {
        stopwatch.ifPresent { e -> e.stopAndLog(exception, 0) }
        return exception
    }


    protected fun render(result: Result<*, Exception>): Any {
        if (isNull(result.component2())) {
            stopwatch.ifPresent { e -> e.stopAndLog(0) }
            return result.component1()!!
        }
        return error(result.component2()!!)
    }


    fun startStopwatch(timer: BackendTimer, transactionInfo: TransactionInfo): BackendPresenter<Any, Throwable> {
        this.stopwatch = Optional.of(timer.start(transactionInfo))
        return this
    }
}
