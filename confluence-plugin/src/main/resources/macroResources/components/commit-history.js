var Git4CCommitHistory = {
        getComponent: function(Events) {
            const baseUrl = AJS.params.baseUrl;
            const restUrl = baseUrl + "/rest/doc/1.0/documentation";
            return {
                template:
'                <div>'+
'                    <a v-on:click="openDialog" href="javascript:void(0)" style="white-space: nowrap;">View Commits</a>'+
'                    <section role="dialog" id="git4c_commit_history_dialog" ref="commit_history_dialog" class="aui-layer aui-dialog2 aui-dialog2 aui-dialog2-medium" aria-hidden="true">'+
'                    <header class="aui-dialog2-header">'+
'                        <h2 class="aui-dialog2-header-main">Commit history</h2>'+
'                        <a class="aui-dialog2-header-close">'+
'                            <span class="aui-icon aui-icon-small aui-iconfont-close-dialog">Close</span>'+
'                        </a>'+
'                    </header>'+
'                    <div class="aui-dialog2-content" style="display: flex; flex-direction: column;">'+
'                    <button style="display: none"></button>'+
'                        <div v-for="commit in commitList" ref="commit_table">'+
'                            <div style="display: flex; flex-direction: row; margin-bottom: 15px">'+
'                                <div style="display: flex; flex-direction: column; flex-grow: 2;">'+
'                                    <div style="word-break: break-all" v-html="commit.message"></div>'+
'                                    <div style="display: flex; flex-direction: row; margin-top: 10px; font-weight: bold;">'+
'                                        <div style="margin-right: 3px"> {{commit.date}} by </div>'+
'                                        <div> {{ commit.authorName}} </div>'+
'                                    </div>'+
'                                </div>'+
'                                <div v-bind:title="commit.id" style="width:85px; align-self: center; height: 30px""> '+
'                                <button @click.prevent href="javascript:void(0)" v-bind:title="commit.id" class="aui-button aui-button-secondary commit-id-tooltip" style="order: 4; pointer-events: none;"> {{commit.id.substring(0,8)}} </button>'+
'                                </div>'+
'                            </div>'+
'                            <hr style="border: 1px solid #CCCCCC"/>'+
'                        </div>'+
'                    </div>'+
'                    <footer class="aui-dialog2-footer">'+
'                        <div class="aui-dialog2-footer-actions">'+
'                            <button v-on:click="closeDialog" class="aui-button aui-button-secondary">Close</button>'+
'                        </div>'+
'                    </footer>'+
'                    </section>'+
'                </div>',
                data: function () {
                    return {
                        commitList: [],
                        macroUuid: null,
                        file: null,
                        branch: null
                    }
                },
                methods: {
                    openDialog: function(){
                        this.getCommitList()
                        AJS.dialog2(this.$refs.commit_history_dialog).show()
                    },
                    closeDialog: function(){
                        AJS.dialog2(this.$refs.commit_history_dialog).hide()
                    },
                    getCommitList: function(){
                        const vm = this
                        var details= {
                            branch: this.branch,
                            file: this.file
                        }
                        promise = Vue.http.post(restUrl + "/" + this.macroUuid + "/file/commits", details)
                        promise.then( function(response) {
                            vm.commitList = response.data.commitList
                            vm.commitList.forEach(function(it) {
                                {
                                    it.date = (new Date(it.date)).toLocaleString()
                                    it.message = it.message.replace(/(?:\r\n|\r|\n)/g, '<br />');
                                    return it
                                }
                            })
                            vm.$nextTick( function (){
                                vm.setTooltips()
                            })
                        })
                    },
                    setTooltips: function() {
                        AJS.$(".commit-id-tooltip").tooltip();
                    },
                    update: function(branch, file){
                        this.branch = branch
                        this.file = file
                        this.getCommitList()

                    },
                    getInfoAndUpdate: function(uuid, file){
                        const vm = this
                        this.macroUuid = uuid
                        this.file = file
                        Vue.http.get(restUrl + "/" + this.macroUuid + "/defaultBranch").then(function (response){
                            vm.branch = response.data.currentBranch
                        })
                    }
                }
            }

        }
    };