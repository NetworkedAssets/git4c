package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.inbound.DetailsToGetFile
import com.networkedassets.git4c.core.usecase.async.AsyncBackendRequest

class GetCommitHistoryForFileByMacroIdQuery(
        val macroId: String,
        val details: DetailsToGetFile, val user: String?
) : AsyncBackendRequest()