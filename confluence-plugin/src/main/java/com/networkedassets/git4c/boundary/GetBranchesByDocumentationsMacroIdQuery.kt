package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.outbound.Branches
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

class GetBranchesByDocumentationsMacroIdQuery(
        val macroId: String,
        val user: String?
) : BackendRequest<Branches>()