package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.outbound.Commits
import com.networkedassets.git4c.core.usecase.async.BackendRequestForAsyncResult


class GetCommitHistoryForFileResultRequest(requestId: String) : BackendRequestForAsyncResult<Commits>(requestId)