Get spaces with macro information
=================================

Git4C backend provides you with an endpoint to fetch information about all pages containing GIT4C macro

```
GET {git4c-backend-url}/spaces
```

#### Example request and response
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