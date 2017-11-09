const router = new VueRouter({
    mode: 'hash',
    routes: [
        {path: '/:fullName?&:branch?', component: Markup}
    ]
});

var Events = new Vue({});

AJS.toInit(function () {


    var lastRevision = undefined;
    var alertShown = false;
    var intervalId = undefined;

    const startInterval = function () {

        if (intervalId) {
            return
        }

        intervalId = setInterval(function () {
            MarkupService.getDocumentation().then(function(documentation) {
                if (!lastRevision) {
                    lastRevision = documentation.revision
                } else {
                    if (lastRevision !== documentation.revision) {
                        lastRevision = documentation.revision;
                        if (!alertShown) {
                            NotifyService.persistent("Content of Git4C Macro is out of date. There is a new version of document available.",
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
                            vm.$router.push('/' + encodeURIComponent(defaultDocItemName) + "&" + vm.getBranchNameFromPath());
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

                MarkupService.getBranches().then(function(branchesResponse) {

                    const branches = branchesResponse.allBranches

                    if ($.inArray(branch.branch, branches) === -1) {
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
            pushBranch: function (branchName, filename) {
                filename = typeof filename !== 'undefined' ? filename : true;

                if (filename && this.$route.params.fullName) {
                    this.$router.push(encodeURIComponent(this.$route.params.fullName) + '&' + encodeURIComponent(branchName))
                } else {
                    this.$router.push('&' + encodeURIComponent(branchName))
                }
            },
        },

        created: function () {
            const vm = this

            Events.$on('updateComplete', function () {
                //NotifyService.info('Info', 'Updating content completed successfully');
                vm.getTree();
            });

            Events.$on('branchChanged', function(id) {
                ParamsService.setUuid(id.id);
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
            Events.$on('treeLoaded', function () {
                const loading = false;
                Events.$emit("OverlayChange", loading)
            });
            Events.$on('branchChanging', function () {
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

            var branch = {
                branch: this.$route.params.branch
            }


            MarkupService.getDefaultBranch().then(function (promise) {
                var defaultBranch = promise.body.currentBranch
                if (!branch.branch || branch.branch === defaultBranch) {
                    vm.pushBranch(defaultBranch)
                    vm.getDocumentation()
                } else {
                    vm.pushBranch(branch.branch)
                    vm.getTemporary(branch)
                }
            })

            MarkupService.getGlobs().then(function(promise) {
                const globs = promise.globs.map(function (glob) {
                    return glob.prettyName
                })

                if (globs.length > 0) {

                    AJS.$(vm.$refs.globtooltip)
                        .tooltip({
                            title: function () {
                                return globs
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
    }).$mount('#app');

});