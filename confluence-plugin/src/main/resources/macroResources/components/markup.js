var Markup = {
    template: '#markup',

    data: function () {
        return {
            fileData: undefined,
            locationPath: "",
            template: undefined,
            content: "",
            toc: undefined
        };
    },

    watch: {
        '$route': 'update'
    },

    methods: {
        update: function () {
            if (this.$route.params.fullName) {
                // this.loading = true;
                MarkupService.getItem(this.$route.params.fullName).then((docItem) => {
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

                }, () => {
                    this.fileData = undefined;
                    this.template = "<span>Error during downloading Markdown</span>"
                    NotifyService.error('Error', 'An error occurred while displaying content.')
                });
            }
        }
    },

    mounted() {

        Events.$on('updateComplete', () => {
            this.content = '';
        });
        Events.$on('treeLoaded', () => {
            this.update();
        });
    },

    updated() {
        $(".markup pre code").each(function (i, block) {
            hljs.highlightBlock(block);
        });
    }

};
Vue.component('markup', Markup);