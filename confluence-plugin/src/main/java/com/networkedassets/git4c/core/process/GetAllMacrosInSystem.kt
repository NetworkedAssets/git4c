package com.networkedassets.git4c.core.process

import com.networkedassets.git4c.core.business.PageMacroExtractor
import com.networkedassets.git4c.core.business.PageManager

class GetAllMacrosInSystem(
        val pageManager: PageManager,
        val macroExtractor: PageMacroExtractor
) : IGetAllMacrosInSystem {

    override fun extract(): List<String> {

        return pageManager.getAllPageKeys()
                .asSequence()
                .mapNotNull {
                    val page = pageManager.getPage(it) ?: return@mapNotNull null
                    macroExtractor.extractMacro(page.content)
                }
                .filterNotNull()
                .flatMap { it.map { it.uuid }.asSequence() }
                .toList()

    }

}

interface IGetAllMacrosInSystem {
    fun extract(): List<String>;
}

