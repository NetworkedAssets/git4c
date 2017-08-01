package com.networkedassets.git4c.core.bussiness


interface FilterPlugin {
    fun filter(file: ImportedFileData): Boolean
}
