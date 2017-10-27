package com.networkedassets.git4c.core.datastore.extractors

class LineNumbersExtractorData(
        uuid: String,
        val startLine: Int,
        val endLine: Int
) : ExtractorData(uuid) {
    override val type = "LINENUMBERS"
}