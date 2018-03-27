package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.core.usecase.async.AsyncBackendRequest

data class VerifyDocumentationMacroByDocumentationsMacroIdQuery(
        val macroId: String
) : AsyncBackendRequest()