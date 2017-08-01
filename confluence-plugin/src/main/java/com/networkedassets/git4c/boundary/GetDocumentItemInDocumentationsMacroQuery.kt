package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.outbound.DocItem
import com.networkedassets.git4c.delivery.executor.result.BackendRequest


class GetDocumentItemInDocumentationsMacroQuery(
        val macroId: String,
        val documentId: String
) : BackendRequest<DocItem>()