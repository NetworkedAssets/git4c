package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.outbound.SavedDocumentationsMacro
import com.networkedassets.git4c.core.usecase.async.BackendRequestForAsyncResult


class CreateDocumentationsMacroResultRequest(requestId: String) : BackendRequestForAsyncResult<SavedDocumentationsMacro>(requestId)