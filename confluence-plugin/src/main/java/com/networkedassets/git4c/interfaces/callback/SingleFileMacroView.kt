package com.networkedassets.git4c.interfaces.callback

import com.atlassian.confluence.content.render.xhtml.ConversionContext
import com.atlassian.confluence.macro.Macro
import com.atlassian.confluence.macro.MacroExecutionException
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils
import com.atlassian.confluence.util.velocity.VelocityUtils
import com.fasterxml.jackson.databind.ObjectMapper
import com.networkedassets.git4c.application.Plugin
import org.slf4j.LoggerFactory


class SingleFileMacroView(val plugin: Plugin) : Macro {

    private val log = LoggerFactory.getLogger(MacroView::class.java)

    @Throws(MacroExecutionException::class)
    override fun execute(params: Map<String, String>, s: String, conversionContext: ConversionContext): String {

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