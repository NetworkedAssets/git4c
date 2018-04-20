var Git4CApi = {

    /**
     * @returns {Promise<T>}
     */
    getMacroInformation: function (macroUuid) {

        return RepeatableAsyncRequest(
            function() { return Vue.http.get(UrlService.getRestUrl('documentation', macroUuid))}
        )
            .then(function (response) {
                return response.body
            })
    },

    getMacroDocumentationTree: function (macroUuid) {

        return RepeatableAsyncRequest(
            function () { return Vue.http.get(UrlService.getRestUrl('documentation', macroUuid, 'tree')) }
        )
            .then(function (response) {
                return response.body
            })
    },

    getMacroDocumentationItem: function (macroUuid, file) {

        return Git4CApi.makeCancelable(
            RepeatableAsyncRequest(
                function () { return Vue.http.post(UrlService.getRestUrl('documentation', macroUuid, 'doc-item'), { file: file }) }
            )
            .then(function (response) {
                return response.body
            })
        )

    },

    /**
     * Returns current branch for given macro
     * @param macroUuid {String}
     * @returns {Promise<String>}
     */
    getCurrentBranchForMacroUuid: function(macroUuid) {

        return new AsyncRequest(
            Vue.http.post(UrlService.getRestUrl('documentation', macroUuid, 'branches')),
            UrlService.getRestUrl("documentation", macroUuid, "branches", "result")
        )
            .then(function (response) {
                return response.body.currentBranch
            })

    },

    /**
     * Returns repository path for given macro
     * @param macroUuid {String}
     * @returns {Promise<String>}
     */
    getRepositoryPathForMacroUuid: function (macroUuid) {

        return Vue.http.get(UrlService.getRestUrl('documentation', macroUuid, 'repository'))
            .then(function (response) {
                return response.body.path
            })

    },

    /**
     * Returns temporary edit branch (if exists) for given macro
     * @param macroUuid {String}
     * @returns {Promise<String>}
     */
    getTemporaryEditBranchForMacroUuid: function (macroUuid) {

        return AsyncRequest(
            Vue.http.post(UrlService.getRestUrl('documentation', macroUuid, 'editBranch')),
            UrlService.getRestUrl('documentation', macroUuid, 'editBranch')
        ).then(function (response) {
            return response.body.branch
        })

    },

    /**
     * Creates temporary macro for given macro and its branch
     * @param macroUuid {String}
     * @param branch {String}
     * @returns {Promise<String>} Uuid of temporary macro
     */
    createTemporaryMacroForMacroAndBranch: function (macroUuid, branch) {
        return Vue.http.post(UrlService.getRestUrl('documentation', macroUuid, 'temporary'), {branch: branch})
            .then(function (response) {
                return response.body.id
            })
    },

    getExtractorDataForMacro: function (macroUuid) {
        return Vue.http.get(UrlService.getRestUrl('documentation', macroUuid, "extractorData"))
            .then(function (response) {
                return response.body
            })
    },

    /**
     * @returns {Promise<String>}
     */
    getLatestRevisionForMacro: function (macroUuid) {
        return new AsyncRequest(
            Vue.http.post(
                UrlService.getRestUrl('documentation', macroUuid, "latestRevision")
            ),
            UrlService.getRestUrl('documentation', macroUuid, "latestRevision", "result")
        )
            .then(function (response) {
                return response.body.id
            })
    },

    getBranchesForMacro: function (macroUuid) {

        return new AsyncRequest(
            Vue.http.post(
                UrlService.getRestUrl('documentation', macroUuid, 'branches')
            ),
            UrlService.getRestUrl('documentation', macroUuid, 'branches', "result")
        )
            .then(function (response) {
                return response.body
            })

    },

    getDefaultBranchForMacro: function (macroUuid) {

        return Vue.http.get(
            UrlService.getRestUrl('documentation', macroUuid, "defaultBranch")
        )
            .then(function (response) {
                return response.body
            })

    },

    getGlobsForMacro: function (macroUuid) {

        return Vue.http.get(
            UrlService.getRestUrl('documentation', macroUuid, 'globs')
        )
            .then(function (response) {
                return response.body.globs
            })

    },

    /**
     * @param macroUuid {String}
     * @param json {String}
     */
    updateFileForMacro: function (macroUuid, json) {

        return AsyncRequest(
            Vue.http.post(UrlService.getRestUrl('documentation', macroUuid, 'file', 'publishFile'), json),
            UrlService.getRestUrl('documentation', 'request', 'publishFile', 'result')
        )
            .then(function (response) {
                return response.body
            })

    },


    /**
     *
     * @param macroUuid {String}
     * @param file {String}
     * @param content {String}
     * @returns {PromiseLike<T>}
     */
    generatePreviewForMacro: function (macroUuid, file, content) {

        const o = {
            file: file,
            content: content
        }

        return new AsyncRequest(
            Vue.http.post(
                UrlService.getRestUrl('documentation', macroUuid, 'file', 'preview'), o
            ),
            UrlService.getRestUrl("documentation", macroUuid, "file", "preview", "result")
        )
            .then(function (response) {
                return response.body
            })

    },

    getListOfCommitsForMacro: function (macroUuid, branch, file) {

        const details = {
            branch: branch,
            file: file
        }

        return Git4CApi.makeCancelable(
            new AsyncRequest(
                Vue.http.post(UrlService.getRestUrl('documentation', macroUuid, 'file', 'commits'), details),
                UrlService.getRestUrl("documentation", macroUuid, "file", "commits", "result")
            )
                .then(function (response) {
                    return response.body.commitList
                })
        )

        // return Vue.http.post(restUrl + "/" + this.macroUuid + "/file/commits", details)

    },

    verifyRepository: function (repositoryUrl, credentials) {

        const o = {
            sourceRepositoryUrl: repositoryUrl,
            credentials: credentials
        }

        return Vue.http.post(UrlService.getRestUrl("documentation", "repository", "verify"), o)
            .then(function (response) {
                return response.body
            })

    },


    getBranches: {

        forCustomRepository: function(repositoryUuid) {

            return Git4CApi.makeCancelable(
                new AsyncRequest(
                    Vue.http.post(
                        UrlService.getRestUrl("documentation", "repository", repositoryUuid, "branches")
                    ),
                    UrlService.getRestUrl("documentation", "repository", repositoryUuid, "branches", "result")
                )
                    .then(function (response) {
                        return response.body
                    })
            )

        },

        forPredefinedRepository: function(repositoryUuid) {

            return Git4CApi.makeCancelable(
                new AsyncRequest(
                    Vue.http.post(
                        UrlService.getRestUrl("documentation", "predefine", repositoryUuid, "branches")
                    ),
                    UrlService.getRestUrl("documentation", "predefine", repositoryUuid, "branches", "result")
                )
                    .then(function (response) {
                        return response.body
                    })
            )

        },

        forRepository: function(repositoryUrl, credentials) {

            const o = {
                sourceRepositoryUrl: repositoryUrl,
                credentials: credentials
            }

            return Git4CApi.makeCancelable(
                new AsyncRequest(
                    Vue.http.post(
                        UrlService.getRestUrl("documentation", "repository", "branches"), o
                    ),
                    UrlService.getRestUrl("documentation", "repository", "branches", "result")
                )
                    .then(function (response) {
                        return response.body
                    })
            )


        }

    },


    getFilesTree: {

        forCustomRepository: function (repositoryUuid, branch) {

            const o = {
                branch: branch
            }

            return Git4CApi.makeCancelable(
                new AsyncRequest(
                    Vue.http.post(
                        UrlService.getRestUrl("documentation", "repository", repositoryUuid, "files"), o
                    ),
                    UrlService.getRestUrl("documentation", "repository", repositoryUuid, "files", "result")
                )
                    .then(function (response) {
                        return response.body
                    })
            )
        },

        forPredefinedRepository: function (repositoryUuid, branch) {

            const o = {
                branch: branch
            }

            return Git4CApi.makeCancelable(
                new AsyncRequest(
                    Vue.http.post(
                        UrlService.getRestUrl("documentation", "predefine", repositoryUuid, "files"), o
                    ),
                    UrlService.getRestUrl("documentation", "predefine", repositoryUuid, "files", "result")
                )
                    .then(function (response) {
                        return response.body
                    })
            )

        },

        forRepository: function (repositoryUrl, credentials, branch) {

            const o = {
                sourceRepositoryUrl: repositoryUrl,
                credentials: credentials,
                branch: branch
            }

            return Git4CApi.makeCancelable(
                new AsyncRequest(
                Vue.http.post(
                    UrlService.getRestUrl("documentation", "repository", "files"), o
                ),
                    UrlService.getRestUrl("documentation", "repository", "files", "result")
                )
                    .then(function (response) {
                        return response.body
                    })
            )

        }

    },

    getFile: {

        forCustomRepository: function (repositoryUuid, branch, file) {

            const o = {
                branch: branch,
                file: file
            }

            return Git4CApi.makeCancelable(
                new AsyncRequest(
                    Vue.http.post(
                        UrlService.getRestUrl("documentation", "repository", repositoryUuid, "file"), o
                    ),
                    UrlService.getRestUrl("documentation", "repository", repositoryUuid, "file", "result")
                )
                    .then(function (response) {
                        return response.body
                    })
            )

        },

        forPredefinedRepository: function (repositoryUuid, branch, file) {

            const o = {
                branch: branch,
                file: file
            }

            return Git4CApi.makeCancelable(
                new AsyncRequest(
                    Vue.http.post(
                        UrlService.getRestUrl("documentation", "predefine", repositoryUuid, "file"), o
                    ),
                    UrlService.getRestUrl("documentation", "predefine", repositoryUuid, "file", "result")
                )
                    .then(function (response) {
                        return response.body
                    })
            )

        },

        forRepository: function (repositoryUrl, credentials, branch, file) {

            const o = {
                sourceRepositoryUrl: repositoryUrl,
                credentials: credentials,
                branch: branch,
                file: file
            };

            return Git4CApi.makeCancelable(
                new AsyncRequest(
                    Vue.http.post(
                        UrlService.getRestUrl("documentation", "repository", "file"), o
                    ),
                    UrlService.getRestUrl("documentation", "repository", "file", "result")
                )
                    .then(function (response) {
                        return response.body
                    })
            )

        }

    },

    getMethods: {

        forCustomRepository: function (repositoryUuid, branch, file) {

            const o = {
                branch: branch,
                file: file
            }

            return Git4CApi.makeCancelable(
                new AsyncRequest(
                    Vue.http.post(
                        UrlService.getRestUrl("documentation", "repository", repositoryUuid, "methods"), o
                    ),
                    UrlService.getRestUrl("documentation", "repository", repositoryUuid, "methods", "result")
                )
                    .then(function (response) {
                        return response.body
                    })
            )

        },

        forPredefinedRepository: function (repositoryUuid, branch, file) {

            const o = {
                branch: branch,
                file: file
            }

            return Git4CApi.makeCancelable(
                new AsyncRequest(
                    Vue.http.post(
                        UrlService.getRestUrl("documentation", "predefine", repositoryUuid, "methods"), o
                    ),
                    UrlService.getRestUrl("documentation", "predefine", repositoryUuid, "methods", "result")
                )
                    .then(function (response) {
                        return response.body
                    })
            )

        },

        forRepository: function (repositoryUrl, credentials, branch, file) {

            const o = {
                sourceRepositoryUrl: repositoryUrl,
                credentials: credentials,
                branch: branch,
                file: file
            };

            return Git4CApi.makeCancelable(
                new AsyncRequest(
                    Vue.http.post(
                        UrlService.getRestUrl("documentation", "repository", "file", "methods"), o
                    ),
                    UrlService.getRestUrl("documentation", "repository","file", "methods", "result")
                )
                    .then(function (response) {
                        return response.body
                    })
            )

        }

    },

    createMacro: function (obj) {

        return new AsyncRequest(
            Vue.http.post(UrlService.getRestUrl("documentation", "creation"), obj),
            UrlService.getRestUrl("documentation", "creation", "result")
        )
            .then(function (response) {
                return response.body
            })

    },

    getGlobs: function () {
        return Vue.http.get(
            UrlService.getRestUrl("documentation", "glob") + "?timestamp="+$.now()
        )
            .then(function (response) {
                return response.body.globs
            })
    },

    createGlob: function (glob) {

        return Vue.http.post(
            UrlService.getRestUrl("documentation", "glob"), glob
        )

    },

    deleteGlob: function (globId) {
        return Vue.http.delete(
            UrlService.getRestUrl("documentation", "glob", globId)
        )
    },

    getPredefinedRepositories: function () {
        return Vue.http.get(UrlService.getRestUrl("documentation", "predefine") + "?timestamp="+$.now())
            .then(function (response) {
                return response.body
            })
    },

    createPredefinedRepository: function (repository) {
        return new AsyncRequest(
            Vue.http.post(UrlService.getRestUrl("documentation", "predefine"), repository),
            UrlService.getRestUrl("documentation", "predefine", "result")
        )
    },

    updatePredefinedRepository: function (repositoryId, repository) {
        return new AsyncRequest(
            Vue.http.post(UrlService.getRestUrl("documentation", "predefine", repositoryId, "modify"), repository),
            UrlService.getRestUrl("documentation", "predefine", repositoryId, "modify", "result")
        )
    },

    deletePredefinedRepository: function (repositoryId) {
        return Vue.http.delete(
            UrlService.getRestUrl("documentation", "predefine", repositoryId)
        )
    },

    getPredefinedRepositoriesForceSetting: function () {

        return Vue.http.get(
            UrlService.getRestUrl("documentation", "settings", "repository", "predefine", "force")
        )
            .then(function (response) {
                return response.body
            })

    },

    setPredefinedRepositoriesForceSetting: function (force) {

        const o = {
            toForce: force
        }

        return Vue.http.post(
            UrlService.getRestUrl("documentation", "settings", "repository", "predefine", "force"), o
        )
            .then(function (response) {
                return response.body
            })

    },

    getRepositoryUsages: function () {

        return Vue.http.get(
            UrlService.getRestUrl("documentation", "repository", "usages")
        ).then(function(response) {
            return response.body.usages
        })

    },

    restoreDefaultGlobs: function () {
        return Vue.http.head(UrlService.getRestUrl("documentation", "glob"))
    },

    getAllSpaces: function () {
        return AsyncRequest(
            Vue.http.post(UrlService.getRestUrl("documentation", "spaces")),
            UrlService.getRestUrl('documentation', 'spaces', "result")
        )
            .then(function (response) {
                return response.body.spaces
            })
    },

    verifyMacro: function (macroUuid) {

        return new AsyncRequest(
            Vue.http.post(
                UrlService.getRestUrl("documentation", macroUuid, "verify")
            ),
            UrlService.getRestUrl("documentation", macroUuid, "verify", "result")
        )
            .then(function (response) {
                return response.body
            })

    },

    getExecutorsSettings: function () {

        return Vue.http.get(
            UrlService.getRestUrl("documentation", "executors")
        )
            .then(function (response) {
                return response.body
            })

    },

    setExecutorSettings: function (settings) {

        return Vue.http.post(
            UrlService.getRestUrl("documentation", "executors"), settings
        )

    },

    cleanData: function () {

        return Vue.http.delete(
            UrlService.getRestUrl("documentation", "remove", "all")
        )

    },

    cleanUnusedData: function () {

        return Vue.http.delete(
            UrlService.getRestUrl("documentation", "remove", "unused")
        )
    },


    //private

    makeCancelable: function (promise) {

        var stop = false

        var stopper = function () {
            stop = true
        }

        const stoppablePromise = promise
            .then(function (value) {

                if (stop) {
                    return new Promise(function() {})
                } else {
                    return Promise.resolve(value)
                }

            })
            .catch(function (reason) {

                if (stop) {
                    return new Promise(function() {})
                } else {
                    return Promise.reject(reason)
                }

            })

        stoppablePromise["stop"] = stopper
        stoppablePromise["cancel"] = stopper

        return stoppablePromise
    }

}