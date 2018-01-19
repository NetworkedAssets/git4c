package com.networkedassets.git4c.data

import java.util.*

data class DocumentView(val uniqueIdOfDocument: String, val macroViewStatus: MacroViewStatus) {


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        other as DocumentView
        if (!uniqueIdOfDocument.equals(other.uniqueIdOfDocument)) return false
        return true
    }

    override fun hashCode(): Int {
        return Objects.hash(uniqueIdOfDocument)
    }

    enum class MacroViewStatus {
        TO_CONVERT, READY
    }

}