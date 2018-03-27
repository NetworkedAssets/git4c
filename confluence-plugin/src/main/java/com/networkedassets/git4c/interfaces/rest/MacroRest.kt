package com.networkedassets.git4c.interfaces.rest

import com.atlassian.plugins.rest.common.security.AnonymousAllowed
import com.atlassian.sal.api.user.UserManager
import com.networkedassets.git4c.application.Plugin
import com.networkedassets.git4c.boundary.*
import com.networkedassets.git4c.boundary.inbound.*
import com.networkedassets.git4c.delivery.executor.execution.BackendDispatcher
import com.networkedassets.git4c.delivery.executor.result.ServiceApi
import com.networkedassets.git4c.utils.SerializationUtils.deserialize
import com.networkedassets.git4c.utils.dispatchAndPresentHttp
import com.networkedassets.git4c.utils.info
import org.slf4j.LoggerFactory
import java.net.URLDecoder
import java.util.concurrent.TimeUnit
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

    override val dispatcher: BackendDispatcher<Response, Response> get() = plugin.components.dispatching.dispatcherHttp

    init {
        log.info { "Initializing REST for Git4C Confluence Plugin" }
    }

    @GET
    @AnonymousAllowed
    @Path("/")
    fun healthcheck(): Response {
        return dispatchAndPresentHttp { HealthCheckCommand() }
    }

    @POST
    @Path("/creation")
    fun createNewDocumentationsMacro(documentationJson: String, @Context req: HttpServletRequest): Response {
        val documentation = deserialize(documentationJson, DocumentationMacro::class.java)
        //Anonymous is not allowed
        val username = req.getUsername()!!
        return dispatchAndPresentHttp { CreateDocumentationsMacroCommand(documentation, username) }
    }

    @GET
    @Path("/creation/result/{requestId}")
    fun getCreateNewDocumentationsMacroResult(@PathParam("requestId") requestId: String): Response {
        return dispatchAndPresentHttp { CreateDocumentationsMacroResultRequest(requestId) }
    }


    @GET
    @AnonymousAllowed
    @Path("/{uuid}")
    fun getMacro(@PathParam("uuid") macroId: String, @Context req: HttpServletRequest): Response {
        val username = req.getUsername()
        return dispatchAndPresentHttp({ GetDocumentationsMacroByDocumentationsMacroIdQuery(macroId.urlDecode(), username) }, 5, TimeUnit.MINUTES)
    }

    @GET
    @AnonymousAllowed
    @Path("/{uuid}/tree")
    fun getDocumentationsContentTree(@PathParam("uuid") macroId: String, @Context req: HttpServletRequest): Response {
        val username = req.getUsername()
        return dispatchAndPresentHttp { GetDocumentationsContentTreeByDocumentationsMacroIdQuery(macroId.urlDecode(), username) }
    }

    @GET
    @AnonymousAllowed
    @Path("/{uuid}/defaultBranch")
    fun getDocumentationsDefaultBranch(@PathParam("uuid") macroId: String, @Context req: HttpServletRequest): Response {
        val username = req.getUsername()
        return dispatchAndPresentHttp { GetDocumentationsDefaultBranchByDocumentationsMacroIdQuery(macroId.urlDecode(), username) }
    }

    @POST
    @AnonymousAllowed
    @Path("/{uuid}/file/commits")
    fun getFileCommitHistoryForDocumentationsMacroUuid(@PathParam("uuid") macroId: String, documentationJson: String, @Context req: HttpServletRequest): Response {
        val username = req.getUsername()
        val documentation = deserialize(documentationJson, DetailsToGetFile::class.java)
        return dispatchAndPresentHttp { GetCommitHistoryForFileByMacroIdQuery(macroId.urlDecode(), documentation, username) }
    }

    @GET
    @AnonymousAllowed
    @Path("/{uuid}/file/commits/result/{requestId}")
    fun getFileCommitHistoryForDocumentationsMacroUuidResult(@PathParam("requestId") requestId: String): Response {
        return dispatchAndPresentHttp { GetCommitHistoryForFileResultRequest(requestId) }
    }

    @GET
    @Path("/request/publishFile/result/{id}")
    fun publishFileResult(@PathParam("id") requestId: String): Response {
        return dispatchAndPresentHttp { PublishFileResultRequest(requestId) }
    }

    @POST
    @Path("/{uuid}/file/publishFile")
    fun publishFile(@PathParam("uuid") macroId: String, documentationJson: String, @Context req: HttpServletRequest): Response {
        val username = req.getUsername()
        val file = deserialize(documentationJson, FileToSave::class.java)
        return dispatchAndPresentHttp { PublishFileCommand(username, macroId, file) }
    }

    @POST
    @AnonymousAllowed
    @Path("/{uuid}/file/preview")
    fun previewFile(@PathParam("uuid") macroId: String, documentationJson: String, @Context req: HttpServletRequest): Response {
        val username = req.getUsername()
        val file = deserialize(documentationJson, FileToGeneratePreview::class.java)
        return dispatchAndPresentHttp { PreviewFileCommand(username, macroId, file) }
    }

    @GET
    @AnonymousAllowed
    @Path("/{uuid}/file/preview/result/{requestId}")
    fun previewFileResult(@PathParam("requestId") requestId: String): Response {
        return dispatchAndPresentHttp { PreviewFileResultRequest(requestId) }
    }

    @POST
    @AnonymousAllowed
    @Path("/{uuid}/refresh")
    fun forceRefreshExistingDocumentationsMacro(@PathParam("uuid") macroId: String, @Context req: HttpServletRequest): Response {
        val username = req.getUsername()
        return dispatchAndPresentHttp { RefreshDocumentationsMacroCommand(macroId.urlDecode(), username) }
    }

    @POST
    @AnonymousAllowed
    @Path("/{uuid}/branches")
    fun getBranchesForDocumentationsMacroUuid(@PathParam("uuid") macroId: String, @Context req: HttpServletRequest): Response {
        val username = req.getUsername()
        return dispatchAndPresentHttp { GetBranchesByDocumentationsMacroIdQuery(macroId, username) }
    }

    @GET
    @AnonymousAllowed
    @Path("/{uuid}/branches/result/{requestId}")
    fun getBranchesForDocumentationsMacroUuidResult(@PathParam("requestId") requestId: String): Response {
        return dispatchAndPresentHttp { GetBranchesByDocumentationsMacroIdResultRequest(requestId) }
    }

    @GET
    @AnonymousAllowed
    @Path("/{uuid}/globs")
    fun getGlobsForDocumentationsMacroUuid(@PathParam("uuid") macroId: String, @Context req: HttpServletRequest): Response {
        val username = req.getUsername()
        return dispatchAndPresentHttp { GetGlobsByDocumentationsMacroIdQuery(macroId, username) }
    }

    @GET
    @AnonymousAllowed
    @Path("/{uuid}/extractorData")
    fun getExtractorDataForDocumentationsMacroUuid(@PathParam("uuid") macroId: String, @Context req: HttpServletRequest): Response {
        val username = req.getUsername()
        return dispatchAndPresentHttp { GetExtractionDataByDocumentationsMacroIdQuery(macroId, username) }
    }

    @POST
    @AnonymousAllowed
    @Path("/{uuid}/latestRevision")
    fun getLatestRevisionForDocumentationsMacroUuid(@PathParam("uuid") macroId: String, @Context req: HttpServletRequest): Response {
        val username = req.getUsername()
        return dispatchAndPresentHttp({ GetLatestRevisionByDocumentationsMacroIdQuery(macroId, username) })
    }

    @GET
    @AnonymousAllowed
    @Path("/{uuid}/latestRevision/result/{requestId}")
    fun getLatestRevisionForDocumentationsMacroUuidResult(@PathParam("requestId") requestId: String): Response {
        return dispatchAndPresentHttp({ GetLatestRevisionByDocumentationsMacroIdResultRequest(requestId) })
    }

    @POST
    @AnonymousAllowed
    @Path("/{uuid}/doc-item")
    fun getSpecificDocumentItem(@PathParam("uuid") macroId: String, documentationJson: String, @Context req: HttpServletRequest): Response {
        val username = req.getUsername()
        val file = deserialize(documentationJson, RequestedFile::class.java)
        return dispatchAndPresentHttp { GetDocumentItemInDocumentationsMacroQuery(macroId.urlDecode(), file.file, username) }
    }

    @POST
    @AnonymousAllowed
    @Path("/{uuid}/editBranch")
    fun getTemporaryEditBranch(@PathParam("uuid") macroId: String): Response {
        return dispatchAndPresentHttp { GetTemporaryEditBranchCommand(macroId) }
    }

    @GET
    @AnonymousAllowed
    @Path("/{uuid}/editBranch/{requestId}")
    fun getTemporaryEditBranchResult(@PathParam("requestId") requestId: String): Response {
        return dispatchAndPresentHttp { GetTemporaryEditBranchResultCommand(requestId) }
    }

    @POST
    @Path("/{uuid}/verify")
    fun verifyDocumentationByDocumentationMacroUuid(@PathParam("uuid") macroId: String): Response {
        return dispatchAndPresentHttp { VerifyDocumentationMacroByDocumentationsMacroIdQuery(macroId) }
    }

    @GET
    @Path("/{uuid}/verify/result/{requestId}")
    fun verifyDocumentationByDocumentationMacroUuidResult(@PathParam("requestId") requestId: String): Response {
        return dispatchAndPresentHttp { VerifyDocumentationMacroByDocumentationsMacroIdResultRequest(requestId) }
    }

    @GET
    @AnonymousAllowed
    @Path("/{uuid}/repository")
    fun getRepositoryInfoForMacro(@PathParam("uuid") uuid: String, @Context req: HttpServletRequest): Response {
        val username = req.getUsername()
        return dispatchAndPresentHttp { GetRepositoryInfoForMacroCommand(uuid, username) }
    }

    @DELETE
    @Path("/remove/all")
    fun removeAllData(@Context req: HttpServletRequest): Response {
        val username = req.getUsername()

        if (username == null || !userManager.isAdmin(username)) {
            return Response.status(Response.Status.FORBIDDEN).build()
        }

        return dispatchAndPresentHttp { RemoveAllDataCommand() }
    }

    @DELETE
    @Path("/remove/unused")
    fun removeUnusedData(@Context req: HttpServletRequest): Response {
        val username = req.getUsername()

        if (username == null || !userManager.isAdmin(username)) {
            return Response.status(Response.Status.FORBIDDEN).build()
        }

        return dispatchAndPresentHttp { RemoveUnusedDataCommand() }
    }

    @POST
    @AnonymousAllowed
    @Path("/{uuid}/temporary")
    fun createTemporaryDocumentationsContent(@PathParam("uuid") macroId: String, documentationJson: String, @Context req: HttpServletRequest): Response {
        val username = req.getUsername()
        val documentation = deserialize(documentationJson, Branch::class.java)
        return dispatchAndPresentHttp({ CreateTemporaryDocumentationsContentCommand(macroId, documentation.branch, username) }, 5, TimeUnit.MINUTES)
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
        return dispatchAndPresentHttp { GetFilesListForPredefinedRepositoryQuery(predefineRepositoryId, branch) }
    }

    @GET
    @Path("/predefine/{uuid}/files/result/{requestId}")
    fun getPredefinedRepositoryFilesResult(@PathParam("requestId") requestId: String): Response {
        return dispatchAndPresentHttp { GetFilesListForPredefinedRepositoryResultRequest(requestId) }
    }

    @POST
    @Path("/predefine/{uuid}/file")
    fun getPredefinedRepositoryFile(@PathParam("uuid") predefineRepositoryId: String, documentationJson: String): Response {
        val documentation = deserialize(documentationJson, DetailsToGetFile::class.java)
        return dispatchAndPresentHttp { GetFileContentForPredefinedRepositoryQuery(predefineRepositoryId, documentation) }
    }

    @GET
    @Path("/predefine/{uuid}/file/result/{requestId}")
    fun getPredefinedRepositoryFileResult(@PathParam("requestId") requestId: String): Response {
        return dispatchAndPresentHttp { GetFileContentForPredefinedRepositoryResultRequest(requestId) }
    }

    @POST
    @Path("/predefine/{uuid}/methods")
    fun getPredefinedRepositoryMethods(@PathParam("uuid") predefineRepositoryId: String, documentationJson: String): Response {
        val documentation = deserialize(documentationJson, DetailsToGetMethods::class.java)
        return dispatchAndPresentHttp { GetMethodsForPredefinedRepositoryQuery(predefineRepositoryId, documentation) }
    }

    @GET
    @Path("/predefine/{uuid}/methods/result/{requestId}")
    fun getPredefinedRepositoryMethodsResult(@PathParam("requestId") requestId: String): Response {
        return dispatchAndPresentHttp { GetMethodsForPredefinedRepositoryResultRequest(requestId) }
    }

    @POST
    @Path("/predefine")
    fun createPredefinedRepository(repositoryJson: String): Response {
        val repository = deserialize(repositoryJson, PredefinedRepository::class.java)
        return dispatchAndPresentHttp { CreatePredefinedRepositoryCommand(repository) }
    }

    @GET
    @Path("/predefine/result/{requestId}")
    fun createPredefinedRepositoryResult(@PathParam("requestId") requestId: String): Response {
        return dispatchAndPresentHttp { CreatePredefinedRepositoryResultRequest(requestId) }
    }

    @POST
    @Path("/predefine/{uuid}/modify")
    fun modifyPredefinedRepository(@PathParam("uuid") repositoryId: String, repositoryJson: String): Response {
        val repository = deserialize(repositoryJson, PredefinedRepository::class.java)
        return dispatchAndPresentHttp { ModifyPredefinedRepositoryCommand(repositoryId, repository) }
    }

    @GET
    @Path("/predefine/{uuid}/modify/result/{requestId}")
    fun modifyPredefinedRepository(@PathParam("requestId") requestId: String): Response {
        return dispatchAndPresentHttp { ModifyPredefinedRepositoryResultRequest(requestId) }
    }

    @POST
    @Path("/predefine/{uuid}/details")
    fun getPredefinedRepository(@PathParam("uuid") repositoryId: String): Response {
        return dispatchAndPresentHttp { GetPredefinedRepositoryCommand(repositoryId) }
    }

    @GET
    @Path("/predefine/{uuid}/details/result/{requestId}")
    fun getPredefinedRepositoryResult(@PathParam("requestId") requestId: String): Response {
        return dispatchAndPresentHttp { GetPredefinedRepositoryResultRequest(requestId) }
    }

    @POST
    @Path("/predefine/{uuid}/branches")
    fun getPredefinedRepositoryBranches(@PathParam("uuid") repositoryId: String): Response {
        return dispatchAndPresentHttp { GetPredefinedRepositoryBranchesQuery(repositoryId) }
    }

    @GET
    @Path("/predefine/{uuid}/branches/result/{requestId}")
    fun getPredefinedRepositoryBranchesResult(@PathParam("requestId") requestId: String): Response {
        return dispatchAndPresentHttp { GetPredefinedRepositoryBranchesResultRequest(requestId) }
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
    @Path("/repository/branches/result/{requestId}")
    fun getBranchesForRepositoryResult(@PathParam("requestId") requestId: String): Response {
        return dispatchAndPresentHttp { GetBranchesResultRequest(requestId) }
    }

    @POST
    @Path("/repository/{uuid}/branches")
    fun getBranchesForExistingRepository(@PathParam("uuid") repositoryId: String): Response {
        return dispatchAndPresentHttp { GetExistingRepositoryBranchesQuery(repositoryId) }
    }

    @GET
    @Path("/repository/{uuid}/branches/result/{requestId}")
    fun getBranchesForExistingRepositoryResult(@PathParam("requestId") requestId: String): Response {
        return dispatchAndPresentHttp { GetExistingRepositoryBranchesResultRequest(requestId) }
    }

    @POST
    @Path("/repository/{uuid}/files")
    fun getFilesForExistingRepository(@PathParam("uuid") repositoryId: String, documentationJson: String): Response {
        val requestedBranch = deserialize(documentationJson, Branch::class.java)
        return dispatchAndPresentHttp { GetFilesListForExistingRepositoryQuery(repositoryId, requestedBranch) }
    }

    @GET
    @Path("/repository/{uuid}/files/result/{requestId}")
    fun getFilesForExistingRepositoryResult(@PathParam("requestId") requestId: String): Response {
        return dispatchAndPresentHttp { GetFilesListForExistingRepositoryResultRequest(requestId) }
    }


    @POST
    @Path("/repository/{uuid}/file")
    fun getFileForExistingRepository(@PathParam("uuid") repositoryId: String, documentationJson: String): Response {
        val detailsToGetFile = deserialize(documentationJson, DetailsToGetFile::class.java)
        return dispatchAndPresentHttp { GetFileContentForExistingRepositoryQuery(repositoryId, detailsToGetFile) }
    }

    @GET
    @Path("/repository/{uuid}/file/result/{requestId}")
    fun getFileForExistingRepositoryResult(@PathParam("requestId") requestId: String): Response {
        return dispatchAndPresentHttp { GetFileContentForExistingRepositoryResultRequest(requestId) }
    }

    @POST
    @Path("/repository/{uuid}/methods")
    fun getMethodsForExistingRepository(@PathParam("uuid") repositoryId: String, documentationJson: String): Response {
        val detailsToGetMethods = deserialize(documentationJson, DetailsToGetMethods::class.java)
        return dispatchAndPresentHttp { GetMethodsForExistingRepositoryQuery(repositoryId, detailsToGetMethods) }
    }

    @GET
    @Path("/repository/{uuid}/methods/result/{requestId}")
    fun getMethodsForExistingRepository(@PathParam("requestId") requestId: String): Response {
        return dispatchAndPresentHttp { GetMethodsForExistingRepositoryResultRequest(requestId) }
    }

    @POST
    @Path("/repository/verify")
    fun verifyRepository(documentationJson: String): Response {
        val repositoryToVerify = deserialize(documentationJson, RepositoryToVerify::class.java)
        return dispatchAndPresentHttp { VerifyRepositoryCommand(repositoryToVerify) }
    }

    @POST
    @Path("/repository/verify/result/{requestId}")
    fun verifyRepositoryResult(@PathParam("requestId") requestId: String): Response {
        return dispatchAndPresentHttp { VerifyRepositoryResultRequest(requestId) }
    }

    @POST
    @Path("/repository/files")
    fun getFilesForRepository(documentationJson: String): Response {
        val documentationToGetFiles = deserialize(documentationJson, RepositoryToGetFiles::class.java)
        return dispatchAndPresentHttp { GetFilesListForRepositoryQuery(documentationToGetFiles) }
    }

    @GET
    @Path("/repository/files/result/{requestId}")
    fun getFilesForRepositoryResult(@PathParam("requestId") requestId: String): Response {
        return dispatchAndPresentHttp { GetFilesListForRepositoryResultRequest(requestId) }
    }

    @POST
    @Path("/repository/file")
    fun getFileForRepository(documentationJson: String): Response {
        val documentationToGetFiles = deserialize(documentationJson, RepositoryToGetFile::class.java)
        return dispatchAndPresentHttp { GetFileContentForRepositoryQuery(documentationToGetFiles) }
    }

    @GET
    @Path("/repository/file/result/{requestId}")
    fun getFileForRepositoryResult(@PathParam("requestId") requestId: String): Response {
        return dispatchAndPresentHttp { GetFileContentForRepositoryResultRequest(requestId) }
    }

    @POST
    @Path("/repository/file/methods")
    fun getMethodsForDocumentationsMacro(documentationJson: String): Response {
        val documentationToGetMethods = deserialize(documentationJson, RepositoryToGetMethods::class.java)
        return dispatchAndPresentHttp { GetMethodsForRepositoryQuery(documentationToGetMethods) }
    }

    @GET
    @Path("/repository/file/methods/result/{requestId}")
    fun getMethodsForDocumentationsMacroResult(@PathParam("requestId") requestId: String): Response {
        return dispatchAndPresentHttp { GetMethodsForRepositoryResultRequest(requestId) }
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

    @POST
    @Path("/spaces")
    fun getAllSpacesWithGit4CMacro(): Response {
        return dispatchAndPresentHttp { GetSpacesWithMacroQuery() }
    }

    @GET
    @Path("/spaces/result/{uuid}")
    fun getAllSpacesWithGit4CMacroResult(@PathParam("uuid") id: String): Response {
        return dispatchAndPresentHttp { GetSpacesWithMacroResultRequest(id) }
    }

    @POST
    @Path("/settings/repository/predefine/force")
    fun forceUsersToUsePredefinedRepositiores(documentationJson: String): Response {
        val toForce = deserialize(documentationJson, ForcePredefinedRepositoriesInfo::class.java)
        return dispatchAndPresentHttp { ForceUsersToUsePredefinedRepositoriesCommand(toForce) }
    }

    @GET
    @Path("/settings/repository/predefine/force")
    fun forceUsersToUsePredefinedRepositiores(): Response {
        return dispatchAndPresentHttp { GetForceUsersToUsePredefinedRepositoriesSettingQuery() }
    }

    @GET
    @Path("/repository/usages")
    fun getRepoositoryUsagesForUser(@Context req: HttpServletRequest): Response {
        //Anonymous it not allowed
        val username = req.getUsername()!!
        return dispatchAndPresentHttp { GetRepositoryUsagesForUserQuery(username) }
    }

    @POST
    @Path("/database/macroLocation/refresh")
    fun refreshMacroLocationDatabase(@Context req: HttpServletRequest): Response {

        val username = req.getUsername()

        if (username == null || !userManager.isAdmin(username)) {
            return Response.status(Response.Status.FORBIDDEN).build()
        }

        return dispatchAndPresentHttp { RefreshMacroLocationsCommand() }
    }

    @GET
    @Path("/database/macroLocation/refresh/{uuid}")
    fun refreshMacroLocationDatabaseResult(@Context req: HttpServletRequest, @PathParam("uuid") uuid: String): Response {

        val username = req.getUsername()

        if (username == null || !userManager.isAdmin(username)) {
            return Response.status(Response.Status.FORBIDDEN).build()
        }

        return dispatchAndPresentHttp { RefreshMacroLocationsResultCommand(uuid) }

    }

    @GET
    @Path("/executors")
    fun getExecutorThreadNumbers(@Context req: HttpServletRequest): Response {
        val username = req.getUsername()

        if (username == null || !userManager.isAdmin(username)) {
            return Response.status(Response.Status.FORBIDDEN).build()
        }

        return dispatchAndPresentHttp { GetExecutorThreadNumbersQuery() }

    }

    @POST
    @Path("/executors")
    fun updateExecutorThreadNumbers(@Context req: HttpServletRequest, json: String): Response? {

        val username = req.getUsername()

        if (username == null || !userManager.isAdmin(username)) {
            return Response.status(Response.Status.FORBIDDEN).build()
        }

        val data = deserialize(json, ExecutorThreadNumbersIn::class.java)

        return dispatchAndPresentHttp { SaveExecutorThreadNumbersQuery(data) }
    }


    private fun String.urlDecode() = URLDecoder.decode(this, "UTF-8")

    private fun HttpServletRequest.getUsername(): String? = userManager.getRemoteUsername(this)

}