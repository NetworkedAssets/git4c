package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.outbound.GlobsForMacro
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

class GetGlobsByDocumentationsMacroIdQuery(
        val macroId: String,
        var user: String?
) : BackendRequest<GlobsForMacro>()