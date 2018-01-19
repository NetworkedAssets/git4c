package com.networkedassets.git4c.utils

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.networkedassets.git4c.core.exceptions.ConDocException

object SerializationUtils {

    val log = getLogger()
    private val objectMapper = jacksonObjectMapper()

    val secrets = listOf("key", "ssh", "pass")

    fun <T> deserialize(json: String, clazz: Class<T>): T {
        try {
            return objectMapper.readValue(json, clazz)
        } catch (e: JsonProcessingException) {

            if (secrets.any { json.contains(it) }) {
                log.info("JSON contains secret and won't be printed")
                throw DeserializationException(e.originalMessage)
            } else {
                throw DeserializationException(e)
            }

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