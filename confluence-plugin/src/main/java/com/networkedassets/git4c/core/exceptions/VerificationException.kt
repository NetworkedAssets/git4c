package com.networkedassets.git4c.core.exceptions

import com.networkedassets.git4c.boundary.outbound.VerificationInfo

class VerificationException(val verification: VerificationInfo) : RuntimeException()