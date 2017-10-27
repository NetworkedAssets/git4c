package com.networkedassets.git4c.infrastructure.plugin.converter.main

import com.networkedassets.git4c.core.bussiness.ImportedFileData

interface MainConverterPlugin {
    fun convert(fileData: ImportedFileData): String
    fun supportedExtensions(): List<String>
}