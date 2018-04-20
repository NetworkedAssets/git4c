var Git4CSingleFileMacro = {
    start: function (uuid, randomNumber, lineNumbers, collapsible, showTopBar, collapseByDefault, toc, fileEditEnabled) {

        const Events = new Vue({});

        const root = "#app-" + uuid + "-" + randomNumber

        const v = new Vue({
            el: root,
            data: {
                // Events,
                uuid: uuid,
                content: undefined,
                collapsed: showTopBar && collapseByDefault,
                markdown: false,
                lines: lineNumbers,
                //This is set by settings during macro creation
                showTopBar: showTopBar,
                showsError: false,
                locationPath: undefined,
                rawContent: undefined,
                toc: undefined,
                showToc: toc,
                editBranch: false,
                originalBranchName: undefined,
                branchName: undefined,
                repositoryName: undefined
            },
            components: {
                toc: Git4CToc.getComponent(),
                git4ceditdialog: Git4CEditDialog.getComponent(),
                git4cinfodialog: Git4CInfoDialog.getComponent(),
                git4csourcedialog: Git4CSourceDialog.getComponent(),
                overlay: Git4COverlay.getComponent(Events),
                topbar: TopBar.getComponent(Events, uuid, lineNumbers, collapsible, collapseByDefault, fileEditEnabled),
                filecontent: Git4CFilePreview.getComponent(),
                topbartoggler: Git4CTopBarToggler.getComponent(Events)
            },
            mounted: function () {
                const vm = this
                downloadFile(uuid)
                    .then(function (pair) {
                        const newUuid = pair[0]
                        vm.editBranch = uuid !== newUuid
                        vm.uuid = newUuid
                        const docItem = pair[1]
                        vm.document = docItem.document
                        vm.content = docItem.content
                        vm.markdown = docItem.name.endsWith(".md") || docItem.name.endsWith(".adoc")
                        vm.locationPath = docItem.locationPath.join("/")
                        vm.rawContent = docItem.rawContent
                        vm.toc = docItem.tableOfContents
                        if (!Git4CUtils.hasLines(docItem.name)) {
                            vm.lines = false
                        }
                        Events.$emit("DocumentDownloaded", docItem)
                        Events.$emit("OverlayChange", false)

                        return Promise.all([
                            getCurrentBranch(uuid),
                            getCurrentBranch(newUuid),
                            getRepositoryName(newUuid)
                        ])

                    })
                    .then(function (arr) {
                        const oldBranch = arr[0]
                        const newBranch = arr[1]
                        const repositoryName = arr[2]
                        vm.originalBranchName = oldBranch
                        vm.branchName = newBranch
                        vm.repositoryName = repositoryName
                    })
                    .catch(function (error) {
                        console.log("Error during downloading file, ", error)
                        var errorMessage
                        if (error.status === 404) {
                            errorMessage = "<div>Repository has been removed by admin</div>"
                        } else {
                            errorMessage = "<div>An error occurred while updating content</div>"
                        }

                        vm.content =
                            '<div class="aui-message aui-message-error">' +
                            '    <p class="title">' +
                            '        <strong>Error!</strong>' +
                            '    </p>' +
                            '    <p>' + errorMessage + '</p>' +
                            '</div>'

                        vm.showsError = true

                        Events.$emit("OverlayChange", false)
                    })

                Events.$on("setCollapse", function (collapsed) {
                    vm.collapsed = collapsed
                })
                Events.$on("setLines", function (lines) {
                    vm.lines = lines
                    if (lines) {
                        $(root + " code.git4c-prismjs-code").css('margin-left', '-30px')
                    } else {
                        $(root + " code.git4c-prismjs-code").css('margin-left', '')

                    }

                })
                Events.$on("editFile", function () {
                    vm.$refs.editdialog.show(vm.uuid, vm.locationPath, vm.rawContent)
                })

                Events.$on("infoDialog", function () {
                    vm.$refs.infodialog.show()
                })

                Events.$on("openSourceDialog", function () {
                    vm.$refs.sourcedialog.show(vm.rawContent)
                })

                Events.$on("toggleTopbar", function () {
                    vm.showTopBar = !vm.showTopBar
                    vm.$nextTick(function () {

                        if (vm.showTopBar) {
                            $(vm.$refs["topbar"].$el).trigger("sticky_kit:detach");
                            $(vm.$refs["topbar"].$el).stick_in_parent({parent: $(vm.$el), offset_top: 41})
                        } else {
                            $(vm.$refs["topbartoggler"].$el).find("#buttonholder").trigger("sticky_kit:detach");
                            $(vm.$refs["topbartoggler"].$el).find("#buttonholder").stick_in_parent({
                                parent: $(vm.$el),
                                offset_top: 150,
                                spacer: false
                            })
                        }

                    })
                })

                $(this.$refs["topbar"].$el).stick_in_parent({parent: $(vm.$el), offset_top: 41})
                $(this.$refs["topbartoggler"].$el).find("#test2").stick_in_parent({parent: $(vm.$el), offset_top: 150, spacer: false})


            }
        })
    }
};