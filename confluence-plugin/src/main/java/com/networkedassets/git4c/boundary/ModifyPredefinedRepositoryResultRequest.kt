package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.outbound.SavedPredefinedRepository
import com.networkedassets.git4c.core.usecase.async.BackendRequestForAsyncResult


class ModifyPredefinedRepositoryResultRequest(requestId: String) : BackendRequestForAsyncResult<SavedPredefinedRepository>(requestId)