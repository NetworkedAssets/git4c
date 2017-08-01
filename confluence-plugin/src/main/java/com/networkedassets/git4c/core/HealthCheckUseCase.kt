package com.networkedassets.git4c.core

import com.github.kittinunf.result.Result
import com.networkedassets.git4c.boundary.HealthCheckCommand
import com.networkedassets.git4c.delivery.executor.execution.UseCase


class HealthCheckUseCase(
) : UseCase<HealthCheckCommand, String> {

    override fun execute(request: HealthCheckCommand): Result<String, Exception> {
        return Result.of { "" }
    }
}