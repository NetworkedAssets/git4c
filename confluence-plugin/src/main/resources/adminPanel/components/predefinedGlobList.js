var Git4CPredefinedGlobList = {
    getComponent: function (Events) {

        const baseUrl = AJS.contextPath() + "/rest/doc/1.0";
        const restUrl = baseUrl + "/documentation";

        return {
            data: function () {
                return {
                    globsList: undefined,
                    loading: true
                }
            },
            template:
                '<div>'+
                '    <div id="manage_globs-div" style="margin-top: 30px;">'+
                '        <div id="add_globs-div">'+
                '            <h3>Predefined filters'+
                '                <button id="add_glob-button" v-on:click="openCustomDialog" class="aui-button" style="float:right; margin-bottom:10px">Add predefined filter</button>'+
                '                <a id="add_glob-button-hint" style="margin-right: 10px; padding-top:13px; float:right;"'+
                '                   class="aui-icon aui-icon-small aui-iconfont-info"></a>'+
                '            </h3>'+
                '        </div>'+
                '    </div>'+
                '    <table v-show="listAvailable" id="globs_repo_table" class="aui">'+
                '        <thead>'+
                '        <tr>'+
                '            <th id="globs_table-url">Name</th>'+
                '            <th id="globs_table-type">Pattern</th>'+
                '            <th id="globs_table-action">Action</th>'+
                '        </tr>'+
                '        </thead>'+
                '        <tbody id="predefined_glob_table_body">'+
                '        <tr v-for="glob in globsList">'+
                '        <td> {{glob.name}} </td>'+
                '        <td> {{glob.glob}} </td>'+
                '        <td>'+
                '        <ul class="menu">'+
                '            <li>'+
                '                <a v-bind:id="\'glob-list_remove_button-\' + glob.uuid" v-on:click="removeGlobRequest(glob.uuid)" style="color: grey; background-color: white" class="aui-icon aui-icon-small aui-iconfont-delete" ></a>'+
                '            </li>'+
                '        </ul>'+
                '        </td>'+
                '        </tr>'+
                '    </tbody>'+
                '    </table>'+
                '    <div v-show="!loading && !listAvailable" class="aui-message aui-message-info">' +
                '        <p class="title">' +
                '            <strong>No items</strong>' +
                '        </p>' +
                '    </div>' +
                '</div>'
            ,
            computed: {
                listAvailable: function(){
                    return this.globsList && this.globsList.length
                }
            },
            methods:{
                openCustomDialog: function() {
                    this.$emit("openCustomGlobDialog")
                },
                getPredefinedList: function() {

                    const vm = this

                    Git4CApi.getGlobs()
                        .then(function(globs) {
                            vm.loading = false
                            vm.globsList = globs
                            vm.$nextTick(function () {
                                vm.setTooltips()
                            })
                        })
                },
                processGlob: function (glob){
                    const vm = this

                    Git4CApi.createGlob(glob)
                        .then(function (response) {
                            if (response.status !== 200) {
                                throw new Error(response.statusText);
                            }
                            vm.$emit("globProcessed", response.json)
                        })
                        .catch(function (err) {
                            err.bodyText().then(function (text) {
                                vm.$emit("globRejected", text);
                            })
                        });
                },
                removeGlobRequest: function(uuid){
                    this.$emit("removeGlobRequest", uuid)
                },
                removeGlob: function(uuid){
                    const vm = this
                    Git4CApi.deleteGlob(uuid)
                        .then(function() {
                            vm.getPredefinedList()
                        })
                },
                setTooltips: function(){
                    AJS.$("#add_glob-button-hint").tooltip({
                        title: function () {
                            return "click here to add new predefined filter. Page owners will be able to use this filter in Git Viewer For Confluence macro creation";
                        }
                    });
                    if(this.globsList){
                        this.globsList.forEach( function(it) {
                            AJS.$("#glob-list_remove_button-" + it.uuid).tooltip({
                                title: function () {
                                    return "click here to remove this filter";
                                }
                            });
                        })
                    }}
            },
            mounted: function(){
                this.setTooltips()
            }
        }
    }
}