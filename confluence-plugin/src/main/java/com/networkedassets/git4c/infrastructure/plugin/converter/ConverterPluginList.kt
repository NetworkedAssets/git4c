package com.networkedassets.git4c.infrastructure.plugin.converter

import com.networkedassets.git4c.core.business.ExtractionResult
import com.networkedassets.git4c.core.business.Macro
import com.networkedassets.git4c.core.bussiness.ConverterPlugin
import com.networkedassets.git4c.core.bussiness.ImportedFileData
import com.networkedassets.git4c.data.macro.documents.item.ConvertedDocumentsItem
import com.networkedassets.git4c.infrastructure.plugin.converter.main.markdown.InternalConverterPlugin

class ConverterPluginList(val plugins: List<InternalConverterPlugin>, val plainText: ConverterPlugin) : ConverterPlugin {

    override fun convert(fileData: ImportedFileData, extractionResult: ExtractionResult, macro: Macro): ConvertedDocumentsItem? {
        val extension = fileData.extension
        val plugin = plugins.firstOrNull { it.supportedExtensions().contains(extension) } ?: plainText
        return plugin.convert(fileData, extractionResult, macro)
    }

    override val identifier = "PluginList"
}