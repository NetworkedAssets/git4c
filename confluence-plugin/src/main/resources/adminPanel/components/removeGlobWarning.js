var Git4CRemoveGlobWarning = {
    getComponent: function (Events) {
        return {
            data: function () {
                return {
                    requestedUuid: undefined
                }
            },
            template:
                   '<section role="dialog" id="remove_glob-warning" class="aui-layer aui-dialog2 aui-dialog2-warning aui-dialog2-small" aria-hidden="true">'+
                   '<!-- Dialog header -->'+
                   '<header class="aui-dialog2-header">'+
                   '    <!-- The dialog\'s title -->'+
                   '    <h2 class="aui-dialog2-header-main">WARNING</h2>'+
                   '    <!-- Close icon -->'+
                   '</header>'+
                   '<!-- Main dialog content -->'+
                   '<div class="aui-dialog2-content">'+
                   '    <p style="font-size: 13px; text-align: center">You are about to permanently remove a predefined filter.</p>'+
                   '    <p style="text-align: center"><b>This filter will not be available anymore in the list shown to user during macro creation</b>.</p>'+
                   '</div>'+
                   '<!-- Dialog footer -->'+
                   '<footer class="aui-dialog2-footer">'+
                   '    <!-- Actions to render on the right of the footer -->'+
                   '    <div class="aui-dialog2-footer-actions">'+
                   '        <button id="remove_glob-remove_button" v-on:click="confirmRemoveGlob" class="aui-button aui-button-primary">Remove</button>'+
                   '        <button id="remove_glob-cancel_button" v-on:click="closeDialog" class="aui-button aui-button-link">Cancel</button>'+
                   '    </div>'+
                   '    <!-- Hint text is rendered on the left of the footer -->'+
                   '</footer>'+
                   '</section>'
            ,
            methods: {
                closeDialog: function () {
                    this.$emit("closeRemoveGlobWarning")
                },
                confirmRemoveGlob: function () {
                    this.$emit("closeRemoveGlobWarning")
                    this.$emit("removeGlobConfirmed", this.requestedUuid)
                }
            }

        }
    }
}