var Git4CPredefinedGlobList = {
    getComponent: function (Events) {

        const baseUrl = AJS.contextPath() + "/rest/doc/1.0";
        const restUrl = baseUrl + "/documentation";

        return {
            data: function () {
                return {
                    globsList: undefined
                }
            },
            template: `
                <div>
                    <div id="manage_globs-div" style="margin-top: 30px;">
                        <div id="add_globs-div ">
                            <h3>Predefined filters
                                <button id="add_glob-button" v-on:click="openCustomDialog" class="aui-button" style="float:right; margin-bottom:10px">Add predefined filter</button>
                                <a id="add_glob-button-hint" style="margin-right: 10px; padding-top:13px; float:right;"
                                   class="aui-icon aui-icon-small aui-iconfont-info"></a>
                            </h3>
                        </div>
                    </div>
                    <table id="globs_repo_table" class="aui">
                        <thead>
                        <tr>
                            <th id="globs_table-url">Name</th>
                            <th id="globs_table-type">Pattern</th>
                            <th id="globs_table-action">Action</th>
                        </tr>
                        </thead>
                        <tbody id="predefined_glob_table_body">
                        <tr v-for="glob in globsList">
                        <td> {{glob.name}} </td>
                        <td> {{glob.glob}} </td>
                        <td>
                        <ul class="menu">
                            <li>
                                <a v-bind:id="'glob-list_remove_button-' + glob.uuid" v-on:click="removeGlobRequest(glob.uuid)" style="color: grey; background-color: white" class="aui-icon aui-icon-small aui-iconfont-delete" ></a>
                            </li>
                        </ul>
                        </td>
                        </tr>
                    </tbody>                    
                    </table>
                </div>
                `,
            methods:{
                openCustomDialog(){
                    this.$emit("openCustomGlobDialog")
                },
                getPredefinedList() {
                    Vue.http.get(restUrl + "/glob")
                        .then((response) => {
                            if (response.status !== 200) {
                                throw new Error(response.statusText)
                            }
                            this.globsList = response.data.globs
                            this.setTooltips()
                        }).catch((err) => {
                        return Promise.reject(err);
                    });
                },
                processGlob(glob){
                    var url = restUrl + "/glob"
                    const response = Vue.http.post(url, glob).then((response) => {
                        if (response.status !== 200) {
                            throw new Error(response.statusText);
                        }
                        this.$emit("globProcessed", response.json)
                    }).catch((err) => {
                        err.text().then(text => {
                            this.$emit("globRejected", text);
                        })
                    });
                },
                removeGlobRequest(uuid){
                    this.$emit("removeGlobRequest", uuid)
                },
                removeGlob(uuid){
                    Vue.http.delete(restUrl + "/glob/" + uuid).then(()=>{
                        this.getPredefinedList()
                    })
                },
                setTooltips(){
                    AJS.$("#add_glob-button-hint").tooltip({
                        title: function () {
                            return "click here to add new predefined filter. Page owners will be able to use this filter in Git Viewer For Confluence macro creation";
                        }
                    });
                    if(this.globsList){
                    this.globsList.forEach( it =>{
                        AJS.$("#glob-list_remove_button-" + it.uuid).tooltip({
                            title: function () {
                                return "click here to remove this filter";
                            }
                        });
                    })
                }}
            },
            mounted(){
                this.setTooltips()
            }
        }
    }
}