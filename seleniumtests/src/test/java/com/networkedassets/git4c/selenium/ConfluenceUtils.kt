package com.networkedassets.git4c.selenium

import com.github.kittinunf.fuel.Fuel
import com.google.gson.JsonParser

object ConfluenceUtils {

    fun createSpace(url: String, username: String, password: String, spaceName: String): Int {

        val spaceId = getSpaceId(url, username, password, spaceName)

        if (spaceId != null) {
            return spaceId
        }

        val spaceJson = getNewSpaceObject(spaceName, spaceName)

        val (req, resp, res) = Fuel.post("$url/rest/api/space")
                .header("Content-Type" to "application/json")
                .body(spaceJson)
                .authenticate(username, password)
                .responseString()

        return JsonParser().parse(res.get()).asJsonObject.get("id").asInt

    }

    fun removeSpace(url: String, username: String, password: String, spaceId: Int) {

        Fuel.delete("$url/rest/api/space/$spaceId")
                .header("Content-Type" to "application/json")
                .authenticate(username, password)
                .response().third.get()

    }

    fun createPage(url: String, username: String, password: String, spaceId: Int, pageName: String): Int {

        val pageId = getPageId(url, username, password, spaceId, pageName)

        if (pageId != null) {
            return pageId
        }

        val spaceKey = getSpaceKey(url, username, password, spaceId)!!

        val page = getNewPageObject(spaceKey, pageName)

        val (req, resp, res) = Fuel.post("$url/rest/api/content")
                .header("Content-Type" to "application/json")
                .body(page)
                .authenticate(username, password)
                .responseString()

        return JsonParser().parse(res.get()).asJsonObject.get("id").asInt

    }

    fun getNewPageObject(spaceKey: String, title: String): String {

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

    fun removePage(url: String, username: String, password: String, pageId: Int) {

        Fuel.delete("$url/api/content/$pageId")
                .header("Content-Type" to "application/json")
                .authenticate(username, password)
                .responseString().third.get()
    }

    private fun getPageId(url: String, username: String, password: String, spaceId: Int, pageName: String): Int? {

        val spaceKey = getSpaceKey(url, username, password, spaceId) ?: return null

        val (req, resp, res) = Fuel.get("$url/rest/api/space/$spaceKey/content")
                .authenticate("admin", "admin")
                .responseString()

        val pages = JsonParser().parse(res.get()).asJsonObject.getAsJsonObject("page").getAsJsonArray("results")

        val pageId = pages.asSequence()
                .map { it.asJsonObject }
                .find {
                    it["type"].asString == "page" &&
                            it["title"].asString == pageName
                }?.get("id")?.asInt

        return pageId

    }

    private fun getSpaceId(url: String, username: String, password: String, spaceName: String): Int? {

        val (req, resp, res) = Fuel.get("$url/rest/api/space")
                .authenticate(username, password)
                .responseString()

        val spacesJson = JsonParser().parse(res.get()).asJsonObject

        val spaceId = spacesJson.get("results").asJsonArray
                .find {
                    it.asJsonObject.get("name").asString == spaceName
                }?.asJsonObject?.get("id")?.asInt

        return spaceId

    }

    private fun getSpaceKey(url: String, username: String, password: String, spaceId: Int): String? {

        val (req, resp, res) = Fuel.get("$url/rest/api/space")
                .authenticate(username, password)
                .responseString()

        val spacesJson = JsonParser().parse(res.get()).asJsonObject

        val spaceKey = spacesJson.get("results").asJsonArray
                .find {
                    it.asJsonObject.get("id").asInt == spaceId
                }?.asJsonObject?.get("key")?.asString

        return spaceKey

    }

    private fun getNewSpaceObject(key: String, name: String): String {

        val json = """
            {
               "key":"$key",
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