package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.inbound.FileToSave
import com.networkedassets.git4c.core.usecase.async.AsyncBackendRequest

class PublishFileCommand(
        val user: String?,
        val macroId: String,
        val fileToSave: FileToSave
) : AsyncBackendRequest()