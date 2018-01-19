package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.inbound.ExecutorThreadNumbersIn
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

class SaveExecutorThreadNumbersQuery(
        val numbers: ExecutorThreadNumbersIn
) : BackendRequest<Unit>()