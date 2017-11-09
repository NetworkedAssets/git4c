## Specific macro information

### Get Branches

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

### Default Branch

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