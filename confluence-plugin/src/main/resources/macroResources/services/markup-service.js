var MarkupService = (function () {
    return {

        getTree: function () {
            return this.getDocumentation().then(function () {
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
            });
        },
        getItem: function (file) {
            return Vue.http.post(
                UrlService.getRestUrl('documentation', ParamsService.getUuid(), 'doc-item'), file)
                .then(function (response) {
                    if (response.status !== 200 && response.status !== 202) {
                        throw new Error(response.statusText);
                    }
                    if (response.status === 202){
                        var promise =  new Promise(function(resolve, reject) {
                            setTimeout(function() {
                                resolve(MarkupService.getItem(file));
                            }, 3000);
                        });
                        return promise
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
                    if (response.status !== 200 && response.status!=202) {
                        throw new Error(response.statusText);
                    }
                    if (response.status==202){
                        var promise =  new Promise(function(resolve, reject) {
                            setTimeout(function() {
                                resolve(MarkupService.getDocumentation());
                            }, 3000);
                        });
                        return promise
                    }
                    return response.json();
                }).catch(function(err){
                console.log("MarkupService.getDocumentation", err);
                return Promise.reject(err);
                }) ;
        },

        getLatestRevision: function () {
            return Vue.http.get(
                UrlService.getRestUrl('documentation', ParamsService.getUuid(), "latestRevision"))
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

        updateDocumentation: function () {
            return Vue.http.post(
                UrlService.getRestUrl('documentation', ParamsService.getUuid(), 'refresh')).then(function (response) {
                if (response.status !== 200 && response.status!=202) {
                    throw new Error(response.statusText);
                }
                if (response.status==202){
                    var promise =  new Promise(function(resolve, reject) {
                        setTimeout(function() {
                            resolve(MarkupService.updateDocumentation());
                        }, 2000);
                    });
                    return promise
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
                    console.log("MarkupService.getGlobs", err);
                    return Promise.reject(err);
                });
        },

        updateFile: function (file, content) {

            const o = JSON.stringify({
                file: file.join("/"),
                content: content
            });

            return Vue.http.post(
                UrlService.getRestUrl('documentation', ParamsService.getUuid(), 'updateFile'), o)
                .then(function (response) {
                    if (response.status !== 200) {
                        throw new Error(response.statusText);
                    }
                    return response.json();
                }).catch(function (err) {
                    console.log("MarkupService.updateFile", err)
                    return Promise.reject(err)
                })
        }
    }
})();