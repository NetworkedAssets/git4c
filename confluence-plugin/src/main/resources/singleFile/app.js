var Git4CSingleFileMacro = {
    start: function (uuid, randomNumber, lineNumbers, collapsible, showTopBar, collapseByDefault) {

        const Events = new Vue({});

        const root = "#app-" + uuid + "-" + randomNumber

        const v = new Vue({
            el: root,
            data: {
                // Events,
                uuid,
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
                downloadFile(uuid)
                    .then((docItem) => {
                        this.document = docItem.document
                        this.content = docItem.content
                        this.markdown = docItem.name.endsWith(".md")
                        if (!Git4CUtils.hasLines(docItem.name)) {
                            this.lines = false
                        }
                        Events.$emit("DocumentDownloaded", docItem)
                        Events.$emit("OverlayChange", false)
                        this.$nextTick(() => {
                            $(root +" pre code.git4c-highlightjs-code").each(function (i, block) {
                                hljs.highlightBlock(block);
                            });
                            $(root +" code.git4c-prismjs-code").each(function (i, block) {
                                Prism.highlightElement(block)
                            });
                        })
                    })
                    .catch((error) => {
                        console.log("Error during downloading file, ", error)
                        let errorMessage
                        if (error.status === 404) {
                            errorMessage = "<div>Repository has been removed by admin</div>"
                        } else {
                            errorMessage = "<div>An error occurred while updating content</div>"
                        }

                        this.content =
                       `<div class="aui-message aui-message-error">
                            <p class="title">
                                <strong>Error!</strong>
                            </p>
                            <p>` + errorMessage + `</p>
                        </div>`

                        this.showsError = true

                        Events.$emit("OverlayChange", false)
                    })

                Events.$on("setCollapse", (collapsed) => {
                    this.collapsed = collapsed
                })
                Events.$on("setLines", (lines) => {
                    this.lines = lines
                })
            }
        })

        // TopBar.initTopBar(v)

        v.$mount(root);
    }
};