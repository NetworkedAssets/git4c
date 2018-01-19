package com.networkedassets.git4c.utils

import com.fasterxml.jackson.core.JsonProcessingException
import java.io.IOException

class DeserializationException: IOException {
    constructor(msg: String): super(msg)
    constructor(ex: JsonProcessingException): super(ex)
}