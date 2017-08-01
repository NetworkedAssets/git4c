package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.outbound.Id
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

class CreateTemporaryDocumentationsContentCommand(
        val macroId: String,
        val branch: String
) : BackendRequest<Id>()
