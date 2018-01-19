package com.networkedassets.git4c.infrastructure.plugin.converter

import com.networkedassets.git4c.core.business.ExtractionResult
import com.networkedassets.git4c.core.common.IdentifierGenerator
import com.networkedassets.git4c.data.macro.documents.item.ConvertedDocumentsItem
import com.networkedassets.git4c.data.macro.documents.item.DocumentsItem
import com.networkedassets.git4c.infrastructure.plugin.converter.main.JSoupPostProcessor
import com.networkedassets.git4c.infrastructure.plugin.converter.main.MainConverterPluginList
import com.networkedassets.git4c.infrastructure.plugin.converter.main.asciidoc.AsciidocConverterPlugin
import com.networkedassets.git4c.infrastructure.plugin.converter.main.markdown.MarkdownConverterPlugin

import java.nio.file.Paths

import static com.networkedassets.git4c.test.Utils.getDataFromDirectory

class ConverterUtils {

    private static def EXTRACTION_RESULT = new ExtractionResult("", 0)

    public static ConvertedDocumentsItem getAsciidoc(String name) {

        def resourceDirectory = Paths.get("src/test/resources", "asciidoc/$name");
        def source = getDataFromDirectory(resourceDirectory)
        def asciidocConverter = new AsciidocConverterPlugin()

        def converter = new MainConverterPluginList([asciidocConverter], new JSoupPostProcessor(new UUIDGen()))

        return source.collect { converter.convert(it, EXTRACTION_RESULT) }.grep()[0]
    }


    public static ConvertedDocumentsItem getMarkdown(String name) {

        def resourceDirectory = Paths.get("src/test/resources", "markdown/$name");
        def source = getDataFromDirectory(resourceDirectory)
        def markdownConverter = new MarkdownConverterPlugin()

        def converter = new MainConverterPluginList([markdownConverter], new JSoupPostProcessor(new UUIDGen()))

        return source.collect { converter.convert(it, EXTRACTION_RESULT) }.grep()[0]
    }

    private static class UUIDGen implements IdentifierGenerator {

        private int i = 0

        @Override
        String generateNewIdentifier() {
            i++
            return i
        }
    }

}
