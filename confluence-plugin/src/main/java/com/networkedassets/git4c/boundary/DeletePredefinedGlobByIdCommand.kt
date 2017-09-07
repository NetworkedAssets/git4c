package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.delivery.executor.result.BackendRequest

class DeletePredefinedGlobByIdCommand(
        val uuid: String
) : BackendRequest<String>()