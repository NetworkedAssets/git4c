var Git4CFileContent = {
    
    getComponent: function () {

        const Loading = {
            template: '<div></div>'
        }

        return {
            functional: true,
            props: {
                template: String,
                parent: String,
                lines: Boolean
            },
            render: function(h, context) {
                const template = context.props.template;
                const dynComponent = {
                    template: '<div class="html-content">' + template + ' </div>',
                    mounted: function () {

                        const vm = this;
                        const rootEl = this.$el;

                        vm.$nextTick(function () {

                            $(rootEl).find("pre code.git4c-highlightjs-code").each(function (i, block) {
                                hljs.highlightBlock(block);
                            });
                            $(rootEl).find("code.git4c-prismjs-code").each(function (i, block) {
                                Prism.highlightElement(block)
                                if (context.props.lines) {
                                    $(this).css('margin-left', '-30px')
                                }
                            });
                        })

                    },
                    data: function() {
                        return {
                            //Used by template
                            anchor: function (id) {
                                const el = $(context.props.parent)[0];
                                const topPos = $(el).find("[name='" + id + "']")[0].offsetTop;
                                window.scrollTo(0, el.offsetTop + topPos)
                            }
                        }
                    }
                }
                const component = template ? dynComponent : Loading;
                return h(component);
            }


        }

    }


}
