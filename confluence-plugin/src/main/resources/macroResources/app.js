const router = new VueRouter({
    mode: 'hash',
    routes: [
        {path: '/:fullName?', component: Markup}
    ]
});

AJS.toInit(function () {

    var lastRevision = undefined;
    var alertShown = false;
    var intervalId = undefined;
    const timeoutInterval = 90000

    const startInterval = function () {

        const checkRevision = function () {
            MarkupService.getLatestRevision().then(function (revision) {
                if (!lastRevision) {
                    lastRevision = revision
                    setTimeout(checkRevision, timeoutInterval)
                } else {
                    if (lastRevision !== revision) {
                        lastRevision = revision;
                        if (!alertShown) {
                            NotifyService.persistent("Content of Git4C Macro is out of date. There is a new version of document available.",
                                '<ul class="aui-nav-actions-list">' +
                                '<li><a href="#" onclick="location.reload(true); return false;">Refresh page</a></li>' +
                                '</ul>');
                            alertShown = true
                        }
                    } else {
                        setTimeout(checkRevision, timeoutInterval)
                    }
                }
            }, function (error) {
                setTimeout(checkRevision, timeoutInterval)
            })
        }

        setTimeout(checkRevision, timeoutInterval)
    }


    ParamsService.initialize()

    var defaultFileName = "";
    new Vue({
        router: router,
        el: "#app",
        data: {
            value: '',
            tree: [],
            sticky: false
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
                        else el.children.forEach(function(ele) { f(ele) })
                    });
                    if (contains) {
                        return docItemName
                    }
                }
                if (element.children != null) {
                    function findfirstItem(element) {
                        const maybeFile = element.children.find(function (file) {return file.type === "DOCITEM"})
                        const child = element.children[0]

                        if (maybeFile) {
                            return maybeFile.fullName
                        } else {
                            return findfirstItem(child)
                        }
                    }
                    docItemName = findfirstItem(element)
                }
                return $.isArray(docItemName) ? docItemName[0] : docItemName;

            },
            getTree: function () {
                const vm = this
                MarkupService.getTree().then(function(tree) {
                    if(tree.children.length == 0){
                        Events.$emit("tree", null)
                        NotifyService.error('Error', 'There are no files present on this branch.')
                    }
                    Vue.set(vm, 'tree', tree.children);
                    Events.$emit("tree", tree.children)
                    if (!vm.$route.params.fullName) {
                        var defaultDocItemName = vm.getDefaultDocItemName(tree);
                        if (defaultDocItemName) {
                            vm.pushFileName(encodeURIComponent(defaultDocItemName))
                        }
                    }
                    var nodesToOpen = decodeURIComponent(vm.$route.params.fullName).split('/');
                    const fullFileName = vm.$route.params.fullName
                    if (nodesToOpen.length > 1) {

                        function recSearch(currentElement, toFind) {
                            var found = false
                            function f(tree){
                                if(tree.fullName == toFind){
                                    found = true
                                }
                                else{
                                    tree.children.forEach(function (it) { f(it) })
                                }
                            }
                            f(currentElement)
                            return found
                        }

                        const fileExists = recSearch(tree, fullFileName)

                        if (fileExists) {
                            vm.$nextTick(function() {
                                Events.$emit("TreeviewInvalidate")
                            })
                        } else {
                            vm.$nextTick(function() {
                                NotifyService.error('Error', 'This file doesn\'t exist in this branch, returning to default file.')
                                vm.clearFilenameInPath()
                                vm.getTree()
                            })
                            return
                        }
                    }
                    vm.$nextTick(function() {
                        Events.$emit("TreeviewInvalidate")
                        Events.$emit('treeLoaded');
                    })

                }, function() {
                    NotifyService.error('Error', 'An error occurred while displaying content.')
                });
            },
            getDocumentation: function () {
                //this.working = true;
                Events.$emit('updateStart');
                MarkupService.getDocumentation().then(function(documentation) {
                        //  this.working = false;
                        startInterval()
                        if (documentation != undefined) {
                            Events.$emit('updateComplete');
                        } else {
                            Events.$emit('updateError');
                        }
                    },
                    function (err) {
                        console.log(err);
                        if (err.status == 404) {
                            Events.$emit('removedRepositoryError');
                        } else
                            Events.$emit('updateError');
                    });

            },
            getFileNameFromPath: function () {
                var fullPath = decodeURIComponent(this.$route.params.fullName)
                return (fullPath ? fullPath : "")
            },
            getBranchNameFromPath: function () {
                const branch = decodeURIComponent(this.$route.query.branch)
                return (branch ? branch : "master")
            },
            getAnchorNameFromPath: function () {
                const anchor = decodeURIComponent(this.$route.query.anchor)
                return (anchor ? anchor : "")
            },
            clearFilenameInPath: function () {
                this.pushFileName("/")
            },
            getTemporary: function (branch) {
                //this.working = true;
                Events.$emit('updateStart');


                MarkupService.getBranches().then(function(branchesResponse) {

                    const branches = branchesResponse.allBranches

                    if ($.inArray(branch, branches) === -1) {
                        console.log("Search for branch: "+branch)
                        console.log("Branch doesn't exist")
                        // NotifyService.error('Error', `Requested branch doesn't exist. Please select another one.`)
                        Events.$emit('branchDoesntExist');
                    } else {
                        console.log("Branch exists")

                        MarkupService.temporary(branch).then(function(id){
                                //  this.working = false;
                                startInterval()
                                if (id) {
                                    Events.$emit('branchChanged', id);
                                } else {
                                    Events.$emit('updateError');
                                }
                            },
                            //TODO: Promisify this
                            function(err) {
                                console.log(err);
                                if (err.status == 404) {
                                    Events.$emit('removedRepositoryError');
                                } else if (err.stack == 500) {
                                    Events.$emit('updateError');
                                }
                            });
                    }
                }, function (err) {
                    console.log(err);
                    if (err.status == 404) {
                        Events.$emit('removedRepositoryError');
                    } else if (err.stack == 500) {
                        Events.$emit('updateError');
                    }
                })
            },
            pushBranch: function (branchName) {
                this.$router.replace({ query: {branch: branchName, anchor: this.$route.query.anchor} })
            },
            pushFileName: function (fullName) {
                this.$router.push({ path: fullName, query: this.$route.query })
            },
            pushAnchor: function (anchor) {
                this.$router.replace({ query: {branch: this.$route.query.branch ,anchor: anchor} })
            }
        },

        created: function () {
            const vm = this
            Events.$on('updateComplete', function () {
                //NotifyService.info('Info', 'Updating content completed successfully');
                vm.getTree();
            });
            Events.$on('branchChanged', function(id) {
                console.log("Macro is: "+id)
                ParamsService.setUuid(id);
                console.log("Macro is: "+ParamsService.getUuid())
                vm.getTree();
                clearInterval(intervalId);
            });

            Events.$on('updateError', function () {
                NotifyService.error('Error', 'An error occurred while updating content.');
            });

            Events.$on("branchDoesntExist", function () {
                Events.$emit("errorOccured", "non_existing_branch")
            })

            //Translate old overlay calls to new one
            Events.$on('updateStart', function () {
                const loading = true;
                Events.$emit("OverlayChange", loading)
            });
            Events.$on('updateError', function () {
                const loading = false;
                Events.$emit("OverlayChange", loading)
            });
            Events.$on('removedRepositoryError', function () {
                NotifyService.error('Error', 'Repository has been removed by Administrator.');
                Events.$emit("errorOccured", "repository_removed")
            });

            Events.$on("Sticky", function(sticky) {
                vm.sticky = sticky
                vm.$nextTick(function () {
                    Events.$emit("TreeviewInvalidate")
                })
            })

            Events.$on("pushAnchor", function (id) {
                vm.pushAnchor(id)
            })

            Events.$on("pushFile", function (id) {
                vm.pushFileName(id)
            })

            const branch = this.$route.query.branch

            MarkupService.getDefaultBranch().then(function (promise) {
                var defaultBranch = promise.currentBranch
                if (!branch || branch === defaultBranch) {
                    vm.pushBranch(defaultBranch)
                    vm.getDocumentation()
                } else {
                    vm.pushBranch(branch)
                    vm.getTemporary(branch)
                }
            })

            MarkupService.getGlobs().then(function(globs) {
                const globsName = globs.map(function (glob) {
                    return glob.prettyName
                })

                if (globsName.length > 0) {

                    AJS.$(vm.$refs.globtooltip)
                        .tooltip({
                            title: function () {
                                return globsName
                            }
                        })

                } else {
                    $(vm.$refs.globtooltip).hide()
                }
            })

        },
        mounted: function () {
            //AJS.$(this.$el).find(".markup-action-buttons button").tooltip();
        }
    })

});