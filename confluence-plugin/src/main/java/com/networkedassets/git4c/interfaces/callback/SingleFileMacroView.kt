package com.networkedassets.git4c.interfaces.callback

import com.atlassian.confluence.content.render.xhtml.ConversionContext
import com.atlassian.confluence.macro.Macro
import com.atlassian.confluence.macro.MacroExecutionException
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils
import com.atlassian.confluence.util.velocity.VelocityUtils
import com.fasterxml.jackson.databind.ObjectMapper
import com.networkedassets.git4c.application.Plugin
import com.networkedassets.git4c.boundary.GetDocumentationsMacroByDocumentationsMacroIdQuery
import com.networkedassets.git4c.boundary.ViewMacroCommand
import com.networkedassets.git4c.boundary.inbound.MacroToView
import com.networkedassets.git4c.boundary.inbound.MacroType
import com.networkedassets.git4c.boundary.inbound.PageToView
import com.networkedassets.git4c.boundary.inbound.SpaceToView
import com.networkedassets.git4c.delivery.executor.result.BackendPresenter
import com.networkedassets.git4c.utils.error
import com.networkedassets.git4c.utils.sendToExecution
import org.slf4j.LoggerFactory

class SingleFileMacroView(val plugin: Plugin) : Macro {

    private val log = LoggerFactory.getLogger(MacroView::class.java)

    @Throws(MacroExecutionException::class)
    override fun execute(params: Map<String, String>, s: String, conversionContext: ConversionContext): String {

        //Yes, it can be null in some cases
        val macroUuid = params["uuid"] ?: return ""

        if (conversionContext.outputType == "pdf" || conversionContext.outputType == "word") {
            //FIXME: It should be async!!!
            // TODO: Macro get in sync for PDF and a separate use case
            val query = GetDocumentationsMacroByDocumentationsMacroIdQuery(macroUuid, null)
            val emptyPresenter = object : BackendPresenter<Any, Throwable> {
                override fun render(result: Any) = result
                override fun error(exception: Throwable) = exception
            }

            val unsafeMacro = plugin.components.documentsViewCache.get(macroUuid)

            val macro = if (unsafeMacro == null) {
                plugin.components.dispatcher.sendToExecution(query, emptyPresenter).get()
                plugin.components.documentsViewCache.get(macroUuid)
            } else {
                unsafeMacro
            } ?: return "<div>Couldn't find macro</div>"
            val content = plugin.components.documentItemCache.get(macro.files[0].path)?.content ?: "<div>Couldn't find macro content</div>"
            val context = MacroUtils.defaultVelocityContext()
            context.put("code", """<div xmlns:v-on="http://www.w3.org/1999/xhtml">${content}</div>""")

            val template = if (conversionContext.outputType == "pdf") "pdfexport" else "wordexport"

            return VelocityUtils.getRenderedTemplate("/singleFile/$template.vm", context)
        }

        try {

            val page = PageToView(conversionContext.pageContext.entity.id.toString())
            val space = SpaceToView(conversionContext.spaceKey)
            val macro = MacroToView(macroUuid, MacroType.SINGLEFILE)

            sendToExecution(plugin.components.dispatcher,
                    ViewMacroCommand(macro, page, space)
            )

            val collapsible = params["collapsible"]?.toBoolean() ?: true
            val collapseByDefault = params["collapseByDefault"]?.toBoolean() ?: false
            val showLineNumbers = params["showLineNumbers"]?.toBoolean() ?: true
            val showTopBar = params["showTopBar"]?.toBoolean() ?: true
            val toc = params["toc"]?.toBoolean() ?: true
            val editingEnabled = plugin.components.pluginSettings.getFileEditingEnabled()

            val context = MacroUtils.defaultVelocityContext()
            context.put("paramsJson", ObjectMapper().writeValueAsString(params))
            context.put("UUID", params["uuid"])
            context.put("COLLAPSIBLE", collapsible)
            context.put("COLLAPSEBYDEFAULT", collapseByDefault)
            context.put("LINENUMBERS", showLineNumbers)
            context.put("SHOWTOPBAR", showTopBar)
            context.put("TOC", toc)
            context.put("RANDOM", plugin.components.idGenerator.generateNewIdentifier())
            context.put("EDITINGENABLED", editingEnabled)

            return VelocityUtils.getRenderedTemplate("/singleFile/macro.vm", context)
        } catch (e: Exception) {
            log.error({ "Error during multi file macro rendering" }, e)
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