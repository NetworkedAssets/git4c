# 6. Runtime View

## 6.1 Macro functionality

### 6.1.1 General macro informations

#### 6.1.1.1 Get information about macro


The endpoint for getting a general mecro information is:

```
GET {git4c-backend-url}/{uuid}
```


##### Example request and response
```
Request URL:
http://naatlas-confluence.openstack.local:8090/rest/doc/1.0/documentation/13e022d6cb1442c9ab7c8148bbd9c090

Request Method:
    GET

Request Headers:
    GET /rest/doc/1.0/documentation/13e022d6cb1442c9ab7c8148bbd9c090 HTTP/1.1
    Host: naatlas-confluence.openstack.local:8090
    Connection: keep-alive
    Accept: application/json, text/plain, */*
    X-Requested-With: XMLHttpRequest
    User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36
    Referer: http://naatlas-confluence.openstack.local:8090/display/TS/Markup-test
    Accept-Encoding: gzip, deflate
    Accept-Language: pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Cookie: JSESSIONID=13F1F830FE60CA13C502A0A3A326293B

Response Status Code:
    200 OK

Response Body:
{
    "uuid": "13e022d6cb1442c9ab7c8148bbd9c090",
    "repositoryUuid": "ee0024db48f2449a94fa7938d373d8dd",
    "currentBranch": "feature/ci-dashboard",
    "revision": "b257abf762b85532883a22f115804cfc1789c841"
}
```


### 6.1.2 Detailed macro infrmations


#### 6.1.2.1 Get Branches

This endpoint provides you with the list of branches and current branch specified by the macro creator as default.


```
GET {git4c-backend-url}/{uuid}/branches
```

##### Example request and response
```
Request URL:
http://naatlas-confluence.openstack.local:8090/rest/doc/1.0/documentation/13e022d6cb1442c9ab7c8148bbd9c090/branches

Request Method:
    GET

Request Headers:
    GET /rest/doc/1.0/documentation/13e022d6cb1442c9ab7c8148bbd9c090/branches HTTP/1.1
    Host: naatlas-confluence.openstack.local:8090
    Connection: keep-alive
    Accept: application/json, text/plain, */*
    X-Requested-With: XMLHttpRequest
    User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36
    Referer: http://naatlas-confluence.openstack.local:8090/display/TS/Markup-test?src=contextnavpagetreemode
    Accept-Encoding: gzip, deflate
    Accept-Language: pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Cookie: JSESSIONID=13F1F830FE60CA13C502A0A3A326293B

Response Status Code:
    200 OK

Response Body:
{
    "currentBranch": "master",
    "allBranches": [
        "feature/lso",
        "feature/testBranch",
        "master",
        "interval_test"
    ]
}
```

#### 6.1.2.2 Default Branch

This endpoint provides you only with the name of default branch specified while macro creation.

```
GET {git4c-backend-url}/{uuid}/defaultBranch
```

##### Example request and response
```
Request URL:
http://naatlas-confluence.openstack.local:8090/rest/doc/1.0/documentation/13e022d6cb1442c9ab7c8148bbd9c090/defaultBranch

Request Method:
    GET

Request Headers:
    GET /rest/doc/1.0/documentation/1ec5fda1aa3a4985bd710a77f2886e08/defaultBranch HTTP/1.1
    Host: naatlas-confluence.openstack.local:8090
    Connection: keep-alive
    Accept: application/json, text/plain, */*
    X-Requested-With: XMLHttpRequest
    User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36
    Referer: http://naatlas-confluence.openstack.local:8090/display/TS/Repo?src=contextnavpagetreemode
    Accept-Encoding: gzip, deflate
    Accept-Language: pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Cookie: JSESSIONID=13F1F830FE60CA13C502A0A3A326293B

Response Status Code:
    200 OK

Response Body:
{
    "currentBranch": "master",
    "allBranches":[]
}
```

#### 6.1.2.3 Get content file tree

This endpoint will provide you with the tree of files from specified in macro repository and branch

```
GET {git4c-backend-url}/{uuid}/tree
```

##### Example request and response

```
Request URL:
http://naatlas-confluence.openstack.local:8090/rest/doc/1.0/documentation/13e022d6cb1442c9ab7c8148bbd9c090

Request Method:
    GET

Request Headers:
    GET /rest/doc/1.0/documentation/13e022d6cb1442c9ab7c8148bbd9c090/tree HTTP/1.1
    Host: naatlas-confluence.openstack.local:8090
    Connection: keep-alive
    Accept: application/json, text/plain, */*
    X-Requested-With: XMLHttpRequest
    User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36
    Referer: http://naatlas-confluence.openstack.local:8090/display/TS/Markup-test
    Accept-Encoding: gzip, deflate
    Accept-Language: pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Cookie: JSESSIONID=13F1F830FE60CA13C502A0A3A326293B

Response Status Code:
    200 OK

Response Body:
{
    "name": "",
    "fullName": "",
    "type": "DIR",
    "children":
    [
        {
            "name": "README.md",
            "fullName": "README.md",
            "type": "DOCITEM",
            "children":[]
        }
    ]
}
```

#### 6.1.2.4 Get specific document item
Git4C backend allows you to request specific document item from repository defined in macro. Result of this request contains many information about file and  it's content.

```
POST {git4c-backend-url}/{uuid}/doc-item
```

##### Example request and response
```
Request URL:
    http://naatlas-confluence.openstack.local:8090/rest/doc/1.0/documentation/13e022d6cb1442c9ab7c8148bbd9c090/doc-item


Request Method:
    POST

Request Headers:
    POST /rest/doc/1.0/documentation/13e022d6cb1442c9ab7c8148bbd9c090/doc-item HTTP/1.1
    Host: naatlas-confluence.openstack.local:8090
    Connection: keep-alive
    Content-Length: 59
    Accept: application/json, text/plain, */*
    Origin: http://naatlas-confluence.openstack.local:8090
    X-Requested-With: XMLHttpRequest
    User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36
    Content-Type: application/json;charset=UTF-8
    Referer: http://naatlas-confluence.openstack.local:8090/display/TS/Markup-test
    Accept-Encoding: gzip, deflate
    Accept-Language: pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Cookie: JSESSIONID=13F1F830FE60CA13C502A0A3A326293B

Request Payload:
{
    "file": "modules/meps/src/main/resources/wsdl/generate.md"
}

Response Status Code:
    200 OK

Response Body:
{
   "uuid": "modules/meps/src/main/resources/wsdl/generate.md",
   "fullName": "modules/meps/src/main/resources/wsdl/generate.md",
   "name": "generate.md",
   "locationPath":[
        "modules",
        "meps",
        "src",
        "main",
        "resources",
        "wsdl",
        "generate.md"
   ],
   "lastUpdateAuthorName": "tkubicz",
   "lastUpdateAuthorEmail": "tkubicz@networkedassets.org",
   "lastUpdateTime":1492008922000,
   "content": "<span><h3 name=\"6beede81a2a04b5999e49e8fc418e2a0\">How to generate java classes from WSDL</h3> \n<p>To generate classes use 'wsimport' tool use command below:</p> \n<pre><code class=\"language-sh git4c-code git4c-highlightjs-code\">wsimport ProvisioningService.wsdl -p \"de.kdg.vas.nps.fixedip.core.provisioning\" -encoding \"UTF-8\" -keep\n</code></pre></span>",
   "rawContent": "### How to generate java classes from WSDL\n\nTo generate classes use 'wsimport' tool use command below:\n\n```sh\nwsimport ProvisioningService.wsdl -p \"de.kdg.vas.nps.fixedip.core.provisioning\" -encoding \"UTF-8\" -keep\n```",
   "tableOfContents":{
        "name": "",
        "anchorName": "",
        "children":[{
            "name": "How to generate java classes from WSDL",
            "anchorName": "6beede81a2a04b5999e49e8fc418e2a0",
            "children":[]
         }]
   }
}
```

#### 6.1.2.5 Get globs

This endpoint provides you with a list of globs(filter patterns) specified by the user while macro creation.

```
GET {git4c-backend-url}/{uuid}/globs
```

##### Example request and response
```
Request URL:
    http://naatlas-confluence.openstack.local:8090/rest/doc/1.0/documentation/13e022d6cb1442c9ab7c8148bbd9c090/globs

