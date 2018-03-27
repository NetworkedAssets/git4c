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
import com.networkedassets.git4c.utils.genTransactionId
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import kotlin.test.assertNotNull

class GetFilesProcessTest() {

    val importer = DirectorySourcePlugin()
    val identifierGenerator = UuidIdentifierGenerator()
    val postProcessor = JSoupPostProcessor(identifierGenerator)
    val mainPlugins = MainConverterPluginList(listOf(AsciidocConverterPlugin(), MarkdownConverterPlugin()), postProcessor)
    val converterPlugins = listOf(mainPlugins, PrismJSConverterPlugin(), ImageConverterPlugin(), PUMLConverterPlugin())
    val converter = ConverterPluginList(converterPlugins, PlainTextConverterPlugin())

    val process = GetFilesProcess(importer);

    @Test
    fun `Get Files should return files tree from repository`() {

        // Given
        val branch = "master";
        val file = "README.md"
        val repository = RepositoryWithNoAuthorization(genTransactionId(), "src/test/resources", false)

        // When
        val answer = process.getFiles(repository, branch);

        // Then
        assertNotNull(answer.tree)
        assertNotNull(answer.files)

        assertThat(answer.files.contains(file))

        assert(answer.tree.getChildByName(file).isPresent)
        assertThat(answer.tree.getChildByName(file).get().fullName).isEqualTo(file)
    }
}