
### Get a predefined repository

Endpoint getting a predefined repository is available at:

```
GET {git4c-backend-url}/predefine/{uuid}
```



#### Example request and response
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