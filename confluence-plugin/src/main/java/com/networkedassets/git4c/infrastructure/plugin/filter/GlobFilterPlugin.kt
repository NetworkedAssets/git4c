package com.networkedassets.git4c.infrastructure.plugin.filter

import com.networkedassets.git4c.core.bussiness.FilterPlugin
import com.networkedassets.git4c.core.bussiness.ImportedFileData
import java.io.File
import java.nio.file.FileSystems

class GlobFilterPlugin(globs: List<String>) : FilterPlugin {

    companion object {
        private val fs = FileSystems.getDefault()
        val EVERYTHING = "**"
    }

    val globs = globs.map { fs.getPathMatcher("glob:$it") }

    override fun filter(file: ImportedFileData) = globs.isEmpty() || globs.any { glob -> glob.matches(File(file.path).toPath()) }

}