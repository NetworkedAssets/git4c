var Git4CSingleFileMacro = {
    start: function (uuid, randomNumber, lineNumbers, collapsible, showTopBar, collapseByDefault) {

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
                showTopBar: showTopBar,
                showsError: false
            },
            components: {
                overlay: Git4COverlay.getComponent(Events),
                topbar: TopBar.getComponent(Events, uuid, lineNumbers, collapsible, collapseByDefault)
            },
            mounted: function () {
                const vm = this
                downloadFile(uuid)
                    .then(function (docItem) {
                        vm.document = docItem.document
                        vm.content = docItem.content
                        vm.markdown = docItem.name.endsWith(".md")
                        if (!Git4CUtils.hasLines(docItem.name)) {
                            vm.lines = false
                        }
                        Events.$emit("DocumentDownloaded", docItem)
                        Events.$emit("OverlayChange", false)
                        vm.$nextTick(function () {
                            $(root + " pre code.git4c-highlightjs-code").each(function (i, block) {
                                hljs.highlightBlock(block);
                            });
                            $(root + " code.git4c-prismjs-code").each(function (i, block) {
                                Prism.highlightElement(block)
                                if (vm.lines) {
                                    $(this).css('margin-left', '-30px')
                                }
                            });
                        })
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
            }
        })

        // TopBar.initTopBar(v)

        v.$mount(root);
    }
};