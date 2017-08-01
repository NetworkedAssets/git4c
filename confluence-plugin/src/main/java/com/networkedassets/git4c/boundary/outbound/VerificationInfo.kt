package com.networkedassets.git4c.boundary.outbound

import org.codehaus.jackson.annotate.JsonCreator
import org.codehaus.jackson.annotate.JsonProperty

data class VerificationInfo @JsonCreator constructor(
        @JsonProperty("status") val status: VerificationStatus
    ){
    fun isOk():Boolean{
        return status==(VerificationStatus.OK)
      }
}

enum class VerificationStatus{
    OK,
    SOURCE_NOT_FOUND,
    WRONG_CREDENTIALS,
    WRONG_BRANCH,
    WRONG_URL,
    WRONG_KEY_FORMAT,
    CAPTCHA_REQUIRED,
}
