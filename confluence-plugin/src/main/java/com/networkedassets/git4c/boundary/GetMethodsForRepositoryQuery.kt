package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.inbound.RepositoryToGetMethods
import com.networkedassets.git4c.boundary.outbound.Methods
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

class GetMethodsForRepositoryQuery(
        val repositoryToGetMethods: RepositoryToGetMethods
) : BackendRequest<Methods>()
