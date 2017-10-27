var Git4CUnusedDataWarning = {
    getComponent: function (Events) {
        return{
            data: function(){
                return{
                    requestedUuid: undefined
                }
            },
            template:
                   '<section role="dialog" id="clean_unused_data-warning" class="aui-layer aui-dialog2 aui-dialog2-warning aui-dialog2-small" aria-hidden="true">'+
                   '<!-- Dialog header -->'+
                   '<header class="aui-dialog2-header">'+
                   '    <!-- The dialog\'s title -->'+
                   '    <h2 class="aui-dialog2-header-main">WARNING</h2>'+
                   '    <!-- Close icon -->'+
                   '</header>'+
                   '<!-- Main dialog content -->'+
                   '<div class="aui-dialog2-content">'+
                   '    <p style="font-size: 13px">You are about to permamently remove unused data from repository.</p>'+
                   '    <p style="text-align: center"><b>Information about repositories that are not currently used will be removed</b>.</p>'+
                   '</div>'+
                   '<!-- Dialog footer -->'+
                   '<footer class="aui-dialog2-footer">'+
                   '    <!-- Actions to render on the right of the footer -->'+
                   '    <div class="aui-dialog2-footer-actions">'+
                   '        <button v-on:click="confirmRemoveRepository" class="aui-button aui-button-primary">Remove</button>'+
                   '        <button v-on:click="closeDialog" class="aui-button aui-button-link">Cancel</button>'+
                   '    </div>'+
                   '    <!-- Hint text is rendered on the left of the footer -->'+
                   '</footer>'+
                   '</section>',
            methods:{
                closeDialog: function() {
                    this.$emit("closeCleanUnusedDataWarning")
                },
                confirmRemoveRepository: function(){
                    this.$emit("closeCleanUnusedDataWarning")
                    this.$emit("cleanUnusedDataConfirmed")
                }
            }

        }
    }
}