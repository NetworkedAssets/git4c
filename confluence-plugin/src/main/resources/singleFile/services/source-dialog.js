var DialogService = {

    dialog:
         '<!-- Render the dialog -->'+
         '<section role="dialog" id="git4c-raw-sourcecode-dialog" class="aui-layer aui-dialog2 aui-dialog2-xlarge"'+
         '         aria-hidden="true">'+
         '    <!-- Dialog header -->'+
         '    <header class="aui-dialog2-header">'+
         '       <!-- The dialog\'s title -->'+
         '        <h2 class="aui-dialog2-header-main">Raw markdown file</h2>'+
         '        <!-- Actions to render on the right of the header -->'+
         '        <!-- Close icon -->'+
         '        <a class="aui-dialog2-header-close">'+
         '            <span class="aui-icon aui-icon-small aui-iconfont-close-dialog">Close</span>'+
         '        </a>'+
         '    </header>'+
         '    <!-- Main dialog content -->'+
         '    <div class="aui-dialog2-content" id="git4c-raw-sourcecode-dialog-content">'+
         '    </div>'+
         '    <!-- Dialog footer -->'+
         '    <footer class="aui-dialog2-footer">'+
         '        <div class="aui-dialog2-footer-actions">'+
         '            <button id="git4c-raw-sourcecode-dialog-close-button" class="aui-button">Close</button>'+
         '        </div>'+
         '    </footer>'+
         '</section>'
    ,

    createDialog: function (content) {

        if (document.getElementById("git4c-singlepage-raw-markdown-dialog") === null) {
            $("body").append(DialogService.dialog)
        }

        // content = "<div>TEST SOURCE</div>"

        // const dialogInstance = $(this.dialog).appendTo("body");
        const dialogId = "#git4c-raw-sourcecode-dialog"
        const dialog = $(dialogId)
        const closeButton = $("#git4c-raw-sourcecode-dialog-close-button")

        // contentLocation.each(function (i, block) {
        //     hljs.highlightBlock(block);
        // });


        AJS.dialog2(dialogId).on("show", function () {
            const contentLocation = $("#git4c-raw-sourcecode-dialog-content")
            // contentLocation.text(content)
            contentLocation.html(content)

            $("#git4c-dialog-code").each(function (i, block) {
                hljs.highlightBlock(block);
            });

            closeButton.blur()

            closeButton.click(function() {
                AJS.dialog2(dialogId).hide();
            })
        })

        AJS.dialog2(dialogId).on("hide", function () {
            // dialog.remove()
        })

        AJS.dialog2(dialogId).show();
        // AJS.dialog2(dialogInstance).show();
    }
}