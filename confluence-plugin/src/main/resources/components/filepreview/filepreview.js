var Git4CFilePreview = {
    getComponent: function () {

        const Loading = {
            template: '<div></div>'
        };

        const dynamiccontent = {
            functional: true,
            props: {
                template: String,
            },
            render: function (h, context) {
                const template = context.props.template;
                const dynComponent = {
                    template: '<div class="html-content">' + template + ' </div>',
                    data: function () {
                        return {
                            anchor: function (id) {
                                this.$emit("anchor", id)
                            },
                            moveToFile: function (file, anchor) {
                                this.$emit("moveToFile", file, anchor)
                            }
                        }
                    },
                    mounted: function () {

                        const vm = this

                        $(vm.$el).find("pre code.git4c-highlightjs-code").each(function (i, block) {
                            hljs.highlightBlock(block);
                        });
                        $(vm.$el).find(".git4c-prismjs-code").each(function (i, block) {
                            Prism.highlightElement(block)
                        });

                    }
                }
                const component = template ? dynComponent : Loading;
                return h(component, context.data);
            }

        };

        return {
            components: {
                toc: Git4CToc.getComponent(),
                dynamiccontent: dynamiccontent
            },
            template:
            '    <div class="git4c-filepreview-component git4c-any-content" v-bind:class=\'{ "git4c-file-preview-markdown" : markdown, "git4c-file-preview-no-lines": !showLineNumbers }\'>'+
            '        <toc @anchor="anchor" v-if="showToc && toc && toc.children && toc.children.length > 0" style="margin-bottom: 20px" :data="toc"></toc>'+
            '        <dynamiccontent ref="dc" @anchor="anchor" @moveToFile="moveToFile" :template="content"></dynamiccontent>'+
            '    </div>',
            props: {
                content: {
                    type: String
                },
                toc: {
                    type: Object
                },
                showToc: {
                    type: Boolean,
                    default: true
                },
                showLineNumbers: {
                    type: Boolean,
                    default: true
                },
                toolbarHeight: {
                    type: Number
                }
            },
            data: function() {
                return {
                    markdown: false
                }
            },
            watch: {
                content: function () {
                    const vm = this

                    vm.$nextTick(function () {
                        vm.refreshMarkdownState()
                    })
                },
            },
            methods: {
                scrollTo: function (anchor) {
                    this.anchor(anchor)
                },
                anchor: function (anchor) {
                    this.$parent.$emit("anchor", anchor)
                    const element = Git4CUtils.findElementToScrollTo(this.$el, anchor)
                    if (element) {
                        element.scrollIntoView(true)

                        const toolbarHeight = this.toolbarHeight

                        if (toolbarHeight) {

                            const scrolledY = window.scrollY;

                            if (scrolledY) {
                                window.scroll(0, scrolledY - toolbarHeight);
                            }
                        }
                    }

                },
                moveToFile: function (file, anchor) {
                    this.$parent.$emit("moveToFile", file, anchor)
                },
                refreshMarkdownState: function() {

                    const vm = this

                    vm.markdown = true

                    $(vm.$el).find(".git4c-prismjs-code").each(function () {
                        vm.markdown = false
                    });

                },
            },
            mounted: function () {
                const vm = this

                vm.$nextTick(function () {
                    vm.refreshMarkdownState()
                });

            }
        }
    }
}
