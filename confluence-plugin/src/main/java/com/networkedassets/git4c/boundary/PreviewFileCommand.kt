package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.inbound.FileToGeneratePreview
import com.networkedassets.git4c.core.usecase.async.AsyncBackendRequest

class PreviewFileCommand(
        val user: String?,
        val macroId: String,
        val fileToGeneratePreview: FileToGeneratePreview
) : AsyncBackendRequest()