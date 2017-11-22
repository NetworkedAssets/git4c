# 3. Context and scope




## 3.1 Business context

![](./images/3.1%20Business%20Context.puml)
  

## 3.2 Technical context

Git4C backend offers various endpoints to obtain and process information about git repositories and documentation.


```
    @GET
    @Path("/")
    fun healthcheck(): Response
```


```
    @POST
    @Path("/")
    fun createNewDocumentationsMacro(documentationJson: String): Response
```

```
    @GET
    @Path("/{uuid}")
    fun getMacro(@PathParam("uuid") macroId: String): Response
```

```
    @GET
    @Path("/{uuid}/tree")
    fun getDocumentationsContentTree(@PathParam("uuid") macroId: String): Response
```

```
    @GET
    @Path("/{uuid}/defaultBranch")
    fun getDocumentationsDefaultBranch(@PathParam("uuid") macroId: String): Response
```

```
    @POST
    @Path("/{uuid}/file/commits")
    fun getFileCommitHistoryForDocumentationsMacroUuid(@PathParam("uuid") macroId: String, documentationJson: String): Response
```

```
    @POST
    @Path("/{uuid}/refresh")
    fun forceRefreshExistingDocumentationsMacro(@PathParam("uuid") macroId: String): Response
```

```
    @GET
    @Path("/{uuid}/branches")
    fun getBranchesForDocumentationsMacroUuid(@PathParam("uuid") macroId: String): Response

    @GET
    @Path("/{uuid}/globs")
    fun getGlobsForDocumentationsMacroUuid(@PathParam("uuid") macroId: String): Response
```

```
    @GET
    @Path("/{uuid}/extractorData")
    fun getExtractorDataForDocumentationsMacroUuid(@PathParam("uuid") macroId: String): Response
```

```
    @POST
    @Path("/{uuid}/doc-item")
    fun getSpecificDocumentItem(@PathParam("uuid") macroId: String, documentationJson: String): Response
```

```
    @GET
    @Path("/{uuid}/verify")
    fun verifyDocumentationByDocumentationMacroUuid(@PathParam("uuid") macroId: String): Response
```

```
    @DELETE
    @Path("/")
    fun removeAllData(@Context req: HttpServletRequest): Response
```

```
    @DELETE
    @Path("/unused")
    fun removeUnusedData(@Context req: HttpServletRequest): Response
```

```
    @POST
    @Path("/{uuid}/temporary")
    fun createTemporaryDocumentationsContent(@PathParam("uuid") macroId: String, documentationJson: String): Response
```

```
    @GET
    @Path("/predefine")
    fun getAllPredefinedRepositories(): Response
```

```
    @POST
    @Path("/predefine/{uuid}/files")
    fun getPredefinedRepositoryFiles(@PathParam("uuid") predefineRepositoryId: String, documentationJson: String): Response
```

```
    @POST
    @Path("/predefine/{uuid}/file")
    fun getPredefinedRepositoryFile(@PathParam("uuid") predefineRepositoryId: String, documentationJson: String): Response
```

```
    @POST
    @Path("/predefine/{uuid}/methods")
    fun getPredefinedRepositoryMethods(@PathParam("uuid") predefineRepositoryId: String, documentationJson: String): Response
```

```
    @POST
    @Path("/predefine")
    fun createPredefinedRepository(repositoryJson: String): Response
```

```
    @GET
    @Path("/predefine/{uuid}")
    fun getPredefinedRepository(@PathParam("uuid") repositoryId: String): Response
```

```
    @GET
    @Path("/predefine/{uuid}/branches")
    fun getPredefinedRepositoryBranches(@PathParam("uuid") repositoryId: String): Response
```

```
    @POST
    @Path("/predefine/{uuid}")
    fun modifyPredefinedRepository(@PathParam("uuid") repositoryId: String, repositoryJson: String): Response
```

```
    @DELETE
    @Path("/predefine/{uuid}")
    fun deletePredefinedRepository(@PathParam("uuid") repositoryId: String): Response
```

```
    @POST
    @Path("/repository/branches")
    fun getBranchesForRepository(documentationJson: String): Response {
```

```
    @GET
    @Path("/repository/{uuid}/branches")
    fun getBranchesForExistingRepository(@PathParam("uuid") repositoryId: String): Response
```

```
    @POST
    @Path("/repository/{uuid}/files")
    fun getFilesForExistingRepository(@PathParam("uuid") repositoryId: String, documentationJson: String): Response
```

```
    @POST
    @Path("/repository/{uuid}/file")
    fun getFileForExistingRepository(@PathParam("uuid") repositoryId: String, documentationJson: String): Response
```

```
    @POST
    @Path("/repository/{uuid}/methods")
    fun getMethodsForExistingRepository(@PathParam("uuid") repositoryId: String, documentationJson: String): Response
```

```
    @POST
    @Path("/repository/verify")
    fun verifyRepository(documentationJson: String): Response
```

```
    @POST
    @Path("/repository/files")
    fun getFilesForRepository(documentationJson: String): Response
```

```
    @POST
    @Path("/repository/file")
    fun getFileForRepository(documentationJson: String): Response
```

```
    @POST
    @Path("/repository/file/methods")
    fun getMethodsForDocumentationsMacro(documentationJson: String): Response
```

```
    @POST
    @Path("/glob")
    fun defaultGlobCreate(documentationJson: String): Response
```

```
    @DELETE
    @Path("/glob")
    fun deleteAllGlobs(): Response
```

```
    @GET
    @Path("/glob")
    fun getAllDefaultGlobs(): Response
```

```
    @HEAD
    @Path("/glob")
    fun restoreDefaultGlobs(): Response
```

```
    @DELETE
    @Path("/glob/{uuid}")
    fun deleteGlobById(@PathParam("uuid") defaultGlobId: String): Response
```

```
    @GET
    @Path("/glob/{uuid}")
    fun getGlobById(@PathParam("uuid") defaultGlobId: String): Response
```

```
    @GET
    @Path("/spaces")
    fun getAllSpacesWithGit4CMacro(): Response
```

```
    @POST
    @Path("/settings/repository/predefine/force")
    fun forceUsersToUsePredefinedRepositiores(documentationJson: String): Response
```

```
    @GET
    @Path("/settings/repository/predefine/force")
    fun getForceUsersToUsePredefinedRepositioresSetting(): Response
```
  


