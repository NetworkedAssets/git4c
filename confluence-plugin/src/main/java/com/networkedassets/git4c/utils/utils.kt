package com.networkedassets.git4c.utils

import com.github.kittinunf.result.Result
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.flatMap
import com.networkedassets.git4c.ConfluencePlugin
import com.networkedassets.git4c.delivery.executor.execution.BackendDispatcher
import com.networkedassets.git4c.delivery.executor.execution.HandleFailure
import com.networkedassets.git4c.delivery.executor.monitoring.BackendTimer
import com.networkedassets.git4c.delivery.executor.monitoring.TransactionInfo
import com.networkedassets.git4c.delivery.executor.result.BackendRequest
import com.networkedassets.git4c.delivery.executor.result.BlockingPresenter
import com.networkedassets.git4c.delivery.executor.result.HttpPresenter
import com.networkedassets.git4c.delivery.executor.result.ServiceApi
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import uy.klutter.core.common.mustStartWith
import java.time.Duration
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.jar.Manifest
import javax.ws.rs.core.Response


fun <T> CompletableFuture<T>.onException(handler: (Throwable) -> Unit) = whenCompleteAsync { _, throwable ->
    throwable?.let(handler)
}!!

@Suppress("UNCHECKED_CAST", "NAME_SHADOWING")
fun <T> List<CompletableFuture<T>>.toFutureOfList(): CompletableFuture<List<T>> =
        this.foldRight(java.util.concurrent.CompletableFuture.completedFuture(mutableListOf<T>())) { x, acc ->
            acc.thenCombineAsync(x) { acc, x -> acc.add(x); acc }
        } as CompletableFuture<List<T>>

fun <T, R> List<CompletableFuture<T>>.afterAllAsync(action: (List<T>) -> R): CompletableFuture<R> =
        this.toFutureOfList().thenApplyAsync(action)

/**
 * Like [Result.map], but the [action] may throw and the exception will be returned properly
 * i.e. if `this` was [Result.Success] and [action] threw, [Result.Failure] will be returned with the exception thrown
 * if `this` was [Result.Failure] `this` will be returned
 */
fun <T : Any, U : Any> Result<T, Exception>.andThenTry(action: (T) -> U) = this.flatMap { x ->
    Result.of<U, Exception> { action(x) }
}

private val log = LoggerFactory.getLogger(ConfluencePlugin::class.java)

fun <R : Any> retry(maxRetries: Int = 10, retryDelayMillis: Long = 100, action: () -> R): Result<R, Exception> {
    var res = Result.of<R, Exception>(action)
    var retryNum = 0
    while (retryNum < maxRetries && res is Result.Failure) {
        Thread.sleep(retryDelayMillis)
        res = Result.of(action)
        retryNum++
    }
    res.failure {
        log.error(it) { "Failure after $retryNum retries." }
    }
    return res
}

fun <R : Any> retryAsync(maxRetries: Int = 10, retryDelayMillis: Long = 100, action: () -> R) {
    CompletableFuture.runAsync { retry(maxRetries, retryDelayMillis, action) }
}

val Long.seconds: Duration get() = Duration.ofSeconds(this)
val Int.seconds: Duration get() = Duration.ofSeconds(this.toLong())
val Long.minutes: Duration get() = Duration.ofMinutes(this)
val Int.minutes: Duration get() = Duration.ofMinutes(this.toLong())
val Long.hours: Duration get() = Duration.ofHours(this)
val Int.hours: Duration get() = Duration.ofHours(this.toLong())

/**
 * Hacky little thing that returns true if the application is running from within IntelliJ IDEA
 */
object Intelij {
    val isRunningFromIntelij by lazy { isRunningFromIntelliJ() }

    fun isRunningFromIntelliJ(): Boolean {
        val classPath = System.getProperty("java.class.path")
        return classPath.contains("IntelliJ IDEA") || classPath.contains("idea_rt")
    }
}

fun genTransactionId() = UUID.randomUUID().toString()

@Suppress("UNCHECKED_CAST")
@Throws(Exception::class)
fun <ANSWER : Any> sendToExecution(dispatcher: BackendDispatcher<Any, Throwable>, query: BackendRequest<ANSWER>): ANSWER {
    val timer = BackendTimer()
    val presenter = BlockingPresenter().startStopwatch(timer, query.transactionInfo)
    val answer = dispatcher.sendToExecution(query, presenter).get()
    if (answer !is Throwable) {
        return answer as ANSWER;
    } else {
        throw answer
    }
}

/*
Will return ANSWER or Exception in response
It has a bug that on exception may not always be proper prepared and always will be return in answer
 */
