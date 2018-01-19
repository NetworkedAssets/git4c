package com.networkedassets.git4c.core.business

import com.networkedassets.git4c.core.business.Computation.ComputationState.RUNNING

class Computation<out T>(
        val id: String,
        val state: ComputationState = RUNNING,
        val data: T? = null,
        val error: Exception? = null
) {

    enum class ComputationState {
        RUNNING,
        FINISHED,
        FAILED;
    }
}