package com.networkedassets.git4c.interfaces.callback

import com.atlassian.confluence.content.render.xhtml.ConversionContext
import com.atlassian.confluence.macro.Macro
import com.atlassian.confluence.macro.MacroExecutionException
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils
import com.atlassian.confluence.util.velocity.VelocityUtils
import com.fasterxml.jackson.databind.ObjectMapper
import com.networkedassets.git4c.application.Plugin
import com.networkedassets.git4c.boundary.GetDocumentationsMacroViewTemplateQuery
import com.networkedassets.git4c.boundary.ViewMacroCommand
import com.networkedassets.git4c.boundary.inbound.MacroToView
import com.networkedassets.git4c.boundary.inbound.MacroType
import com.networkedassets.git4c.boundary.inbound.PageToView
import com.networkedassets.git4c.boundary.inbound.SpaceToView
import com.networkedassets.git4c.utils.error
import com.networkedassets.git4c.utils.sendToExecution
import org.slf4j.LoggerFactory


class MacroView(val plugin: Plugin) : Macro {

    private val log = LoggerFactory.getLogger(MacroView::class.java)

    @Throws(MacroExecutionException::class)
    override fun execute(params: Map<String, String>, s: String, conversionContext: ConversionContext): String {

        if (conversionContext.outputType == "pdf" || conversionContext.outputType == "word") {
            //We don't support exporting multi file
            return ""
        }

        //Yes, it can be null in some cases
        val macroUuid = params["uuid"] ?: return ""

        try {

            val page = PageToView(conversionContext.pageContext.entity.id.toString())
            val space = SpaceToView(conversionContext.spaceKey)
            val macro = MacroToView(macroUuid, MacroType.MULTIFILE)

            sendToExecution(plugin.components.dispatching.dispatcher,
                    ViewMacroCommand(macro, page, space)
            )

            val content = sendToExecution(plugin.components.dispatching.dispatcher,
                    GetDocumentationsMacroViewTemplateQuery("macroResources")
            )


            val resourcesPath = "download/resources/com.networkedassets.git4c.confluence-plugin:macro-resources/macroResources/"
            val context = MacroUtils.defaultVelocityContext()
            context.put("macroSectionHtml", content)
            context.put("resourcesPath", resourcesPath)
            context.put("paramsJson", ObjectMapper().writeValueAsString(params))

            return VelocityUtils.getRenderedTemplate("/macroResources/macro.vm", context)
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