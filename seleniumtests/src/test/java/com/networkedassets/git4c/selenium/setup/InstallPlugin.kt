package com.networkedassets.git4c.selenium.setup

import com.github.kittinunf.fuel.Fuel
import org.junit.Test
import java.io.File
import java.nio.file.Paths

class InstallPlugin {

    @Test
    fun installPlugin() {

        val pluginId = "com.networkedassets.git4c.confluence-plugin"

        val plugin = File("../confluence-plugin/target/confluence-plugin.obr")

        if (!plugin.exists()) {
            throw RuntimeException("Plugin file doesn't exist: ${plugin}")
        }


        val (request, response, result) = Fuel.get("""http://localconfluence:8090/rest/plugins/1.0/?os_authType=basic""").authenticate("admin", "admin").responseString()

        val token = response.headers["upm-token"]!!.first()
        println(request)
        println(result)

        val res2 = Fuel.delete("""http://localconfluence:8090/rest/plugins/1.0/$pluginId-key""").authenticate("admin", "admin").responseString().third

        println(res2)

        val (req3, resp3, res3) = Fuel.upload("""http://localconfluence:8090/rest/plugins/1.0/?token=${token}""")
                .authenticate("admin", "admin")
//                .header("Accept" to "application/json", "X-Atlassian-Token" to "nocheck")
                .source { _, _ ->
                    plugin
                }
                .name {
                    "plugin"
                }.responseString()

//        println(req3)
//        println(resp3)
        println(res3)


//        println(token)

    }


}
