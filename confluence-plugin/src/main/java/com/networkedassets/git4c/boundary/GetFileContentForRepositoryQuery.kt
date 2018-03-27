package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.inbound.RepositoryToGetFile
import com.networkedassets.git4c.core.usecase.async.AsyncBackendRequest

class GetFileContentForRepositoryQuery(
        val repositoryToGetFile: RepositoryToGetFile
) : AsyncBackendRequest()
