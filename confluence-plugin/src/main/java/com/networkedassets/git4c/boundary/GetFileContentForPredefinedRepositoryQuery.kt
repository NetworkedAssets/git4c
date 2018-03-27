package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.inbound.DetailsToGetFile
import com.networkedassets.git4c.core.usecase.async.AsyncBackendRequest


class GetFileContentForPredefinedRepositoryQuery(
        val repository: String,
        val detailsToGetFile: DetailsToGetFile
) : AsyncBackendRequest()