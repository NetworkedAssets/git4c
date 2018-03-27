package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.outbound.RepositoryUsages
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

class GetRepositoryUsagesForUserQuery(
        val username: String
) : BackendRequest<RepositoryUsages>()