package com.networkedassets.git4c.standalone

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.networkedassets.git4c.application.Plugin
import com.networkedassets.git4c.boundary.GetBranchesByMacroIdQuery
import com.networkedassets.git4c.boundary.GetDocumentItemInMacroQuery
import com.networkedassets.git4c.boundary.GetDocumentationByMacroIdQuery
import com.networkedassets.git4c.boundary.GetDocumentationTreeByMacroIdQuery
import com.networkedassets.git4c.delivery.executor.result.BackendRequest
import com.networkedassets.git4c.delivery.executor.result.ServiceApi
import org.jetbrains.ktor.application.ApplicationCall
import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.application.install
import org.jetbrains.ktor.content.default
import org.jetbrains.ktor.content.files
import org.jetbrains.ktor.content.static
import org.jetbrains.ktor.features.CORS
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.jetty.Jetty
import org.jetbrains.ktor.pipeline.PipelineContext
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.route
import org.jetbrains.ktor.routing.routing
import java.net.URLDecoder
import kotlin.reflect.KClass

class MainServer(val plugin: Plugin): ServiceApi {

    override val dispatcher = plugin.components.dispatcherHttp

    fun start() {
        embeddedServer(Jetty, 8080) {
            install(CORS) {
                anyHost()
            }
            routing {
                static("/") {
                    val dir = "confluence-plugin/src/main/resources/macroResources"
                    files(dir)
                    default("$dir/index.html")
                }
                route("/rest/documentation/{uuid}") {
                    get {
                        executeRequest(GetDocumentationByMacroIdQuery("1"))
                    }
                    get("tree") {
                        executeRequest(GetDocumentationTreeByMacroIdQuery("1"))
                    }
                    get("doc-items/{item}") {
                        val documentId = call.parameters["item"]!!
                        executeRequest(GetDocumentItemInMacroQuery("1", documentId.urlDecode()))
                    }
                    get("branches") {
                        executeRequest(GetBranchesByMacroIdQuery("1"))
                    }
                }
            }
        }.start(wait = true)
    }

//    fun <T> Route.execute(req: BackendRequest<T>) {
//
//    }

    private val objectMapper = jacksonObjectMapper()

    fun <T: Any> String.deserialize(clazz: KClass<T>) = objectMapper.readValue(this, clazz.java)

//    private fun <TSubject> PipelineContext<Any>.execute(subject: String) {
//    }

    fun Any.serialize(): String = objectMapper.writeValueAsString(this)

    suspend private fun <T: Any> PipelineContext<ApplicationCall>.executeRequest(s: BackendRequest<T>) {

        val t = plugin.components.executor.executeRequest<Any>(s)
        if (t.component1() != null) {
            call.respondText(t.component1()!!.serialize())
        } else {
            t.component2()!!.printStackTrace()
            call.respondText(t.component2()!!.serialize())
        }
    }

    private fun String.urlDecode() = URLDecoder.decode(this, "UTF-8")

}
