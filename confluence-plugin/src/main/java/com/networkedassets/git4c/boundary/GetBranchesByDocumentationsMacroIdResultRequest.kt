package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.outbound.Branches
import com.networkedassets.git4c.core.usecase.async.BackendRequestForAsyncResult


class GetBranchesByDocumentationsMacroIdResultRequest(requestId: String) : BackendRequestForAsyncResult<Branches>(requestId)