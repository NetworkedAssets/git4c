package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.delivery.executor.result.BackendRequest

data class VerifyDocumentationMacroByDocumentationsMacroIdQuery(
        val macroId: String
): BackendRequest<String>()