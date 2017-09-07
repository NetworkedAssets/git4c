
### Add a new predefined repository

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


#### Example request and response
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