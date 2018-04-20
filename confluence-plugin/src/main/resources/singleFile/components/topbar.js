

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
        '            <span ref="branch_tooltip" style="margin-right: 5px" v-bind:class="{ \'git4c-red-text\': editBranch }" class="aui-icon aui-icon-small aui-iconfont-devtools-branch-small"></span> ' +
        '            <span ref="pathholder" v-bind:class="{ \'git4c-edited-red-text\': editBranch }" style="margin-right: 5px" class="git4c-file" >'+
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
        '            <button v-if="fileData && fileEditEnabled" id="git4c-toolbar-edit-button" title="Edit file" class="aui-button" ref="editfile" v-on:click="editFile()" style="margin-left: 10px">' +
        '                <span class="aui-icon aui-icon-small aui-iconfont-edit" style="margin-right: 1px">' +
        '                Edit file' +
        '                </span>' +
        '            </button>' +
        '            <div v-if="fileData">'+
        '                <button ref="raw_file_button" class="aui-button raw-file-button" v-on:click="openSourceDialog()" v-if="hasSource" style="margin-left: 10px">'+
        '                    <span class="aui-icon aui-icon-small aui-iconfont-devtools-file">'+
        '                        Show raw markdown'+
        '                    </span>'+
        '                </button>'+
        '            </div>'+
        '            <div style="margin-left: 10px; display: flex; flex-direction: column; justify-content: center" v-if="document">'+
        '                <commit-history macro-uuid="' + uuid  +'" :file="document.fullName" :branch="branchName" ref="commit_history"></commit-history>'+
        '            </div>'+
        '            <button ref="macro_info_button" v-show="fileData" class="aui-button" v-on:click="openInfoDialog()" style="margin-left: 10px">'+
        '                <span class="aui-icon aui-icon-small aui-iconfont-info">Info about macro</span>'+
        '            </button>'+
        '            <button ref="topbar_button" class="aui-button" v-on:click="toggleTopbar()" v-show="fileData && !collapsed" style="margin-left: 10px;  height: 30px; width: 38px">'+
        '                 <span class="aui-icon aui-icon-small aui-iconfont-close-dialog">'+
        '                    Hide topbar'+
        '                 </span>'+
        '            </button>'+
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
                    fileEditEnabled: fileEditEnabled,
                    hasSource: false
                }
            },
            props: {
                editBranch: {
                    type: Boolean
                },
                branchName: {
                    type: String
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
                    const vm = this
                    if (this.locationPath && this.locationPath.length > 0) {
                        if (this.locationPath.length === 1) {
                            const path = this.locationPath[0]
                            AJS.$(this.$refs.pathholder).tooltip({
                                title: function () {
                                    const suffix = vm.editBranch ? " - file was edited." : ""
                                    return path + suffix
                                }
                            });
                            return path
                        } else {
                            const fullPath = this.locationPath.join("/");
                            AJS.$(this.$refs.pathholder).tooltip({
                                title: function () {
                                    const suffix = vm.editBranch ? " - file was edited." : ""
                                    return fullPath + suffix
                                }
                            });
                            return "../" + this.locationPath[this.locationPath.length - 1]
                        }
                    }
                }
            },
            watch: {
                branchName: function () {
                    this.updateBranchTooltip()
                },
                editBranch: function () {
                    this.updateBranchTooltip()
                }
            },
            mounted: function () {
                const vm = this

                AJS.$(vm.$refs["macro_info_button"]).tooltip({
                    title: function () {
                        return "Info about macro"
                    }
                })

                AJS.$(vm.$refs["edit_branch_icon"]).tooltip();

                Git4CApi.getExtractorDataForMacro(uuid)
                    .then(function (extractorData) {
                        if (extractorData.type === "METHOD") {
                            vm.extractorData = extractorData.name
                        } else if (extractorData.type === "LINES") {
                            vm.extractorData = "Lines: " + extractorData.startLine + " - " + extractorData.endLine
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

                    vm.hasSource = Git4CUtils.hasSourceCode(name)

                    vm.$nextTick(function() {

                        $(vm.$refs.editfile).tooltip({
                            title: function(){
                                if (vm.editBranch) {
                                    return "Edit file (file has been modified)"
                                } else {
                                    return "Edit file"
                                }
                            }
                        })

                        $(vm.$refs.raw_file_button).tooltip({
                            title: function(){
                                return "View the full source of this file"
                            }
                        })
                        $(vm.$refs.topbar_button).tooltip({
                            title: function () {
                                return "Hide toolbar"
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
                    })

                    // alert(document)
                })
            },
            methods: {
                updateBranchTooltip: function() {
                    const vm = this
                    if (this.branchName) {
                        AJS.$(this.$refs["branch_tooltip"]).tooltip({
                            title: function () {
                                const suffix = vm.editBranch ? " - file was edited" : ""
                                return "Current branch: " + vm.branchName + suffix
                            }
                        })
                    }
                },
                openSourceDialog: function () {
                    Events.$emit("openSourceDialog")
                },
                toggleCollapsed: function () {
                    Events.$emit("setCollapse", !this.collapsed)
                    this.collapsed = !this.collapsed
                },
                toggleTopbar: function () {
                    Events.$emit("toggleTopbar")
                },
                toggleLines: function () {
                    Events.$emit("setLines", !this.lines)
                    this.lines = !this.lines
                },
                openInfoDialog: function () {
                    Events.$emit("infoDialog")
                },
                editFile: function () {
                    Events.$emit("editFile")
                }
            }
        }
    }

};
