### Add new predefined glob

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


#### Example request and response
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