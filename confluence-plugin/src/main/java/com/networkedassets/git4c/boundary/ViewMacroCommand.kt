package com.networkedassets.git4c.boundary

import com.networkedassets.git4c.boundary.inbound.MacroToView
import com.networkedassets.git4c.boundary.inbound.PageToView
import com.networkedassets.git4c.boundary.inbound.SpaceToView
import com.networkedassets.git4c.boundary.outbound.MacroView
import com.networkedassets.git4c.delivery.executor.result.BackendRequest


class ViewMacroCommand(
        val macro: MacroToView,
        val page: PageToView,
        val space: SpaceToView
) : BackendRequest<MacroView>()