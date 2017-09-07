package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.inbound.PredefinedGlobToCreate
import com.networkedassets.git4c.boundary.outbound.PredefinedGlobData
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

class CreatePredefinedGlobCommand(
        val globToCreate: PredefinedGlobToCreate
) : BackendRequest<PredefinedGlobData>()