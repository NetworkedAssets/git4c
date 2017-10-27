package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.inbound.RepositoryToGetFile
import com.networkedassets.git4c.boundary.outbound.FileContent
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

class GetFileContentForRepositoryQuery(
        val repositoryToGetFile: RepositoryToGetFile
) : BackendRequest<FileContent>()
