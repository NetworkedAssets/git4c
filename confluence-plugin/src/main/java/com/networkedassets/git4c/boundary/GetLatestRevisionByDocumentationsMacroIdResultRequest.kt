package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.outbound.Revision
import com.networkedassets.git4c.core.usecase.async.BackendRequestForAsyncResult


class GetLatestRevisionByDocumentationsMacroIdResultRequest(requestId: String) : BackendRequestForAsyncResult<Revision>(requestId)