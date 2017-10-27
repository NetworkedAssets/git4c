package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.inbound.Branch
import com.networkedassets.git4c.boundary.outbound.FilesList
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

class GetFilesListForExistingRepositoryQuery(
        val repository: String,
        val branch: Branch
) : BackendRequest<FilesList>()