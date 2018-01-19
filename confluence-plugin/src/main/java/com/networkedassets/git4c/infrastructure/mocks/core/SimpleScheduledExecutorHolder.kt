package com.networkedassets.git4c.infrastructure.mocks.core

import com.networkedassets.git4c.core.business.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

class SimpleScheduledExecutorHolder : RevisionCheckExecutorHolder, BaseExecutorHolder<ScheduledExecutorService>(2, { Executors.newScheduledThreadPool(it) }), ConverterExecutorHolder, RepositoryPullExecutorHolder, ConfluenceQueryExecutorHolder
