var Git4CForcePredefinedRepositoriesWarning = {
    getComponent: function (Events) {
        return{
            template:
                   '<section role="dialog" id="force_predefined_repositories-warning" class="aui-layer aui-dialog2 aui-dialog2-warning aui-dialog2-small" aria-hidden="true">'+
                   '<!-- Dialog header -->'+
                   '<header class="aui-dialog2-header">'+
                   '    <!-- The dialog\'s title -->'+
                   '    <h2 class="aui-dialog2-header-main">WARNING</h2>'+
                   '    <!-- Close icon -->'+
                   '</header>'+
                   '<!-- Main dialog content -->'+
                   '<div class="aui-dialog2-content">'+
                   '    <p style="font-size: 12px; text-align: center;">You are about to block the possibility of using custom repositories.</p>'+
                   '    <p style="text-align: center"><b>Users will not be allowed to define any new repository, but those that already are in use will still be functional</b>.</p>'+
                   '</div>'+
                   '<!-- Dialog footer -->'+
                   '<footer class="aui-dialog2-footer">'+
                   '    <!-- Actions to render on the right of the footer -->'+
                   '    <div class="aui-dialog2-footer-actions">'+
                   '        <button v-on:click="confirmForcePredefined" class="aui-button aui-button-primary">Block</button>'+
                   '        <button v-on:click="closeDialog" class="aui-button aui-button-link">Cancel</button>'+
                   '    </div>'+
                   '    <!-- Hint text is rendered on the left of the footer -->'+
                   '</footer>'+
                   '</section>',
            methods:{
                closeDialog: function() {
                    this.$emit("cancelForcePredefinedRepositoriesWarning")
                },
                confirmForcePredefined: function(){
                    this.$emit("closeForcePredefinedRepositoriesWarning")
                    this.$emit("confirmForcePredefinedRepositoriesWarning")
                }
            }

        }
    }
}