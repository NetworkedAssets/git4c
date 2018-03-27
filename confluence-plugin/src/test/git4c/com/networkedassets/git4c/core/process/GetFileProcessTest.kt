package com.networkedassets.git4c.core.process

import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.infrastructure.UuidIdentifierGenerator
import com.networkedassets.git4c.infrastructure.mocks.core.DirectorySourcePlugin
import com.networkedassets.git4c.infrastructure.plugin.converter.ConverterPluginList
import com.networkedassets.git4c.infrastructure.plugin.converter.images.ImageConverterPlugin
import com.networkedassets.git4c.infrastructure.plugin.converter.main.JSoupPostProcessor
import com.networkedassets.git4c.infrastructure.plugin.converter.main.MainConverterPluginList
import com.networkedassets.git4c.infrastructure.plugin.converter.main.asciidoc.AsciidocConverterPlugin
import com.networkedassets.git4c.infrastructure.plugin.converter.main.markdown.MarkdownConverterPlugin
import com.networkedassets.git4c.infrastructure.plugin.converter.plaintext.PlainTextConverterPlugin
import com.networkedassets.git4c.infrastructure.plugin.converter.plantuml.PUMLConverterPlugin
import com.networkedassets.git4c.infrastructure.plugin.converter.prismjs.PrismJSConverterPlugin
import com.networkedassets.git4c.infrastructure.plugin.parser.Parsers
import com.networkedassets.git4c.utils.genTransactionId
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test
import java.util.*
import kotlin.test.assertNotNull

class GetFileProcessTest() {

    val importer = DirectorySourcePlugin()
    val identifierGenerator = UuidIdentifierGenerator()
    val postProcessor = JSoupPostProcessor(identifierGenerator)
    val mainPlugins = MainConverterPluginList(listOf(AsciidocConverterPlugin(), MarkdownConverterPlugin()), postProcessor)
    val converterPlugins = listOf(mainPlugins, PrismJSConverterPlugin(), ImageConverterPlugin(), PUMLConverterPlugin())
    val converter = ConverterPluginList(converterPlugins, PlainTextConverterPlugin())
    val parser = Parsers()
    val extractContentProcess = ExtractContentProcess(parser)

    val process = GetFileProcess(
            importer,
            converter,
            extractContentProcess
    );

    @Test
    fun `Get File should return converted file content`() {

        // Given
        val branch = "master";
        val file = "README.md"
        val repository = RepositoryWithNoAuthorization(genTransactionId(), "src/test/resources", false)

        // When
        val answer = process.getFile(repository, branch, file);

        // Then
        assertNotNull(answer.content)
        assertThat(answer.content).contains("Readme Header")
    }

    @Test
    fun `Get File should throw an exception when file has not been found`() {

        // Given
        val branch = "master";
        val file = "NotExisting.File"
        val repository = RepositoryWithNoAuthorization(genTransactionId(), "src/test/resources", false)

        // When
        val fileProcess = { process.getFile(repository, branch, file) }

        // Then
        assertThatThrownBy { fileProcess.invoke() }
                .isInstanceOf(NoSuchElementException::class.java)

    }
}