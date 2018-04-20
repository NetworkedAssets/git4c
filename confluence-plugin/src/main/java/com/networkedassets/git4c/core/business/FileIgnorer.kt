package com.networkedassets.git4c.core.business

import com.networkedassets.git4c.core.bussiness.ImportedFileData

/**
 * For given file FileIgnorer returns list of files that shouldn't be converted
 */
interface FileIgnorer {
    fun getFilesToIgnore(fileData: ImportedFileData): List<String>
    fun supportedExtensions(): List<String>
}