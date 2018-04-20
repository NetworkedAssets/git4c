var commitHistory = function (){
    const Bus = new Vue({})
    return Git4CCommitHistory.getComponent(Bus)
}

var Markup = {
    template: '#markup',

    components: {
        toc: Git4CToc.getComponent(Events),
        commitHistory: commitHistory(),
        filesourcedialog: Git4CSourceDialog.getComponent(),
        filepreview: Git4CFilePreview.getComponent()
    },

    data: function () {
        return {
            fileData: undefined,
            locationPath: "",
            template: undefined,
            content: "",
            rawContent: "",
            toc: undefined,
            defaultFile: undefined,
            singleFile: false,
            hasSource: false,
            overlayVisible: false,
            tree: undefined,
            fullscreen: false,
            sticky: false,
            nextAnchor: undefined,
            simpleMce: undefined,
            fileTimeout: undefined,

            //Commit history data
            macroUuid: undefined,
            branch: undefined,
            file: undefined
        };
    },
    watch: {
        '$route.params.fullName': function () {
            this.update()
            if(this.$route.params.fullName) {
                this.fileTimeout = setTimeout(function () {
                    Events.$emit("FileLoading", true)
                }, 250)
            }
        },
        '$route.query.branch': function (branch) {
                if (branch) {
                    Events.$emit("branchChangeRequest", branch)
                }
        }
    },
    methods: {
        update: function () {
            const fullName = this.$route.params.fullName
            const urlAnchor = this.$route.query.anchor
            if(urlAnchor){
                this.nextAnchor = urlAnchor
            }

            if (fullName) {
                Events.$emit("OverlayChange", false)
                const vm = this
                vm.content=''
                MarkupService.getItem(fullName)
                .then(function (docItem) {
                    //make sure content will be visible
                    $(vm.$el.parentElement).height("")


                    vm.locationPath = docItem.locationPath;
                    vm.fileData = {
                        authorFullName: docItem.lastUpdateAuthorName,
                        authorEmail: docItem.lastUpdateAuthorEmail,
                        updateTime: new Date(docItem.lastUpdateTime)
                    };

                    const template = docItem.content;
                    vm.template = docItem.content;
                    vm.content = docItem.content;
                    vm.toc = docItem.tableOfContents;
                    vm.rawContent = docItem.rawContent

                    vm.singleFile = docItem.content.indexOf("git4c-prismjs-code") !== -1

                    vm.hasSource = Git4CUtils.hasSourceCode(fullName)

                    vm.$nextTick(function(){
                        vm.resizeContent()
                    })

                    vm.$nextTick(function() {
                        $(vm.$refs.raw_file_button).tooltip({
                            title: function(){
                                return "View the full source of this file"
                            }
                        })
                        $(vm.$refs.updatetime).tooltip('destroy')
                        $(vm.$refs.updatetime).tooltip({
                            title: function () {
                                return new Date(docItem.lastUpdateTime).toLocaleString()
                            }
                        });
                        $(vm.$refs.author).tooltip('destroy')
                        $(vm.$refs.author).tooltip({
                            title: function () {
                                // return new Date(docItem.lastUpdateTime).toLocaleString()
                                return vm.fileData.authorFullName + " <" + vm.fileData.authorEmail + ">"
                            }

                        });

                        vm.macroUuid = ParamsService.getUuid()
                        vm.branch = vm.$route.query.branch
                        vm.file = fullName

                        vm.$nextTick(function() {
                            clearTimeout(vm.fileTimeout)
                            Events.$emit("OverlayChange", false)
                            Events.$emit("FileLoading", false)
                            vm.stickToolbar()
                        })


                        if (vm.nextAnchor) {
                            vm.$refs["preview"].scrollTo(vm.nextAnchor)
                            vm.nextAnchor = undefined
                        }

                    })


                })
                .catch(function (err) {
                    clearTimeout(vm.fileTimeout)
                    Events.$emit("OverlayChange", false)
                    Events.$emit("FileLoading", false)
                    vm.nextAnchor = undefined
                    vm.fileData = undefined;
                    vm.template = "<span>File cannot be found</span>"
                    NotifyService.error('Error', 'File cannot be found. Returning to default file.')
                    vm.$router.push({ path: "/", query: vm.$route.query })
                    vm.$root.getTree()
                });
            }
        },
        openDialog: function () {
            this.$refs.sourcedialog.show(this.rawContent)
            $("#git4c-markdown-dialog-close-button").blur()
        },
        closeRawContentDialog: function(){
            AJS.dialog2(this.$refs.git4c_raw_file_dialog).hide()
        },
        resizeContent: function () {
            const root = $(this.$root.$el)

            const pre = root.find("pre.line-numbers")

            //51 is topbar height
            pre.height(root.height() - 51)

            // console.log(pre)
        },
        openTree: function () {
            //Fix inline dialog content
            $("#git4c-tree-dialog").find(".aui-inline-dialog-contents:not(.git4c-tree-container)").removeClass("aui-inline-dialog-contents")
        },
        toggleSticky: function () {
            const vm = this
            const sticky = this.sticky
            this.sticky = !sticky
            Events.$emit("Sticky", !sticky)

            this.$nextTick(function() {
                Events.$emit("TreeviewInvalidate")
                vm.stickToolbar()
            })
        },
        stickToolbar: function () {
            if (this.sticky) {
                $("#git4c-breadcrumbs-div").stick_in_parent({parent: $("#git4c-main-content"), offset_top: 41})
            } else {
                $("#git4c-breadcrumbs-div").trigger("sticky_kit:detach");
            }
            this.resizeContent()
        },
        disableFullscreen: function(){
            Events.$emit("FullscreenModeDisable")
        },
        toggleSidebar: function() {
            Events.$emit("toggleSideBar")
        }
    },
    computed: {
        stickyButtonText: function () {
            return (this.sticky ? "Non-sticky" : "Sticky") + " toolbar"
        },
        path: function () {

            var tooltipText
            var path

            if (this.locationPath && this.locationPath.length > 0) {
                if (this.locationPath.length === 1) {
                    const p = this.locationPath[0]
                    tooltipText = p
                    path = p
                } else {
                    tooltipText = this.locationPath.join("/")
                    path = "../" + this.locationPath[this.locationPath.length - 1]
                }
            }

            if (tooltipText && path) {

                this.$nextTick(function(){
                    AJS.$(this.$refs.pathholder).tooltip("destroy")
                    AJS.$(this.$refs.pathholder).tooltip({
                        title: function () {
                            return tooltipText
                        }
                    });
                })

                return path
            }
        }
    },
    mounted: function() {

        //@ doesn't seem to work FIXME
        this.$on('anchor', function (anchor) {
            Events.$emit("pushAnchor", anchor)
        });

        this.$on('moveToFile', function (file, anchor) {

            Events.$emit("pushFile", file)

            if(anchor) {
                Events.$emit("pushAnchor", anchor)
                Events.$emit("NextAnchor", anchor)
            }

            Events.$emit("TreeviewInvalidate")
        })

        const vm = this

        Events.$on('updateComplete', function() {
            vm.content = '';
        });
        Events.$on('treeLoaded', function() {
            vm.update();
        });

        Events.$on('updateStart', function() {
            vm.content = ''
        });

        Events.$on('OverlayChange', function(overlayVisible) {
            if(overlayVisible){
                //remove content and make overlay visible
                vm.content = ''
                $(vm.$el.parentElement).height("0")
            }else {
                //make content visible
                $(vm.$el.parentElement).height("")
            }
            vm.overlayVisible = overlayVisible
        })

        Events.$on('FileLoading', function(isLoading) {
            if(isLoading){
                vm.locationPath=''
                $(vm.$el.parentElement).height("0")
                vm.content=''
            }else {
                //make content visible
                $(vm.$el.parentElement).height("")
            }
        })

        Events.$on("FullscreenModeToggled", function(){
            vm.fullscreen = !this.fullscreen
            vm.toggleSticky()
        })

        Events.$on("StickyToolbarToogled", function(){
            vm.toggleSticky()
        })

        Events.$on('tree', function (tree) {
            vm.tree = tree
        })

        Events.$on('errorOccured', function() {
            $(vm.$el.parentElement).height("500px")
        })

        Events.$on('branchChanging', function () {
            $(vm.$el.parentElement).height("0")
        })

        Events.$on("NextAnchor", function(anchor) {
            vm.nextAnchor = anchor
        })


        $(this.$el).find("#git4c-toolbar_filetree-button").tooltip()
        $(this.$el).find("#git4c-filetree-expand_button").tooltip()
    },

    updated: function() {
        $("pre code.git4c-highlightjs-code").each(function (i, block) {
            hljs.highlightBlock(block);
        });
        $(".git4c-prismjs-code").each(function (i, block) {
            Prism.highlightElement(block)
            $(this).css('margin-left', '-30px')
        });
        $(".git4c-highlightjs-code a").replaceWith(function () {
            return this.innerHTML;
        });
    }

};

Vue.component('markup', Markup);