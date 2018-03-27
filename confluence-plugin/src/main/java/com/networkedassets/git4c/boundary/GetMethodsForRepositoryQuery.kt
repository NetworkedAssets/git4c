package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.inbound.RepositoryToGetMethods
import com.networkedassets.git4c.core.usecase.async.AsyncBackendRequest

class GetMethodsForRepositoryQuery(
        val repositoryToGetMethods: RepositoryToGetMethods
) : AsyncBackendRequest()
