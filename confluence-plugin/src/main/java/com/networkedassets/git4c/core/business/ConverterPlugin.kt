package com.networkedassets.git4c.core.bussiness

import com.networkedassets.git4c.core.business.ExtractionResult
import com.networkedassets.git4c.core.business.Macro
import com.networkedassets.git4c.data.macro.documents.item.ConvertedDocumentsItem

interface ConverterPlugin : Plugin {
    fun convert(fileData: ImportedFileData, extractionResult: ExtractionResult, macro: Macro): ConvertedDocumentsItem?
}