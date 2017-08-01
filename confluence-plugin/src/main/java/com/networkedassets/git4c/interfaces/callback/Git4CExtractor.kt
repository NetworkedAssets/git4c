package com.networkedassets.git4c.interfaces.callback

import com.atlassian.confluence.pages.Page
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.networkedassets.git4c.application.Plugin
import com.networkedassets.git4c.boundary.GetAllDocumentsByDocumentationsMacroIdQuery
import com.networkedassets.git4c.utils.sendToExecution
import org.jsoup.Jsoup
import java.lang.StringBuilder
import kotlin.reflect.full.safeCast

class Git4CExtractor(val plugin: Plugin) : Extractor2 {
    override fun extractText(searchable: Any): StringBuilder {
        return Page::class.safeCast(searchable)?.let {
            val pageBody = it.bodyAsString
            val h = Jsoup.parse(pageBody)
            //UUID
            h.select("""ac|structured-macro""").firstOrNull { it.attr("ac:name") == "Git4C" }
                    ?.select("""ac|parameter""")?.firstOrNull { it.attr("ac:name") == "uuid" }?.text()
        }?.let { uuid ->
            val content =
                    sendToExecution(plugin.components.dispatcher, GetAllDocumentsByDocumentationsMacroIdQuery(uuid))
                            .map { it.content }
                            .map { Jsoup.parse(it).text() }

            val s = content.joinToString(separator = " ")
            StringBuilder(s)
        } ?: StringBuilder()
    }

    override fun extractFields(searchable: Any) = arrayListOf<FieldDescriptor>()
}