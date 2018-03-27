package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.outbound.RepositoryInfo
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

class GetRepositoryInfoForMacroCommand(
        val macroUuid: String,
        val user: String?
): BackendRequest<RepositoryInfo>()