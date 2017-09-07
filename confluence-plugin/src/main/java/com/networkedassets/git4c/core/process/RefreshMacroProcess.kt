package com.networkedassets.git4c.core.process

import com.networkedassets.git4c.core.bussiness.ConverterPlugin
import com.networkedassets.git4c.core.bussiness.ParserPlugin
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.datastore.cache.DocumentsViewCache
import com.networkedassets.git4c.core.exceptions.VerificationException
import com.networkedassets.git4c.data.GlobForMacro
import com.networkedassets.git4c.data.MacroSettings
import com.networkedassets.git4c.data.Repository
import com.networkedassets.git4c.data.macro.documents.DocumentationsMacro
import com.networkedassets.git4c.infrastructure.plugin.filter.GlobFilterPlugin
import com.networkedassets.git4c.utils.contentEquals

class RefreshMacroProcess(
        val cache: DocumentsViewCache,
        val importer: SourcePlugin,
        val converter: ConverterPlugin,
        val parser: ParserPlugin
) {

    @Throws(VerificationException::class)
    fun fetchDataFromSourceThenConvertAndCache(
            macroSettings: MacroSettings,
            globs: List<GlobForMacro>,
            repository: Repository
    ): DocumentationsMacro {

        val filter = GlobFilterPlugin(globs.map { it.glob })
        val method = macroSettings.method

        return cache.get(macroSettings.uuid).takeIf {
            it?.currentBranch == macroSettings.branch
                    && importer.revision(macroSettings, repository).use { it.revision } == it.revision
                    && globs.map { it.glob } contentEquals it.glob.map { it.glob }
        } ?: importer.pull(repository, macroSettings.branch).use {
            it.imported.filter { filter.filter(it) }
                    .map {
                        if (method.isNullOrEmpty()) {
                            Pair(it, null)
                        } else {
                            parser.getMethod(it, method!!)
                        }
                    }
                    .mapNotNull { converter.convert(it.first, it.second?.range?.start ?: 0) }
        }.let {
            val revision = importer.revision(macroSettings, repository).use { it.revision }
            DocumentationsMacro(macroSettings, revision, it, globs)
        }.apply {
            cache.insert(this.uuid, this)
        }
    }
}
