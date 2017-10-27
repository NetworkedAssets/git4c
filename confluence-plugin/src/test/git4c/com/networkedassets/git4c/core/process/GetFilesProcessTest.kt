package com.networkedassets.git4c.core.process

import com.networkedassets.git4c.data.RepositoryWithNoAuthorization
import com.networkedassets.git4c.infrastructure.UuidIdentifierGenerator
import com.networkedassets.git4c.infrastructure.git.DefaultGitClient
import com.networkedassets.git4c.infrastructure.plugin.converter.ConverterPluginList
import com.networkedassets.git4c.infrastructure.plugin.converter.images.ImageConverterPlugin
import com.networkedassets.git4c.infrastructure.plugin.converter.main.JSoupPostProcessor
import com.networkedassets.git4c.infrastructure.plugin.converter.main.MainConverterPluginList
import com.networkedassets.git4c.infrastructure.plugin.converter.main.asciidoc.AsciidocConverterPlugin
import com.networkedassets.git4c.infrastructure.plugin.converter.main.markdown.MarkdownConverterPlugin
import com.networkedassets.git4c.infrastructure.plugin.converter.plaintext.PlainTextConverterPlugin
import com.networkedassets.git4c.infrastructure.plugin.converter.plantuml.PUMLConverterPlugin
import com.networkedassets.git4c.infrastructure.plugin.converter.prismjs.PrismJSConverterPlugin
import com.networkedassets.git4c.infrastructure.plugin.source.git.GitSourcePlugin
import com.networkedassets.git4c.utils.genTransactionId
import org.junit.Test
import kotlin.test.assertNotNull

class GetFilesProcessTest() {

    val gitClient = DefaultGitClient()
    val importer = GitSourcePlugin(gitClient)
    val identifierGenerator = UuidIdentifierGenerator()
    val postProcessor = JSoupPostProcessor(identifierGenerator)
    val mainPlugins = MainConverterPluginList(listOf(AsciidocConverterPlugin(), MarkdownConverterPlugin()), postProcessor)
    val converterPlugins = listOf(mainPlugins, PrismJSConverterPlugin(), ImageConverterPlugin(), PUMLConverterPlugin())
    val converter = ConverterPluginList(converterPlugins, PlainTextConverterPlugin())

    val process = GetFilesProcess(importer);

    @Test
    fun `Get File should return files tree from repository`() {
        val repositoryUrl = "https://github.com/NetworkedAssets/git4c.git"
        val branch = "master";
        val repository = RepositoryWithNoAuthorization(genTransactionId(), repositoryUrl)

        val answer1 = process.getFiles(repository, branch);
        assertNotNull(answer1.tree)
    }

    @Test
    fun `Get File should return files tree from repository in avarge short time`() {
        val repositoryUrl = "https://github.com/NetworkedAssets/git4c.git"
        val branch = "master";
        val repository = RepositoryWithNoAuthorization(genTransactionId(), repositoryUrl)
        gitClient.pull(repository, branch)

        val times = arrayListOf<Long>()

        for (i in 1..10) {
            val time = System.currentTimeMillis()
            val answer = process.getFiles(repository, branch);
            assertNotNull(answer.tree)
            val runtime = System.currentTimeMillis() - time
            times.add(runtime)
        }

        assert(times.average() < 2000)
    }
}