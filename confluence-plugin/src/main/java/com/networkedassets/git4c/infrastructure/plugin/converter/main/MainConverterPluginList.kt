package com.networkedassets.git4c.infrastructure.plugin.converter.main

import com.networkedassets.git4c.core.business.ExtractionResult
import com.networkedassets.git4c.core.bussiness.ImportedFileData
import com.networkedassets.git4c.data.macro.documents.item.ConvertedDocumentsItem
import com.networkedassets.git4c.infrastructure.UuidIdentifierGenerator
import com.networkedassets.git4c.infrastructure.plugin.converter.main.markdown.InternalConverterPlugin

class MainConverterPluginList(
        private val plugins: List<MainConverterPlugin>,
        private val postProcessor: ConverterPostProcessor
) : InternalConverterPlugin {

    var idGenerator = UuidIdentifierGenerator()

    override fun supportedExtensions() = plugins.flatMap { it.supportedExtensions() }

    override fun convert(fileData: ImportedFileData, extractionResult: ExtractionResult): ConvertedDocumentsItem? {

        val plugin = plugins.firstOrNull { it.supportedExtensions().contains(fileData.extension) } ?: return null

        val convertedHtml = plugin.convert(fileData)

        val result2 = postProcessor.parse(convertedHtml, fileData.getAbsolutePath().toFile(), fileData.context)

        val (finalResult, toc) = postProcessor.generateTableOfContents(result2)

        return ConvertedDocumentsItem(fileData.path, fileData.updateAuthorFullName, fileData.updateAuthorEmail, fileData.updateDate, String(fileData.content()), finalResult, toc)

    }

    override val identifier = "MAIN_CONVERTERS"
}