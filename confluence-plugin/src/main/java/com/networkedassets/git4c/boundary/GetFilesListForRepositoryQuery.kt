package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.inbound.RepositoryToGetFiles
import com.networkedassets.git4c.core.usecase.async.AsyncBackendRequest

class GetFilesListForRepositoryQuery(
        val repositoryToGetFiles: RepositoryToGetFiles
) : AsyncBackendRequest()
