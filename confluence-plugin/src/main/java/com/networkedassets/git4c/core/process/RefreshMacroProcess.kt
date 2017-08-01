package com.networkedassets.git4c.core.process

import com.networkedassets.git4c.core.bussiness.ConverterPlugin
import com.networkedassets.git4c.core.bussiness.SourcePlugin
import com.networkedassets.git4c.core.datastore.DocumentsViewCache
import com.networkedassets.git4c.core.datastore.MacroSettingsRepository
import com.networkedassets.git4c.data.macro.DocumentationsMacroSettings
import com.networkedassets.git4c.data.macro.documents.DocumentationsMacro
import com.networkedassets.git4c.infrastructure.plugin.filter.GlobFilterPlugin

class RefreshMacroProcess(
        val cache: DocumentsViewCache,
        val importer: SourcePlugin,
        val converter: ConverterPlugin,
        val macroSettingsRepository: MacroSettingsRepository
) {

    fun fetchDataFromSourceThenConvertAndSave(
            documentationsMacroSettings: DocumentationsMacroSettings,
            save: Boolean = true
    ): DocumentationsMacro {

        val filter = GlobFilterPlugin(documentationsMacroSettings.glob)

        return cache.get(documentationsMacroSettings.id).takeIf {
            it?.currentBranch == documentationsMacroSettings.branch
                    && importer.verify(documentationsMacroSettings).isOk()
                    && importer.revision(documentationsMacroSettings) == it.revision
                    && documentationsMacroSettings.glob == it.glob
        } ?: importer.createFetchProcess(documentationsMacroSettings).use { fetchProcess ->
            fetchProcess.fetch()
                    .filter { filter.filter(it) }
                    .mapNotNull { converter.convert(it) }
        }
                .let {
                    val revision = importer.revision(documentationsMacroSettings)
                    DocumentationsMacro(documentationsMacroSettings, revision, it)
                }
                .apply {
                    cache.put(this.uuid, this)
                }
                .apply {
                    if (save) {
                        save(documentationsMacroSettings)
                    }
                }
    }

    private fun save(documentationsMacroSettings: DocumentationsMacroSettings) {
        macroSettingsRepository.put(
                documentationsMacroSettings.id,
                documentationsMacroSettings
        )
    }
}