package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.inbound.RepositoryToGetFiles
import com.networkedassets.git4c.boundary.outbound.Files
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

class GetFilesForRepositoryQuery(
        val repositoryToGetFiles: RepositoryToGetFiles
) : BackendRequest<Files>()