fun <ANSWER : Any> sendToExecutionAsync(dispatcher: BackendDispatcher<Any, Throwable>, query: BackendRequest<ANSWER>): CompletableFuture<*> {
    val timer = BackendTimer()
    val presenter = BlockingPresenter().startStopwatch(timer, query.transactionInfo)
    return dispatcher.sendToExecution(query, presenter)
}

data class DispatchAndPresentHttpRunContext(var transactionInfo: TransactionInfo, var timer: BackendTimer)

inline fun <reified T : BackendRequest<Any>> ServiceApi.dispatchAndPresentHttp(
        requestProducer: DispatchAndPresentHttpRunContext.() -> T
) = dispatchAndPresentHttp(requestProducer, 45, TimeUnit.SECONDS)


inline fun <reified T : BackendRequest<Any>> ServiceApi.dispatchAndPresentHttp(
        requestProducer: DispatchAndPresentHttpRunContext.() -> T,
        timeout: Long, unit: TimeUnit
): Response {
    val transactionInfo = TransactionInfo(T::class.java)
    val timer = BackendTimer()

    val rContext = DispatchAndPresentHttpRunContext(transactionInfo, timer)
    val command = rContext.requestProducer()

    val presenter = HttpPresenter().startStopwatch(rContext.timer, rContext.transactionInfo)

    try {
        return dispatcher.sendToExecution(command, presenter).get(timeout, unit) as Response
    } catch (e: TimeoutException) {
        LoggerFactory.getLogger(ServiceApi::class.java).trace("{} >> Request timeout", command.transactionInfo)
        return HandleFailure(presenter, command.transactionInfo).onFailure(e)
//        throw e
    }
}


/** Utility function to properly glue url segments:
 *  ```
 *  url("http://localhost:80/", "/foo", "bar") => "http://localhost:80/foo/bar"
 *  url("http://localhost:80", "foo", "bar") => "http://localhost:80/foo/bar"
 *  url("http://localhost:80/", "/foo"/, "/bar") => "http://localhost:80/foo/bar"
 *  ```
 */
fun url(first: String, vararg rest: String): String {
    var base = first
    for (segment in rest) {
        base = base.removeSuffix("/")
        base += segment.mustStartWith('/')
    }
    return base
}

private object versionGetter

fun getThisJarVersion(): String? = versionGetter.javaClass.classLoader.getResources("META-INF/MANIFEST.MF").asSequence().singleOrNull()?.let {
    val manifest = Manifest(it.openStream())
    log.debug { "Manifest: ${manifest.mainAttributes}" }
    manifest.mainAttributes.getValue("Implementation-Version")?.toString()
}

fun Logger.debug(log: () -> String) {
    if (Intelij.isRunningFromIntelij) println("DEBUG - " + Thread.currentThread().name + " - " + log.invoke())
    if (this.isDebugEnabled) debug(log.invoke())
}

fun Logger.info(log: () -> String) {
    if (Intelij.isRunningFromIntelij) println("INFO  - " + Thread.currentThread().name + " - " + log.invoke())
    if (this.isInfoEnabled) info(log.invoke())
}

fun Logger.warn(log: () -> String) {
    if (Intelij.isRunningFromIntelij) println("WARN  - " + Thread.currentThread().name + " - " + log.invoke())
    if (this.isWarnEnabled) warn(log.invoke())
}

fun Logger.error(log: () -> String) {
    if (Intelij.isRunningFromIntelij) println("ERROR - " + Thread.currentThread().name + " - " + log.invoke())
    if (this.isErrorEnabled) error(log.invoke())
}

fun Logger.error(log: () -> String, exception: Throwable) {
    if (Intelij.isRunningFromIntelij) println("ERROR - " + Thread.currentThread().name + " - " + log.invoke() + "${exception.javaClass.simpleName}: ${exception.message} at ${exception.stackTrace[0].className}:${exception.stackTrace[0].lineNumber}")
    if (this.isErrorEnabled) error(log.invoke() + "${exception.javaClass.simpleName}: ${exception.message} at ${exception.stackTrace[0].className}:${exception.stackTrace[0].lineNumber}")
}

fun Logger.error(exception: Throwable, log: () -> String) {
    if (Intelij.isRunningFromIntelij) println("ERROR - " + Thread.currentThread().name + " - " + log.invoke() + "${exception.javaClass.simpleName}: ${exception.message} at ${exception.stackTrace[0].className}:${exception.stackTrace[0].lineNumber}")
    if (this.isErrorEnabled) error(log.invoke() + "${exception.javaClass.simpleName}: ${exception.message} at ${exception.stackTrace[0].className}:${exception.stackTrace[0].lineNumber}")
}

inline infix fun <reified E> List<E>.contentEquals(map: List<E>): Boolean {
    return Arrays.equals(this.toTypedArray(), map.toTypedArray())
}


@Suppress("unused")
inline fun <reified T> T.getLogger(): Logger = LoggerFactory.getLogger(T::class.java)
