var downloadFile = function (uuid2) {

    var uuid = uuid2

    const getTemporaryEditBranch = function () {
        return Vue.http.get(UrlService.getRestUrl('documentation', uuid, 'editBranch'))
            .then(function (response) {
                if (response.status !== 200) {
                    return Promise.reject(response.statusText)
                } else {
                    return response.body.requestId
                }
            })
            .then(function fun(requestId) {

                return Vue.http.get(UrlService.getRestUrl('documentation', 'editBranch', requestId))
                    .then(function (response) {
                        if (response.status !== 200 && response.status !== 202) {
                            return Promise.reject(response.statusText)
                        }
                        if (response.status === 200) {
                            return response.body.branch
                        }

                        return new Promise(function (resolve) {
                            setTimeout(function () {
                                resolve(fun(requestId))
                            }, 2000)
                        })

                    })

            })
    }

    const getCorrectUuid = function () {

        return getTemporaryEditBranch()
            .then(function (branch) {

                if (branch) {
                    return Vue.http.post(UrlService.getRestUrl('documentation', uuid, 'temporary'), {branch: branch})
                        .then(function (response) {
                            uuid = response.body.id
                        })

                } else {
                    return ""
                }

            })

    }

    const processResponseFromDoc = function (response) {
        if (response.status !== 200 && response.status !== 202) {
            return Promise.reject(response.statusText)
        }
        if (response.status === 202) {
            return new Promise(function (resolve, reject) {
                setTimeout(function () {
                    resolve(Vue.http.get(UrlService.getRestUrl('documentation', uuid)).then(function (response) {
                        return processResponseFromDoc(response)
                    }));
                }, 2000);
            });
        }
        return Vue.http.get(UrlService.getRestUrl('documentation', uuid, 'tree'))
    }

    console.log("Download file")


    //Cache document
    const getDocumentationWithFile = function () {
        return Vue.http.get(UrlService.getRestUrl('documentation', uuid))
            .then(function (response) {
                return processResponseFromDoc(response)
            })
            .then(function (response) {
                if (response.status !== 200) {
                    return Promise.reject(response.statusText)
                }

                var file = response.body

                while (file.type !== "DOCITEM") {
                    file = file.children[0]
                }

                const fullName = file.fullName

                const toSend = {
                    file: fullName
                }

                return Vue.http.post(UrlService.getRestUrl('documentation', uuid, 'doc-item'), toSend)
            })
    }


    const processResponseFromDocItem = function (response) {
        if (response.status !== 200 && response.status !== 202) {
            return Promise.reject(response.statusText)
        }
        if (response.status == 202) {
            var promise = new Promise(function (resolve, reject) {
                setTimeout(function () {
                    resolve(getDocumentationWithFile().then(function (response) {
                        return processResponseFromDocItem(response)
                    }));
                }, 2000);
            });
            return promise
        }
        return response
    }


    return getCorrectUuid()
        .then(getDocumentationWithFile)
        .then(function (response) {
            return processResponseFromDocItem(response)
        })
        .then(function (response) {
            if (response.status !== 200) {
                return Promise.reject(response.statusText)
            }

            return [uuid, response.body]
        })
};