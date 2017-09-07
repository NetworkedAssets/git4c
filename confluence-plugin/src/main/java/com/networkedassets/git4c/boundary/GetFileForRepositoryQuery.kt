package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.inbound.RepositoryToGetFile
import com.networkedassets.git4c.boundary.outbound.File
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

class GetFileForRepositoryQuery(
        val repositoryToGetFile: RepositoryToGetFile
) : BackendRequest<File>()
