package com.networkedassets.git4c.infrastructure

import com.networkedassets.git4c.core.business.PageMacroExtractor
import com.networkedassets.git4c.core.business.Macro
import org.jsoup.Jsoup

class AtlassianPageMacroExtractor : PageMacroExtractor {

    override fun extractMacro(pageContent: String): List<Macro> {

        val normalMacro = "Git4C"
        val singlePageMacro = "Git4C Single File"

        val h = Jsoup.parse(pageContent)

        val allMacros = h.select("""ac|structured-macro""")

        val git4cMacro = allMacros
                .filter { it.attr("ac:name") == normalMacro || it.attr("ac:name") == singlePageMacro }

        return git4cMacro.mapNotNull {
            val typeS = it.attr("ac:name")

            val type = when(typeS) {
                normalMacro -> Macro.MacroType.MULTIFILE
                singlePageMacro -> Macro.MacroType.SINGLEFILE
                else -> throw IllegalArgumentException("Unknown macro type: $typeS")
            }

            val uuid = it.select("""ac|parameter""").firstOrNull {it.attr("ac:name") == "uuid"}?.text()

            if (uuid != null) {
                Macro(uuid, type)
            } else {
                null
            }

        }

//        return uuids.map { Macro(it) }

        //UUID
//        h.select("""ac|structured-macro""").firstOrNull { it.attr("ac:name") == "Git4C" }
//                ?.select("""ac|parameter""")?.firstOrNull { it.attr("ac:name") == "uuid" }?.text()


    }

}