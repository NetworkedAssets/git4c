package com.networkedassets.git4c.core.business

import com.networkedassets.git4c.data.MacroSettings

data class Macro(val uuid: String, val type: MacroType) {
    constructor() : this("", MacroType.SINGLEFILE)

    enum class MacroType {
        MULTIFILE,
        SINGLEFILE
    }

    companion object {
        fun from(macroSettings: MacroSettings): Macro {
            val macroType = Macro.MacroType.valueOf(macroSettings.type?.name ?: MacroType.SINGLEFILE.name)
            val macro = Macro(macroSettings.uuid, macroType)
            return macro
        }
    }
}