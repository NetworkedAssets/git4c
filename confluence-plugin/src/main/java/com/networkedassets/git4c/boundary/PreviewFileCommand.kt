package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.inbound.FileToGeneratePreview
import com.networkedassets.git4c.boundary.outbound.FileContent
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

class PreviewFileCommand(
        val user: String?,
        val macroId: String,
        val fileToGeneratePreview: FileToGeneratePreview
): BackendRequest<FileContent>()