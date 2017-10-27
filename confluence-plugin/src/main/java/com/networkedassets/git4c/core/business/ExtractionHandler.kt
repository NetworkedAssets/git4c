package com.networkedassets.git4c.core.business

import com.networkedassets.git4c.core.datastore.extractors.ExtractorData

interface ExtractionHandler<in T> where T: ExtractorData {
    fun extract(content: String, data: T): ExtractionResult
}