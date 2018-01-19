package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.inbound.EditingEnabled
import com.networkedassets.git4c.delivery.executor.result.BackendRequest

class SetEditingEnabledQuery(
        val editingEnabled: EditingEnabled
): BackendRequest<Unit>()