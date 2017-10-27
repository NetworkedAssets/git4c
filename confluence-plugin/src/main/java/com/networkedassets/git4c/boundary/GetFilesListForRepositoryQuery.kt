package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.inbound.RepositoryToGetFiles
import com.networkedassets.git4c.boundary.outbound.FilesList
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

class GetFilesListForRepositoryQuery(
        val repositoryToGetFiles: RepositoryToGetFiles
) : BackendRequest<FilesList>()
