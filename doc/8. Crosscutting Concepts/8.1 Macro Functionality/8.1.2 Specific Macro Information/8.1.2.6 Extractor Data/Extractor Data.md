### Get Extractor Data

Endpoint for getting an Extractor Data for macro is available at:

```
GET {git4c-backend-url}/{uuid}/extractorData
```

#### Example request and response
```
Request URL:
    http://pc-kurban:1990/confluence/rest/doc/1.0/documentation/{uuid}/extractorData

Request Method:
    GET

Request Headers:
    GET /confluence/rest/doc/1.0/documentation/7b4879237a2c4de480225ea10cb45710/extractorData HTTP/1.1
    Host: pc-kurban:1990
    Connection: keep-alive
    Accept: application/json, text/plain, */*
    X-Requested-With: XMLHttpRequest
    User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36
    Referer: http://pc-kurban:1990/confluence/display/ds/Git4si
    Accept-Encoding: gzip, deflate
    Accept-Language: pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4
    Cookie: JSESSIONID=E521A81AD7B7C80521AA744A1B3A8625; confluence.browse.space.cookie=space-templates

Response Status Code:
    200 OK

Response Body:

For lines:
{"startLine":13,"endLine":63,"type":"LINES"}

For method:
{"name":"getNumSides","type":"METHOD"}

```


Backend classes containing information about Extractor data are defined as follows:

```
abstract class ExtractorData(val uuid: String) {
    abstract val type: String
}

class EmptyExtractor: ExtractorData("") {
    override val type = "EMPTY"
}

class LineNumbersExtractorData(
         uuid: String,
         val startLine: Int,
         val endLine: Int
) : ExtractorData(uuid) {
     override val type = "LINENUMBERS"
    }

class MethodExtractorData(
        uuid: String,
        val method: String
) : ExtractorData(uuid) {
        override val type = "METHOD"
    }
```