package com.networkedassets.git4c.infrastructure

import com.networkedassets.git4c.core.common.IdentifierGenerator
import java.util.*

class UuidIdentifierGenerator : IdentifierGenerator {
    override fun generateNewIdentifier(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }
}
