var MarkupService = {

    getTree: function () {
        console.log("Macro is: "+ParamsService.getUuid())

        return Git4CApi.getMacroDocumentationTree(ParamsService.getUuid())
            .catch(function (err) {
                console.log("MarkupService.getTree", err);
                return Promise.reject(err);
            })
    },

    getItemPromise: undefined,
    getItem: function (file) {
        if (this.getItemPromise) {
            this.getItemPromise.cancel()
        }

        const promise = Git4CApi.getMacroDocumentationItem(ParamsService.getUuid(), file)

        this.getItemPromise = promise

        return promise
            .catch(function (err) {
                console.log("MarkupService.getItem", err);
                return Promise.reject(err);
            })

    },
    getDocumentation: function () {

        return Git4CApi.getMacroInformation(ParamsService.getUuid())
            .catch(function (err) {
                console.log("MarkupService.getDocumentation", err);
                return Promise.reject(err);
            })
    },

    getLatestRevision: function () {
        return Git4CApi.getLatestRevisionForMacro(ParamsService.getUuid())
            .catch(function (err) {
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
        return Git4CApi.getBranchesForMacro(ParamsService.getUuid())
            .catch(function (err) {
                console.log("MarkupService.getBranches", err);
                return Promise.reject(err);
            });
    },

    temporary: function (branch) {

        return Git4CApi.createTemporaryMacroForMacroAndBranch(ParamsService.getInitialUuid(), branch)
            .catch(function (err) {
                console.log("MarkupService.temporary", err);
                return Promise.reject(err);
            })
    },

    getDefaultBranch: function () {

        return Git4CApi.getDefaultBranchForMacro(ParamsService.getInitialUuid())
            .catch(function (err) {
                console.log("MarkupService.temporary", err);
                return Promise.reject(err);
            });

    },

    getGlobs: function () {

        return Git4CApi.getGlobsForMacro(ParamsService.getUuid())
            .catch(function (err) {
                console.log("MarkupService.getGlobs", err);
                return Promise.reject(err);
            });

    }

}
