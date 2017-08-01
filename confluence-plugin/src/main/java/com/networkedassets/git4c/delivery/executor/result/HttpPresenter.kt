package com.networkedassets.git4c.delivery.executor.result

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.utils.SerializationUtils.serialize
import com.networkedassets.git4c.delivery.executor.monitoring.TransactionInfo
import com.networkedassets.git4c.delivery.executor.monitoring.BackendTimer
import com.networkedassets.git4c.boundary.outbound.exceptions.ConflictException
import com.networkedassets.git4c.boundary.outbound.exceptions.NotFoundException
import java.util.*
import java.util.Objects.isNull
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.*

class HttpPresenter() : BackendPresenter<Response, Response> {

    private var stopwatch: Optional<BackendTimer.Stopwatch> = Optional.empty<BackendTimer.Stopwatch>()

    override fun render(result: Any): Response {
        if (result is Result<*, Exception>) {
            return render(result)
        } else {
            return error(IllegalArgumentException())
        }
    }

    override fun error(exception: Throwable): Response {
        val response = getErrorFromException(exception)
        stopwatch.ifPresent { e -> e.stopAndLog(exception, response.status) }
        return response
    }

    private fun getErrorFromException(exception: Throwable) = when (exception) {
        is IllegalArgumentException -> status(400)
        is NotFoundException -> status(404)
        is ConflictException -> status(409)
        else -> status(500)
    }.entity(exception.message).build()

    protected fun render(result: Result<*, Exception>): Response {
        if (isNull(result.component2())) {
            val response = contentToResponse(result.component1())
            stopwatch.ifPresent { e -> e.stopAndLog(response.status) }
            return response
        }
        return error(result.component2()!!)
    }

    private fun contentToResponse(content: Any?): Response =
            if (content == null) status(Status.NOT_FOUND).build()
            else ok().entity(serialize(content)).build()

    fun startStopwatch(timer: BackendTimer, transactionInfo: TransactionInfo): BackendPresenter<Response, Response> {
        this.stopwatch = Optional.of(timer.start(transactionInfo))
        return this
    }
}
