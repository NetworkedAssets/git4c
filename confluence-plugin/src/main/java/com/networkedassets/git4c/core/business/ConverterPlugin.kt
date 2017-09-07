package com.networkedassets.git4c.core.bussiness

import com.networkedassets.git4c.data.macro.documents.item.DocumentsItem

interface ConverterPlugin : Plugin {
    fun convert(fileData: ImportedFileData): DocumentsItem?
    fun convert(fileData: ImportedFileData, startLineNumber: Int): DocumentsItem? = convert(fileData)
}