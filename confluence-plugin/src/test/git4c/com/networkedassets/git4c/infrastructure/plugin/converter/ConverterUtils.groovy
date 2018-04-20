package com.networkedassets.git4c.infrastructure.plugin.converter

import com.networkedassets.git4c.core.business.ExtractionResult
import com.networkedassets.git4c.core.business.Macro
import com.networkedassets.git4c.core.common.IdentifierGenerator
import com.networkedassets.git4c.data.macro.documents.item.ConvertedDocumentsItem
import com.networkedassets.git4c.infrastructure.plugin.converter.main.JSoupPostProcessor
import com.networkedassets.git4c.infrastructure.plugin.converter.main.MainConverterPluginList
import com.networkedassets.git4c.infrastructure.plugin.converter.main.asciidoc.AsciidocConverterPlugin
import com.networkedassets.git4c.infrastructure.plugin.converter.main.markdown.MarkdownConverterPlugin
import com.networkedassets.git4c.infrastructure.plugin.converter.plaintext.PlainTextConverterPlugin
import com.networkedassets.git4c.infrastructure.plugin.converter.plantuml.PUMLConverterPlugin

import java.nio.file.Paths

import static com.networkedassets.git4c.test.Utils.getDataFromDirectory

class ConverterUtils {

    private static def EXTRACTION_RESULT = new ExtractionResult("", 0)

    public static def asciidocConverter = AsciidocConverterPlugin.get(false)

    public static ConvertedDocumentsItem getConvertedAsciidoc(String name, Macro.MacroType macroType) {

        def resourceDirectory = Paths.get("src/test/resources", "asciidoc/$name");
        def source = getDataFromDirectory(resourceDirectory)

        def converter = new MainConverterPluginList([asciidocConverter], new JSoupPostProcessor(new UUIDGen()))

        return source.collect { converter.convert(it, EXTRACTION_RESULT, new Macro("", macroType)) }.grep()[0]
    }


    public static ConvertedDocumentsItem getConvertedMarkdown(String name, Macro.MacroType macroType) {

        def resourceDirectory = Paths.get("src/test/resources", "markdown/$name");
        def source = getDataFromDirectory(resourceDirectory)
        def markdownConverter = new MarkdownConverterPlugin()

        def converter = new MainConverterPluginList([markdownConverter], new JSoupPostProcessor(new UUIDGen()))

        return source.collect { converter.convert(it, EXTRACTION_RESULT, new Macro("", macroType)) }.grep()[0]
    }

    public static ConvertedDocumentsItem getConvertedPUML(String location, Macro.MacroType macroType) {

        def source = getDataFromDirectory(Paths.get(location))

        def pumlConverter = new PUMLConverterPlugin()

        def converter = new ConverterPluginList([pumlConverter], new PlainTextConverterPlugin())

        return source.collect { converter.convert(it, EXTRACTION_RESULT, new Macro("", macroType)) }.grep()[0]

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
