var MarkupService = (function () {
    return {

        getTree: () => {
            return Vue.http.get(
                UrlService.getRestUrl('documentation', ParamsService.getUuid(), 'tree'))
                .then((response) => {
                    if (response.status !== 200) {
                        throw new Error(response.statusText)
                    }
                    return response.json();
                }).catch((err) => {
                    console.log("MarkupService.getTree", err);
                    return Promise.reject(err);
                });
        },

        getItem: (file) => {
            return Vue.http.post(
                UrlService.getRestUrl('documentation', ParamsService.getUuid(), 'doc-item'), file)
                .then((response) => {
                    if (response.status !== 200) {
                        throw new Error(response.statusText);
                    }
                    return response.json();
                }).catch((err) => {
                    console.log("MarkupService.getItem", err);
                    return Promise.reject(err);
                });
        },

        getDocumentation: () => {
            return Vue.http.get(
                UrlService.getRestUrl('documentation', ParamsService.getUuid()))
                .then((response) => {
                    if (response.status !== 200) {
                        throw new Error(response.statusText);
                    }

                    return response.json();
                }).catch((err) => {
                    console.log("MarkupService.getDocumentation", err);
                    return Promise.reject(err);
                });
        },

        updateDocumentation: () => {
            return Vue.http.post(
                UrlService.getRestUrl('documentation', ParamsService.getUuid(), 'refresh')).then((response) => {
                    if (response.status !== 200) {
                        throw new Error(response.statusText);
                    }
                    return Promise.resolve(response);
                }).catch((err) => {
                    console.log("MarkupService.updateDocumentation", err);
                    return Promise.reject(err);
                });
        },

        getBranches: () => {
            return Vue.http.get(
                UrlService.getRestUrl('documentation', ParamsService.getUuid(), 'branches'))
                .then((response) => {
                    if (response.status !== 200) {
                        throw new Error(response.statusText);
                    }
                    return response.json();
                }).catch((err) => {
                    console.log("MarkupService.getDocumentation", err);
                    return Promise.reject(err);
                });
        },

        temporary: (branch) => {
            return Vue.http.post(
                UrlService.getRestUrl('documentation', ParamsService.getInitialUuid(), 'temporary'), branch)
                .then((response) => {
                    if (response.status !== 200) {
                        throw new Error(response.statusText);
                    }
                    return response.json();
                }).catch((err) => {
                    console.log("MarkupService.temporary", err);
                    return Promise.reject(err);
                });
        },
        getDefaultBranch: () => {
            return Vue.http.get(
                UrlService.getRestUrl('documentation', ParamsService.getInitialUuid(), "defaultBranch" ))
                .then((response) => {
                    if (response.status !== 200) {
                        throw new Error(response.statusText);
                    }
                    return Promise.resolve(response);
                }).catch((err) => {
                    console.log("MarkupService.temporary", err);
                    return Promise.reject(err);
                });
        },

        getGlobs: () => {
            return Vue.http.get(
                UrlService.getRestUrl('documentation', ParamsService.getUuid(), 'globs'))
                .then((response) => {
                    if (response.status !== 200) {
                        throw new Error(response.statusText);
                    }
                    return response.json();
                }).catch((err) => {
                    console.log("MarkupService.getItem", err);
                    return Promise.reject(err);
                });
        },
    }
})();