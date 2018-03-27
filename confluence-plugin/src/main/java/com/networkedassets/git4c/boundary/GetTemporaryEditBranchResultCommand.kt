package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.outbound.TemporaryBranch
import com.networkedassets.git4c.core.usecase.async.BackendRequestForAsyncResult

class GetTemporaryEditBranchResultCommand(
        requestId: String
) : BackendRequestForAsyncResult<TemporaryBranch>(requestId)