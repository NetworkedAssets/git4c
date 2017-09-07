package com.networkedassets.git4c.core.bussiness

import java.io.Closeable

class Revision(
        val revision: String,
        private val finished: Closeable
) : Closeable {

    override fun close() {
        finished.close()
    }
}