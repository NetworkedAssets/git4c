var Git4CEditDialog = {
    getComponent: function () {

        const uuid = "edit_file_dialog_" + Git4CEditDialog.generateId(20)

        return {

            components: {
                preview: Git4CFilePreview.getComponent()
            },

            template:
            '        <!-- Render the dialog -->'+
            '        <section ref="git4c_edit_file_dialog" role="dialog" id="' + uuid + '" class="aui-layer aui-dialog2 aui-dialog2-xlarge"'+
            '                 aria-hidden="true">'+
            '            <!-- Dialog header -->'+
            '            <header class="aui-dialog2-header">'+
            '                <!-- The dialog\'s title -->'+
            '                <h2 class="aui-dialog2-header-main">Edit file</h2>'+
            '                <!-- Actions to render on the right of the header -->'+
            '                <!-- Close icon -->'+
            '                <a class="aui-dialog2-header-close">'+
            '                    <span class="aui-icon aui-icon-small aui-iconfont-close-dialog">Close</span>'+
            '                </a>'+
            '            </header>'+
            '             <div ref="dialogcontent" class="aui-dialog2-content" style="padding: 0; min-height: 100%; display: flex">'+
            '             <!-- Main dialog content -->'+
            '                 <div v-show="!preview" style="height: 100%; width: 100%">'+
            '                     <div v-show="!isMarkdown" ref="code_content">'+
            '                         <pre ref="ace" class="git4c_ace_editor"></pre>'+
            '                     </div>'+
            '                     <div v-show="isMarkdown" ref="markdown_content" style="padding: 20px">'+
            '                         <textarea ref="textarea" class="git4c_edit_file_textarea"></textarea>'+
            '                     </div>'+
            '                 </div>'+
            '                 <div v-show="preview" style="width: 100%; height: 100%;">'+
            '                     <preview :container="container" :file="content"></preview>'+
            '                 </div>'+
            '             </div>'+
            '            <!-- Dialog footer -->' +
            '            <footer class="aui-dialog2-footer">'+
            '                <div class="aui-dialog2-footer-actions" style="height: 100%; display: flex; align-items: center">' +
            '                    <div class="publish-spinner"></div>'+
            '                    <button :disabled="isAnonymous" id="git4c-edit-dialog-publish-button" @click="saveFile" class="aui-button" style="margin-left: 15px;">Publish</button>' +
            '                    <span v-show="isAnonymous" ref="publish_button_question_mark" style="margin-left: 10px" class="aui-icon aui-icon-small aui-iconfont-help">Anonymous</span>' +
            '                    <button class="aui-button aui-button-link" @click="close">Close</button>' +
            '                </div>' +
            '                <div class="aui-dialog2-footer-actions" style="margin-right: 15px">'+
            '                    <form class="aui" action="#">' +
            '                        <input id="git4c_commit_message" v-model="commitMessage" class="text" placeholder="What did you change?">' +
            '                    </form>' +
            '                </div>' +
            '                <div v-if="commitMessageErrorMessage" class="aui-dialog2-footer-actions" style="color: red; height: 100%; display: flex; align-items: center; margin-right: 5px">' +
            '                    <div id="git4c-edit-file-dialog-error-message">' +
            '                        {{ commitMessageErrorMessage }} '+
            '                    </div>' +
            '                </div>' +
            '                <div class="aui-dialog2-footer-actions" style="height: 100%; display: flex; align-items: center; margin-right: 20px">' +
            '                   <div v-show="customBranchCheckbox" style="margin-right: 5px;">' +
            '                       <div>documentation/</div>' +
            '                   </div>' +
            '                   <div>' +
            '                       <form v-show="customBranchCheckbox" class="aui" action="#">' +
            '                           <input id="git4c_custom_branch_input" v-model="customBranch" class="text" placeholder="Branch name">' +
            '                       </form>' +
            '                   </div>' +
            '                   <div v-if="false" class="checkbox" style="height: 100%; display: flex; align-items: center">' +
            '                       <input id="git4c_custom_branch_checkbox" class="checkbox" type="checkbox" v-model="customBranchCheckbox">' +
            '                       <label for="checkBoxTwo">Create branch</label>' +
            '                   </div>' +
            '                   <span v-show="false" ref="branch_tooltip" style="margin-left: 10px" class="aui-icon aui-icon-small aui-iconfont-help">New branch</span>' +
            '                   <div class="preview-spinner" style="margin-left: 20px"></div>'+
            '                   <button id="togglePreviewButton" @click="togglePreview" class="aui-button" style="margin-left: 15px">{{previewButtonText}}</button>'+
            '                </div>' +
            '           </footer>'+
            '        </section>' ,
            data: function () {
                return {
                    isAnonymous: !AJS.Data.get("remote-user"),
                    preview: false,
                    content: "",
                    customBranchErrorMessage: null,
                    customBranch: "",
                    customBranchCheckbox: false,
                    commitMessageErrorMessage: null,
                    commitMessage: "",
                    inter: undefined,
                    file: undefined,
                    macroUuid: undefined,

                    container: "#" + uuid + " .aui-dialog2-content"
                }
            },
            computed: {
                isMarkdown: function() {
                    if (this.file) {
                        return this.file.endsWith("md")
                    } else {
                        return true
                    }
                },
                previewButtonText: function () {
                    if (this.preview) {
                        return "Edit"
                    } else {
                        return "Preview"
                    }
                }
            },
            watch: {
                preview: function () {
                    if (this.inter) {
                        this.$nextTick(function () {
                            this.inter.resize()
                        })
                    }
                    if (this.preview) {
                        $(this.$el).find("#git4c_editdialog_editor").css('visibility', 'hidden');
                        $(this.$el).find("#git4c_editdialog_preview").css('visibility', 'visible');
                    } else {
                        $(this.$el).find("#git4c_editdialog_editor").css('visibility', 'visible');
                        $(this.$el).find("#git4c_editdialog_preview").css('visibility', 'hidden');
                    }
                }
            },
            mounted: function() {
                AJS.$(this.$refs["branch_tooltip"]).tooltip({
                    title: function () {
                        return "This option allows you to create new branch for your edited file."
                    }
                });

                AJS.$(this.$refs["publish_button_question_mark"]).tooltip({
                    title: function () {
                        return "Anonymous users cannot edit files"
                    }
                });

            },
            methods: {

                togglePreview: function () {

                    if (this.preview) {
                        this.preview = false
                        return
                    }

                    const vm = this

                    AJS.$("#" + uuid + " .preview-spinner").spin()
                    $(this.$el).find("#togglePreviewButton").attr("disabled", true);

                    const doc = vm.inter.getValue()

                    const o = {
                        file: vm.file,
                        content: doc
                    }

                    Vue.http.post(
                        UrlService.getRestUrl('documentation', vm.macroUuid, 'file', 'preview'), o)
                        .then(function (response) {
                            vm.content = response.data
                            AJS.$("#" + uuid + " .preview-spinner").spinStop()
                            $(vm.$el).find("#togglePreviewButton").removeAttr("disabled");
                            vm.preview = true
                        })
                        .catch(function (err) {
                            console.error(err)
                            alert("Cannot generate file preview")
                            AJS.$("#" + uuid + " .preview-spinner").spinStop()
                            $(vm.$el).find("#togglePreviewButton").removeAttr("disabled");
                        })

                },

                close: function () {

                    const dialogId = "#" + uuid

                    AJS.dialog2(dialogId).hide();
                },

                show: function (macroUuid, file, content) {

                    this.preview = false
                    this.commitMessageErrorMessage = null
                    this.commitMessage = ""
                    this.file = file
                    this.macroUuid = macroUuid
                    const vm = this

                    var fileName

                    if (file.contains("/")) {
                        fileName = file.substr(file.lastIndexOf("/") + 1)
                    } else {
                        fileName = file
                    }

                    //interface
                    var inter

                    this.$nextTick(function () {

                        var simpleMde = null

                        if (file.endsWith("md")) {
                            //SimpleMCE
                            $(this.$refs.textarea).show()
                            simpleMde = new SimpleMDE({
                                element: this.$refs.textarea,
                                spellChecker: false,
                                shortcuts: {
                                    "toggleOrderedList": null
                                }
                            });

                            simpleMde.value(content)

                            inter = {
                                getValue: function () {
                                    return simpleMde.value()
                                },
                                hide: function () {
                                    simpleMde.toTextArea()
                                },
                                resize: function () {

                                }
                            }

                        } else {
                            //Ace editor
                            $(this.$refs.textarea).hide()
                            var editor = ace.edit($(this.$el).find(".git4c_ace_editor")[0]);
                            editor.setTheme("ace/theme/tomorrow");
                            var mode = ace.require("ace/ext/modelist").getModeForPath(file).mode
                            editor.session.setMode(mode)
                            editor.getSession().setUseWrapMode(true);
                            editor.setValue(content)
                            editor.gotoLine(0);

                            inter = {
                                getValue: function () {
                                    return editor.getValue()
                                },
                                hide: function () {
                                      editor.destroy()
                                },
                                resize: function () {
                                    const height = $(vm.$refs["dialogcontent"]).height()
                                    $(vm.$refs["ace"]).height(height)
                                    editor.resize();
                                }
                            }
                        }

                        this.inter = inter

                        const dialogId = "#" + uuid

                        AJS.dialog2(dialogId).on("show", function () {

                            $(dialogId + " .aui-dialog2-header-main").text("Editing: " + fileName)

                            inter.resize()
                        })

                        var timeoutId = undefined

                        var hidden = false

                        AJS.dialog2(dialogId).on("hide", function() {
                            inter.hide()
                            hidden = true
                            if (timeoutId) {
                                clearTimeout(timeoutId)
                            }
                        });

                        AJS.dialog2(dialogId).show();

                    })

                },

                saveFile: function () {

                    const vm = this
                    this.commitMessageErrorMessage = null
                    this.customBranchErrorMessage = null
                    const commitMessage = this.commitMessage

                    if (commitMessage.length === 0) {
                        this.commitMessageErrorMessage = "You have to write what did you change!"
                    }

                    if (this.customBranchCheckbox && this.customBranch.length === 0) {
                        this.customBranchErrorMessage = "Custom branch cannot be empty"
                    }

                    if (this.customBranchErrorMessage || this.commitMessageErrorMessage) {
                        return
                    }

                    const branch = function () {

                        if (vm.customBranchCheckbox) {
                            return "documentation/" + vm.customBranch
                        } else {
                            return null
                        }

                    }()

                    const content = this.inter.getValue()

                    const o = JSON.stringify({
                        file: this.file,
                        content: content,
                        commitMessage: commitMessage,
                        branch: branch
                    });

                    AJS.$("#" + uuid + " .publish-spinner").spin()
                    $(vm.$el).find("#git4c-edit-dialog-publish-button").attr("disabled", true);

                    this.publishDocumentation(o)
                        .then(function (response) {
                            AJS.$("#" + uuid + " .publish-spinner").spinStop()
                            $(vm.$el).find("#git4c-edit-dialog-publish-button").removeAttr("disabled");

                            if (response.status !== 200) {
                                throw new Error(response.statusText);
                            }

                            AJSC.flag({
                                type: 'success',
                                close: "manual",
                                body: 'File upload succeeded'
                            });

                            const dialogId = "#" + uuid

                            AJS.dialog2(dialogId).hide();

                            setTimeout(function () {
                                window.location.reload(true)
                            }, 3000)

                        })
                        .catch(function (err) {
                            AJS.$("#" + uuid + " .publish-spinner").spinStop()
                            $(vm.$el).find("#git4c-edit-dialog-publish-button").removeAttr("disabled");

                            console.log("MarkupService.updateFile", err)

                            if (!err.data) {

                                AJSC.flag({
                                    type: "error",
                                    title: "Uploading failed",
                                    close: "manual",
                                    body: "File cannot be uploaded. Please try again and contact administrator"
                                })


                            } else if (err.data.type === "READ_ONLY_REPO") {

                                const branch = err.data.branch
                                const repoLocation = err.data.repoLocation

                                AJSC.flag({
                                    type: "error",
                                    title: "Uploading failed",
                                    close: "manual",
                                    body: "Repository is read-only. Your changes were saved on local only branch: <b>" + branch + "</b> in following location: <b>" + repoLocation +"</b>. Please contact Confluence administrator."
                                })

                            } else if (err.data.type === "ANOTHER_BRANCH") {

                                const branch = err.data.branch

                                AJSC.flag({
                                    type: 'success',
                                    close: "manual",
                                    // body: "File cannot be uploaded to given branch. File was saved on following branch: <b>" + branch + "</b>."
                                    body: 'File upload succeeded'
                                })

                                const dialogId = "#" + uuid

                                AJS.dialog2(dialogId).hide();

                                setTimeout(function () {
                                    window.location.reload(true)
                                }, 3000)

                            } else {

                                AJSC.flag({
                                    type: "error",
                                    title: "Uploading failed",
                                    close: "manual",
                                    body: "File cannot be uploaded. Please try again and contact administrator"
                                })

                            }

                            // alert("File upload failed")
                        })

                },

                publishDocumentation: function (o) {

                    const wait = function (requestId) {

                        return Vue.http.get(
                            UrlService.getRestUrl('documentation', 'request', 'publishFile', requestId))
                            .then(function (response) {

                                if (response.status === 202) {

                                    return new Promise(function (resolve, reject) {
                                        setTimeout(function () {
                                            resolve(wait(requestId))
                                        }, 1000)
                                    })

                                }

                                return response

                            })

                    }

                    return this.$http.post(
                        UrlService.getRestUrl('documentation', this.macroUuid, 'file', 'publishFile'), o)
                        .then(function (response) {
                            return response.body.requestId
                        })
                        .then(function (requestId) {
                            return wait(requestId)
                        })
                }

            }

        }

    },

    //https://stackoverflow.com/a/1349426/2511670
    generateId: function (length) {
        var text = "";
        var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for (var i = 0; i < length; i++)
            text += possible.charAt(Math.floor(Math.random() * possible.length));

        return text;
    }

}
