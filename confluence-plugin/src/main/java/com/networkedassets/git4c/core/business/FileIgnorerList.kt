package com.networkedassets.git4c.core.business

import com.networkedassets.git4c.core.bussiness.ImportedFileData

class FileIgnorerList(
        private vararg val ignorers: FileIgnorer
): FileIgnorer {

    override fun getFilesToIgnore(fileData: ImportedFileData): List<String> {
        if (!extentions.contains(fileData.extension)) return emptyList()
        val ignorer = ignorers.first { it.supportedExtensions().contains(fileData.extension) }
        return ignorer.getFilesToIgnore(fileData)
    }

    override fun supportedExtensions() = extentions.toList()

    private val extentions by lazy { ignorers.flatMap { it.supportedExtensions() }.toSet() }

}