

var commitHistory = function (){
    const Bus = new Vue({})
    return Git4CCommitHistory.getComponent(Bus)
}

var TopBar = {
    getComponent: function(Events, uuid, lineNumbers, collapsible, collapseByDefault, fileEditEnabled) {
        return {
            template:
        '<div class="git4c-singlefile-topbar-outer">'+
        '    <div class="git4c-singlefile-topbar-inner">'+
        '        <div v-if="!document" style="padding-left: 20px;">'+
        '            Loading'+
        '        </div>'+
        '        <div v-show="document" style="padding-left: 20px;">'+
        '            <span ref="pathholder" class="git4c-file">'+
        '                {{path}}'+
        '            </span>'+
        '            <span class="aui-lozenge" v-if="extractorData">{{ extractorData }}</span>'+
        '        </div>'+
        '        <div>'+
        '            <span v-show="fileData" style="padding-left: 20px; padding-right: 4px; font-size: 12px;">'+
        '                Last change:'+
        '                 <a ref="updatetime" class="git4c-link-black" href="javascript:void(0)"><span v-if="fileData">{{fileData.updateTime.toLocaleDateString()}}</span></a>'+
        '                by'+
        '                <a ref="author" class="git4c-link-black" href="javascript:void(0)"><span v-if="fileData">{{fileData.authorFullName}}</span></a>'+
        '            </span>'+
        '        </div>'+
        '    </div>'+
        '    <div>'+
        '        <span style="display: flex; float: right; padding-right: 20px">'+
        '            <div v-if="fileData && !collapsed && canHaveLines">'+
        '                <button id="git4c-line_numbers-toggle_button" class="aui-button aui-button-link" v-on:click="toggleLines">'+
        '                   <span class="expand-control-icon icon" v-bind:class="{ expanded: lines }">&nbsp;</span>{{ linesButtonText }}'+
        '                </button> '+
        '            </div>'+
        '            <div v-if="collapsible">'+
        '                <button class="aui-button aui-button-link git4c-collapse-source-button" v-on:click="toggleCollapsed">'+
        '                <span class="expand-control-icon icon" v-bind:class="{ expanded: !collapsed }">&nbsp;</span>{{ collapsedButtonText }}'+
        '                </button> '+
        '            </div>'+
        '            <button v-if="fileData && fileEditEnabled" id="git4c-toolbar-edit-button" title="Edit file" class="aui-button" v-on:click="editFile()" style="margin-left: 10px">' +
        '                <span class="aui-icon aui-icon-small aui-iconfont-edit" style="margin-right: 1px">' +
        '                Edit file' +
        '                </span>' +
        '            </button>' +
        '            <div v-if="fileData">'+
        '                <button ref="raw_file_button" class="aui-button raw-file-button" v-on:click="openDialog()" v-if="hasSource" style="margin-left: 10px">'+
        '                    <span class="aui-icon aui-icon-small aui-iconfont-devtools-file">'+
        '                        Show raw markdown'+
        '                    </span>'+
        '                </button>'+
        '            </div>'+
        '            <div style="margin-left: 10px; display: flex; flex-direction: column; justify-content: center" v-if="document">'+
        '                <commit-history ref="commit_history"></commit-history>'+
        '            </div>'+
        '            <div style="margin-left: 10px; display: flex; flex-direction: column; justify-content: center" v-show="editBranch">'+
        '                <span title="You are currently on modified file" ref="edit_branch_icon" class="aui-icon aui-icon-small aui-iconfont-error">Insert meaningful text here for accessibility</span>'+
        '            </div>'+
        '        </span>'+
        '    </div>'+
        '</div>',
            data: function () {
                return {
                    document: undefined,
                    locationPath: undefined,
                    fileData: undefined,
                    collapsed: collapsible && collapseByDefault,
                    extractorData: undefined,
                    lines: lineNumbers,
                    canHaveLines: true,
                    collapsible: collapsible,
                    fileEditEnabled: fileEditEnabled
                }
            },
            props: {
                editBranch: {
                    type: Boolean
                }
            },
            components:{
                commitHistory: commitHistory()
            },
            computed: {
                collapsedButtonText: function () {
                    return (this.collapsed ? "Expand" : "Collapse") + " source"
                },
                linesButtonText: function () {
                    return (this.lines ? "Hide" : "Show") + " line numbers"
                },
                path: function () {
                    if (this.locationPath && this.locationPath.length > 0) {
                        if (this.locationPath.length === 1) {
                            const path = this.locationPath[0]
                            AJS.$(this.$refs.pathholder).tooltip({
                                title: function () {
                                    return path
                                }
                            });
                            return path
                        } else {
                            const fullPath = this.locationPath.join("/");
                            AJS.$(this.$refs.pathholder).tooltip({
                                title: function () {
                                    return fullPath
                                }
                            });
                            return "../" + this.locationPath[this.locationPath.length - 1]
                        }
                    }
                }
            },
            mounted: function () {
                const vm = this

                AJS.$(vm.$refs["edit_branch_icon"]).tooltip();

                Vue.http.get(UrlService.getRestUrl('documentation', uuid, "extractorData")).then(function(extractorData) {
                    if(extractorData.body.type === "METHOD") {
                        vm.extractorData = extractorData.body.name
                    }else if(extractorData.body.type === "LINES"){
                        vm.extractorData = "Lines: " + extractorData.body.startLine + " - " + extractorData.body.endLine
                    } else {
                        vm.extractorData = null
                    }
                })

                Events.$on("DocumentDownloaded", function(document) {


                    vm.document = document
                    vm.locationPath = document.locationPath
                    vm.fileData = {
                        authorFullName: document.lastUpdateAuthorName,
                        authorEmail: document.lastUpdateAuthorEmail,
                        updateTime: new Date(document.lastUpdateTime)
                    };

                    vm.canHaveLines = Git4CUtils.hasLines(document.name)

                    const name = document.name;
                    if (name.endsWith("md") || name.endsWith("svg") || name.endsWith("puml")) {
                        vm.hasSource = true
                    } else {
                        vm.hasSource = false
                    }

                    vm.$nextTick(function() {
                        $(vm.$refs.raw_file_button).tooltip({
                            title: function(){
                                return "View the full source of this file"
                            }
                        })
                        $(vm.$refs.updatetime).tooltip('destroy')
                        $(vm.$refs.updatetime).tooltip({
                            title: function () {
                                return vm.fileData.updateTime.toLocaleString()
                            }
                        });
                        $(vm.$refs.author).tooltip('destroy')
                        $(vm.$refs.author).tooltip({
                            title: function () {
                                // return new Date(docItem.lastUpdateTime).toLocaleString()
                                return vm.fileData.authorFullName + " <" + vm.fileData.authorEmail + ">"
                            }
                        });
                        vm.updateCommitHistory()
                    })

                    // alert(document)
                })
            },
            methods: {
                openDialog: function () {
                    const normalizedString = this.document.rawContent.replace(/\s+/g, '')

                    //https://stackoverflow.com/a/6234804/2511670
                    const escapeHtml = function(unsafe) {
                        return unsafe
                            .replace(/&/g, "&amp;")
                            .replace(/</g, "&lt;")
                            .replace(/>/g, "&gt;")
                            .replace(/"/g, "&quot;")
                            .replace(/'/g, "&#039;");
                    }

                    var dialogContent

                    if (!normalizedString) {
                        dialogContent =
                                '<div class="aui-message aui-message-generic">'+
                                    '<p class="title">'+
                                        '<strong>This file is empty</strong>'+
                                   '</p>'+
                                '</div>'
                    } else {
                        const content = this.document.rawContent
                        dialogContent =
                                '<pre>'+
                                '    <code id="git4c-dialog-code" class="git4c-code markdown">' + escapeHtml(content) + '</code>'+
                                '</pre>'
                    }

                    DialogService.createDialog(dialogContent)
                },
                toggleCollapsed: function () {
                    Events.$emit("setCollapse", !this.collapsed)
                    this.collapsed = !this.collapsed
                },
                toggleLines: function () {
                    Events.$emit("setLines", !this.lines)
                    this.lines = !this.lines
                },
                updateCommitHistory: function(){
                    this.$refs.commit_history.getInfoAndUpdate(uuid, this.document.fullName)
                },
                editFile: function () {
                    Events.$emit("editFile")
                }
            }
        }
    }

};
