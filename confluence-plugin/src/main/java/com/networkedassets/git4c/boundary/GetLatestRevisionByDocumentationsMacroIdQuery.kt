package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.core.usecase.async.AsyncBackendRequest

data class GetLatestRevisionByDocumentationsMacroIdQuery(
        val macroId: String,
        val user : String?
) : AsyncBackendRequest()