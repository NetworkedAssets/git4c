package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.outbound.FilesList
import com.networkedassets.git4c.core.usecase.async.BackendRequestForAsyncResult


class GetFilesListForPredefinedRepositoryResultRequest(requestId: String) : BackendRequestForAsyncResult<FilesList>(requestId)