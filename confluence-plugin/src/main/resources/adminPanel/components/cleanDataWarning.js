var Git4CCleanDataWarning = {
    getComponent: function (Events) {
        return{
            template:

                   '<section role="dialog" id="clean_data_warning-dialog" class="aui-layer aui-dialog2 aui-dialog2-warning aui-dialog2-small" aria-hidden="true">'+
                   '<!-- Dialog header -->'+
                   '<header class="aui-dialog2-header">'+
                   '    <!-- The dialog\'s title -->'+
                   '    <h2 class="aui-dialog2-header-main">WARNING</h2>'+
                   '    <!-- Close icon -->'+
                   '</header>'+
                   '<!-- Main dialog content -->'+
                   '<div class="aui-dialog2-content">'+
                   '    <p style="text-align: center">You are about to permamently remove all data.</p>'+
                   '    <p style="text-align: center"><b>All Git Viewer for Confluence macros will stop working.</b></p>'+
                   '</div>'+
                   '<!-- Dialog footer -->'+
                   '<footer class="aui-dialog2-footer">'+
                   '    <!-- Actions to render on the right of the footer -->'+
                   '    <div class="aui-dialog2-footer-actions">'+
                   '        <button v-if="!confirmed"  v-on:click="cleanData" id="clean_data_warning-clean_button" class="aui-button aui-button-primary">Clean</button>'+
                   '        <button v-if="confirmed && timer>0" id="clean_data_warning-timer" disabled="true" class="aui-button aui-button-primary">{{timer}}</button>'+
                   '        <button v-if="timer == 0" v-on:click="confirmCleanData" id="clean_data_warning-clean_button" class="aui-button aui-button-primary">Confirm</button>'+
                   '        <button v-on:click="closeDialog" id="clean_data_warning-cancel_button" class="aui-button aui-button-link">Cancel</button>'+
                   '    </div>'+
                   '    <!-- Hint text is rendered on the left of the footer -->'+
                   '</footer>'+
                   '</section>',
            data: function() {
              return{
                  confirmed: false,
                  timer: 5
              }
            },
            methods:{
                closeDialog: function(){
                    this.$emit("closeCleanDataWarning")
                },
                cleanData: function(){
                    const vm = this
                    this.confirmed = true
                    setTimeout(function f() {
                        vm.timer--
                        if(vm.timer>0)
                        {
                            setTimeout(f, 1000)
                        }
                    },1000)

                },
                confirmCleanData: function(){
                    this.timer = 5
                    this.confirmed = false
                    this.$emit("closeCleanDataWarning")
                    this.$emit("cleanDataConfirmed")
                }
            }

    }
    }
}