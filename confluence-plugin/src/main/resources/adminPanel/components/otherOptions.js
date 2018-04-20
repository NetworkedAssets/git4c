var Git4COtherOptions = {
    getComponent: function (Events) {


        const baseUrl = AJS.contextPath() + "/rest/doc/1.0";
        const restUrl = baseUrl + "/documentation";


        return {
            template: '<div style="margin-top: 80px;">' +
            '     <hr/>' +
            '     <h3 style="margin-bottom: 15px; margin-top:0">Other Settings</h3>' +
            '     <div id="restore_default_globs-div" style="margin-top: 10px;">' +
            '         <button id="restore_default_globs-button" v-on:click="restoreDefaultGlobsRequest" class="aui-button aui-button-primary" >Restore default filters</button>' +
            '         <a id="restore_default_globs-button-hint" style="margin-left: 10px;" class="aui-icon aui-icon-small aui-iconfont-error"></a>' +
            '     </div>' +
            '     <div>' +
            '         <button v-on:click="cleanUnusedDataRequest" style="margin-top: 10px" class="aui-button aui-button-primary">Clean unused data</button>' +
            '         <a id="clean_unused_data_hint" style="margin-left: 10px;" class="aui-icon aui-icon-small aui-iconfont-error"></a>' +
            '     </div>' +
            '     <div id="remove_data-div">' +
            '         <button id="remove_data-button" style="margin-top: 10px; background-color: #d04437" v-on:click="cleanDataRequest" class="aui-button aui-button-primary" >Clean plugin data</button>' +
            '         <a id="remove_data-button-hint" style="margin-left: 10px; color: red;" class="aui-icon aui-icon-small aui-iconfont-error"></a>' +
            '     </div>' +
            '     <div id="force_predefined_repositories-div" style="margin-top: 50px;">' +
            '      <h3 style="margin-top: 20px; display: flex; justify-content: space-between; margin-bottom: 10px"> Force Predefined Repositories ' +
            '      <div style="display: flex;">' +
            '        <label class="switch">' +
            '        <input v-model="forcePredefinedUiState" ref="force_predefined_toggle" type="checkbox">' +
            '        <span class="slider round"></span> ' +
            '        </label>' +
            '      </div>' +
                '</h3>'+
            '     </div>' +
            '</div>'
            ,
            data: function () {
                return {
                    forcePredefinedUiState: undefined,
                    forcePredefinedServerState: undefined

                }
            },
            watch: {
                forcePredefinedUiState: function () {
                    this.toggleForcePredefinedRepositoriesRequest()
                }
            },
            methods: {
                cleanDataRequest: function () {
                    this.$emit("cleanDataRequest")
                },
                restoreDefaultGlobsRequest: function () {
                    this.$emit("restoreDefaultGlobsRequest")
                },
                cleanUnusedDataRequest: function () {
                    this.$emit("cleanUnusedDataRequest")
                },
                cleanData: function () {
                    const vm = this
                    Git4CApi.cleanData()
                        .then(function () {
                            AJSC.flag({
                                type: 'success',
                                title: "Removal successful",
                                close: 'auto',
                                persistent: false,
                                body: '<p>Cleaning data was finished successfully</p>'
                            });
                            vm.$emit("refreshRequest")
                        })
                        .catch(function () {
                            AJSC.flag({
                                type: 'error',
                                title: "Error",
                                close: 'auto',
                                persistent: false,
                                body: '<p>Error occurred during cleaning data</p>'
                            });
                        })
                },
                restoreDefaultGlobs: function () {
                    const vm = this
                    Git4CApi.restoreDefaultGlobs()
                        .then(function () {
                            vm.$emit("refreshRequest")
                        })
                },

                toggleForcePredefinedRepositoriesRequest: function () {
                    var state = this.forcePredefinedUiState;
                    if (state !== this.forcePredefinedServerState) {

                        if (this.forcePredefinedUiState) {
                            this.$emit("forcePredefinedRepositoriesRequest")
                        }
                        else {
                            this.toggleForcePredefinedRepositories()
                        }
                    }
                },
                toggleForcePredefinedRepositories: function () {
                    const vm = this;

                    const state = this.forcePredefinedUiState;

                    Git4CApi.setPredefinedRepositoriesForceSetting(state)
                        .then(function () {
                            vm.forcePredefinedServerState = state;
                        })

                },
                toggleForcePredefinedRepositoriesCanceled: function () {
                    this.forcePredefinedUiState = this.forcePredefinedServerState;
                },
                cleanUnusedData: function () {
                    Git4CApi.cleanUnusedData()
                        .then(function () {
                            AJSC.flag({
                                type: 'success',
                                title: "Removal successful",
                                close: 'auto',
                                persistent: false,
                                body: '<p>Cleaning unused data was finished successfully</p>'
                            });
                        })
                        .catch(function () {
                            AJSC.flag({
                                type: 'error',
                                title: "Error",
                                close: 'auto',
                                persistent: false,
                                body: '<p>Error occurred during cleaning unused data</p>'
                            });
                        })
                },
                setTooltips: function () {
                    AJS.$("#remove_data-button-hint").tooltip({
                        title: function () {
                            return "Click here clean all plugin data";
                        }
                    });
                    AJS.$("#restore_default_globs-button-hint").tooltip({
                        title: function () {
                            return "Click here to restore default filters (for Markdown, Java, Gherkin, Kotlin and Scala)";
                        }
                    });
                    AJS.$("#clean_unused_data_hint").tooltip({
                        title: function () {
                            return "Click here to remove unused data";
                        }
                    });
                },
                setToggleForcedPredefined: function () {
                    const vm = this
                    Git4CApi.getPredefinedRepositoriesForceSetting()
                        .then(function (response) {
                            vm.forcePredefinedServerState = response.forced;
                            vm.forcePredefinedUiState = response.forced;
                        })
                }
            },
            mounted: function () {
                this.setTooltips()
                this.setToggleForcedPredefined()
            }

        }
    }
}