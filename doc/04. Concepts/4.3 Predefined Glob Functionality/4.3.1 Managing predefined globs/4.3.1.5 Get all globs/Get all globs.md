### Getting all globs

Endpoint for getting all globs is available at:

```
GET {git4c-backend-url}/glob/
```

The payload of the request should be empty.


#### Example request and response
```
Request URL:
http://pc-aressel:1990/confluence/rest/doc/1.0/documentation/glob

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
   "globs":[  
      {
         "uuid":"e74e6461fa534dc987ead1bf1f5dec11",
         "name":"Gherkin",
         "glob":"**.feature"
      },
      {
         "uuid":"1435208b9d464cf6aace9559595cab38",
         "name":"Kotlin",
         "glob":"**.kt"
      },
      {
         "uuid":"b869499952f64d9a9f2607ad789c050e",
         "name":"Scala",
         "glob":"**.scala"
      },
      {
         "uuid":"d2c6c479c0b04a3ba61ebd1477192519",
         "name":"Java",
         "glob":"**.java"
      },
      {
         "uuid":"b4c35321ac6442fe80aee0395bf38775",
         "name":"Markdown",
         "glob":"**.md"
      }
   ]
}
```