
### Edit a predefined repository

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


#### Example request and response
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