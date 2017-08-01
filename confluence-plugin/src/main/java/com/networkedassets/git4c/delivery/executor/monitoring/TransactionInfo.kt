package com.networkedassets.git4c.delivery.executor.monitoring

import com.networkedassets.git4c.utils.genTransactionId


data class TransactionInfo @JvmOverloads constructor(
        val actionClass: Class<*>,
        val transaction: String = genTransactionId(),
        val action: String = actionClass.simpleName.toString(),
        val additionalParams: List<Pair<String, String?>> = listOf()
) {

    val params by lazy {
        val list = arrayListOf<Pair<String, String?>>("ACTION" to action,
                "TRANSACTION" to transaction)
        list.addAll(additionalParams)
        list.toList()
    }

    val logMsg = "${printParams(params)}, MESSAGE="

    override fun toString() = logMsg

    fun printParams(params: List<Pair<String, String?>>) =
            params.map { printIfNotNull(it.first, it.second) }.filterNotNull().joinToString()

    fun printIfNotNull(caption: String, value: String?) = if (value!!.isNotBlank()) "$caption=$value" else null

}