Request Method:
    GET

Request Headers:
    Accept:application/json, text/plain, */*
    Accept-Encoding:gzip, deflate
    Accept-Language:pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Connection:keep-alive
    Cookie:JSESSIONID=13F1F830FE60CA13C502A0A3A326293B
    Host:naatlas-confluence.openstack.local:8090
    Referer:http://naatlas-confluence.openstack.local:8090/display/TS/Markup-test?src=contextnavpagetreemode
    User-Agent:Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36
    X-Requested-With:XMLHttpRequest

Response Status Code:
    200 OK

Response Body:
{
    "globs":
    [
        {
            "prettyName": "Java",
            "value": "**.java"
        },
        {
            "prettyName": "Markdown",
            "value": "**.md"
        }
    ]
}
```

#### 6.1.2.6 Get commit history for file

Endpoint for getting a commit history for file from repository is available at:

```
POST {git4c-backend-url}/{uuid}/file/commits
```

##### Example request and response
```
Request URL:
    http://pc-kurban:1990/confluence/rest/doc/1.0/documentation/repository/file

Request Method:
    POST

Request Payload:
{
    "branch":"master",
    "file":"Presentation.md"
}


Request Headers:
    POST /confluence/rest/doc/1.0/documentation/1f152a523c6740c1891f646a821dc4d1/file/commits HTTP/1.1
    Connection: keep-alive
    Content-Length: 44
    Accept: application/json, text/plain, */*
    X-Requested-With: XMLHttpRequest
    User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36
    Content-Type: application/json;charset=UTF-8
    Referer: http://pc-kurban:1990/confluence/display/ds/Git4si
    Accept-Encoding: gzip, deflate
    Accept-Language: pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Cookie: JSESSIONID=E521A81AD7B7C80521AA744A1B3A8625; confluence.browse.space.cookie=space-templates

Response Status Code:
    200 OK

Response Body:{
"commitList":{
                {
                    "id":"b6d90a6a8b2661a4ebae1f8bd6f20f4c1dfc2c0f",
                    "authorName":"Andrzej Ressel",
                    "message":"Update",
                    "date":1507194304000
                },
                {
                    "id":"91b8a9857e8edf8de231b766fa9aa6fc4e420b64",
                    "authorName":"Bartosz Bednarek",
                    "message":"abc",
                    "date":1505735249000
                }]
}
```

#### 6.1.2.7 Get Extractor Data

Endpoint for getting an Extractor Data for macro is available at:

```
GET {git4c-backend-url}/{uuid}/extractorData
```

##### Example request and response
```
Request URL:
    http://pc-kurban:1990/confluence/rest/doc/1.0/documentation/{uuid}/extractorData

Request Method:
    GET

Request Headers:
    GET /confluence/rest/doc/1.0/documentation/7b4879237a2c4de480225ea10cb45710/extractorData HTTP/1.1
    Host: pc-kurban:1990
    Connection: keep-alive
    Accept: application/json, text/plain, */*
    X-Requested-With: XMLHttpRequest
    User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36
    Referer: http://pc-kurban:1990/confluence/display/ds/Git4si
    Accept-Encoding: gzip, deflate
    Accept-Language: pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Cookie: JSESSIONID=E521A81AD7B7C80521AA744A1B3A8625; confluence.browse.space.cookie=space-templates

Response Status Code:
    200 OK

Response Body:

For lines:
{"startLine":13,"endLine":63,"type":"LINES"}

For method:
{"name":"getNumSides","type":"METHOD"}

```


Backend classes containing information about Extractor data are defined as follows:

```
abstract class ExtractorData(val uuid: String) {
    abstract val type: String
}

class EmptyExtractor: ExtractorData("") {
    override val type = "EMPTY"
}

class LineNumbersExtractorData(
         uuid: String,
         val startLine: Int,
         val endLine: Int
) : ExtractorData(uuid) {
     override val type = "LINENUMBERS"
    }

class MethodExtractorData(
        uuid: String,
        val method: String
) : ExtractorData(uuid) {
        override val type = "METHOD"
    }
```


### 6.1.3 Management of macro

#### 6.1.3.1 Add new macro

The endpoint for adding new mecro is:

```
POST {git4c-backend-url}/
```

The payload of the request should be a JSON with required information for creating a macro.

Backend classes containing informations necessary for creating macro are defined as follows:

```
data class DocumentationMacro(
        val repositoryDetails: RepositoryDetails,
        val branch: String,
        val glob: List<String>,
        val defaultDocItem: String,
        val method: String?
)


data class RepositoryDetails(
        val repository: RepositoryToCreate
)


abstract class RepositoryToCreate(
)

data class CustomRepositoryToCreate(
        val url: String,
        val credentials: AuthorizationData
) : RepositoryToCreate()

data class PredefinedRepositoryToCreate(
        val uuid: String
) : RepositoryToCreate()
```

The RepositoryToCreate Json also contains 'type' property that specifies if the repository that backend recieves is either custom or predefined.
Values should be respectively "CUSTOM" and "PREDEFINED"

##### Example request and response
```
Request URL:
http://naatlas-confluence.openstack.local:8090/rest/doc/1.0/documentation

Request Method:
    POST

Request Payload:
{
  "repositoryDetails": {
      "repository": {
          "type": "PREDEFINED",
          "uuid": "d245604470194027bfb30472251fb6c2"
      }
  },
  "branch": "master",
  "glob": [],
  "defaultDocItem": ""
}

Request Headers:
    POST /rest/doc/1.0/documentation HTTP/1.1
    Host: naatlas-confluence.openstack.local:8090
    Connection: keep-alive
    Content-Length: 148
    Accept: application/json, text/plain, */*
    Origin: http://naatlas-confluence.openstack.local:8090
    X-Requested-With: XMLHttpRequest
    User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36
    Content-Type: application/json;charset=UTF-8
    Referer: http://naatlas-confluence.openstack.local:8090/pages/resumedraft.action?draftId=8486914&draftShareId=e94d482a-8f77-460a-a139-a0bee40f8779
    Accept-Encoding: gzip, deflate
    Accept-Language: pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Cookie: JSESSIONID=13F1F830FE60CA13C502A0A3A326293B

