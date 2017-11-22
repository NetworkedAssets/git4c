package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.outbound.SimpleExtractorData
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

class GetExtractionDataByDocumentationsMacroIdQuery(
        val macroId: String, val user: String?
) : BackendRequest<SimpleExtractorData>()