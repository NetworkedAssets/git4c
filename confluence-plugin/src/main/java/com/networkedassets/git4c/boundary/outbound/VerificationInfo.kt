package com.networkedassets.git4c.boundary.outbound

data class VerificationInfo constructor(
        val status: VerificationStatus
) {
    fun isOk(): Boolean {
        return status == (VerificationStatus.OK)
    }
}

enum class VerificationStatus {
    OK,
    SOURCE_NOT_FOUND,
    WRONG_CREDENTIALS,
    WRONG_BRANCH,
    WRONG_URL,
    WRONG_KEY_FORMAT,
    CAPTCHA_REQUIRED,
    REMOVED
}
