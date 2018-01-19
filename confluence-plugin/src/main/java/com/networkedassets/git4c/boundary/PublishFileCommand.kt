package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.inbound.FileToSave
import com.networkedassets.git4c.boundary.outbound.RequestId
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

class PublishFileCommand(
        val user: String?,
        val macroId: String,
        val fileToSave: FileToSave
): BackendRequest<RequestId>()