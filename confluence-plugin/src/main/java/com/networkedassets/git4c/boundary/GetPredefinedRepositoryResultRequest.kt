package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.outbound.PredefinedRepository
import com.networkedassets.git4c.core.usecase.async.BackendRequestForAsyncResult


class GetPredefinedRepositoryResultRequest(requestId: String) : BackendRequestForAsyncResult<PredefinedRepository>(requestId)