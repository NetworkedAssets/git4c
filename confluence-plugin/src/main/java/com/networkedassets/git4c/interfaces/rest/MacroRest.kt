package com.networkedassets.git4c.interfaces.rest

import com.atlassian.sal.api.user.UserManager
import com.networkedassets.git4c.application.Plugin
import com.networkedassets.git4c.boundary.*
import com.networkedassets.git4c.boundary.inbound.DocumentationMacroToCreate
import com.networkedassets.git4c.boundary.inbound.DocumentationsMacroToChangeBranch
import com.networkedassets.git4c.boundary.inbound.DocumentationToGetBranches
import com.networkedassets.git4c.utils.SerializationUtils.deserialize
import com.networkedassets.git4c.delivery.executor.execution.BackendDispatcher
import com.networkedassets.git4c.delivery.executor.result.ServiceApi
import com.networkedassets.git4c.utils.dispatchAndPresentHttp
import com.networkedassets.git4c.utils.info
import org.slf4j.LoggerFactory
import javax.servlet.http.HttpServletRequest
import java.net.URLDecoder
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response


@Path("/documentation/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class MacroRest(val plugin: Plugin,
                val userManager: UserManager) : ServiceApi {


    private val log = LoggerFactory.getLogger(MacroRest::class.java)

    override val dispatcher: BackendDispatcher<Response, Response> get() = plugin.components.dispatcherHttp

    init {
        log.info { "Initializing REST for Git4C Confluence Plugin" }
    }

    @GET
    @Path("/{uuid}")
    operator fun get(@PathParam("uuid") macroId: String): Response {
        return dispatchAndPresentHttp { GetDocumentationsMacroByDocumentationsMacroIdQuery(macroId.urlDecode()) }
    }

    @GET
    @Path("/{uuid}/tree")
    fun getDocumentationsContentTree(@PathParam("uuid") macroId: String): Response {
        return dispatchAndPresentHttp { GetDocumentationsContentTreeByDocumentationsMacroIdQuery(macroId.urlDecode()) }
    }

    @POST
    @Path("/")
    fun createNewDocumentationsMacro(documentationJson: String): Response {
        val documentation = deserialize(documentationJson, DocumentationMacroToCreate::class.java)
        return dispatchAndPresentHttp { CreateDocumentationsMacroCommand(documentation) }
    }

    @GET
    @Path("/")
    fun healthcheck(documentationJson: String): Response {
        return dispatchAndPresentHttp { HealthCheckCommand() }
    }

    @POST
    @Path("/getBranches")
    fun getBranchesForDocumentationsMacro(documentationJson: String): Response {
        val documentationToGetBranches = deserialize(documentationJson, DocumentationToGetBranches::class.java)
        return dispatchAndPresentHttp { GetBranchesQuery(documentationToGetBranches) }
    }

    @POST
    @Path("/{uuid}/refresh")
    fun forceRefreshExistingDocumentationsMacro(@PathParam("uuid") macroId: String): Response {
        return dispatchAndPresentHttp { RefreshDocumentationsMacroCommand(macroId.urlDecode()) }
    }

    @GET
    @Path("/{uuid}/branches")
    fun getBranchesForDocumentationsMacroUuid(@PathParam("uuid") macroId: String): Response {
        return dispatchAndPresentHttp { GetBranchesByDocumentationsMacroIdQuery(macroId) }
    }

    @GET
    @Path("/{uuid}/doc-items/{documentId}")
    fun getSpecificDocumentItem(@PathParam("uuid") macroId: String, @PathParam("documentId") documentId: String): Response {
        return dispatchAndPresentHttp { GetDocumentItemInDocumentationsMacroQuery(macroId.urlDecode(), documentId.urlDecode()) }
    }

    @DELETE
    @Path("/")
    fun removeAllData(@Context req: HttpServletRequest): Response {
        val username = userManager.getRemoteUsername(req)

        if (username == null || !userManager.isAdmin(username)) {
            return Response.status(Response.Status.FORBIDDEN).build()
        }

        return dispatchAndPresentHttp { RemoveAllDataCommand() }
    }

    @POST
    @Path("/{uuid}/createTemporary")
    fun createTemporaryDocumentationsContent(@PathParam("uuid") macroId: String, documentationJson: String): Response {
        val documentation = deserialize(documentationJson, DocumentationsMacroToChangeBranch::class.java)
        return dispatchAndPresentHttp { CreateTemporaryDocumentationsContentCommand(macroId, documentation.branch) }
    }

    private fun String.urlDecode() = URLDecoder.decode(this, "UTF-8")

}