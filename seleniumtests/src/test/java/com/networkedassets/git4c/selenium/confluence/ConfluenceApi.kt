package com.networkedassets.git4c.selenium.confluence

import com.github.kittinunf.fuel.Fuel
import com.google.gson.JsonParser
import com.networkedassets.git4c.selenium.TestConfig
import org.apache.xmlrpc.client.XmlRpcClient
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl
import java.net.URL

class ConfluenceApi(
        val url: String,
        val username: String,
        val password: String
) {

    constructor(config: TestConfig) : this(config.url, config.username, config.password)

    fun createSpace(spaceName: String): Space {

        val space = getSpaceByName(spaceName)

        if (space != null) {
            throw RuntimeException("Space already exists")
        }

        val spaceJson = getNewSpaceObject(spaceName)

        val (req, resp, res) = Fuel.post("$url/rest/api/space")
                .header("Content-Type" to "application/json")
                .body(spaceJson)
                .authenticate(username, password)
                .responseString()

        if (res.component2() != null) {
            throw RuntimeException("Cannot create space", res.component2()!!)
        }

        val id = JsonParser().parse(res.get()).asJsonObject.get("id").asLong
        val key = JsonParser().parse(res.get()).asJsonObject.get("key").asString

        return Space(id, key)
    }

    fun removeSpace(space: Space) {

        val (_, _, res) = Fuel.delete("$url/rest/api/space/${space.spaceKey}")
                .header("Content-Type" to "application/json")
                .authenticate(username, password)
                .responseString()

        val err = res.component2()
        if (err != null) {
            throw RuntimeException("Cannot remove space", err)
        }

        Thread.sleep(5000)

    }

    fun getSpaceByName(spaceName: String): Space? {

        val (req, resp, res) = Fuel.get("$url/rest/api/space")
                .authenticate(username, password)
                .responseString()

        val err = res.component2()

        if (err != null) {
            throw RuntimeException("Can't get space $spaceName", err)
        }


        val spacesJson = JsonParser().parse(res.get()).asJsonObject

        val spaceJsonObj = spacesJson.get("results").asJsonArray
                .find {
                    it.asJsonObject.get("name").asString == spaceName
                }?.asJsonObject ?: return null

        val spaceId = spaceJsonObj["id"].asLong
        val spaceKey = spaceJsonObj["key"].asString

        return Space(spaceId, spaceKey)

    }

    fun createPage(space: Space, pageName: String): Page {

        val page = getPageByName(space, pageName)

        if (page != null) {
            throw RuntimeException("Page already exists")
        }

        val pageObj = getNewPageObject(space.spaceKey, pageName)

        val (req, resp, res) = Fuel.post("$url/rest/api/content")
                .header("Content-Type" to "application/json")
                .body(pageObj)
                .authenticate(username, password)
                .responseString()

        val err = res.component2()

        if (err != null) {
            throw RuntimeException("Cannot create page", err)
        }

        val pageJsonObj = JsonParser().parse(res.get()).asJsonObject

        val id = pageJsonObj["id"].asLong
        val link = pageJsonObj["_links"].asJsonObject["webui"].asString

        return Page(id, link)

    }

    fun removePage(page: Page) {

        val (req, resp, res) = Fuel.delete("$url/rest/api/content/${page.id}")
                .header("Content-Type" to "application/json")
                .authenticate(username, password)
                .response()

        val err = res.component2()

        if (err != null) {
            throw RuntimeException("Cannot remove page ${page}", err)
        }

    }

    fun getPageByName(space: Space, pageName: String): Page? {

        val (req, resp, res) = Fuel.get("$url/rest/api/space/${space.spaceKey}/content?limit=500")
                .authenticate(username, password)
                .responseString()

        val err = res.component2()

        if (err != null) {
            throw RuntimeException("Can't get page $pageName for space ${space.spaceKey}", err)
        }

        val pages = JsonParser().parse(res.get()).asJsonObject.getAsJsonObject("page").getAsJsonArray("results")

        val pageJsonObj = pages.asSequence()
                .map { it.asJsonObject }
                .find {
                    it["type"].asString == "page" &&
                            it["title"].asString == pageName
                } ?: return null

        val pageId = pageJsonObj["id"].asLong
        val link = pageJsonObj["_links"].asJsonObject["webui"].asString

        return Page(pageId, link)

    }
/*
    //TODO: Finish this method: https://developer.atlassian.com/static/connect/docs/latest/scopes/confluence-jsonrpc-scopes.html
    fun isAnonymousAccessEnabled(): Unit {

        val body = """{"jsonrpc": "2,0"}"""

        val (req, resp, res) = Fuel.post("$url/rpc/json-rpc/confluenceservice-v2/getPermissions")
                .body(body)
                .header("Content-Type" to "application/json")
                .authenticate(username, password)
                .responseString()

        val err = res.component2()

        if (err != null) {
            throw RuntimeException("Can't check if anonymous access is enabled", err)
        }

        println(resp)

    }*/

    fun enableAnonymousAccessToSpace(space: Space) {

        val config = XmlRpcClientConfigImpl()
        config.serverURL = URL("$url/rpc/xmlrpc")
        val client = XmlRpcClient()
        client.setConfig(config)
        val params = listOf(username, password)
        val token = client.execute("confluence2.login", params) as String
        client.execute("confluence2.addAnonymousPermissionToSpace", listOf(token, "VIEWSPACE", space.spaceKey))
        client.execute("confluence2.logout", listOf(token))
    }

    private fun getNewPageObject(spaceKey: String, title: String): String {

        return """
            {
              "space": {
                "key": "$spaceKey"
              },
              "body": {
              },
              "title": "$title",
              "type": "page"
            }
            """

    }

    private fun getNewSpaceObject(name: String): String {

        val json = """
            {
               "key":"${name.replace("\\s".toRegex(), "")}",
               "name":"$name",
               "type":"global",
               "description":{
                  "plain":{
                     "value":"Space for tests",
                     "representation":"plain"
                  }
               }
            }
            """

        return json
    }

}