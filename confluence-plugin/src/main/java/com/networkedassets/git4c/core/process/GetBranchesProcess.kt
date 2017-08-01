package com.networkedassets.git4c.core.process

import com.networkedassets.git4c.boundary.outbound.Branches
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.data.macro.DocumentationsMacroSettings

class GetBranchesProcess(
        val importer: SourcePlugin
) {

    fun fetchBranchList(settingsDocumentations: DocumentationsMacroSettings): Branches {
        val branches = importer.getBranches(settingsDocumentations)
        return Branches(settingsDocumentations.branch, branches)
    }

}