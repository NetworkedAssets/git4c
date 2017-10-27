var Git4CAdminPanel = {
    start: function () {


        const Events = new Vue({});

        const v = new Vue({
            el: "#git4c_admin-root",
            data: {

            },
            template:
                '<div>'+
                '    <predefinedRepositoryList ref="predefined_repository_list"'+
                '     @openCustomRepositoryDialog="openCustomRepositoryDialog($event)"'+
                '     @repositoryRejected="processRepositoryError($event)"'+
                '     @repositoryProcessed="postProcessNewRepository"'+
                '     @removeRepositoryRequest="openRemoveRepositoryWarning($event)"'+
                '     ></predefinedRepositoryList>'+
                '    <hr style="margin-top: 80px"/>'+
                '    <predefinedGlobList ref="predefined_glob_list"'+
                '     @openCustomGlobDialog="openCustomGlobDialog()"'+
                '     @globRejected="processGlobError($event)"'+
                '     @globProcessed="postProcessNewGlob"'+
                '     @removeGlobRequest="openRemoveGlobWarning($event)"'+
                '     ></predefinedGlobList>'+
                '     <pagesWithMacroList></pagesWithMacroList>'+
                '    <otherOptions ref="other_options" id="other_options" @refreshRequest="refresh" @cleanDataRequest="openCleanDataWarning" @restoreDefaultGlobsRequest="openRestoreDefaultGlobsWarning" @cleanUnusedDataRequest="openCleanUnusedDataWarning"></otherOptions>'+
                '    <div id="custom_repository-dialog-div">'+
                '        <customRepositoryDialog id="custom_repository-dialog" ref="custom_repository_dialog" @closeCustomRepositoryDialog="closeCustomRepositoryDialog()" @repositoryDefined="forwardNewRepository($event)"></customRepositoryDialog>'+
                '    </div>'+
                '    <div id="custom_glob-dialog-div">'+
                '        <customGlobDialog id="custom_glob-dialog" ref="custom_glob_dialog" @closeCustomGlobDialog="closeCustomGlobDialog()" @globDefined="forwardNewGlob($event)"></customGlobDialog>'+
                '    </div>'+
                '    <div id="admin_panel-warnings">'+
                '         <removeRepositoryWarning  id="remove_repository-warning" ref="remove_repository_warning" @removeRepositoryConfirmed="forwardRemoveRepositoryConfirmation($event)" @closeRemoveRepositoryWarning="closeRemoveRepositoryWarning"></removeRepositoryWarning>'+
                '         <removeGlobWarning  id="remove_glob-warning" ref="remove_glob_warning" @removeGlobConfirmed="forwardRemoveGlobConfirmation($event)" @closeRemoveGlobWarning="closeRemoveGlobWarning"></removeGlobWarning>'+
                '         <cleanDataWarning id="clean_data-warning" ref="clean_data_warning" @closeCleanDataWarning="closeCleanDataWarning" @cleanDataConfirmed="forwardCleanDataConfirmation"></cleanDataWarning>'+
                '         <restoreDefaultGlobsWarning id="restore_default_globs-warning" ref="restore_default_globs" @closeRestoreDefaultGlobsWarning="closeRestoreDefaultGlobsWarning" @restoreDefaultGlobsWarningConfirmed="forwardRestoreDefaultGlobsConfirmation"></restoreDefaultGlobsWarning>'+
                '         <cleanUnusedDataWarning id="clean_unused-data-warning" ref="clean-unused-data" @closeCleanUnusedDataWarning="closeCleanUnusedDataWarning" @cleanUnusedDataConfirmed="forwardCleanUnusedDataConfirmation"></cleanUnusedDataWarning>'+
                '    </div>'+
                '</div>'
                ,
            components: {
                customRepositoryDialog: Git4CCustomRepositoryDialog.getComponent(Events),
                customGlobDialog: Git4CCustomGlobDialog.getComponent(Events),

                predefinedRepositoryList: Git4CPredefinedRepositoryList.getComponent(Events),
                predefinedGlobList: Git4CPredefinedGlobList.getComponent(Events),

                pagesWithMacroList: Git4CPagesWithMacroList.getComponent(Events),

                otherOptions: Git4COtherOptions.getComponent(Events),

                removeRepositoryWarning: Git4CRemoveRepositoryWarning.getComponent(Events),
                removeGlobWarning: Git4CRemoveGlobWarning.getComponent(Events),
                cleanDataWarning: Git4CCleanDataWarning.getComponent(Events),
                restoreDefaultGlobsWarning: Git4CRestoreDefaultGlobsWarning.getComponent(Events),
                cleanUnusedDataWarning: Git4CUnusedDataWarning.getComponent(Events)
            },
            methods: {

                // Custom Repository
                //dialog
                openCustomRepositoryDialog: function (repositoryInfo) {
                    if (repositoryInfo) {
                        this.$refs.custom_repository_dialog.initFields(repositoryInfo)
                    }
                    AJS.dialog2("#custom_repository-dialog").show()
                },
                closeCustomRepositoryDialog: function () {
                    this.$refs.custom_repository_dialog.clearFields()
                    AJS.dialog2("#custom_repository-dialog").hide()
                },

                //process
                forwardNewRepository: function (repository) {
                    this.$refs.predefined_repository_list.processRepository(repository)
                },
                processRepositoryError: function (error) {
                    this.$refs.custom_repository_dialog.showError(error)
                    this.$refs.custom_repository_dialog.saving = false
                },
                postProcessNewRepository: function () {
                    this.closeCustomRepositoryDialog()
                    this.$refs.predefined_repository_list.getPredefinedList()
                },

                //warning
                openRemoveRepositoryWarning: function (uuid) {
                    this.$refs.remove_repository_warning.requestedUuid = uuid
                    AJS.dialog2("#remove_repository-warning").show()
                },
                closeRemoveRepositoryWarning: function () {
                    AJS.dialog2("#remove_repository-warning").hide()
                },
                forwardRemoveRepositoryConfirmation: function (uuid) {
                    this.$refs.predefined_repository_list.removeRepository(uuid)
                },

                // Custom Glob
                //dialog
                openCustomGlobDialog: function(){
                    AJS.dialog2("#custom_glob-dialog").show()
                    AJS.$("#new-glob-dialog-tooltip").tooltip({
                        title: function () {
                            return "Git4C uses Glob patterns to filter files."
                        }
                    })
                },
                closeCustomGlobDialog: function() {
                    this.$refs.custom_glob_dialog.clearFields()
                    AJS.dialog2("#custom_glob-dialog").hide()
                },

                //process
                forwardNewGlob: function(glob){
                    this.$refs.predefined_glob_list.processGlob(glob)
                },
                processGlobError: function(error){
                    this.$refs.custom_glob_dialog.showError(error)
                    this.$refs.custom_glob_dialog.saving = false
                },
                postProcessNewGlob: function(){
                    this.closeCustomGlobDialog()
                    this.$refs.predefined_glob_list.getPredefinedList()
                },

                //warning
                openRemoveGlobWarning: function(uuid){
                    this.$refs.remove_glob_warning.requestedUuid = uuid
                    AJS.dialog2("#remove_glob-warning").show()
                },
                closeRemoveGlobWarning: function(){
                    AJS.dialog2("#remove_glob-warning").hide()
                },
                forwardRemoveGlobConfirmation: function(uuid){
                    this.$refs.predefined_glob_list.removeGlob(uuid)
                },


                // Clean Data
                openCleanDataWarning: function(){
                    AJS.dialog2("#clean_data-warning").show()
                },
                closeCleanDataWarning: function(){
                    AJS.dialog2("#clean_data-warning").hide()
                },
                forwardCleanDataConfirmation: function(){
                    this.$refs.other_options.cleanData()
                },


                // Restore Default Globs
                openRestoreDefaultGlobsWarning: function(){
                    AJS.dialog2("#restore_default_globs-warning").show()
                },
                closeRestoreDefaultGlobsWarning: function(){
                    AJS.dialog2("#restore_default_globs-warning").hide()
                },
                forwardRestoreDefaultGlobsConfirmation: function(){
                    this.$refs.other_options.restoreDefaultGlobs()
                },
                refresh: function(){
                    this.$refs.predefined_repository_list.getPredefinedList()
                    this.$refs.predefined_glob_list.getPredefinedList()
                },

                // Clean unused data
                openCleanUnusedDataWarning: function() {
                    AJS.dialog2("#clean_unused-data-warning").show()
                },
                closeCleanUnusedDataWarning: function() {
                    AJS.dialog2("#clean_unused-data-warning").hide()
                },

                forwardCleanUnusedDataConfirmation: function() {
                    this.$refs.other_options.cleanUnusedData()
                }


            },
            mounted: function() {
                this.refresh()
            }


        })

    }

}