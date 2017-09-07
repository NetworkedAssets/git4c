var Git4CRemoveRepositoryWarning = {
    getComponent: function (Events) {
        return{
            data: function(){
                return{
                    requestedUuid: undefined
                }
            },
            template:
                `                
                   <section role="dialog" id="remove_repository-warning" class="aui-layer aui-dialog2 aui-dialog2-warning aui-dialog2-small" aria-hidden="true">
                   <!-- Dialog header -->
                   <header class="aui-dialog2-header">
                       <!-- The dialog's title -->
                       <h2 class="aui-dialog2-header-main">WARNING</h2>
                       <!-- Close icon -->
                   </header>
                   <!-- Main dialog content -->
                   <div class="aui-dialog2-content">
                       <p style="font-size: 13px">You are about to permamently remove a predefined repository.</p>
                       <p style="text-align: center"><b>All Git Viewer for Confluence macros using this repository will stop working</b>.</p>
                   </div>
                   <!-- Dialog footer -->
                   <footer class="aui-dialog2-footer">
                       <!-- Actions to render on the right of the footer -->
                       <div class="aui-dialog2-footer-actions">
                           <button id="remove_repository-remove_button" v-on:click="confirmRemoveRepository" class="aui-button aui-button-primary">Remove</button>
                           <button id="remove_repository-cancel_button" v-on:click="closeDialog" class="aui-button aui-button-link">Cancel</button>
                       </div>
                       <!-- Hint text is rendered on the left of the footer -->
                   </footer>
                   </section>`,
            methods:{
                closeDialog(){
                    this.$emit("closeRemoveRepositoryWarning")
                },
                confirmRemoveRepository(){
                    this.$emit("closeRemoveRepositoryWarning")
                    this.$emit("removeRepositoryConfirmed", this.requestedUuid)
                }
            }

        }
    }
}