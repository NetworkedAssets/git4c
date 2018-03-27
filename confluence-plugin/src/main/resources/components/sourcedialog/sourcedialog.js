var Git4CSourceDialog = {

    getComponent: function () {

        return {

            template:
            '<!-- Render the dialog -->'+
            '<section ref="dialog" role="dialog" id="git4c-raw-sourcecode-dialog" class="aui-layer aui-dialog2 aui-dialog2-xlarge"'+
            '         aria-hidden="true">'+
            '    <!-- Dialog header -->'+
            '    <header class="aui-dialog2-header">'+
            '       <!-- The dialog\'s title -->'+
            '        <h2 class="aui-dialog2-header-main">File source</h2>'+
            '        <!-- Actions to render on the right of the header -->'+
            '        <!-- Close icon -->'+
            '        <a class="aui-dialog2-header-close">'+
            '            <span class="aui-icon aui-icon-small aui-iconfont-close-dialog">Close</span>'+
            '        </a>'+
            '    </header>'+
            '    <!-- Main dialog content -->'+
            '    <div class="aui-dialog2-content" ref="dialog_content">'+
            '    </div>'+
            '    <!-- Dialog footer -->'+
            '    <footer class="aui-dialog2-footer">'+
            '        <div class="aui-dialog2-footer-actions">'+
            '            <button v-on:click="closeDialog" id="git4c-raw-sourcecode-dialog-close-button" class="aui-button aui-button-link">Close</button>'+
            '        </div>'+
            '    </footer>'+
            '</section>',

            methods: {

                show: function (fileContent) {

                    const normalizedString = fileContent.replace(/\s+/g, '')

                    //https://stackoverflow.com/a/6234804/2511670
                    const escapeHtml = function(unsafe) {
                        return unsafe
                            .replace(/&/g, "&amp;")
                            .replace(/</g, "&lt;")
                            .replace(/>/g, "&gt;")
                            .replace(/"/g, "&quot;")
                            .replace(/'/g, "&#039;");
                    }

                    var dialogContent

                    if (!normalizedString) {
                        dialogContent =
                            '<div class="aui-message aui-message-generic">'+
                            '<p class="title">'+
                            '<strong>This file is empty</strong>'+
                            '</p>'+
                            '</div>'
                    } else {
                        const content = fileContent
                        dialogContent =
                            '<pre>'+
                            '    <code id="git4c-dialog-code" class="git4c-code markdown">' + escapeHtml(content) + '</code>'+
                            '</pre>'
                    }

                    const dialogContentDom = $(this.$refs["dialog_content"])

                    dialogContentDom.html(dialogContent)

                    dialogContentDom.find('#git4c-dialog-code').each(function (i, block) {
                        hljs.highlightBlock(block);
                    });

                    const dialogDom = this.$refs.dialog

                    AJS.dialog2(dialogDom).show()

                },

                closeDialog: function () {
                    const dialogDom = this.$refs.dialog

                    AJS.dialog2(dialogDom).hide()
                }


            }

        }

    }

}