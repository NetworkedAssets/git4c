package com.networkedassets.git4c.interfaces.rest

import com.fasterxml.jackson.core.JsonProcessingException
import com.networkedassets.git4c.utils.SerializationUtils
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.Test

class DeserializationTest {

    @Test
    fun `Malformed JSON doesn't leak secrets`() {


        val json = """

            {
                "username": "admin",
                "password": "supersecretpassword"
                "error

            }

            """


        val ex = catchThrowable { SerializationUtils.deserialize(json, ClassWithSecrets::class.java) }

        assertThat(ex).isNotNull()
        assertThat(ex).hasNoCause()

        val message = ex.message
        assertThat(message).doesNotContain("supersecretpassword")
    }

    @Test
    fun `JSON with additional fields doesn't leak secrets`() {


        val json = """

            {
                "username": "admin",
                "password": "password",
                "supersecredfield": "supersecretkey"

            }

            """


        val ex = catchThrowable { SerializationUtils.deserialize(json, ClassWithSecrets::class.java) }
        assertThat(ex).isNotNull()
        assertThat(ex).hasNoCause()

        val message = ex.message
        assertThat(message).doesNotContain("supersecretkey")
    }

    @Test
    fun `JSON without secrets will rethrow exception`() {

        val json = """

            {
                "repository": "https://github.com/NetworkedAssets/git4c"
                "info

            }

            """

        val ex = catchThrowable { SerializationUtils.deserialize(json, ClassWithSecrets::class.java) }

        assertThat(ex).isNotNull()
        assertThat(ex).hasCauseInstanceOf(JsonProcessingException::class.java)

    }

    @Test
    fun `JSON without errors won't throw exceptions`() {

        val json = """

            {
                "username": "admin",
                "password": "password"

            }

            """

        val ex = catchThrowable { SerializationUtils.deserialize(json, ClassWithSecrets::class.java) }
        assertThat(ex).isNull()

    }

    data class ClassWithSecrets(val username: String, val password: String)

}