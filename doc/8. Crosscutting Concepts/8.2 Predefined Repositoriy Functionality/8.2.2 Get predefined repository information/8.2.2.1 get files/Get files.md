
### Get files

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


#### Example request and response
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
