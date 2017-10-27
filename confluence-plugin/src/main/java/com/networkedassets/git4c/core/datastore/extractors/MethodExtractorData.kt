package com.networkedassets.git4c.core.datastore.extractors

class MethodExtractorData(
        uuid: String,
        val method: String
) : ExtractorData(uuid) {
    override val type = "METHOD"
}