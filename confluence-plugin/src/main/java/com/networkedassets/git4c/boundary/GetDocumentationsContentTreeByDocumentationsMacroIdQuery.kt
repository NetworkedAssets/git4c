package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.outbound.DocumentationsContentTree
import com.networkedassets.git4c.delivery.executor.result.BackendRequest


class GetDocumentationsContentTreeByDocumentationsMacroIdQuery(
        val macroId: String
) : BackendRequest<DocumentationsContentTree>()