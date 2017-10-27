var MarkupService = (function () {
    return {

        getTree: function () {
            return Vue.http.get(
                UrlService.getRestUrl('documentation', ParamsService.getUuid(), 'tree'))
                .then(function (response) {
                    if (response.status !== 200) {
                        throw new Error(response.statusText)
                    }
                    return response.json();
                }).catch(function (err) {
                    console.log("MarkupService.getTree", err);
                    return Promise.reject(err);
                });
        },

        getItem: function (file) {
            return Vue.http.post(
                UrlService.getRestUrl('documentation', ParamsService.getUuid(), 'doc-item'), file)
                .then(function (response) {
                    if (response.status !== 200) {
                        throw new Error(response.statusText);
                    }
                    return response.json();
                }).catch(function (err) {
                    console.log("MarkupService.getItem", err);
                    return Promise.reject(err);
                });
        },

        getDocumentation: function () {
            return Vue.http.get(
                UrlService.getRestUrl('documentation', ParamsService.getUuid()))
                .then(function (response) {
                    if (response.status !== 200) {
                        throw new Error(response.statusText);
                    }

                    return response.json();
                }).catch(function(err){
                console.log("MarkupService.getDocumentation", err);
                return Promise.reject(err);
                }) ;
        },

        updateDocumentation: function () {
            return Vue.http.post(
                UrlService.getRestUrl('documentation', ParamsService.getUuid(), 'refresh')).then(function (response) {
                if (response.status !== 200) {
                    throw new Error(response.statusText);
                }
                return Promise.resolve(response);
            }).catch(function (err) {
                console.log("MarkupService.updateDocumentation", err);
                return Promise.reject(err);
            });
        },

        getBranches: function () {
            return Vue.http.get(
                UrlService.getRestUrl('documentation', ParamsService.getUuid(), 'branches'))
                .then(function (response) {
                    if (response.status !== 200) {
                        throw new Error(response.statusText);
                    }
                    return response.json();
                }).catch(function (err) {
                    console.log("MarkupService.getDocumentation", err);
                    return Promise.reject(err);
                });
        },

        temporary: function (branch) {
            return Vue.http.post(
                UrlService.getRestUrl('documentation', ParamsService.getInitialUuid(), 'temporary'), branch)
                .then(function (response) {
                    if (response.status !== 200) {
                        throw new Error(response.statusText);
                    }
                    return response.json();
                }).catch(function (err) {
                    console.log("MarkupService.temporary", err);
                    return Promise.reject(err);
                });
        },
        getDefaultBranch: function () {
            return Vue.http.get(
                UrlService.getRestUrl('documentation', ParamsService.getInitialUuid(), "defaultBranch"))
                .then(function (response) {
                    if (response.status !== 200) {
                        throw new Error(response.statusText);
                    }
                    return Promise.resolve(response);
                }).catch(function (err) {
                    console.log("MarkupService.temporary", err);
                    return Promise.reject(err);
                });
        },

        getGlobs: function () {
            return Vue.http.get(
                UrlService.getRestUrl('documentation', ParamsService.getUuid(), 'globs'))
                .then(function (response) {
                    if (response.status !== 200) {
                        throw new Error(response.statusText);
                    }
                    return response.json();
                }).catch(function (err) {
                    console.log("MarkupService.getItem", err);
                    return Promise.reject(err);
                });
        }
    }
})();