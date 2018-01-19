package com.networkedassets.git4c.data

import java.util.*

data class MacroView(val macro: String, val macroViewStatus: MacroViewStatus) {


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        other as MacroView
        if (!macro.equals(other.macro)) return false
        return true
    }

    override fun hashCode(): Int {
        return Objects.hash(macro)
    }

    enum class MacroViewStatus {
        CHECKING, READY, FAILED
    }

}