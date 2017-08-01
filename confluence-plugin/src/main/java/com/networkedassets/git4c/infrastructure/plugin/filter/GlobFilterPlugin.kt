package com.networkedassets.git4c.infrastructure.plugin.filter

import com.networkedassets.git4c.core.bussiness.FilterPlugin
import com.networkedassets.git4c.core.bussiness.ImportedFileData
import org.apache.commons.lang3.SystemUtils
import java.io.File
import java.nio.file.FileSystems

class GlobFilterPlugin (val glob: String) : FilterPlugin {

    companion object {
        val EVERYTHING = "**"
    }

    override fun filter(file: ImportedFileData): Boolean {
        val safeGlob = if (glob.isNotEmpty()) {
            glob
        } else {
            EVERYTHING
        }

        return FileSystems.getDefault().getPathMatcher("glob:$safeGlob").matches(File(file.path).toPath())
    }
}