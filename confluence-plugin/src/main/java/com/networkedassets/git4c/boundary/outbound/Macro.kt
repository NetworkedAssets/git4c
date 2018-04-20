package com.networkedassets.git4c.boundary.outbound

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

class MultiPageMacro(id: String) : Macro(id)

class SinglePageMacro(id: String, val file: String) : Macro(id)

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
        JsonSubTypes.Type(value = SinglePageMacro::class, name = "SINGLEFILEMACRO"),
        JsonSubTypes.Type(value = MultiPageMacro::class, name = "MULTIFILEMACRO")
)
abstract class Macro(val id: String)