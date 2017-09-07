## Specific macro information

This endpoint will provide you with the tree of files from specified in macro repository and branch

### Get content file tree

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

