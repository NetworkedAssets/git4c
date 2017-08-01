package com.networkedassets.git4c.utils

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.networkedassets.git4c.core.exceptions.ConDocException
import java.io.IOException

object SerializationUtils {

    private val objectMapper = jacksonObjectMapper()

    fun <T> deserialize(json: String, clazz: Class<T>): T {
        try {
            return objectMapper.readValue(json, clazz)
        } catch (e: IOException) {
            throw ConDocException(e)
        }
    }

    fun serialize(`object`: Any): String {
        try {
            return objectMapper.writeValueAsString(`object`)
        } catch (e: JsonProcessingException) {
            throw ConDocException(e)
        }
    }
}