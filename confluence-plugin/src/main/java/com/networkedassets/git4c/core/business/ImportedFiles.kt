package com.networkedassets.git4c.core.bussiness

import java.io.Closeable

class ImportedFiles(
        val imported: List<ImportedFileData>,
        private val finished: Closeable
) : Closeable {

    override fun close() {
        finished.close()
    }
}