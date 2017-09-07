package com.networkedassets.git4c.core.bussiness

interface ParserPlugin {
    fun getMethods(file: ImportedFileData): List<Method>
    fun getMethod(file: ImportedFileData, methodName: String): Pair<ImportedFileData, Method?>
}