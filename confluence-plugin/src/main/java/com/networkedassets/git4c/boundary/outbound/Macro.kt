package com.networkedassets.git4c.boundary.outbound

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

//data class MultiPageMacro(): Macro()

//data class Macro(val url: String?, val type: Macro.MacroType?, val file: String?, val exists: Boolean = true)

class MultiPageMacro(id: String, url: String) : Macro(id, url)

class SinglePageMacro(id: String, url: String, val file: String) : Macro(id, url)

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
        JsonSubTypes.Type(value = SinglePageMacro::class, name = "SINGLEFILEMACRO"),
        JsonSubTypes.Type(value = MultiPageMacro::class, name = "MULTIFILEMACRO")

)
abstract class Macro(val id: String, val url: String)