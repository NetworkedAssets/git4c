var CancelableRequest = function (
    request
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
            return response.body
        })
        .then(function (value) {

            if (stop) {
                return new Promise()
            } else {
                return value
            }

        })
        .catch(function (reason) {

            if (stop) {
                return Promise()
            } else {
                return Promise.reject(reason)
            }

        })

    promise.stop = stopper

    return promise
}
