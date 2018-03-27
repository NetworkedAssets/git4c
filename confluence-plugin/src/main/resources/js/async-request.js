var AsyncRequest = function (
    request,
    repeatEndpoint
) {

    var stop = false
    var req = undefined

    var stopper = function () {
        stop = true
        if (req) {
            req.cancel()
        }
    }

    const promise = request
        .then(function (response) {
            if (response.status !== 200) {
                return Promise.reject(response.statusText)
            } else {
                return response.body.requestId
            }
        })
        .then(function fun(requestId) {

            const before = {
                before: function(request) {
                    req = request
                }
            }

            return Vue.http.get(repeatEndpoint + "/" + requestId, before)
                .then(function (response) {

                    if (response.status !== 200 && response.status !== 202) {
                        return Promise.reject(response.statusText)
                    }
                    if (response.status === 200) {
                        return response
                    }

                    return new Promise(function (resolve) {
                        setTimeout(function () {
                            resolve(fun(requestId))
                        }, 2000)
                    })

                })

        })
        .then(function (value) {

            if (stop) {
                return Promise()
            } else {
                return Promise.resolve(value)
            }

        })
        .catch(function (reason) {

            if (stop) {
                return Promise()
            } else {
                return Promise.reject(reason)
            }

        })

    promise.cancel = stopper

    return promise

}

var RepeatableAsyncRequest = function (
    requestFunction
) {

    return requestFunction()
        .then(function fun(response) {

            if (response.status === 202) {
                return new Promise(function (resolve) {
                    setTimeout(function () {
                        requestFunction()
                            .then(function (value) {
                                resolve(fun(value))
                            })
                    }, 2000)
                })
            } else {
                return response
            }
        })
        .then(function (response) {
            return response
        })


}
