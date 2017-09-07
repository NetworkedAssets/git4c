package com.networkedassets.git4c.interfaces.rest

import com.atlassian.sal.api.user.UserManager
import com.networkedassets.git4c.application.Plugin
import com.networkedassets.git4c.boundary.*
import com.networkedassets.git4c.boundary.inbound.*
import com.networkedassets.git4c.core.GetExistingRepositoryBranchesUseCase
import com.networkedassets.git4c.delivery.executor.execution.BackendDispatcher
import com.networkedassets.git4c.delivery.executor.result.ServiceApi
import com.networkedassets.git4c.utils.SerializationUtils.deserialize
import com.networkedassets.git4c.utils.dispatchAndPresentHttp
import com.networkedassets.git4c.utils.info
import org.slf4j.LoggerFactory
import java.net.URLDecoder
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response


@Path("/documentation/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class MacroRest(
        val plugin: Plugin,
        val userManager: UserManager
) : ServiceApi {


    private val log = LoggerFactory.getLogger(MacroRest::class.java)

    override val dispatcher: BackendDispatcher<Response, Response> get() = plugin.components.dispatcherHttp

    init {
        log.info { "Initializing REST for Git4C Confluence Plugin" }
    }

    @GET
    @Path("/")
    fun healthcheck(): Response {
        return dispatchAndPresentHttp { HealthCheckCommand() }
    }

    @POST
    @Path("/")
    fun createNewDocumentationsMacro(documentationJson: String): Response {
        val documentation = deserialize(documentationJson, DocumentationMacro::class.java)
        return dispatchAndPresentHttp { CreateDocumentationsMacroCommand(documentation) }
    }

    @GET
    @Path("/{uuid}")
    fun getMacro(@PathParam("uuid") macroId: String): Response {
        return dispatchAndPresentHttp { GetDocumentationsMacroByDocumentationsMacroIdQuery(macroId.urlDecode()) }
    }

    @GET
    @Path("/{uuid}/tree")
    fun getDocumentationsContentTree(@PathParam("uuid") macroId: String): Response {
        return dispatchAndPresentHttp { GetDocumentationsContentTreeByDocumentationsMacroIdQuery(macroId.urlDecode()) }
    }

    @GET
    @Path("/{uuid}/defaultBranch")
    fun getDocumentationsDefaultBranch(@PathParam("uuid") macroId: String): Response {
        return dispatchAndPresentHttp { GetDocumentationsDefaultBranchByDocumentationsMacroIdQuery(macroId.urlDecode()) }
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
    @Path("/{uuid}/globs")
    fun getGlobsForDocumentationsMacroUuid(@PathParam("uuid") macroId: String): Response {
        return dispatchAndPresentHttp { GetGlobsByDocumentationsMacroIdQuery(macroId) }
    }

    @GET
    @Path("/{uuid}/method")
    fun getMethodForDocumentationsMacroUuid(@PathParam("uuid") macroId: String): Response {
        return dispatchAndPresentHttp { GetMethodByDocumentationsMacroIdQuery(macroId) }
    }

    @POST
    @Path("/{uuid}/doc-item")
    fun getSpecificDocumentItem(@PathParam("uuid") macroId: String, documentationJson: String): Response {
        val file = deserialize(documentationJson, RequestedFile::class.java)
        return dispatchAndPresentHttp { GetDocumentItemInDocumentationsMacroQuery(macroId.urlDecode(), file.file) }
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
    @Path("/{uuid}/temporary")
    fun createTemporaryDocumentationsContent(@PathParam("uuid") macroId: String, documentationJson: String): Response {
        val documentation = deserialize(documentationJson, Branch::class.java)
        return dispatchAndPresentHttp { CreateTemporaryDocumentationsContentCommand(macroId, documentation.branch) }
    }

    @GET
    @Path("/predefine")
    fun getAllPredefinedRepositories(): Response {
        return dispatchAndPresentHttp { GetAllPredefinedRepositoriesCommand() }
    }

    @POST
    @Path("/predefine/{uuid}/files")
    fun getPredefinedRepositoryFiles(@PathParam("uuid") predefineRepositoryId: String, documentationJson: String): Response {
        val branch = deserialize(documentationJson, Branch::class.java)
        return dispatchAndPresentHttp { GetFilesForPredefinedRepositoryQuery(predefineRepositoryId, branch) }
    }

    @POST
    @Path("/predefine/{uuid}/file")
    fun getPredefinedRepositoryFile(@PathParam("uuid") predefineRepositoryId: String, documentationJson: String): Response {
        val documentation = deserialize(documentationJson, DetailsToGetFile::class.java)
        return dispatchAndPresentHttp { GetFileForPredefinedRepositoryQuery(predefineRepositoryId, documentation) }
    }

    @POST
    @Path("/predefine/{uuid}/methods")
    fun getPredefinedRepositoryMethods(@PathParam("uuid") predefineRepositoryId: String, documentationJson: String): Response {
        val documentation = deserialize(documentationJson, DetailsToGetMethods::class.java)
        return dispatchAndPresentHttp { GetMethodsForPredefinedRepositoryQuery(predefineRepositoryId, documentation) }
    }

    @POST
    @Path("/predefine")
    fun createPredefinedRepository(repositoryJson: String): Response {
        val repository = deserialize(repositoryJson, PredefinedRepository::class.java)
        return dispatchAndPresentHttp { CreatePredefinedRepositoryCommand(repository) }
    }

    @GET
    @Path("/predefine/{uuid}")
    fun getPredefinedRepository(@PathParam("uuid") repositoryId: String): Response {
        return dispatchAndPresentHttp { GetPredefinedRepositoryCommand(repositoryId) }
    }

    @GET
    @Path("/predefine/{uuid}/branches")
    fun getPredefinedRepositoryBranches(@PathParam("uuid") repositoryId: String): Response {
        return dispatchAndPresentHttp { GetPredefinedRepositoryBranchesQuery(repositoryId) }
    }

    @POST
    @Path("/predefine/{uuid}")
    fun modifyPredefinedRepository(@PathParam("uuid") repositoryId: String, repositoryJson: String): Response {
        val repository = deserialize(repositoryJson, PredefinedRepository::class.java)
        return dispatchAndPresentHttp { ModifyPredefinedRepositoryCommand(repositoryId, repository) }
    }

    @DELETE
    @Path("/predefine/{uuid}")
    fun deletePredefinedRepository(@PathParam("uuid") repositoryId: String): Response {
        return dispatchAndPresentHttp { RemovePredefinedRepositoryCommand(repositoryId) }
    }

    @POST
    @Path("/repository/branches")
    fun getBranchesForRepository(documentationJson: String): Response {
        val repositoryToGetBranches = deserialize(documentationJson, RepositoryToGetBranches::class.java)
        return dispatchAndPresentHttp { GetBranchesQuery(repositoryToGetBranches) }
    }

    @GET
    @Path("/repository/{uuid}/branches")
    fun getBranchesForExistingRepository(@PathParam("uuid") repositoryId: String): Response {
        return dispatchAndPresentHttp { GetExistingRepositoryBranchesQuery(repositoryId) }
    }

    @POST
    @Path("/repository/{uuid}/files")
    fun getFilesForExistingRepository(@PathParam("uuid") repositoryId: String, documentationJson: String): Response {
        val requestedBranch = deserialize(documentationJson, Branch::class.java)
        return dispatchAndPresentHttp { GetFilesForExistingRepositoryQuery(repositoryId, requestedBranch) }
    }

    @POST
    @Path("/repository/{uuid}/file")
    fun getFileForExistingRepository(@PathParam("uuid") repositoryId: String, documentationJson: String): Response {
        val detailsToGetFile = deserialize(documentationJson, DetailsToGetFile::class.java)
        return dispatchAndPresentHttp { GetFileForExistingRepositoryQuery(repositoryId, detailsToGetFile) }
    }

    @POST
    @Path("/repository/{uuid}/methods")
    fun getMethodsForExistingRepository(@PathParam("uuid") repositoryId: String, documentationJson: String): Response {
        val detailsToGetMethods = deserialize(documentationJson, DetailsToGetMethods::class.java)
        return dispatchAndPresentHttp { GetMethodsForExistingRepositoryQuery(repositoryId, detailsToGetMethods) }
    }

    @POST
    @Path("/repository/verify")
    fun verifyRepository(documentationJson: String): Response {
        val repositoryToVerify = deserialize(documentationJson, RepositoryToVerify::class.java)
        return dispatchAndPresentHttp { VerifyRepositoryCommand(repositoryToVerify) }
    }

    @POST
    @Path("/repository/files")
    fun getFilesForRepository(documentationJson: String): Response {
        val documentationToGetFiles = deserialize(documentationJson, RepositoryToGetFiles::class.java)
        return dispatchAndPresentHttp { GetFilesForRepositoryQuery(documentationToGetFiles) }
    }

    @POST
    @Path("/repository/file")
    fun getFileForRepository(documentationJson: String): Response {
        val documentationToGetFiles = deserialize(documentationJson, RepositoryToGetFile::class.java)
        return dispatchAndPresentHttp { GetFileForRepositoryQuery(documentationToGetFiles) }
    }

    @POST
    @Path("/repository/file/methods")
    fun getMethodsForDocumentationsMacro(documentationJson: String): Response {
        val documentationToGetMethods = deserialize(documentationJson, RepositoryToGetMethods::class.java)
        return dispatchAndPresentHttp { GetMethodsForRepositoryQuery(documentationToGetMethods) }
    }

    @POST
    @Path("/glob")
    fun defaultGlobCreate(documentationJson: String): Response {
        val defaultGlobToCreate = deserialize(documentationJson, PredefinedGlobToCreate::class.java)
        return dispatchAndPresentHttp { CreatePredefinedGlobCommand(defaultGlobToCreate) }
    }

    @DELETE
    @Path("/glob")
    fun deleteAllGlobs(): Response {
        return dispatchAndPresentHttp { DeleteAllPredefinedGlobsCommand() }
    }

    @GET
    @Path("/glob")
    fun getAllDefaultGlobs(): Response {
        return dispatchAndPresentHttp { GetAllPredefinedGlobsQuery() }
    }

    @HEAD
    @Path("/glob")
    fun restoreDefaultGlobs(): Response {
        return dispatchAndPresentHttp { RestoreDefaultPredefinedGlobsCommand() }
    }

    @DELETE
    @Path("/glob/{uuid}")
    fun deleteGlobById(@PathParam("uuid") defaultGlobId: String): Response {
        return dispatchAndPresentHttp { DeletePredefinedGlobByIdCommand(defaultGlobId) }
    }

    @GET
    @Path("/glob/{uuid}")
    fun getGlobById(@PathParam("uuid") defaultGlobId: String): Response {
        return dispatchAndPresentHttp { GetPredefinedGlobByIdQuery(defaultGlobId) }
    }

    private fun String.urlDecode() = URLDecoder.decode(this, "UTF-8")

}