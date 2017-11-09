var Loading = {
    template: '<div></div>'
}

var dynamiccontent = {
    functional: true,
    props: {
        template: String,
    },
    render: function(h, context) {
        const template = context.props.template;
        const dynComponent = {
            template: '<div class="html-content">' + template + ' </div>',
            data: function() {
                return {
                    //Used by template
                    anchor: function (id) {
                        const top = document.getElementsByName(id)[0].offsetTop;
                        window.scrollTo(0, top);
                    },
                    moveToFile: function (file, anchor) {
                        //File is already encoded
                        this.$router.push(file + "&" + encodeURIComponent(this.$route.params.branch))
                        Events.$emit("NextAnchor", anchor)
                        Events.$emit("TreeviewInvalidate")
                    }
                }
            }
        }
        const component = template ? dynComponent : Loading;
        return h(component);
    }

}

var commitHistory = function (){
    const Bus = new Vue({})
    return Git4CCommitHistory.getComponent(Bus)
}

var Markup = {
    template: '#markup',

    components: {
        dynamiccontent: dynamiccontent,
        commitHistory: commitHistory()
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
            nextAnchor: undefined
        };
    },
    watch: {
        '$route.params.fullName': 'update',
        '$route.params.branch': function (branch) {
            if (branch) {
                Events.$emit("branchChangeRequest", branch)
            }
        }
    },
    methods: {
        update: function () {
            const fullName = this.$route.params.fullName
            if (fullName) {
                const file = {
                    file: fullName
                }
                // this.loading = true;
                const vm = this

                MarkupService.getItem(file).then(function (docItem) {


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

                    if (fullName.endsWith("md") || fullName.endsWith("svg") || fullName.endsWith("puml")) {
                        vm.hasSource = true
                    } else {
                        vm.hasSource = false
                    }

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
                        vm.$refs.commit_history.macroUuid = ParamsService.getUuid()
                        vm.$refs.commit_history.update(vm.$route.params.branch, fullName)

                        vm.$nextTick(function() {
                            vm.stickToolbar()
                        })


                        if (vm.nextAnchor) {
                            const top = document.getElementsByName(vm.nextAnchor)[0].offsetTop;
                            window.scrollTo(0, top);
                            vm.nextAnchor = undefined
                        }

                    })


                }, function () {
                    vm.nextAnchor = undefined
                    vm.fileData = undefined;
                    vm.template = "<span>File cannot be found</span>"
                    NotifyService.error('Error', 'File cannot be found. Returning to default file.')
                    vm.$router.push("&" + encodeURIComponent(vm.$route.params.branch))
                    vm.$root.getTree()
                });
            }
        },
        openDialog: function () {
            // AJS.toInit(function () {
            const normalizedString = this.rawContent.replace(/\s+/g, '')

            //https://stackoverflow.com/a/6234804/2511670
            const escapeHtml = function(unsafe) {
                return unsafe
                    .replace(/&/g, "&amp;")
                    .replace(/</g, "&lt;")
                    .replace(/>/g, "&gt;")
                    .replace(/"/g, "&quot;")
                    .replace(/'/g, "&#039;");
            }

            var dialogContent

            if (!normalizedString) {
                dialogContent =
                '<div class="aui-message aui-message-generic">'+
                '    <p class="title">'+
                '        <strong>This file is empty</strong>'+
                '    </p>'+
                '</div>'
            } else {
                const content = this.rawContent
                dialogContent =
            '<pre>'+
            '    <code id="git4c-dialog-code" class="git4c-code markdown ">' + escapeHtml(content) + '</code>'+
            '</pre>'

            }



            $("#git4c-source-content").html(dialogContent)
            $('#git4c-dialog-code').each(function (i, block) {
                hljs.highlightBlock(block);
            });

            const dialogId = "#git4c-raw-markdown-dialog"

            AJS.dialog2(dialogId).show();


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

        const vm = this

        Events.$on('updateComplete', function() {
            vm.content = '';
        });
        Events.$on('treeLoaded', function() {
            vm.update();
        });

        Events.$on('branchChanging', function() {
            //Remove content, so loader would be visible
            vm.content = ''
        });

        Events.$on('updateStart', function() {
            vm.content = ''
        });

        Events.$on('OverlayChange', function(overlayVisible) {
            vm.overlayVisible = overlayVisible
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

        Events.$on('branchChanged', function () {
            $(vm.$el.parentElement).height("")
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