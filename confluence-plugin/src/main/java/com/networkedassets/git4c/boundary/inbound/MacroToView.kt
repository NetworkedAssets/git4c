package com.networkedassets.git4c.boundary.inbound

data class MacroToView(val uuid: String, val type: MacroType)

enum class MacroType {
    MULTIFILE,
    SINGLEFILE
}