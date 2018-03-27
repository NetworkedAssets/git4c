package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.core.usecase.async.BackendRequestForAsyncResult

class RefreshMacroLocationsResultCommand(
        requestId: String
) : BackendRequestForAsyncResult<Unit>(requestId)