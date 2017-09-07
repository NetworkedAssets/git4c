var TopBar = {
    getComponent: function(Events, uuid, lineNumbers, collapsible, collapseByDefault) {
        return {
            template: `
        <div class="git4c-singlefile-topbar-outer">
            <div class="git4c-singlefile-topbar-inner">
                <div v-if="!document" style="padding-left: 20px;">
                    Loading
                </div>
                <div v-else style="padding-left: 20px;">
                    <span v-for="(item, index) in locationPath">
                        <span v-if="index == locationPath.length - 1">
                            <span class="git4c-file">{{item}}</span>
                        </span>
                        <span v-else>
                            <span class="git4c-directory">{{item}}</span>
                            <span class="git4c-directory">&nbsp/&nbsp</span>
                        </span>
                    </span>
                    <span class="aui-lozenge" v-if="method">{{ method }}</span>
                </div>
                <div>
                    <!--<span v-if="fileData" style="padding-left: 20px; padding-right: 4px; font-size: 12px;">-->
        <!--Last change: {{fileData.updateTime.toLocaleString()}} by {{fileData.authorFullName}} <{{fileData.authorEmail}}>-->
                    <!--</span>-->
                    <span v-if="fileData" style="padding-left: 20px; padding-right: 4px; font-size: 12px;">
                        Last change:
                        <a ref="updatetime" class="git4c-link-black" href="javascript:void(0)">{{fileData.updateTime.toLocaleDateString()}}</a>
                        by
                        <a ref="author" class="git4c-link-black" href="javascript:void(0)">{{fileData.authorFullName}}</a>
                    </span>
                </div>
            </div>
            <div>
                <span style="display: flex; float: right; padding-right: 20px">
                    <div v-if="fileData && !collapsed && canHaveLines">
                        <button class="aui-button aui-button-link" v-on:click="toggleLines">
                        <span class="expand-control-icon icon" v-bind:class="{ expanded: lines }">&nbsp;</span>{{ linesButtonText }}
                        </button> 
                    </div>
                    <div v-if="collapsible">
                        <button class="aui-button aui-button-link" v-on:click="toggleCollapsed">
                        <span class="expand-control-icon icon" v-bind:class="{ expanded: !collapsed }">&nbsp;</span>{{ collapsedButtonText }}
                        </button> 
                    </div>
                    <div v-if="fileData">
                        <button class="aui-button" v-on:click="openDialog()" v-if="hasSource" style="margin-left: 10px">
                            <span class="aui-icon aui-icon-small aui-iconfont-devtools-file">
                                Show raw markdown
                            </span>
                        </button>
                    </div>
                </span>
            </div>
        </div>
            `,
            data: function () {
                return {
                    document: undefined,
                    locationPath: undefined,
                    fileData: undefined,
                    collapsed: collapsible && collapseByDefault,
                    method: undefined,
                    lines: lineNumbers,
                    canHaveLines: true,
                    collapsible: collapsible
                }
            },
            computed: {
                collapsedButtonText: function () {
                    return (this.collapsed ? "Expand" : "Collapse") + " source"
                },
                linesButtonText: function () {
                    return (this.lines ? "Hide" : "Show") + " code lines"
                }
            },
            mounted: function () {

                Vue.http.get(UrlService.getRestUrl('documentation', uuid, "method")).then((method) => {
                    this.method = method.body.name
                })

                Events.$on("DocumentDownloaded", (document) => {

                    const vm = this

                    this.document = document
                    this.locationPath = document.locationPath
                    this.fileData = {
                        authorFullName: document.lastUpdateAuthorName,
                        authorEmail: document.lastUpdateAuthorEmail,
                        updateTime: new Date(document.lastUpdateTime)
                    };

                    this.canHaveLines = Git4CUtils.hasLines(document.name)

                    const name = document.name;
                    if (name.endsWith("md") || name.endsWith("svg") || name.endsWith("puml")) {
                        this.hasSource = true
                    } else {
                        this.hasSource = false
                    }

                    this.$nextTick(() => {
                        $(this.$refs.updatetime).tooltip('destroy')
                        $(this.$refs.updatetime).tooltip({
                            title: function () {
                                return vm.fileData.updateTime.toLocaleString()
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

                    // alert(document)
                })
            },
            methods: {
                openDialog: function () {
                    DialogService.createDialog(this.document.rawContent)
                },
                toggleCollapsed: function () {
                    Events.$emit("setCollapse", !this.collapsed)
                    this.collapsed = !this.collapsed
                },
                toggleLines: function () {
                    Events.$emit("setLines", !this.lines)
                    this.lines = !this.lines
                }
            }
        }
    }
};
