package com.networkedassets.git4c.interfaces.callback

import com.atlassian.confluence.content.render.xhtml.ConversionContext
import com.atlassian.confluence.macro.Macro
import com.atlassian.confluence.macro.MacroExecutionException
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils
import com.atlassian.confluence.util.velocity.VelocityUtils
import com.atlassian.sal.api.user.UserManager
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.flatMap
import com.github.kittinunf.result.map
import com.networkedassets.git4c.application.Plugin
import com.networkedassets.git4c.boundary.GetDocumentItemInDocumentationsMacroQuery
import com.networkedassets.git4c.boundary.GetDocumentationsContentTreeByDocumentationsMacroIdQuery
import com.networkedassets.git4c.boundary.GetDocumentationsMacroByDocumentationsMacroIdQuery
import com.networkedassets.git4c.boundary.ViewMacroCommand
import com.networkedassets.git4c.boundary.inbound.MacroToView
import com.networkedassets.git4c.boundary.inbound.MacroType
import com.networkedassets.git4c.boundary.inbound.PageToView
import com.networkedassets.git4c.boundary.inbound.SpaceToView
import com.networkedassets.git4c.boundary.outbound.DocItem
import com.networkedassets.git4c.boundary.outbound.DocumentationsContentTree
import com.networkedassets.git4c.boundary.outbound.DocumentationsContentTree.NodeType.DOCITEM
import com.networkedassets.git4c.boundary.outbound.DocumentationsMacro
import com.networkedassets.git4c.boundary.outbound.exceptions.NotReadyException
import com.networkedassets.git4c.utils.error
import com.networkedassets.git4c.utils.sendToExecution
import org.slf4j.LoggerFactory

class SingleFileMacroView(
        val plugin: Plugin,
        val userManager: UserManager
) : Macro {

    private val log = LoggerFactory.getLogger(MacroView::class.java)

    @Throws(MacroExecutionException::class)
    override fun execute(params: Map<String, String>, s: String, conversionContext: ConversionContext): String {

        //Yes, it can be null in some cases
        val macroUuid = params["uuid"] ?: return ""

        val user = userManager.remoteUsername

        if (conversionContext.outputType == "pdf" || conversionContext.outputType == "word") {

            val context = MacroUtils.defaultVelocityContext()

            val content = getDocumentationsMacro(macroUuid, user)
                    .flatMap { getFileTree(macroUuid, user) }
                    .map { it.getFirstFile() }
                    .flatMap { getFileContent(macroUuid, it, user) }
                    .fold({
                        if (it.isEmpty()) {
                                "<div>Document is empty</div>"
                        } else {
                            "<div>$it</div>"
                        }
                    }, {
                        log.error("Exception during pdf/word converting", it)
                        "<div>Exception occured: ${it.message}</div>"
                    })

            context.put("code", """<div xmlns:v-on="http://www.w3.org/1999/xhtml">$content</div>""")

            val template = if (conversionContext.outputType == "pdf") "pdfexport" else "wordexport"

            return VelocityUtils.getRenderedTemplate("/singleFile/$template.vm", context)
        }

        try {

            val pageToView = PageToView(conversionContext.pageContext.entity.id.toString())
            val spaceToView = SpaceToView(conversionContext.spaceKey)
            val macroToView = MacroToView(macroUuid, MacroType.SINGLEFILE)

            val macroSettingsRepository = plugin.components.providers.macroSettingsProvider
            val repositoryDatabase = plugin.components.providers.repositoryProvider

            val repository = macroSettingsRepository.get(macroUuid)?.repositoryUuid?.let { repositoryDatabase.get(it) }

            val editingEnabledForRepo = repository?.isEditable == true

            sendToExecution(plugin.components.dispatching.dispatcher,
                    ViewMacroCommand(macroToView, pageToView, spaceToView)
            )

            val collapsible = params["collapsible"]?.toBoolean() ?: true
            val collapseByDefault = params["collapseByDefault"]?.toBoolean() ?: false
            val showLineNumbers = params["showLineNumbers"]?.toBoolean() ?: true
            val showTopBar = params["showTopBar"]?.toBoolean() ?: true
            val toc = params["toc"]?.toBoolean() ?: true

            val context = MacroUtils.defaultVelocityContext()
            context.put("paramsJson", ObjectMapper().writeValueAsString(params))
            context.put("UUID", params["uuid"])
            context.put("COLLAPSIBLE", collapsible)
            context.put("COLLAPSEBYDEFAULT", collapseByDefault)
            context.put("LINENUMBERS", showLineNumbers)
            context.put("SHOWTOPBAR", showTopBar)
            context.put("TOC", toc)
            context.put("RANDOM", plugin.components.utils.idGenerator.generateNewIdentifier())
            context.put("EDITINGENABLED", editingEnabledForRepo)

            return VelocityUtils.getRenderedTemplate("/singleFile/macro.vm", context)
        } catch (e: Exception) {
            log.error({ "Error during multi file macro rendering" }, e)
            throw MacroExecutionException(e)
        }
    }

    private fun getDocumentationsMacro(macroUuid: String, user: String): Result<DocumentationsMacro, Exception> {

        val query = GetDocumentationsMacroByDocumentationsMacroIdQuery(macroUuid, user)

        val executor = plugin.components.dispatching.executor

        var result: Result<DocumentationsMacro, Exception>

        do {
            Thread.sleep(200)
            result = executor.executeRequest(query)
        } while (!(result.component1() != null || result.component2() is NotReadyException))

        return result

    }

    private fun getFileTree(macroUuid: String, user: String): Result<DocumentationsContentTree, Exception> {

        val query = GetDocumentationsContentTreeByDocumentationsMacroIdQuery(macroUuid, user)

        val executor = plugin.components.dispatching.executor

        var result: Result<DocumentationsContentTree, Exception>

        do {
            Thread.sleep(200)
            result = executor.executeRequest(query)
        } while (!(result.component1() != null || result.component2() is NotReadyException))

        return result

    }

    fun getFileContent(macroUuid: String, file: String, user: String): Result<String, Exception> {

        val query = GetDocumentItemInDocumentationsMacroQuery(macroUuid, file, user)

        val executor = plugin.components.dispatching.executor

        var result: Result<DocItem, Exception>

        do {
            Thread.sleep(200)
            result = executor.executeRequest(query)
        } while (!(result.component1() != null || result.component2() is NotReadyException))

        return result.map { it.content ?: "" }
    }

    private tailrec fun DocumentationsContentTree.getFirstFile(): String {
        val tree = this
        if (tree.type == DOCITEM) {
            return tree.fullName
        } else {
            return tree.getChildren().first().getFirstFile()
        }
    }

    override fun getBodyType(): Macro.BodyType {
        return Macro.BodyType.NONE
    }

    override fun getOutputType(): Macro.OutputType {
        return Macro.OutputType.BLOCK
    }
}