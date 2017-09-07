package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.outbound.PredefinedGlobData
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

class GetPredefinedGlobByIdQuery(
        val uuid: String
) : BackendRequest<PredefinedGlobData>()