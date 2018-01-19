package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.outbound.TemporaryBranch
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

class GetTemporaryEditBranchResultCommand(
        val requestId: String
): BackendRequest<TemporaryBranch>()