Response Status Code:
    200 OK

Response Body:
{
  {"uuid": "3722878d728d490fbac604720020c760"}
}
```

#### 6.1.3.2 Remove all macros
=============

Git4C backend provides you with an endpoint to remove all data. This will also delete every macro that has ever been created.
This endpoint will also remove all predefined repositories and globs. <b>All data will be cleaned.</b>

```
DELETE {git4c-backend-url}/
```

##### Example request and response
```
Request URL:
    http://naatlas-confluence.openstack.local:8090/rest/doc/1.0/documentation

Request Method:
    DELETE

Request Headers:
    DELETE /confluence/rest/doc/1.0/documentation HTTP/1.1
    Host: naatlas-confluence.openstack.local:8090
    Connection: keep-alive
    Content-Length: 148
    Accept: application/json, text/plain, */*
    Origin: http://naatlas-confluence.openstack.local:8090
    X-Requested-With: XMLHttpRequest
    User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36
    Content-Type: application/json;charset=UTF-8
    Referer: http://naatlas-confluence.openstack.local:8090/pages/resumedraft.action?draftId=8486914&draftShareId=e94d482a-8f77-460a-a139-a0bee40f8779
    Accept-Encoding: gzip, deflate
    Accept-Language: pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Cookie: JSESSIONID=13F1F830FE60CA13C502A0A3A326293B

Response Status Code:
    200 OK
```


#### 6.1.3.3 Remove all macros
=============

Git4C backend provides you with an endpoint to remove all data. This will also delete every macro that has ever been created.
This endpoint will also remove all predefined repositories and globs. <b>All data will be cleaned.</b>

```
DELETE {git4c-backend-url}/
```

##### Example request and response
```
Request URL:
    http://naatlas-confluence.openstack.local:8090/rest/doc/1.0/documentation

Request Method:
    DELETE

Request Headers:
    DELETE /confluence/rest/doc/1.0/documentation/unused HTTP/1.1
    Host: pc-kurban:1990
    Connection: keep-alive
    Accept: application/json, text/plain, */*
    Origin: http://pc-kurban:1990
    X-Requested-With: XMLHttpRequest
    User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36
    Content-Type: application/json;charset=utf-8
    Referer: http://pc-kurban:1990/confluence/plugins/servlet/git4c/admin
    Accept-Encoding: gzip, deflate
    Accept-Language: pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Cookie: JSESSIONID=E521A81AD7B7C80521AA744A1B3A8625; confluence.browse.space.cookie=space-templates

Response Status Code:
    200 OK
```

#### 6.1.3.4 Add new macro

The endpoint creating temporary macro is available at
```
POST {git4c-backend-url}/
```

Temporary macro is based on already existing macro, so you can create it by passing uuid of a macro and a branch name.

The payload of the request should be a JSON with branch information.
Class that specifies branch.
```
data class Branch(
    val branch: String
)
```


##### Example request and response
```
Request URL:
    http://naatlas-confluence.openstack.local:8090/rest/doc/1.0/documentation/13e022d6cb1442c9ab7c8148bbd9c090/temporary

Request Method:
    POST

Request Payload:
{
    "branch": "feature/empty-ip-fix"
}


Request Headers:
    POST /rest/doc/1.0/documentation/13e022d6cb1442c9ab7c8148bbd9c090/temporary HTTP/1.1
    Host: naatlas-confluence.openstack.local:8090
    Connection: keep-alive
    Content-Length: 33
    Accept: application/json, text/plain, */*
    Origin: http://naatlas-confluence.openstack.local:8090
    X-Requested-With: XMLHttpRequest
    User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36
    Content-Type: application/json;charset=UTF-8
    Referer: http://naatlas-confluence.openstack.local:8090/display/TS/Markup-test
    Accept-Encoding: gzip, deflate
    Accept-Language: pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Cookie: JSESSIONID=13F1F830FE60CA13C502A0A3A326293B

Response Status Code:
    200 OK

Response Body:
{
    "id": "3491dea323304f7c8ee9c9346a8c90e3"
}
```

#### 6.1.3.5 Get spaces with macro information
=================================

Git4C backend provides you with an endpoint to fetch information about all pages containing GIT4C macro

```
GET {git4c-backend-url}/spaces
```

##### Example request and response
```
Request URL:
    http://pc-kurban:1990/confluence/rest/doc/1.0/documentation/spaces

Request Method:
    GET

Request Headers:
    GET /confluence/rest/doc/1.0/documentation/spaces HTTP/1.1
    Host: pc-kurban:1990
    Connection: keep-alive
    Accept: application/json, text/plain, */*
    X-Requested-With: XMLHttpRequest
    User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36
    Referer: http://pc-kurban:1990/confluence/plugins/servlet/git4c/admin
    Accept-Encoding: gzip, deflate
    Accept-Language: pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Cookie: JSESSIONID=022AC4E7ADDE5EF21EE83F164E5C28CE; confluence.browse.space.cookie=space-templates

Response Body:
{
"spaces":
    [
        {
            "name": "Demonstration Space",
            "url": "/display/ds",
            "pages":[
                {
                    "name": "Git4C",
                    "url": "/display/ds/Git4C",
                    "macros":[
                        {
                            "type":"SINGLEFILEMACRO",
                            "id":"0a8ebdcfabd84420a869e98ef362f93e",
                            "url": "https://XXX/markup-test.git",
                            "file":"testing/asciidoc/code/java.java"
                        }
                    ]
                },
                {
                    "name":"Git4si",
                    "url":"/display/ds/Git4si",
                    "macros":[
                        {
                            "type":"MULTIFILEMACRO",
                            "id":"298fcc33e83c47469b55bdab5f3f66cc",
                            "url":"https://XXX/markup-test.git"
                        }
                    ]
                }
            ]
        }
    ]
}



Response Status Code:
    200 OK
```


## 6.2 Predefined Repositories functionality

### 6.2.1 Management of Predefined Repositories

#### 6.2.1.1 Add a new predefined repository

Endpoint for predefining a new repository is available at:

```
POST {git4c-backend-url}/predefine
```


The payload of the request should be a JSON with required information for defining a repository class.

Backend class containing information about repository is defined as follows:

```
data class PredefinedRepository(
        val sourceRepositoryUrl: String,
        val credentials: AuthorizationData,
        val repositoryName : String
)

data class SshKeyAuthorization(
        val sshKey: String
) : AuthorizationData

