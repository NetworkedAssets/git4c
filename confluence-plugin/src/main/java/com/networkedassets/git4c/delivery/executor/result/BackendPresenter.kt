package com.networkedassets.git4c.delivery.executor.result

interface BackendPresenter<ANSWER, ERROR> {

    fun render(result: Any): ANSWER

    fun error(exception: Throwable): ERROR

}
