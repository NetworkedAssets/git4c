const router = new VueRouter({
    mode: 'hash',
    routes: [
        {path: '/:fullName?&:branch?', component: Markup}
    ]
});

var Events = new Vue({});

AJS.toInit(function () {

    let lastRevision = undefined;
    let alertShown = false;
    let intervalId = undefined;

    const startInterval = function () {

        if (intervalId) {
            return
        }

        intervalId = setInterval(function () {
            MarkupService.getDocumentation().then((documentation) => {
                if (!lastRevision) {
                    lastRevision = documentation.revision
                } else {
                    if (lastRevision !== documentation.revision) {
                        lastRevision = documentation.revision;
                        if (!alertShown) {
                            NotifyService.persistent("Git Viewer for Confluence macro is out of date",
                                '<ul class="aui-nav-actions-list">' +
                                '<li><a href="#" onclick="location.reload(true); return false;">Refresh page</a></li>' +
                                '</ul>');
                            clearInterval(intervalId);
                            alertShown = true
                        }
                    }
                }
            })
        }, 10000);
    }


    ParamsService.initialize()

    var defaultFileName = "";
    var vue = new Vue({
        router: router,
        el: "#app",
        data: {
            value: '',
            tree: []
        },
        components: {
            overlay: Git4COverlay.getComponent(Events)
        },
        methods: {
            getDefaultDocItemName: function (element) {
                var docItemName = ParamsService.getParams().defaultDocItem;
                if (docItemName.length != 0) {
                    var contains = false;
                    element.children.forEach(function f(el) {
                        if (el.fullName.toLowerCase() == docItemName.toLowerCase()) {
                            docItemName = el.fullName
                            contains = true;
                        }
                        else el.children.forEach(ele => f(ele))
                    });
                    if (contains) {
                        return docItemName
                    }
                }
                if (element.children != null) {
                    docItemName = element.children.filter((docItem) => {
                        return (defaultFileName == docItem.name.toLowerCase());
                    }).map((docItem) => {
                        return docItem.name;
                    });

                    if (docItemName.length == 0) {
                        docItemName = element.children.filter((docItem) => {
                            return (docItem.type !== "DIR");
                        }).map((docItem) => {
                            return docItem.name;
                        });
                    }
                }
                return $.isArray(docItemName) ? docItemName[0] : docItemName;

            },
            getTree: function () {
                MarkupService.getTree().then((tree) => {
                    Vue.set(this, 'tree', tree.children);
                    if (!this.$route.params.fullName) {
                        var defaultDocItemName = this.getDefaultDocItemName(tree);
                        if (defaultDocItemName) {
                            this.$router.push('/' + encodeURIComponent(defaultDocItemName) + "&" + encodeURIComponent(this.getBranchNameFromPath()));
                        }
                    }
                    var nodesToOpen = decodeURIComponent(this.$route.params.fullName).split('/');
                    const fullFileName = this.$route.params.fullName
                    if (nodesToOpen.length > 1) {
                        function openTree(node, level) {

                            if (node.type === "DOCITEM") {
                                if (node.fullName === fullFileName) {
                                    return node
                                } else {
                                    return null
                                }
                            }

                            const below = node.children.filter(n => fullFileName.startsWith(n.fullName)).map(n => openTree(n, level + 1)).filter(n => n)[0];

                            let finalArr;

                            if (below) {
                                finalArr = [node].concat(below)
                            } else {
                                finalArr = null
                            }

                            if (level === 0) {
                                if (finalArr) {
                                    [node].concat(below).forEach(n => n.isOpened = true)
                                }
                                return finalArr
                            } else {
                                return finalArr
                            }
                        }
                        if (!openTree(tree, 0)) {
                            this.clearFilenameInPath()
                            this.getTree()
                            return
                        }
                    }
                    Events.$emit('treeLoaded');


                }, () => {
                    NotifyService.error('Error', 'An error occurred while displaying content.')
                });
            },
            getDocumentation: function () {
                //this.working = true;
                Events.$emit('updateStart');
                MarkupService.getDocumentation().then((documentation) => {
                        //  this.working = false;
                        startInterval()
                        if (documentation != undefined) {
                            Events.$emit('updateComplete');
                        } else {
                            Events.$emit('updateError');
                        }
                    },
                    (err) => {
                        console.log(err);
                        if (err.status == 404) {
                            Events.$emit('removedRepositoryError');
                        } else
                            Events.$emit('updateError');
                    });

            },
            getFileNameFromPath: function () {
                var fullPath = this.$route.path;
                fullPath = fullPath.split("&");
                return (fullPath[0] ? fullPath[0] : "")
            },
            getBranchNameFromPath: function () {
                var fullPath = this.$route.path;
                fullPath = fullPath.split("&");
                return (fullPath[1] ? fullPath[1] : "master")
            },
            clearFilenameInPath: function () {
                this.pushBranch(this.getBranchNameFromPath(), false)
            },
            getTemporary: function (branch) {
                //this.working = true;
                Events.$emit('updateStart');

                MarkupService.getBranches().then((branchesResponse) => {

                    const branches = branchesResponse.allBranches

                    if ($.inArray(branch.branch, branches) === -1) {
                        console.log("Branch doesn't exist")
                        // NotifyService.error('Error', `Requested branch doesn't exist. Please select another one.`)
                        Events.$emit('branchDoesntExist');
                    } else {
                        console.log("Branch exists")
                        MarkupService.temporary(branch).then((id) => {
                                //  this.working = false;
                                startInterval()
                                if (id) {
                                    Events.$emit('branchChanged', id);
                                } else {
                                    Events.$emit('updateError');
                                }
                            },
                            //TODO: Promisify this
                            (err) => {
                                console.log(err);
                                if (err.status == 404) {
                                    Events.$emit('removedRepositoryError');
                                } else if (err.stack == 500) {
                                    Events.$emit('updateError');
                                }
                            });
                    }

                }, (err) => {
                    console.log(err);
                    if (err.status == 404) {
                        Events.$emit('removedRepositoryError');
                    } else if (err.stack == 500) {
                        Events.$emit('updateError');
                    }
                })
            },
            pushBranch: function (branchName, filename = true) {
                if (filename && this.$route.params.fullName) {
                    this.$router.push(encodeURIComponent(this.$route.params.fullName) + '&' + encodeURIComponent(branchName))
                } else {
                    this.$router.push('&' + encodeURIComponent(branchName))
                }
            }
        },

        created: function () {

            Events.$on('updateComplete', () => {
                //NotifyService.info('Info', 'Updating content completed successfully');
                this.getTree();
            });

            Events.$on('branchChanged', (id) => {
                ParamsService.setUuid(id.id);
                this.getTree();
                clearInterval(intervalId);
            });

            Events.$on('updateError', () => {
                NotifyService.error('Error', 'An error occurred while updating content.');
            });

            Events.$on("branchDoesntExist", () => {
                const loading = false;
                Events.$emit("OverlayChange", loading)
                NotifyService.error('Error', `Requested branch doesn't exist. Please select another one.`)
            })

            //Translate old overlay calls to new one
            Events.$on('updateStart', () => {
                const loading = true;
                Events.$emit("OverlayChange", loading)
            });
            Events.$on('treeLoaded', () => {
                const loading = false;
                Events.$emit("OverlayChange", loading)
            });
            Events.$on('updateComplete', () => {
                const loading = false;
                Events.$emit("OverlayChange", loading)
            });
            Events.$on('branchChanging', () => {
                const loading = true;
                Events.$emit("OverlayChange", loading)
            });
            Events.$on('updateError', () => {
                const loading = false;
                Events.$emit("OverlayChange", loading)
            });
            Events.$on('removedRepositoryError', () => {
                NotifyService.error('Error', 'Repository has been removed by Administrator.');
                const loading = false;
                Events.$emit("OverlayChange", loading)
            });

            var branch = {
                branch: this.$route.params.branch
            }


            MarkupService.getDefaultBranch().then((promise) => {
                var defaultBranch = promise.body.currentBranch
                if (!branch.branch || branch.branch === defaultBranch) {
                    this.pushBranch(defaultBranch)
                    this.getDocumentation()
                } else {
                    this.pushBranch(branch.branch)
                    this.getTemporary(branch)
                }
            })
        },
        mounted: function () {
            //AJS.$(this.$el).find(".markup-action-buttons button").tooltip();
        }
    }).$mount('#app');

});