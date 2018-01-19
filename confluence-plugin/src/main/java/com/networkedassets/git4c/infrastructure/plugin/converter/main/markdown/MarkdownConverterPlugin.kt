package com.networkedassets.git4c.infrastructure.plugin.converter.main.markdown

import com.networkedassets.git4c.core.bussiness.ImportedFileData
import com.networkedassets.git4c.infrastructure.plugin.converter.main.MainConverterPlugin
import com.networkedassets.git4c.infrastructure.plugin.converter.main.markdown.tables.TablesExtension
import org.commonmark.Extension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.slf4j.LoggerFactory

class MarkdownConverterPlugin : MainConverterPlugin {

    private val log = LoggerFactory.getLogger(MarkdownConverterPlugin::class.java)

    private val extensions: List<Extension> = listOf(TablesExtension.create())
    private val parser: Parser

    init {
        parser = Parser.builder().extensions(extensions).build()
    }

    override fun convert(fileData: ImportedFileData): String {
        val renderer = HtmlRenderer.builder().extensions(extensions).build()
        val node = parser.parse(String(fileData.content()))
        return "<span>${renderer.render(node)}</span>"
    }

    override fun supportedExtensions() = listOf("md")

}
