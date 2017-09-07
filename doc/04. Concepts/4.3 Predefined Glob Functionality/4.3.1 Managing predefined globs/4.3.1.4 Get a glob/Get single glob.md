### Getting single glob

Endpoint for getting single glob is available at:

```
GET {git4c-backend-url}/glob/{uuid}
```

The payload of the request should be empty.


#### Example request and response
```
Request URL:
http://pc-aressel:1990/confluence/rest/doc/1.0/documentation/glob/e74e6461fa534dc987ead1bf1f5dec11

Request Method:
    GET

Request Payload:
null

Request Headers:
    Accept:application/json, text/plain, */*
    Accept-Encoding:gzip, deflate
    Accept-Language:pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Cache-Control:no-cache
    Connection:keep-alive
    Cookie:JSESSIONID=9CE6F073B8F157E7F7F3FC8EC605B5A3
    Host:pc-aressel:1990
    Pragma:no-cache
    Referer:http://pc-aressel:1990/confluence/plugins/servlet/git4c/admin
    User-Agent:Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36
    X-Requested-With:XMLHttpRequest

Response:
{
    "uuid":"e74e6461fa534dc987ead1bf1f5dec11",
    "name":"Gherkin",
    "glob":"**.feature"
}
```