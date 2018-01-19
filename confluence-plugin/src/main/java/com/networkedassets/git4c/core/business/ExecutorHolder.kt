package com.networkedassets.git4c.core.business

import java.util.concurrent.ScheduledExecutorService

interface ExecutorHolder<out T> where T : ScheduledExecutorService {
    fun getExecutor(): ScheduledExecutorService
    fun getThreadNumber(): Int
    fun reset(threadNumber: Int)
}

abstract class BaseExecutorHolder<out T : ScheduledExecutorService>(
        baseThreadNumber: Int,
        private val executorFun: (Int) -> ScheduledExecutorService
) : ExecutorHolder<T> {

    @Volatile
    var threadNumber_ = baseThreadNumber
    @Volatile
    protected var executor_ = executorFun(baseThreadNumber)
    val lock = java.lang.Object()

    override fun getExecutor() = executor_

    override fun getThreadNumber() = threadNumber_

    override fun reset(threadNumber: Int) {
        synchronized(lock) {
            threadNumber_ = threadNumber
            executor_.shutdown()
            executor_ = executorFun(threadNumber)
        }
    }
}

interface RevisionCheckExecutorHolder : ExecutorHolder<ScheduledExecutorService>
interface RepositoryPullExecutorHolder : ExecutorHolder<ScheduledExecutorService>
interface ConverterExecutorHolder : ExecutorHolder<ScheduledExecutorService>
interface ConfluenceQueryExecutorHolder: ExecutorHolder<ScheduledExecutorService>
interface EditedFilesExecutorHolder : ExecutorHolder<ScheduledExecutorService>

class EditedFilesBaseExecutorHolder(baseThreadNumber: Int, executorFun: (Int) -> ScheduledExecutorService) : BaseExecutorHolder<ScheduledExecutorService>(baseThreadNumber, executorFun), EditedFilesExecutorHolder
class RevisionCheckBaseExecutorHolder(baseThreadNumber: Int, executorFun: (Int) -> ScheduledExecutorService) : BaseExecutorHolder<ScheduledExecutorService>(baseThreadNumber, executorFun), RevisionCheckExecutorHolder
class RepositoryPullBaseExecutorHolder(baseThreadNumber: Int, executorFun: (Int) -> ScheduledExecutorService) : BaseExecutorHolder<ScheduledExecutorService>(baseThreadNumber, executorFun), RepositoryPullExecutorHolder
class ConverterBaseExecutorHolder(baseThreadNumber: Int, executorFun: (Int) -> ScheduledExecutorService) : BaseExecutorHolder<ScheduledExecutorService>(baseThreadNumber, executorFun), ConverterExecutorHolder
class ConfluenceQueryBaseExecutorHolder(baseThreadNumber: Int, executorFun: (Int) -> ScheduledExecutorService): BaseExecutorHolder<ScheduledExecutorService>(baseThreadNumber, executorFun), ConfluenceQueryExecutorHolder
