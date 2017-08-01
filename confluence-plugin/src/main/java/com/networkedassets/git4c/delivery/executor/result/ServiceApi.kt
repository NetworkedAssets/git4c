package com.networkedassets.git4c.delivery.executor.result

import com.networkedassets.git4c.delivery.executor.execution.BackendDispatcher
import javax.ws.rs.core.Response

interface ServiceApi {
    val dispatcher: BackendDispatcher<Response, Response>
}