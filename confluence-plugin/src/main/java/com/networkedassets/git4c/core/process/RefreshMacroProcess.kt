package com.networkedassets.git4c.core.process

import com.networkedassets.git4c.core.business.ErrorPageBuilder
import com.networkedassets.git4c.core.bussiness.ConverterPlugin
import com.networkedassets.git4c.core.bussiness.ParserPlugin
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.datastore.cache.DocumentsViewCache
import com.networkedassets.git4c.core.datastore.extractors.ExtractorData
import com.networkedassets.git4c.core.exceptions.VerificationException
import com.networkedassets.git4c.data.GlobForMacro
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.data.macro.documents.DocumentationsMacro
import com.networkedassets.git4c.data.macro.documents.item.DocumentsItem
import com.networkedassets.git4c.data.macro.documents.item.TableOfContents
import com.networkedassets.git4c.infrastructure.plugin.filter.GlobFilterPlugin
import com.networkedassets.git4c.utils.contentEquals

class RefreshMacroProcess(
        val cache: DocumentsViewCache,
        val importer: SourcePlugin,
        val converter: ConverterPlugin,
        val parser: ParserPlugin,
        val errorPageBuilder: ErrorPageBuilder,
        val extractContentProcess: ExtractContentProcess
) {

    @Throws(VerificationException::class)
    fun fetchDataFromSourceThenConvertAndCache(
            macroSettings: MacroSettings,
            globs: List<GlobForMacro>,
            repository: Repository,
            extractor: ExtractorData?
    ): DocumentationsMacro {

        val filter = GlobFilterPlugin(globs.map { it.glob })

        return cache.get(macroSettings.uuid).takeIf {
            it?.currentBranch == macroSettings.branch
                    && importer.revision(macroSettings, repository).use { it.revision } == it.revision
                    && globs.map { it.glob } contentEquals it.glob.map { it.glob }
        } ?: importer.pull(repository, macroSettings.branch).use {
            it.imported.filter { filter.filter(it) }
                    .mapNotNull {
                        val extractionResult = extractContentProcess.extract(extractor, it)
                        try {
                            converter.convert(it, extractionResult)
                        } catch (e: Exception) {
                            val page = errorPageBuilder.build(it, e)
                            DocumentsItem(it.path, it.updateAuthorFullName, it.updateAuthorEmail, it.updateDate, it.contentString, page, TableOfContents("", "", listOf()))
                        }
                    }
        }.let {
            val revision = importer.revision(macroSettings, repository).use { it.revision }
            DocumentationsMacro(macroSettings, revision, it, globs)
        }.apply {
            cache.insert(this.uuid, this)
        }
    }
}