data class UsernamePasswordAuthorization(
        val username: String,
        val password: String
) : AuthorizationData

class NoAuth : AuthorizationData

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
        JsonSubTypes.Type(value = UsernamePasswordAuthorization::class, name = "USERNAMEPASSWORD"),
        JsonSubTypes.Type(value = SshKeyAuthorization::class, name = "SSHKEY"),
        JsonSubTypes.Type(value = NoAuth::class, name = "NOAUTH")
)
interface AuthorizationData
```


##### Example request and response
```
Request URL:
http://pc-kurban:1990/confluence/rest/doc/1.0/documentation/predefine

Request Method:
    POST

Request Payload:
{
    "repositoryName":"Git4C",
    "sourceRepositoryUrl":"https://github.com/NetworkedAssets/git4c.git",
    "credentials":
    {
        "type":"NOAUTH"
    }
}


Request Headers:
    POST /confluence/rest/doc/1.0/documentation/predefine HTTP/1.1
    Host: pc-kurban:1990
    Connection: keep-alive
    Content-Length: 127
    Accept: application/json, text/plain, */*
    Origin: http://pc-kurban:1990
    X-Requested-With: XMLHttpRequest
    User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36
    Content-Type: application/json;charset=UTF-8
    Referer: http://pc-kurban:1990/confluence/plugins/servlet/git4c/admin
    Accept-Encoding: gzip, deflate
    Accept-Language: pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Cookie: JSESSIONID=D265D06D93CAB101C9FAE30055347E94; confluence.browse.space.cookie=space-templates

Response Status Code:
    200 OK

Response Body:
{
  "uuid": "3722878d728d490fbac604720020c760"
}
```


#### 6.2.1.2 Remove a predefined repository

Endpoint for removing a predefined repository is available at:

```
DELETE {git4c-backend-url}/predefine/{uuid}
```


##### Example request and response
```
Request URL:
    http://pc-kurban:1990/confluence/rest/doc/1.0/documentation/predefine

Request Method:
    DELETE

Request Payload:
{
    "repositoryName":"Git4C",
    "sourceRepositoryUrl":"https://github.com/NetworkedAssets/git4c.git",
    "credentials":
    {
        "type":"NOAUTH"
    }
}


Request Headers:
    DELETE /confluence/rest/doc/1.0/documentation/predefine/67e52c01843844988d038076ef71b70d HTTP/1.1
    Host: pc-kurban:1990
    Connection: keep-alive
    Accept: application/json, text/plain, */*
    Origin: http://pc-kurban:1990
    X-Requested-With: XMLHttpRequest
    User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36
    Content-Type: application/json;charset=utf-8
    Referer: http://pc-kurban:1990/confluence/plugins/servlet/git4c/admin
    Accept-Encoding: gzip, deflate
    Accept-Language: pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Cookie: JSESSIONID=AE443C4BAB5989773D070B4AAB512FF7; confluence.browse.space.cookie=space-templates

Response Status Code:
    200 OK
```


#### 6.2.1.3 Edit a predefined repository

Endpoint for editing an existing predefined repository is available at:

```
POST {git4c-backend-url}/predefine/{uuid}
```


The payload of the request should be a JSON with required information for defining a repository class.

Backend class containing information about repository is defined as follows:

```
data class PredefinedRepository(
        val sourceRepositoryUrl: String,
        val credentials: AuthorizationData,
        val repositoryName : String
)

data class SshKeyAuthorization(
        val sshKey: String
) : AuthorizationData

data class UsernamePasswordAuthorization(
        val username: String,
        val password: String
) : AuthorizationData

class NoAuth : AuthorizationData

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
        JsonSubTypes.Type(value = UsernamePasswordAuthorization::class, name = "USERNAMEPASSWORD"),
        JsonSubTypes.Type(value = SshKeyAuthorization::class, name = "SSHKEY"),
        JsonSubTypes.Type(value = NoAuth::class, name = "NOAUTH")
)
interface AuthorizationData
```


##### Example request and response
```
Request URL:
    http://pc-kurban:1990/confluence/rest/doc/1.0/documentation/predefine/e48d4c85c7ce470d91500b2f7ce1b2b9

Request Method:
    POST

Request Payload:
{
    "repositoryName": "custoo",
    "sourceRepositoryUrl": "ssh://git@bitbucket.networkedassets.net:7999/condoc/markup.git",
    "credentials":{
        "type":"SSHKEY",
        "sshKey":"-----BEGIN RSA PRIVATE KEY-----SSHkee-----END RSA PRIVATE KEY-----\n"
    }
}


Request Headers:
    POST /confluence/rest/doc/1.0/documentation/predefine/e48d4c85c7ce470d91500b2f7ce1b2b9 HTTP/1.1
    Host: pc-kurban:1990
    Connection: keep-alive
    Content-Length: 1056
    Accept: application/json, text/plain, */*
    Origin: http://pc-kurban:1990
    X-Requested-With: XMLHttpRequest
    User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36
    Content-Type: application/json;charset=UTF-8
    Referer: http://pc-kurban:1990/confluence/plugins/servlet/git4c/admin
    Accept-Encoding: gzip, deflate
    Accept-Language: pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Cookie: JSESSIONID=AE443C4BAB5989773D070B4AAB512FF7; confluence.browse.space.cookie=space-templates

Response Status Code:
    200 OK

Response Body:
{
  "uuid": "e48d4c85c7ce470d91500b2f7ce1b2b9"

"
}
```

### 6.2.2 Obtain information about Predefined Repositories


#### 6.2.2.1 Get files

Endpoint for getting a file list from predefined repository is available at:

```
POST {git4c-backend-url}/predefine/{uuid}/files
```


The payload of the request should be a JSON with the branch specification.

Class that specifies branch:
```
data class Branch(
    val branch: String
)
```


##### Example request and response
```
Request URL:
    http://pc-kurban:1990/confluence/rest/doc/1.0/documentation/predefine/e48d4c85c7ce470d91500b2f7ce1b2b9/files


Request Method:
    POST

Request Payload:
{
    "branch": "feature/empty-ip-fix"
}


Request Headers:
    POST /confluence/rest/doc/1.0/documentation/predefine/e48d4c85c7ce470d91500b2f7ce1b2b9/files HTTP/1.1
    Host: pc-kurban:1990
    Connection: keep-alive
    Content-Length: 19
    Accept: application/json, text/plain, */*
    Origin: http://pc-kurban:1990
    X-Requested-With: XMLHttpRequest
    User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36
    Content-Type: application/json;charset=UTF-8
    Referer: http://pc-kurban:1990/confluence/pages/createpage.action?spaceKey=ds
    Accept-Encoding: gzip, deflate
    Accept-Language: pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Cookie: JSESSIONID=AE443C4BAB5989773D070B4AAB512FF7; confluence.browse.space.cookie=space-templates

Response Status Code:
    200 OK

