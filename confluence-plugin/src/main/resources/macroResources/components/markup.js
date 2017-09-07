var Loading = {
    template: `<div></div>`
}

var dynamiccontent = {
    functional: true,
    props: {
        template: String,
    },
    render(h, context) {
        const template = context.props.template;
        const dynComponent = {
            template: `<div class="html-content">` + template + ` </div>`,
            data() {
                return {
                    //Used by template
                    anchor: function (id) {
                        const top = document.getElementsByName(id)[0].offsetTop;
                        window.scrollTo(0, top);
                    },
                    moveToFile: function (file) {
                        //File is already encoded
                        this.$router.push(file + "&" + encodeURIComponent(this.$route.params.branch))
                    }
                }
            }
        }
        const component = template ? dynComponent : Loading;
        return h(component);
    }

}

var Markup = {
    template: '#markup',

    components: {
        dynamiccontent
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
            overlayVisible: false
        };
    },
    watch: {
        '$route': 'update'
    },
    methods: {
        update: function () {
            const fullName = this.$route.params.fullName
            if (fullName) {
                const file = {
                    file: fullName
                }
                // this.loading = true;
                MarkupService.getItem(file).then((docItem) => {

                    const vm = this

                    this.locationPath = docItem.locationPath;
                    this.fileData = {
                        authorFullName: docItem.lastUpdateAuthorName,
                        authorEmail: docItem.lastUpdateAuthorEmail,
                        updateTime: new Date(docItem.lastUpdateTime)
                    };

                    const template = docItem.content;
                    this.template = docItem.content;
                    this.content = docItem.content;
                    this.toc = docItem.tableOfContents;
                    this.rawContent = docItem.rawContent

                    this.singleFile = docItem.content.indexOf("git4c-prismjs-code") !== -1

                    if (fullName.endsWith("md") || fullName.endsWith("svg") || fullName.endsWith("puml")) {
                        this.hasSource = true
                    } else {
                        this.hasSource = false
                    }

                    this.$nextTick(() => {
                        this.resizeContent()
                    })

                    this.$nextTick(() => {
                        $(this.$refs.updatetime).tooltip('destroy')
                        $(this.$refs.updatetime).tooltip({
                            title: function () {
                                return new Date(docItem.lastUpdateTime).toLocaleString()
                            }
                        });
                        $(this.$refs.author).tooltip('destroy')
                        $(this.$refs.author).tooltip({
                            title: function () {
                                // return new Date(docItem.lastUpdateTime).toLocaleString()
                                return vm.fileData.authorFullName + " <" + vm.fileData.authorEmail + ">"
                            }
                        });
                    })

                }, () => {
                    this.fileData = undefined;
                    this.template = "<span>File cannot be found</span>"
                    NotifyService.error('Error', 'File cannot be found. Returning to default file.')
                    this.$router.push("&" + encodeURIComponent(this.$route.params.branch))
                    this.$root.getTree()
                });
            }
        },
        openDialog: function () {
            // AJS.toInit(function () {
            $("#git4c-dialog-code").text(this.rawContent)
            $('#git4c-dialog-code').each(function (i, block) {
                hljs.highlightBlock(block);
            });

            const dialogId = "#git4c-raw-markdown-dialog"

            AJS.dialog2(dialogId).show();

            $("#git4c-markdown-dialog-close-button").blur()

            $("#git4c-markdown-dialog-close-button").click(function () {
                AJS.dialog2(dialogId).hide();
            })
        },
        resizeContent: function () {
            const root = $(this.$root.$el)

            const pre = root.find("pre.line-numbers")

            //51 is topbar height
            pre.height(root.height() - 51)

            // console.log(pre)
        }
    },
    mounted() {

        Events.$on('updateComplete', () => {
            this.content = '';
        });
        Events.$on('treeLoaded', () => {
            this.update();
        });

        Events.$on('branchChanging', () => {
            //Remove content, so loader would be visible
            this.content = ''
        });

        Events.$on('updateStart', () => {
            this.content = ''
        });

        Events.$on('OverlayChange', (overlayVisible) => {
            this.overlayVisible = overlayVisible
        })

    },

    updated() {
        $("pre code.git4c-highlightjs-code").each(function (i, block) {
            hljs.highlightBlock(block);
        });
        $(".git4c-prismjs-code").each(function (i, block) {
            Prism.highlightElement(block)
        });
        $(".git4c-highlightjs-code a").replaceWith(function () {
            return this.innerHTML;
        });
    }

};
Vue.component('markup', Markup);