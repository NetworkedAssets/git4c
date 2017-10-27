Remove all macros
=============

Git4C backend provides you with an endpoint to remove all data. This will also delete every macro that has ever been created.
This endpoint will also remove all predefined repositories and globs. <b>All data will be cleaned.</b>

```
DELETE {git4c-backend-url}/
```

#### Example request and response
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