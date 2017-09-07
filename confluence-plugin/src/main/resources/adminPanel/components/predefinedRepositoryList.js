var Git4CPredefinedRepositoryList = {
    getComponent: function (Events) {

        const baseUrl = AJS.contextPath() + "/rest/doc/1.0";
        const restUrl = baseUrl + "/documentation";

        return {
            data: function () {
                return {
                    repositoryList: undefined,
                    editing: false,
                    editedId: undefined
                }
            },
            template: `
            <div id="manage_repositories-div" style="margin-top: 30px;">
            
                <div id="add_repository-div ">
                    <h3>Predefined Repositories
                        <button v-on:click="openCustomDialog()" id="add_repository-button" class="aui-button" style="float:right; margin-bottom:10px">Add Predefined Repository</button>
                        <a id="add_repository-button-hint" style="margin-right: 10px; padding-top:13px; float:right;" class="aui-icon aui-icon-small aui-iconfont-info"></a>
                    </h3>
                </div>
                
                <table id="predefined_repo_table" class="aui">
                    <thead>
                        <tr>
                            <th id="predefined_table-url">URL</th>
                            <th id="predefined_table-name">Name</th>
                            <th id="predefined_table-type">Type</th>
                            <th id="predefined_table-action">Action</th>
                        </tr>
                    </thead>
                    <tbody id="predefined_repo_table_body">
                        <tr v-for="repo in repositoryList">
                        <td> {{repo.sourceRepositoryUrl}} </td>
                        <td> {{repo.name}} </td>
                        <td> {{repo.authType}} </td>
                        <td>
                        <ul class="menu">
                            <li>
                                <a v-bind:id="'repository-list_edit_button-' + repo.uuid" v-on:click="editRepository(repo.uuid)" style="color: grey; background-color: white" class="aui-icon aui-icon-small aui-iconfont-edit"></a>
                            </li>
                            <li>
                                <a v-bind:id="'repository-list_remove_button-' + repo.uuid" v-on:click="removeRepositoryRequest(repo.uuid)" style="color: grey; background-color: white" class="aui-icon aui-icon-small aui-iconfont-delete" ></a>
                            </li>
                        </ul>
                        </td>
                        </tr>
                    </tbody>
                </table>
            </div>
            `,
            methods: {
                openCustomDialog(repositoryInfo = undefined){
                    if(this.editing == true)
                    {
                        this.$emit("openCustomRepositoryDialog", repositoryInfo)
                    }
                    else {
                        this.$emit("openCustomRepositoryDialog")
                    }
                },
                getPredefinedList() {
                    Vue.http.get(restUrl + "/predefine")
                        .then((response) => {
                            if (response.status !== 200) {
                                throw new Error(response.statusText)
                            }
                            this.repositoryList = response.data
                            this.setTooltips()
                        }).catch((err) => {
                        return Promise.reject(err);
                    });
                },

                processRepository(repository){
                    var url = restUrl + "/predefine"
                    if(this.editing == true)
                    {
                        url += "/" + this.editedId

                    }
                    const response = Vue.http.post(url, repository).then((response) => {
                        if (response.status !== 200) {
                            throw new Error(response.statusText);
                        }
                        this.$emit("repositoryProcessed", response.json)
                    }).catch((err) => {
                        err.text().then(text => {
                            this.$emit("repositoryRejected", text);
                        })
                    });
                },
                editRepository(uuid){
                    this.editing = true
                    this.editedId = uuid
                    const repository = this.repositoryList.find(it => it.uuid == this.editedId)
                    this.openCustomDialog(repository)
                },
                removeRepositoryRequest(uuid){
                    this.$emit("removeRepositoryRequest", uuid)
                },
                removeRepository(uuid){
                    Vue.http.delete(restUrl + "/predefine/" + uuid).then(()=>{
                    this.getPredefinedList()
                    })
                },
                setTooltips(){
                    AJS.$("#add_repository-button-hint").tooltip({
                        title: function () {
                            return "click here to add a new predefined repository. Page owners will be able to use this glob in Git Viewer For Confluence macro creation";
                        }
                    });
                    if(this.repositoryList){
                    this.repositoryList.forEach( it =>{
                        AJS.$("#repository-list_edit_button-" + it.uuid).tooltip({
                            title: function () {
                                return "click here to edit this repository";
                            }
                        });
                        AJS.$("#repository-list_remove_button-" + it.uuid).tooltip({
                            title: function () {
                                return "click here to remove this repository";
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


