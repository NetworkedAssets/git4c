package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.inbound.DetailsToGetMethods
import com.networkedassets.git4c.boundary.outbound.Methods
import com.networkedassets.git4c.delivery.executor.result.BackendRequest


class GetBranchesForPredefinedRepositoryQuery (
        val repository: String,
        detailesToGetMethods: DetailsToGetMethods
) : BackendRequest<Methods>()