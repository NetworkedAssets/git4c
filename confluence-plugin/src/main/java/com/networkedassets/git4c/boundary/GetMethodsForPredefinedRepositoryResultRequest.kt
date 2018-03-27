package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.outbound.Methods
import com.networkedassets.git4c.core.usecase.async.BackendRequestForAsyncResult


class GetMethodsForPredefinedRepositoryResultRequest(requestId: String) : BackendRequestForAsyncResult<Methods>(requestId)