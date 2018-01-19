package com.networkedassets.git4c.core.business

import com.networkedassets.git4c.core.bussiness.ImportedFileData

interface ErrorPageBuilder {
    fun build(file: ImportedFileData, ex: Throwable): String
}