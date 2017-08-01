package com.networkedassets.git4c.interfaces.rest

import com.atlassian.sal.api.user.UserManager
import com.atlassian.templaterenderer.TemplateRenderer
import com.atlassian.webresource.api.assembler.PageBuilderService
import com.networkedassets.git4c.application.Plugin
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AdminServlet(val userManager: UserManager,
                   val plugin: Plugin,
                   val renderer: TemplateRenderer,
                   val pageBuilderService: PageBuilderService
) : HttpServlet() {

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {

        val username = userManager.getRemoteUsername(req)

        if (username == null || !userManager.isAdmin(username)) {
            resp.setStatus(403)
            return
        }

        resp.contentType = "text/html;charset=utf-8"

        pageBuilderService.assembler().resources()
                .requireContext("com.networkedassets.git4c.ajscompat")
                .requireContext("com.networkedassets.git4c.adminResources")

        renderer.render("admin.vm", resp.writer)
    }
}