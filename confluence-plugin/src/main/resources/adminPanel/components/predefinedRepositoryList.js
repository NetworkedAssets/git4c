var Git4CPredefinedRepositoryList = {
    getComponent: function (Events) {

        const baseUrl = AJS.contextPath() + "/rest/doc/1.0";
        const restUrl = baseUrl + "/documentation";

        return {
            data: function () {
                return {
                    repositoryList: undefined,
                    editing: false,
                    loading: true,
                    editedId: undefined
                }
            },
            template: '<div id="manage_repositories-div" style="margin-top: 30px;">' +
            '    <div id="add_repository-div ">' +
            '        <h3>Predefined Repositories' +
            '            <button v-on:click="openCreateNewRepositoryDialog()" id="add_repository-button" class="aui-button" style="float:right; margin-bottom:10px">Add Predefined Repository</button>' +
            '            <a id="add_repository-button-hint" style="margin-right: 10px; padding-top:13px; float:right;" class="aui-icon aui-icon-small aui-iconfont-info"></a>' +
            '        </h3>' +
            '    </div>' +
            '    ' +
            '    <table v-show="listAvailable" id="predefined_repo_table" class="aui">' +
            '        <thead>' +
            '            <tr>' +
            '                <th id="predefined_table-url">URL</th>' +
            '                <th id="predefined_table-name">Name</th>' +
            '                <th id="predefined_table-type">Type</th>' +
            '                <th id="predefined_table-editable">Editable</th>' +
            '                <th id="predefined_table-action">Action</th>' +
            '            </tr>' +
            '        </thead>' +
            '        <tbody id="predefined_repo_table_body">' +
            '            <tr v-for="repo in repositoryList">' +
            '            <td> {{repo.sourceRepositoryUrl}} </td>' +
            '            <td> {{repo.name}} </td>' +
            '            <td> {{repo.authType}} </td>' +
            '             <td>'+
            '                 <span class="aui-icon aui-icon-small aui-iconfont-approve" v-if="repo.editable">Editable</span>'+
            '                 <span v-else class="aui-icon aui-icon-small aui-iconfont-error">Not editable</span>'+
            '             </td>'+
            '            <td>' +
            '            <ul class="menu">' +
            '                <li>' +
            '                    <a v-bind:id="\'repository-list_edit_button-\' + repo.uuid" v-on:click="editRepository(repo.uuid)" style="color: grey; background-color: white" class="aui-icon aui-icon-small aui-iconfont-edit"></a>' +
            '                </li>' +
            '                <li>' +
            '                    <a v-bind:id="\'repository-list_remove_button-\' + repo.uuid" v-on:click="removeRepositoryRequest(repo.uuid)" style="color: grey; background-color: white" class="aui-icon aui-icon-small aui-iconfont-delete" ></a>' +
            '                </li>' +
            '            </ul>' +
            '            </td>' +
            '            </tr>' +
            '        </tbody>' +
            '    </table>' +
            '    <div v-show="!loading && !listAvailable" class="aui-message aui-message-info">' +
            '        <p class="title">' +
            '            <strong>No items</strong>' +
            '        </p>' +
            '    </div>' +
            '</div>'
            ,
            computed: {
                listAvailable: function(){
                    return this.repositoryList && this.repositoryList.length
                }
            },
            methods: {
                openCreateNewRepositoryDialog: function () {
                    this.editing = false
                    this.$nextTick(function () {
                        this.openCustomDialog()
                    })
                },
                openCustomDialog: function (repositoryInfo) {
                    if (this.editing == true) {
                        this.$emit("openCustomRepositoryDialog", repositoryInfo)
                    }
                    else {
                        this.$emit("openCustomRepositoryDialog")
                    }
                },
                getPredefinedList: function () {
                    const vm = this

                    vm.loading = true

                    vm.repositoryList = []

                    Git4CApi.getPredefinedRepositories()
                        .then(function (repositories) {
                            vm.loading = false
                            vm.repositoryList = repositories
                            vm.$nextTick(function () {
                                vm.setTooltips()
                            })
                        })
                },

                processRepository: function (repository) {
                    const vm = this

                    var promise

                    if (this.editing) {
                        promise = Git4CApi.updatePredefinedRepository(this.editedId, repository)
                    } else {
                        promise = Git4CApi.createPredefinedRepository(repository)
                    }

                    this.editing = false

                    promise
                        .then(function (response) {
                            if (response.status !== 200) {
                                throw new Error(response.statusText);
                            }
                            vm.$emit("repositoryProcessed", response.json)
                        })
                        .catch(function (err) {
                            err.text().then(function (text) {
                                vm.$emit("repositoryRejected", text);
                            })
                        });

                },
                editRepository: function (uuid) {
                    const vm = this
                    this.editing = true
                    this.editedId = uuid
                    const repository = this.repositoryList.find(function (it) {
                        return it.uuid === vm.editedId
                    })
                    this.openCustomDialog(repository)
                },
                removeRepositoryRequest: function (uuid) {
                    this.$emit("removeRepositoryRequest", uuid)
                },
                removeRepository: function (uuid) {
                    const vm = this
                    Git4CApi.deletePredefinedRepository(uuid)
                        .then(function () {
                            vm.getPredefinedList()
                        })
                },
                setTooltips: function () {
                    AJS.$("#add_repository-button-hint").tooltip({
                        title: function () {
                            return "click here to add a new predefined repository. Page owners will be able to use this glob in Git Viewer For Confluence macro creation";
                        }
                    });
                    if (this.repositoryList) {
                        this.repositoryList.forEach(function (it) {
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
                    }
                }
            },
            mounted: function () {
                this.setTooltips()
            }


        }
    }
}


