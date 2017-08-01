package com.networkedassets.git4c.boundary.outbound.exceptions

import com.networkedassets.git4c.delivery.executor.monitoring.TransactionInfo

class NotFoundException(transactionInfo: TransactionInfo, msg: String) : Exception("$transactionInfo $msg")

class ConflictException(transactionInfo: TransactionInfo, msg: String) : Exception("$transactionInfo $msg")