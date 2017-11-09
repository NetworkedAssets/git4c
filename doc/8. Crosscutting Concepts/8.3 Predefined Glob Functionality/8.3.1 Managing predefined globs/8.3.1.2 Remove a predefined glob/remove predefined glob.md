### Remove predefined glob

Endpoint for removing a predefined glob is available at:

```
DELETE {git4c-backend-url}/glob/{glob_uuid}
```

The payload of the request should be empty.


#### Example request and response
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