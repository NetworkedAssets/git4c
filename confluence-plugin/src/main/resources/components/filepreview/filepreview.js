var Git4CFilePreview = {
    getComponent: function () {

        return {
            components: {
                toc: Git4CToc.getComponent()
            },
            template:
            '    <div class="git4c-any-content" v-bind:class=\'{ "git4c-file-preview-markdown" :markdown }\'>'+
            '        <toc style="margin-bottom: 20px" :container="container" :data="toc"></toc>'+
            '        <div v-html="content"></div>'+
            '    </div>',
            props: ["file", "container"],
            data: function() {
                return {
                    toc: undefined,
                    content: undefined,
                    markdown: false
                }
            },
            watch: {
                file: function () {

                    const vm = this
                    const toc = vm.file.tableOfContents
                    const content = vm.file.content
                    vm.markdown = true
                    vm.toc = toc
                    vm.content = content

                    this.$nextTick(function () {

                        $(this).css('margin-left', '0')

                        $(vm.$el).find("pre code.git4c-highlightjs-code").each(function (i, block) {
                            hljs.highlightBlock(block);
                        });
                        $(vm.$el).find(".git4c-prismjs-code").each(function (i, block) {
                            Prism.highlightElement(block)
                            $(this).css('margin-left', '-30px')
                            vm.markdown = false
                        });

                    })
                }
            }
        }
    }
}
