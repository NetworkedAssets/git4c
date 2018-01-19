package com.networkedassets.git4c.core.bussiness

import com.networkedassets.git4c.core.business.ExtractionResult
import com.networkedassets.git4c.data.macro.documents.item.ConvertedDocumentsItem
import com.networkedassets.git4c.data.macro.documents.item.DocumentsItem

interface ConverterPlugin : Plugin {
    fun convert(fileData: ImportedFileData, extractionResult: ExtractionResult): ConvertedDocumentsItem?
}