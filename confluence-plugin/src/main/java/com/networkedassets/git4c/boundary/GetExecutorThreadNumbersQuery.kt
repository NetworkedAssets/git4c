package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.outbound.ExecutorThreadNumbers
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

class GetExecutorThreadNumbersQuery(
) : BackendRequest<ExecutorThreadNumbers>()