package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.inbound.Branch
import com.networkedassets.git4c.core.usecase.async.AsyncBackendRequest

class GetFilesListForPredefinedRepositoryQuery(
        val repository: String,
        val branch: Branch
) : AsyncBackendRequest()