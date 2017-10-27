package com.networkedassets.git4c.core.business

interface PageMacroExtractor {
    fun extractMacro(pageContent: String): List<Macro>
}