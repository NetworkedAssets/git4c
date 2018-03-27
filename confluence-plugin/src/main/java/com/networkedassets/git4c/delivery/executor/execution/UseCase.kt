package com.networkedassets.git4c.delivery.executor.execution


import com.github.kittinunf.result.Result
import com.networkedassets.git4c.application.*
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

abstract class UseCase<in REQUEST : BackendRequest<RESULT>, out RESULT : Any>(
        components: BussinesPluginComponents,
        val database: DatabasePluginComponents = components.database,
        val cache: CachePluginComponents = components.cache,
        val async: ResultsCachePluginComponents = components.async,
        val macro: MacroPluginComponents = components.macro,
        val utils: UtilitiesPluginComponents = components.utils,
        val executors: ExecutorsPluginComponents = components.executors,
        val providers: ProvidersPluginComponents = components.providers,
        val processing: ProcessesPluginComponents = components.processing
) {

    abstract fun execute(request: REQUEST): Result<RESULT, Exception>

}