Response Body:

    {
        "files": [
        "confluence-plugin/pom.xml",
        ],
        "tree":{"name":"","fullName":"","type":"DIR","children":
        [
            {
                "name":"confluence-plugin","fullName":"confluence-plugin","type":"DIR","children":
                [
                    name: "pom.xml", fullName: "confluence-plugin/pom.xml", type: "DOCITEM", children[]
                }
            }
        }
   }
```


#### 6.2.2.2 Get file

Endpoint for getting a file from predefined repository is available at:

```
POST {git4c-backend-url}/predefine/{uuid}/file
```


The payload of the request should be a JSON with specifiaction of a file and the branch.

Backend classes containing information about file and branch are defined as follows:

```
data class RequestedFile(
        val file: String
)

data class Branch(
    val branch: String
)
```


##### Example request and response
```
Request URL:
    http://pc-kurban:1990/confluence/rest/doc/1.0/documentation/predefine/e48d4c85c7ce470d91500b2f7ce1b2b9/file


Request Method:
    POST

Request Payload:
{
    "branch": "master",
    "file": "confluence-plugin/LICENCE.md"
}


Request Headers:
    POST /confluence/rest/doc/1.0/documentation/predefine/e48d4c85c7ce470d91500b2f7ce1b2b9/file HTTP/1.1
    Host: pc-kurban:1990
    Connection: keep-alive
    Content-Length: 57
    Accept: application/json, text/plain, */*
    Origin: http://pc-kurban:1990
    X-Requested-With: XMLHttpRequest
    User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36
    Content-Type: application/json;charset=UTF-8
    Referer: http://pc-kurban:1990/confluence/pages/createpage.action?spaceKey=ds
    Accept-Encoding: gzip, deflate
    Accept-Language: pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Cookie: JSESSIONID=AE443C4BAB5989773D070B4AAB512FF7; confluence.browse.space.cookie=space-templates

Response Status Code:
    200 OK

Response Body:
{
    "content": "<p> file content </p>"
}
```


#### 6.2.2.3 Get branches

Endpoint for getting a branch list from predefined repository is available at:

```
GET {git4c-backend-url}/predefine/{uuid}/branches
```


##### Example request and response
```
Request URL:
    http://pc-kurban:1990/confluence/rest/doc/1.0/documentation/predefine/e48d4c85c7ce470d91500b2f7ce1b2b9/branches

Request Method:
    GET


Request Headers:
    GET /confluence/rest/doc/1.0/documentation/predefine/e48d4c85c7ce470d91500b2f7ce1b2b9/branches HTTP/1.1
    Host: pc-kurban:1990
    Connection: keep-alive
    Accept: application/json, text/plain, */*
    X-Requested-With: XMLHttpRequest
    User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36
    Referer: http://pc-kurban:1990/confluence/pages/createpage.action?spaceKey=ds
    Accept-Encoding: gzip, deflate
    Accept-Language: pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Cookie: JSESSIONID=AE443C4BAB5989773D070B4AAB512FF7; confluence.browse.space.cookie=space-templates

Response Status Code:
    200 OK

Response Body:
{
    "currentBranch":"",
    "allBranches":
    [
        "feature/NAATLAS-first-use-case",
        "feature/fix_tests",
        "feature/remove_failing_tests",
        "feature/NAATLAS-1261-when-clicking-on-the-link-to",
        "feature/NAATLAS-removed-url-parse",
        "bugfix/NAATLAS-1213-typos-in-texts-of-plugin",
        "feature/NAATLAS-1001-refresh-at-backend",
        "release/1.0",
        "feature/NAATLAS-1048-br-tags-are-lost-during-conversion",
        "feature/NAATLAS-980-remove-padding",
        "feature/NAATLAS-1190-fix-glob-names",
        "feature/NAATLAS-1288-background-color",
        "feature/NAATLAS-1181-administrator-should-be-able",
        "feature/ci_testing"
    ]
}

```


#### 6.2.2.4 Get methods

Endpoint for getting a methods list for a predefined repository and their contents is available at:
```
POST {git4c-backend-url}/predefine/{uuid}/methods
```


The payload of the request should be a JSON with specifiaction of a file and the branch.

Backend classes containing information about file and branch are defined as follows:
```
data class RequestedFile(
        val file: String
)

data class Branch(
    val branch: String
)
```


##### Example request and response
```
Request URL:
    http://pc-kurban:1990/confluence/rest/doc/1.0/documentation/predefine/e48d4c85c7ce470d91500b2f7ce1b2b9/methods

Request Method:
    POST

Request Payload:
{
    "branch": "master",
    "file": "confluence-plugin/LICENCE.md"
}

Request Headers:
    POST /confluence/rest/doc/1.0/documentation/predefine/e48d4c85c7ce470d91500b2f7ce1b2b9/methods HTTP/1.1
    Host: pc-kurban:1990
    Connection: keep-alive
    Content-Length: 141
    Accept: application/json, text/plain, */*
    Origin: http://pc-kurban:1990
    X-Requested-With: XMLHttpRequest
    User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36
    Content-Type: application/json;charset=UTF-8
    Referer: http://pc-kurban:1990/confluence/pages/createpage.action?spaceKey=ds
    Accept-Encoding: gzip, deflate
    Accept-Language: pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Cookie: JSESSIONID=AE443C4BAB5989773D070B4AAB512FF7; confluence.browse.space.cookie=space-templates

Response Status Code:
    200 OK

Response Body:
{
    "methods":
     [
         {
                "name": "treeify",
                "content": "Method content"
         }

     ]
}
```


#### 6.2.2.5 Get all predefined repositories

Endpoint for getting the list of predefined repositories is available at:

```
GET {git4c-backend-url}/predefine
```


##### Example request and response
```
Request URL:
    http://pc-kurban:1990/confluence/rest/doc/1.0/documentation/predefine

Request Method:
    GET

Request Headers:
    GET /confluence/rest/doc/1.0/documentation/predefine HTTP/1.1
    Host: pc-kurban:1990
    Connection: keep-alive
    Accept: application/json, text/plain, */*
    X-Requested-With: XMLHttpRequest
    User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36
    Referer: http://pc-kurban:1990/confluence/pages/createpage.action?spaceKey=ds
    Accept-Encoding: gzip, deflate
    Accept-Language: pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Cookie: JSESSIONID=AE443C4BAB5989773D070B4AAB512FF7; confluence.browse.space.cookie=space-templates

Response Status Code:
    200 OK

Response Body:
[
    {
        "uuid":"e48d4c85c7ce470d91500b2f7ce1b2b9",
        "sourceRepositoryUrl":"ssh://git@bitbucket.networkedassets.net:7999/condoc/markup.git",
        "authType":"SSHKEY",
        "name":"custom2"
    },
    {
        "uuid":"5ea6c204a09941d68979a6b60229d480",
        "sourceRepositoryUrl":"https://kurban@bitbucket.networkedassets.net/bitbucket/scm/condoc/markup.git",
        "authType":"USERNAMEANDPASSWORD",
        "name":"custom1"
    }
]
```


#### 6.2.2.6 Get a predefined repository

Endpoint getting a predefined repository is available at:

```
GET {git4c-backend-url}/predefine/{uuid}
```



##### Example request and response
```
Request URL:
http://pc-kurban:1990/confluence/rest/doc/1.0/documentation/predefine/e48d4c85c7ce470d91500b2f7ce1b2b9

Request Method:
    GET

Request Headers:
    GET /confluence/rest/doc/1.0/documentation/predefine/e48d4c85c7ce470d91500b2f7ce1b2b9 HTTP/1.1
    Host: pc-kurban:1990
    Connection: keep-alive
    Content-Length: 127
    Accept: application/json, text/plain, */*
    Origin: http://pc-kurban:1990
    X-Requested-With: XMLHttpRequest
    User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36
    Content-Type: application/json;charset=UTF-8
    Referer: http://pc-kurban:1990/confluence/plugins/servlet/git4c/admin
    Accept-Encoding: gzip, deflate
    Accept-Language: pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Cookie: JSESSIONID=D265D06D93CAB101C9FAE30055347E94; confluence.browse.space.cookie=space-templates

Response Status Code:
    200 OK

Response Body:
{
        "uuid":"e48d4c85c7ce470d91500b2f7ce1b2b9",
        "sourceRepositoryUrl":"ssh://git@bitbucket.networkedassets.net:7999/condoc/markup.git",
        "authType":"SSHKEY",
        "name":"custoo"
}
```

### 6.2.3 Force to use Predefined Repositories

When this option is set to ON then using Custom Repositories by users is not allowed. 

Predefined Repository is a repository defined by the administrator and made available for every user without authentication. The repository that has been predefined will be displayed on a list of repositories while creation of macro.

#### 6.2.3.1  Force using predefined repositories

An administrator can force users to use only predefined repositories.
The endpoint for setting such an option is available at:

```
POST {git4c-backend-url}/settings/repository/predefine/force
```


The payload of the request should be a JSON with the boolean.

```
data class ForcePredefinedRepositoriesInfo(
        val toForce: Boolean
)
```


##### Example request and response
```
Request URL:
    http://pc-kurban:1990/confluence/rest/doc/1.0/documentation/settings/repository/predefine/force

Request Method:
    POST

Request Headers:

POST /confluence/rest/doc/1.0/documentation/settings/repository/predefine/force HTTP/1.1
Host: pc-kurban:1990
Connection: keep-alive
Content-Length: 16
Accept: application/json, text/plain, */*
Origin: http://pc-kurban:1990
X-Requested-With: XMLHttpRequest
User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36
Content-Type: application/json;charset=UTF-8
Referer: http://pc-kurban:1990/confluence/plugins/servlet/git4c/admin
Accept-Encoding: gzip, deflate
Accept-Language: pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
Cookie: JSESSIONID=70C7DB1D70B4710FF3926DBFC940077E; confluence.browse.space.cookie=space-templates

Request Payload:
{
    "toForce": true
}


Response Status Code:
    200 OK

Response Body:
    {
        "forced": true
    }
```


#### 6.2.3.2 Checking the current settings

You can check the current settings for forcing predefined repositories at
```
GET {git4c-backend-url}/settings/repository/predefine/force

Request Headers
    GET /confluence/rest/doc/1.0/documentation/settings/repository/predefine/force HTTP/1.1
    Host: pc-kurban:1990
    Connection: keep-alive
    Accept: application/json, text/plain, */*
    X-Requested-With: XMLHttpRequest
    User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36
    Referer: http://pc-kurban:1990/confluence/pages/createpage.action?spaceKey=ds
    Accept-Encoding: gzip, deflate
    Accept-Language: pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Cookie: JSESSIONID=A714C053D1A1EDA8E587937B28330A64; confluence.browse.space.cookie=space-templates

Response Status Code:
    200 OK

Response Body:
    {
        "forced": false
    }
```

## 6.3 Predefined Filters functionality

Also known as Predefined Globs functionality. Allow to use file filtering for repositories based on GLOB pattern filtering.

### 6.3.1 Management of Predefined Filters

#### 6.3.1.1 Add new predefined glob

Endpoint for adding a new predefined glob is available at:

```
POST {git4c-backend-url}/glob/
```

The payload of the request should be a JSON with required information for defining a glob class.

Backend class containing information about glob is defined as follows:

```
data class PredefinedGlobToCreate(
        val name: String,
        val glob: String
)
```


##### Example request and response
```
Request URL:
http://pc-aressel:1990/confluence/rest/doc/1.0/documentation/glob

Request Method:
    POST

Request Payload:
{
    "name":"C++",
    "glob":"**.cpp,**.c++"
}

Request Headers:
    Accept:application/json, text/plain, */*
    Accept-Encoding:gzip, deflate
    Accept-Language:pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Connection:keep-alive
    Content-Length:37
    Content-Type:application/json;charset=UTF-8
    Cookie:JSESSIONID=34DCF453195C17DBD49A0FCF7C8E895D
    Host:pc-aressel:1990
    Origin:http://pc-aressel:1990
    Referer:http://pc-aressel:1990/confluence/plugins/servlet/git4c/admin
    User-Agent:Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36
    X-Requested-With:XMLHttpRequest

Response:
{
    "uuid":"5863071639a64892939d042ca8cbaaea",
    "name":"C++",
    "glob":"**.cpp,**.c++"
}
```

#### 6.3.1.2 Remove predefined glob

Endpoint for removing a predefined glob is available at:

```
DELETE {git4c-backend-url}/glob/{glob_uuid}
```

The payload of the request should be empty.


##### Example request and response
```
Request URL:
http://pc-aressel:1990/confluence/rest/doc/1.0/documentation/glob/74d58662eac849539d107715baf9c212

Request Method:
    DELETE

Request Payload:
null

Request Headers:
    Accept:application/json, text/plain, */*
    Accept-Encoding:gzip, deflate
    Accept-Language:pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Connection:keep-alive
    Content-Type:application/json;charset=utf-8
    Cookie:JSESSIONID=34DCF453195C17DBD49A0FCF7C8E895D
    Host:pc-aressel:1990
    Origin:http://pc-aressel:1990
    Referer:http://pc-aressel:1990/confluence/plugins/servlet/git4c/admin
    User-Agent:Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36
    X-Requested-With:XMLHttpRequest


Response:
""
```

#### 6.3.1.3 Remove all predefined globs

Endpoint for removing all predefined glob is available at:

```
DELETE {git4c-backend-url}/glob/
```

The payload of the request should be empty.


##### Example request and response
```
Request URL:
http://pc-aressel:1990/confluence/rest/doc/1.0/documentation/glob

Request Method:
    DELETE

Request Payload:
null

Request Headers:
    Accept:application/json, text/plain, */*
    Accept-Encoding:gzip, deflate
    Accept-Language:pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Connection:keep-alive
    Content-Type:application/json;charset=utf-8
    Cookie:JSESSIONID=34DCF453195C17DBD49A0FCF7C8E895D
    Host:pc-aressel:1990
    Origin:http://pc-aressel:1990
    Referer:http://pc-aressel:1990/confluence/plugins/servlet/git4c/admin
    User-Agent:Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36
    X-Requested-With:XMLHttpRequest


Response:
""
```

#### 6.3.1.4 Getting single glob

Endpoint for getting single glob is available at:

```
GET {git4c-backend-url}/glob/{uuid}
```

The payload of the request should be empty.


##### Example request and response
```
Request URL:
http://pc-aressel:1990/confluence/rest/doc/1.0/documentation/glob/e74e6461fa534dc987ead1bf1f5dec11

Request Method:
    GET

Request Payload:
null

Request Headers:
    Accept:application/json, text/plain, */*
    Accept-Encoding:gzip, deflate
    Accept-Language:pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Cache-Control:no-cache
    Connection:keep-alive
    Cookie:JSESSIONID=9CE6F073B8F157E7F7F3FC8EC605B5A3
    Host:pc-aressel:1990
    Pragma:no-cache
    Referer:http://pc-aressel:1990/confluence/plugins/servlet/git4c/admin
    User-Agent:Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36
    X-Requested-With:XMLHttpRequest

Response:
{
    "uuid":"e74e6461fa534dc987ead1bf1f5dec11",
    "name":"Gherkin",
    "glob":"**.feature"
}
```

#### 6.3.1.5 Getting all globs

Endpoint for getting all globs is available at:

```
GET {git4c-backend-url}/glob/
```

The payload of the request should be empty.


##### Example request and response
```
Request URL:
http://pc-aressel:1990/confluence/rest/doc/1.0/documentation/glob

Request Method:
    GET

Request Payload:
null

Request Headers:
    Accept:application/json, text/plain, */*
    Accept-Encoding:gzip, deflate
    Accept-Language:pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Cache-Control:no-cache
    Connection:keep-alive
    Cookie:JSESSIONID=9CE6F073B8F157E7F7F3FC8EC605B5A3
    Host:pc-aressel:1990
    Pragma:no-cache
    Referer:http://pc-aressel:1990/confluence/plugins/servlet/git4c/admin
    User-Agent:Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36
    X-Requested-With:XMLHttpRequest

Response:
{
   "globs":[  
      {
         "uuid":"e74e6461fa534dc987ead1bf1f5dec11",
         "name":"Gherkin",
         "glob":"**.feature"
      },
      {
         "uuid":"1435208b9d464cf6aace9559595cab38",
         "name":"Kotlin",
         "glob":"**.kt"
      },
      {
         "uuid":"b869499952f64d9a9f2607ad789c050e",
         "name":"Scala",
         "glob":"**.scala"
      },
      {
         "uuid":"d2c6c479c0b04a3ba61ebd1477192519",
         "name":"Java",
         "glob":"**.java"
      },
      {
         "uuid":"b4c35321ac6442fe80aee0395bf38775",
         "name":"Markdown",
         "glob":"**.md"
      }
   ]
}
```

#### 6.3.1.6 Restore default globs

Endpoint for restoring default predefined glob is available at:

```
HEAD {git4c-backend-url}/glob/
```

The payload of the request should be empty.


##### Example request and response
```
Request URL:
http://pc-aressel:1990/confluence/rest/doc/1.0/documentation/glob

Request Method:
    HEAD

Request Payload:
null

Request Headers:
    Accept:application/json, text/plain, */*
    Accept-Encoding:gzip, deflate
    Accept-Language:pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Connection:keep-alive
    Content-Type:application/json;charset=utf-8
    Cookie:JSESSIONID=34DCF453195C17DBD49A0FCF7C8E895D
    Host:pc-aressel:1990
    Origin:http://pc-aressel:1990
    Referer:http://pc-aressel:1990/confluence/plugins/servlet/git4c/admin
    User-Agent:Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36
    X-Requested-With:XMLHttpRequest


Response:
200
```


## 6.4 Repositories

### 6.4.1 Obtain informations about repository

#### 6.4.1.1 Get branches

Endpoint for getting a branch list from repository is available at:

```
POST {git4c-backend-url}/repository/branches
```

##### Example request and response
```
Request URL:
    http://pc-kurban:1990/confluence/rest/doc/1.0/documentation/repository/branches

Request Method:
    POST

Request Payload:
{
    "sourceRepositoryUrl": "https://kurban@bitbucket.networkedassets.net/bitbucket/scm/condoc/markup.git",
    "credentials":
    {
        "type": "USERNAMEPASSWORD",
        "username": "kurban",
        "password": "mySuperSecretPassword1a4"
    }
}


Request Headers:
    POST /confluence/rest/doc/1.0/documentation/repository/branches HTTP/1.1
    Host: pc-kurban:1990
    Connection: keep-alive
    Content-Length: 195
    Accept: application/json, text/plain, */*
    Origin: http://pc-kurban:1990
    X-Requested-With: XMLHttpRequest
    User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36
    Content-Type: application/json;charset=UTF-8
    Referer: http://pc-kurban:1990/confluence/pages/createpage.action?spaceKey=ds
    Accept-Encoding: gzip, deflate
    Accept-Language: pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Cookie: JSESSIONID=AE443C4BAB5989773D070B4AAB512FF7; confluence.browse.space.cookie=space-templates

Response Status Code:
    200 OK

Response Body:
{
    "currentBranch":null,
    "allBranches":
    [
        "NAATLAS-962-Active-Objects",
        "bugfix/dbfix",
        "bugfix/resturl_branches",
        "develop",
        "feature/#923",
        "feature/NAATLAS-1000-1020"
    ]
}
```

##### Existing Repository


You can also refer the existing repository directly by its uuid.

Endpoint for getting the list of branches is available at:
```
GET {git4c-backend-url}/repository/{uuid}/branches
```


#### 6.4.1.2 Get files

Endpoint for getting a file list from repository is available at:

```
POST {git4c-backend-url}/repository/files
```

##### Example request and response
```
Request URL:
    http://pc-kurban:1990/confluence/rest/doc/1.0/documentation/repository/files

Request Method:
    POST

Request Payload:
{
    "sourceRepositoryUrl": "https://kurban@bitbucket.networkedassets.net/bitbucket/scm/condoc/markup.git",
    "credentials":
    {
        "type": "USERNAMEPASSWORD",
        "username": "kurban",
        "password": "mySuperSecretPassword1a4"
    },
    "branch": "master"
}



Request Headers:
    POST /confluence/rest/doc/1.0/documentation/repository/branches HTTP/1.1
    Host: pc-kurban:1990
    Connection: keep-alive
    Content-Length: 195
    Accept: application/json, text/plain, */*
    Origin: http://pc-kurban:1990
    X-Requested-With: XMLHttpRequest
    User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36
    Content-Type: application/json;charset=UTF-8
    Referer: http://pc-kurban:1990/confluence/pages/createpage.action?spaceKey=ds
    Accept-Encoding: gzip, deflate
    Accept-Language: pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Cookie: JSESSIONID=AE443C4BAB5989773D070B4AAB512FF7; confluence.browse.space.cookie=space-templates

Response Status Code:
    200 OK

Response Body:
{
    "currentBranch":null,
    "allBranches":
    [
        "NAATLAS-962-Active-Objects",
        "bugfix/dbfix",
        "bugfix/resturl_branches",
        "develop",
        "feature/#923",
        "feature/NAATLAS-1000-1020"
    ]
}
```

##### Existing Repository

You can also refer the existing repository directly by its uuid.

Endpoint for getting the list of branches is:
```
GET {git4c-backend-url}/repository/{uuid}/files
```

#### 6.4.1.3 Get file

Endpoint for getting a file from repository is available at:

```
POST {git4c-backend-url}/repository/file
```

##### Example request and response
```
Request URL:
    http://pc-kurban:1990/confluence/rest/doc/1.0/documentation/repository/file

Request Method:
    POST

Request Payload:
{
    "sourceRepositoryUrl": "https://kurban@bitbucket.networkedassets.net/bitbucket/scm/condoc/markup.git",
    "credentials":
    {
        "type": "USERNAMEPASSWORD",
        "username": "kurban",
        "password": "mySuperSecretPassword1a4"
    },
    "branch": "master",
    "file": "confluence-plugin/LICENCE.md"
}


Request Headers:
    POST /confluence/rest/doc/1.0/documentation/repository/file HTTP/1.1
    Host: pc-kurban:1990
    Connection: keep-alive
    Content-Length: 251
    Accept: application/json, text/plain, */*
    Origin: http://pc-kurban:1990
    X-Requested-With: XMLHttpRequest
    User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36
    Content-Type: application/json;charset=UTF-8
    Referer: http://pc-kurban:1990/confluence/pages/createpage.action?spaceKey=ds
    Accept-Encoding: gzip, deflate
    Accept-Language: pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Cookie: JSESSIONID=AE443C4BAB5989773D070B4AAB512FF7; confluence.browse.space.cookie=space-templates

Response Status Code:
    200 OK

Response Body:
{
    "content": "<p>file content</p>"
}
```

##### Existing repository

You can also refer the existing repository directly by its uuid.

Endpoint for getting the file is available at:
```
GET {git4c-backend-url}/repository/{uuid}/file
```



#### 6.4.1.4 Get methods

Endpoint for getting a methods list for a predefined repository and their contents is available at:
```
POST {git4c-backend-url}/repository/methods
```


##### Example request and response
```
Request URL:
    http://pc-kurban:1990/confluence/rest/doc/1.0/documentation/repository/file/methods

Request Method:
    POST

{
    "sourceRepositoryUrl": "https://kurban@bitbucket.networkedassets.net/bitbucket/scm/condoc/markup.git",
    "credentials":
    {
        "type": "USERNAMEPASSWORD",
        "username": "kurban",
        "password": "mySuperSecretPassword1a4"
    },
    "branch": "master",
    "file": "core/src/main/java/com/networkedassets/condoc/common/Documentation.java"
}

Request Headers:
    POST /confluence/rest/doc/1.0/documentation/repository/file/methods HTTP/1.1
    Host: pc-kurban:1990
    Connection: keep-alive
    Content-Length: 294
    Accept: application/json, text/plain, */*
    Origin: http://pc-kurban:1990
    X-Requested-With: XMLHttpRequest
    User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36
    Content-Type: application/json;charset=UTF-8
    Referer: http://pc-kurban:1990/confluence/pages/createpage.action?spaceKey=ds
    Accept-Encoding: gzip, deflate
    Accept-Language: pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Cookie: JSESSIONID=AE443C4BAB5989773D070B4AAB512FF7; confluence.browse.space.cookie=space-templates

Response Status Code:
    200 OK

Response Body:
{
    "methods":
     [
         {
             "name": "treeify",
             "content": "Method content"
         }

     ]
}
```


##### Existing repository

You can also refer the existing repository directly by its uuid.

Endpoint for getting the file is available at:
```
GET {git4c-backend-url}/repository/{uuid}/methods
```


#### 6.4.1.5 Verify repository

Endpoint for veryfing repository is available at:
```
POST {git4c-backend-url}/repository/verify
```


##### Example request and response
```
Request URL:
    http://pc-kurban:1990/confluence/rest/doc/1.0/documentation/repository/verify


Request Method:
    POST

{
    "sourceRepositoryUrl": "https://kurban@bitbucket.networkedassets.net/bitbucket/scm/condoc/markup.git",
    "credentials":
    {
        "type": "USERNAMEPASSWORD",
        "username": "kurban",
        "password": "mySuperSecretPassword1a4"
    }
}

Request Headers:
    POST /confluence/rest/doc/1.0/documentation/repository/verify HTTP/1.1
    Host: pc-kurban:1990
    Connection: keep-alive
    Content-Length: 195
    Accept: application/json, text/plain, */*
    Origin: http://pc-kurban:1990
    X-Requested-With: XMLHttpRequest
    User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36
    Content-Type: application/json;charset=UTF-8
    Referer: http://pc-kurban:1990/confluence/pages/createpage.action?spaceKey=ds
    Accept-Encoding: gzip, deflate
    Accept-Language: pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Cookie: JSESSIONID=AE443C4BAB5989773D070B4AAB512FF7; confluence.browse.space.cookie=space-templates

Response Status Code:
    200 OK

Response Body:
{
    "status": "OK",
    "ok":true
}
```

## 6.5 Possible Errors


### Non existing macro
Requested macro may have been deleted by the administrator. In that case backend will return error:

```
ACTION=GetDocumentationsMacroByDocumentationsMacroIdQuery, TRANSACTION=63cb6dac-141e-4a4f-8f58-2613150dbbd6, MESSAGE= REMOVED
```

caused by:

```
NotFoundException(request.transactionInfo, VerificationStatus.REMOVED.name)
```

### Wrong verification information

Credentials authorizing repository passed by user may be invalid.
In that case backend will response with an IllegalArgumentException corresponding to different verification status

```
data class VerificationInfo constructor(
        val status: VerificationStatus
)

enum class VerificationStatus {
    OK,
    SOURCE_NOT_FOUND,
    WRONG_CREDENTIALS,
    WRONG_BRANCH,
    WRONG_URL,
    WRONG_KEY_FORMAT,
    CAPTCHA_REQUIRED,
    REMOVED
}
```

### Jgit Exceptions

User may pass in path the name of a branch that doesn't exist, in that situation backend will return the exception error:
 ````
 org.eclipse.jgit.api.errors.RefNotFoundException: Ref origin/branchname can not be resolved
 ````
This and other Jgit errors' description can be found [here](http://download.eclipse.org/jgit/docs/jgit-2.3.1.201302201838-r/apidocs/org/eclipse/jgit/api/errors/package-summary.html)

