package com.networkedassets.git4c.infrastructure.plugin.converter.prismjs

import com.networkedassets.git4c.core.business.ExtractionResult
import com.networkedassets.git4c.core.bussiness.ImportedFileData
import com.networkedassets.git4c.data.macro.documents.item.DocumentsItem
import com.networkedassets.git4c.data.macro.documents.item.TableOfContents
import com.networkedassets.git4c.infrastructure.plugin.converter.main.markdown.InternalConverterPlugin
import org.apache.commons.lang3.StringEscapeUtils

class PrismJSConverterPlugin : InternalConverterPlugin {

    val map = mapOf(
            "c" to listOf("c", "h"),
            "cpp" to listOf("cpp", "c++", "hpp"),
            "csharp" to listOf("cs"),
            "css" to listOf("css"),
            "gherkin" to listOf("feature"),
            "groovy" to listOf("groovy", "gradle"),
            "java" to listOf("java", "jsp"),
            "kotlin" to listOf("kt"),
            "scala" to listOf("scala", "sbt"),
            "xml" to listOf("xml")
    )

    val extensions = map.values.flatten()

//    override fun convert(fileData: ImportedFileData) = convert(fileData, 0)

    override fun convert(fileData: ImportedFileData, extractionResult: ExtractionResult): DocumentsItem? {

        val content = fileData.contentString
        val extension = fileData.extension
        val language = map.entries.first { it.value.contains(extension) }.key

        val pageContent = """
            <div class="git4c-prismjs-div">
            <pre class="line-numbers language-$language" data-start="${extractionResult.firstLine}">
            <code class="git4c-prismjs-code language-$language">""" +
                StringEscapeUtils.escapeHtml4(extractionResult.content) +
                """</code>
            </pre>
            </div>
        """

        return DocumentsItem(fileData.path, fileData.updateAuthorFullName, fileData.updateAuthorEmail, fileData.updateDate, content, pageContent, TableOfContents("", "", listOf()))


    }

    override fun supportedExtensions() = extensions

    override val identifier = "Highlight.js"

}