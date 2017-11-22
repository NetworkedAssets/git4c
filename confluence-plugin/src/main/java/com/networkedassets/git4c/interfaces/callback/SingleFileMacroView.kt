package com.networkedassets.git4c.interfaces.callback

import com.atlassian.confluence.content.render.xhtml.ConversionContext
import com.atlassian.confluence.macro.Macro
import com.atlassian.confluence.macro.MacroExecutionException
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils
import com.atlassian.confluence.util.velocity.VelocityUtils
import com.fasterxml.jackson.databind.ObjectMapper
import com.networkedassets.git4c.application.Plugin
import com.networkedassets.git4c.boundary.GetDocumentationsMacroByDocumentationsMacroIdQuery
import com.networkedassets.git4c.delivery.executor.result.BackendPresenter
import org.slf4j.LoggerFactory

class SingleFileMacroView(val plugin: Plugin) : Macro {

    private val log = LoggerFactory.getLogger(MacroView::class.java)

    @Throws(MacroExecutionException::class)
    override fun execute(params: Map<String, String>, s: String, conversionContext: ConversionContext): String {

        if (conversionContext.outputType == "pdf" || conversionContext.outputType == "word") {
            val uuid = params["uuid"]!!
            //FIXME
            val query = GetDocumentationsMacroByDocumentationsMacroIdQuery(uuid, null)
            val emptyPresenter = object: BackendPresenter<Any, Throwable> {
                override fun render(result: Any) = result
                override fun error(exception: Throwable) = exception
            }

            val unsafeMacro = plugin.components.documentsViewCache.get(uuid)

            val macro = if (unsafeMacro == null) {
                plugin.components.dispatcher.sendToExecution(query, emptyPresenter).get()
                plugin.components.documentsViewCache.get(uuid)
            } else {
                unsafeMacro
            } ?: return "<div>Couldn't find macro</div>"

            val context = MacroUtils.defaultVelocityContext()
            context.put("code", """<div xmlns:v-on="http://www.w3.org/1999/xhtml">${macro.files[0].content}</div>""")

            val template = if (conversionContext.outputType == "pdf") "pdfexport" else "wordexport"

            return VelocityUtils.getRenderedTemplate("/singleFile/$template.vm", context)
        }

        try {
            val collapsible = params["collapsible"]?.toBoolean() ?: true
            val collapseByDefault = params["collapseByDefault"]?.toBoolean() ?: false
            val showLineNumbers = params["showLineNumbers"]?.toBoolean() ?: true
            val showTopBar = params["showTopBar"]?.toBoolean() ?: true

            val context = MacroUtils.defaultVelocityContext()
            context.put("paramsJson", ObjectMapper().writeValueAsString(params))
            context.put("UUID", params["uuid"])
            context.put("COLLAPSIBLE", collapsible)
            context.put("COLLAPSEBYDEFAULT", collapseByDefault)
            context.put("LINENUMBERS", showLineNumbers)
            context.put("SHOWTOPBAR", showTopBar)
            context.put("RANDOM", plugin.components.idGenerator.generateNewIdentifier())

            return VelocityUtils.getRenderedTemplate("/singleFile/macro.vm", context)
        } catch (e: Exception) {
            throw MacroExecutionException(e);
        }
    }

    override fun getBodyType(): Macro.BodyType {
        return Macro.BodyType.NONE
    }

    override fun getOutputType(): Macro.OutputType {
        return Macro.OutputType.BLOCK
    }
}