var downloadFile = function (uuid) {

    console.log("Download file")

    //Cache document
    return Vue.http.get(UrlService.getRestUrl('documentation', uuid))
        .then((response) => {
            if (response.status !== 200) {
                return Promise.reject(response.statusText)
            }
            return Vue.http.get(UrlService.getRestUrl('documentation', uuid, 'tree'))
        })
        .then((response) => {
            if (response.status !== 200) {
                return Promise.reject(response.statusText)
            }
            let file = response.body

            while (file.type !== "DOCITEM") {
                file = file.children[0]
            }

            const fullName = file.fullName

            const toSend = {
                file: fullName
            }

            return Vue.http.post(UrlService.getRestUrl('documentation', uuid, 'doc-item'), toSend)
        }).then((response) => {
            if (response.status !== 200) {
                return Promise.reject(response.statusText)
            }

            return response.body
        })
};