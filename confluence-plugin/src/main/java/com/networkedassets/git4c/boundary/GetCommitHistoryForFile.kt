package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.inbound.DetailsToGetFile
import com.networkedassets.git4c.boundary.outbound.Commits
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

class GetCommitHistoryForFileByMacroIdQuery(
        val macroId: String,
        val details: DetailsToGetFile, val user: String?
): BackendRequest<Commits>()