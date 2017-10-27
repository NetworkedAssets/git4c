package com.networkedassets.git4c.core.business

data class Macro(val uuid: String, val type: MacroType) {

    enum class MacroType {
        MULTIFILE,
        SINGLEFILE
    }

}