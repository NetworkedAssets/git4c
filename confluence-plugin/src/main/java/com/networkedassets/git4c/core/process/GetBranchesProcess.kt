package com.networkedassets.git4c.core.process

import com.networkedassets.git4c.boundary.outbound.Branches
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.data.Repository

class GetBranchesProcess(
        val importer: SourcePlugin
) {

    fun fetchBranchList(macroSettings: MacroSettings, repository: Repository): Branches {
        val branches = importer.getBranches(repository)
        return Branches(macroSettings.branch, branches)
    }

}