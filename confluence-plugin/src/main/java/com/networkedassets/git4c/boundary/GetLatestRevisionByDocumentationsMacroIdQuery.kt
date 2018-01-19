package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.outbound.Revision
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

data class GetLatestRevisionByDocumentationsMacroIdQuery(
        val macroId: String
) : BackendRequest<Revision>()