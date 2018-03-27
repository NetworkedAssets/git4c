package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.inbound.DetailsToGetMethods
import com.networkedassets.git4c.core.usecase.async.AsyncBackendRequest


class GetMethodsForExistingRepositoryQuery(
        val repository: String,
        val detailsToGetMethods: DetailsToGetMethods
) : AsyncBackendRequest()