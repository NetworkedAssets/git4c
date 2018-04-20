package com.networkedassets.git4c.infrastructure.plugin.converter.plaintext

import com.networkedassets.git4c.core.business.ExtractionResult
import com.networkedassets.git4c.core.business.Macro
import com.networkedassets.git4c.core.bussiness.ConverterPlugin
import com.networkedassets.git4c.core.bussiness.ImportedFileData
import com.networkedassets.git4c.data.macro.documents.item.ConvertedDocumentsItem
import com.networkedassets.git4c.data.macro.documents.item.TableOfContents
import org.apache.commons.lang3.StringEscapeUtils

class PlainTextConverterPlugin : ConverterPlugin {

    override fun convert(fileData: ImportedFileData, extractionResult: ExtractionResult, macro: Macro): ConvertedDocumentsItem? {

        val content = extractionResult.content

        val pageContent = """
            <div class="git4c-prismjs-div">
            <pre class="line-numbers language-none">
            <code class="git4c-prismjs-code language-none">${StringEscapeUtils.escapeHtml4(content)}</code>
            </pre>
            </div>
        """

        return ConvertedDocumentsItem(fileData.path, fileData.updateAuthorFullName, fileData.updateAuthorEmail, fileData.updateDate, content, pageContent, TableOfContents("", "", listOf()))
    }

    override val identifier = "PlainText"

}