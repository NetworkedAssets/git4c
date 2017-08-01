package com.networkedassets.git4c.utils

import kotlin.reflect.KClass

object ThrowableUtils {
    fun Throwable.getAllCauses(): Sequence<Throwable> = generateSequence(this, { it.cause })
    fun Throwable.isCausedBy(c: KClass<out Throwable>) = this.getAllCauses().any { c.isInstance(it) }
}