package com.networkedassets.git4c.utils

import com.networkedassets.git4c.core.business.MacroExtractor

class EditableMacroExtractor : MacroExtractor {

    val list = mutableSetOf<String>()

    override fun extract() = list.toList()

    fun put(s: String) = list.add(s)

    fun removeAll() = list.clear()

}