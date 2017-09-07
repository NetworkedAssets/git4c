var Git4CRestoreDefaultGlobsWarning = {
    getComponent: function (Events) {
        return{
            template:
                `                
                   <section role="dialog" id="restore_default_globs-dialog" class="aui-layer aui-dialog2 aui-dialog2-warning aui-dialog2-small" aria-hidden="true">
                   <!-- Dialog header -->
                   <header class="aui-dialog2-header">
                       <!-- The dialog's title -->
                       <h2 class="aui-dialog2-header-main">WARNING</h2>
                       <!-- Close icon -->
                   </header>
                   <!-- Main dialog content -->
                   <div class="aui-dialog2-content">
                       <p style="text-align: center">You are about to restore default filters.</p>
                       <p style="text-align: center"><b>All predefined filters will be deleted.</b> This process is permanent</p>
                   </div>
                   <!-- Dialog footer -->
                   <footer class="aui-dialog2-footer">
                       <!-- Actions to render on the right of the footer -->
                       <div class="aui-dialog2-footer-actions">
                           <button v-on:click="confirmRestoreDefaultGlobs" id="restore_default_globs-confirm_button" class="aui-button aui-button-primary">Restore</button>
                           <button v-on:click="closeDialog" id="restore_default_globs-cancel_button" class="aui-button aui-button-link">Cancel</button>
                       </div>
                       <!-- Hint text is rendered on the left of the footer -->
                   </footer>
                   </section>`,
            methods:{
                closeDialog(){
                    this.$emit("closeRestoreDefaultGlobsWarning")
                },
                confirmRestoreDefaultGlobs(){
                    this.$emit("closeRestoreDefaultGlobsWarning")
                    this.$emit("restoreDefaultGlobsWarningConfirmed")
                }
            }

        }
    }
}