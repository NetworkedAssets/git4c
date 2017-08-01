const router = new VueRouter({
    mode: 'hash',
    routes: [
        { path: '/:fullName?', component: Markup }
    ]
});


var Events = new Vue({});


AJS.toInit(function () {

    ParamsService.initialize()

    var defaultFileName = "";
    var vue = new Vue({
        router: router,
        el: "#app",
        data: {
            value: '',
            tree: []
        },
        methods: {
            getDefaultDocItemName: function (element) {
                var docItemName = ParamsService.getParams().defaultDocItem;
                if(docItemName.length != 0){
                    var contains = false;
                    element.children.forEach(function f(el) {
                        if(el.fullName.toLowerCase() == docItemName.toLowerCase())
                        {
                            docItemName = el.fullName
                            contains = true;
                        }
                        else el.children.forEach(ele => f(ele))
                    });
                    if(contains){
                        return docItemName
                    }
                }
                    if (element.children != null) {
                        docItemName = element.children.filter((docItem) => {
                            return (defaultFileName == docItem.name.toLowerCase());
                        }).map((docItem) => {
                            return docItem.name;
                        });

                        if (docItemName.length == 0) {
                            docItemName = element.children.filter((docItem) => {
                                return (docItem.type !== "DIR");
                            }).map((docItem) => {
                                return docItem.name;
                            });
                        }
                    }
                    return $.isArray(docItemName) ? docItemName[0] : docItemName;

            },
            getTree: function () {
                MarkupService.getTree().then((tree) => {
                    Vue.set(this, 'tree', tree.children);
                    if (this.$route.path == '/') {
                        var defaultDocItemName = this.getDefaultDocItemName(tree);
                        if (defaultDocItemName) {
                            this.$router.push('/' + encodeURIComponent(defaultDocItemName));
                        }
                    }
                    var nodesToOpen = decodeURIComponent(this.$route.path).split('/');
                    nodesToOpen.shift();
                    if(nodesToOpen.length>1)  {
                        function openTree(node, level) {
                            if (level < nodesToOpen.length) {
                                node.isOpened = true;
                                var child = node.children.filter(n => n.name == nodesToOpen[level])[0];
                                openTree(child, level + 1);
                            }
                        }

                        openTree(tree, 0);
                    }
                    Events.$emit('treeLoaded');


            }, () => {
                    NotifyService.error('Error', 'An error occurred while displaying content.')
                });
            },
            getDocumentation: function () {
                //this.working = true;
                Events.$emit('updateStart');
                MarkupService.getDocumentation().then((documentation) => {
                    //  this.working = false;
                    if (documentation != undefined) {
                        Events.$emit('updateComplete');
                    } else {
                        Events.$emit('updateError');
                    }
                },
                    (err) => {
                        console.log(err);
                        Events.$emit('updateError');
                    });
            }
        },

        created: function () {

            Events.$on('updateComplete', () => {
                //NotifyService.info('Info', 'Updating content completed successfully');
                this.getTree();
            });

            Events.$on('branchChanged', (id) => {
                ParamsService.setUuid(id.id);
                this.getTree();
                clearInterval(intervalId);
            });

            Events.$on('updateError', () => {
                NotifyService.error('Error', 'An error occurred while updating content.');
            });

            this.getDocumentation()
        },
        mounted: function () {
            //AJS.$(this.$el).find(".markup-action-buttons button").tooltip();
        }
    }).$mount('#app');

    let lastRevision = undefined;
    let alertShown = false;
    let intervalId = undefined;

    intervalId = setInterval(function () {
        MarkupService.getDocumentation().then((documentation) => {
            if (!lastRevision) {
                lastRevision = documentation.revision
            } else {
                if (lastRevision !== documentation.revision) {
                    lastRevision = documentation.revision;
                    if (!alertShown) {
                        NotifyService.persistent("Git Viewer for Confluence macro is out of date",
                            '<ul class="aui-nav-actions-list">' +
                            '<li><a href="#" onclick="location.reload(true); return false;">Refresh page</a></li>' +
                            '</ul>');
                        clearInterval(intervalId);
                        alertShown = true
                    }
                }
            }
        })
    }, 10000);

});