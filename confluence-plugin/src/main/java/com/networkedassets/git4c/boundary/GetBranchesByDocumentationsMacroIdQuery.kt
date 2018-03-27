package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.core.usecase.async.AsyncBackendRequest

class GetBranchesByDocumentationsMacroIdQuery(
        val macroId: String,
        val user: String?
) : AsyncBackendRequest()