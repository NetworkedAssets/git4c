package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.outbound.VerificationInfo
import com.networkedassets.git4c.core.usecase.async.BackendRequestForAsyncResult


class VerifyRepositoryResultRequest(requestId: String) : BackendRequestForAsyncResult<VerificationInfo>(requestId)