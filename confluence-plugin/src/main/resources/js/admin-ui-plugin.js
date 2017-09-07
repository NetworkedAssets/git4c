(function ($) { // this closure helps us keep our variables to ourselves.
// This pattern is known as an "iife" - immediately invoked function expression

    const urlFieldId = "doc_macro-repo_url"
    const usernameFieldId = "doc_macro-login_email"
    const passwordFieldId = "doc_macro-login_password"
    const authSelectId = "doc_macro-auth_type"
    const sshFieldId = "doc_macro-ssh_key"

    const globNameFieldId = "doc_macro-glob_name"
    const globPatternFieldId = "doc_macro-glob_pattern"


    const loginForm = () => {
        return $("#doc_macro-login-form")
    }

    const urlField = () => {
        return $("#" + urlFieldId)
    }

    const usernameField = () => {
        return $("#" + usernameFieldId)
    }
    const usernameDiv = () => {
        return $("#div_" + usernameFieldId)
    }

    const passwordField = () => {
        return $("#" + passwordFieldId)
    }
    const passwordDiv = () => {
        return $("#div_" + passwordFieldId)
    }

    const authSelect = () => {
        return $("#" + authSelectId)
    }

    const sshField = () => {
        return $("#" + sshFieldId)
    }
    const sshDiv = () => {
        return $("#div_" + sshFieldId)
    }


    const loginPasswordDivs = () => {
        return [usernameDiv(), passwordDiv()]
    }
    const sshDivs = () => {
        return [sshDiv()]
    }

    const loginPasswordInputs = () => {
        return [usernameField(), passwordField(), urlField()]
    }
    const sshInputs = () => {
        return [sshField(), urlField()]
    }
    const noAuthInputs = () => {
        return [urlField()]
    }

    const globsInputs = () => {
        return [globNameField(), globPatternField()]
    }

    const usernameandpasswordOption = () => {
        return $("#doc_macro-auth_type-usernameandpasswordOption")
    }
    const sshOption = () => {
        return $("#doc_macro-auth_type-sshOption")
    }
    const noAuthOption = () => {
        return $("#doc_macro-auth_type-noAuthOption")
    }

    const saveButton = () => {
        return $('#doc_macroDialogSaveButton')
    }

    const globNameField = () => {
        return $("#" + globNameFieldId)
    }

    const globPatternField = () => {
        return $("#" + globPatternFieldId)
    }

    const saveGlobsButton = () => {
        return $('#doc_macroGlobsDialogSaveButton')
    }

    var editing = false
    var currentEditingUuid = null

    class Error {
        /**
         * @param {string} id
         * @param {string[]} serverError
         * @param {string} text
         */
        constructor(id, serverError, text) {
            this.id = id
            this.serverError = serverError
            this.text = text
        }
    }

    class FormState {
        constructor(url, connectionType, username, password, sshkey) {
            this.url = url
            this.connectionType = connectionType
            this.username = username
            this.password = password
            this.sshkey = sshkey
        }
    }

    class GlobFormState {
        constructor(name, glob) {
            this.name = name
            this.glob = glob
        }
    }

    class Credentials {
        /**
         * @param {string} type
         */
        constructor(type) {
            this.type = type
        }
    }

    class UsernamePasswordCredentials extends Credentials {
        /**
         * @param {string} username
         * @param {string} password
         */
        constructor(username, password) {
            super("USERNAMEPASSWORD")
            this.username = username
            this.password = password
        }
    }

    class SSHKeyCredentials extends Credentials {
        /**
         * @param {string} sshKey
         */
        constructor(sshKey) {
            super("SSHKEY")
            this.sshKey = sshKey
        }
    }

    class NoAuthCredentials extends Credentials {
        constructor() {
            super("NOAUTH")
        }
    }

    let stage = 0;

    var createPredefinedRepositoryDialogInstance = null;
    var createPredefinedGlobDialogInstance = null;
    var cleanDataPopUpInstance = null;
    var removeRepositoryPopUpInstance = null;
    var removeGlobPopUpInstance = null;


    AJS.toInit(function () {

        const baseUrl = AJS.contextPath() + "/rest/doc/1.0";
        const restUrl = baseUrl + "/documentation";
        const cleanAllDataButton = $("#remove_data-button");
        const createPredefinedRepositoryButton = $("#add_repository-button")
        const createPredefinedGlobButton = $("#add_glob-button")

        refreshPredefinedRepositoriesTable()

        refreshPredefinedGlobsTable()

        cleanAllDataButton.click(function () {
            AJS.dialog2(getCleanAllDataPopUpInstance()).show()
        });

        AJS.$("#remove_data-button-hint").tooltip({
            title: function () {
                return "Warning! " +
                    "This button will remove all data. " +
                    "This process can not be reverted! ";
            }
        });

        AJS.$("#add_repository-button-hint").tooltip({
            title: function () {
                return "Press this button to create a new predefined repository. This repository will be available for users to use in Git Viewer For Confluence macro.";
            }
        });

        createPredefinedRepositoryButton.click(function () {
            AJS.dialog2(getCreateOrEditPredefinedRepositoryDialogInstance()).show();
            clearErrorsAtCreateOrEditPredefinedRepositoryDialog()
            collectDataFromCreateOrEditPredefinedRepositoryDialog("start")
        });

        AJS.$("#add_glob-button-hint").tooltip({
            title: function () {
                return "Press this button to create a new predefined glob. This glob will be available for users to use in Git Viewer For Confluence during creation of macro.";
            }
        });

        createPredefinedGlobButton.click(function () {
            AJS.dialog2(getCreatePredefinedGlobDialogInstance()).show();
            clearErrorsAtCreatePredefinedGlobDialog()
            collectDataFromCreatePredefinedGlobDialog("start")
        });

    });


    const baseUrl = AJS.contextPath() + "/rest/doc/1.0";
    const restUrl = baseUrl + "/documentation";


    var getPredefinedRepositoriesList = function () {
        $.ajax({
            url: restUrl + "/predefine",
            type: 'GET',
            data: "",
            contentType: "application/json",
            dataType: "json",
            success: function (data, status, jqXHR) {
                setPredefinedRepositoriesTable(data)
            },
            error: function (jqXHR, status) {
                showError(jqXHR.responseText)
            }
        })
    };

    var getGlobsList = function () {
        $.ajax({
            url: restUrl + "/glob",
            type: 'GET',
            data: "",
            contentType: "application/json",
            dataType: "json",
            success: function (data, status, jqXHR) {
                setGlobsTable(data)
            },
            error: function (jqXHR, status) {
                showGlobsError(jqXHR.responseText)
            }
        })
    };

    function removePredefined(uuid, url, type) {

        removePredefinedFromTable(url, type)

        $.ajax({
            url: restUrl + "/predefine/" + uuid,
            type: 'DELETE',
            data: "",
            contentType: "application/json",
            dataType: "json",
            success: function (data, status, jqXHR) {
                refreshPredefinedRepositoriesTable()
            },
            error: function (jqXHR, status) {
                showError(jqXHR.responseText)
            }
        })

    }

    function removeGlob(uuid, glob, name) {

        removeGlobFromTable(glob, name)

        $.ajax({
            url: restUrl + "/glob/" + uuid,
            type: 'DELETE',
            data: "",
            contentType: "application/json",
            dataType: "json",
            success: function (data, status, jqXHR) {
                refreshPredefinedGlobsTable()
            },
            error: function (jqXHR, status) {
                showGlobsError(jqXHR.responseText)
            }
        })

    }

    function editPredefined(uuid, url, type) {
        editing = true
        currentEditingUuid = uuid
        AJS.dialog2(getCreateOrEditPredefinedRepositoryDialogInstance()).show();
        setDialog(url, type)
        clearErrorsAtCreateOrEditPredefinedRepositoryDialog()
        collectDataFromCreateOrEditPredefinedRepositoryDialog("start")
    }


    var removePredefinedFromTable = function (url, type) {
        table = $("#predefined_repo_table_body")
        table.children().each(function () {
            if (this.cells[0].innerText == url && this.cells[1].innerText == type) {
                this.remove()
                return false
            }
        })
    }

    var removeGlobFromTable = function (glob, name) {
        table = $("#globs_repo_table_body")
        table.children().each(function () {
            if (this.cells[0].innerText == name && this.cells[1].innerText == glob) {
                this.remove()
                return false
            }
        })
    }

    var refreshPredefinedRepositoriesTable = function () {
        removeAllPredefinedFromTable()
        getPredefinedRepositoriesList()
    }

    var refreshPredefinedGlobsTable = function () {
        removeAllGlobsFromTable()
        getGlobsList()
    }

    var removeAllPredefinedFromTable = function () {
        table = $("#predefined_repo_table_body")
        table.children().remove()
    }

    var removeAllGlobsFromTable = function () {
        table = $("#globs_repo_table_body")
        table.children().remove()
    }

    var setPredefinedRepositoriesTable = function (predefinedList) {
        var list = predefinedList
        if (list.length == 0)
            $("#predefined_repo_table").hide()
        else
            $("#predefined_repo_table").show()
        list.forEach((p) => {
            getPredefinedRow(p.sourceRepositoryUrl, p.authType, p.uuid)
        })

    };

    var setGlobsTable = function (globsList) {
        var list = globsList.globs
        if (list.length == 0)
            $("#globs_repo_table").hide()
        else
            $("#globs_repo_table").show()
        list.forEach((p) => {
            getGlobsRow(p.name, p.glob, p.uuid)
        })

    };

    var getPredefinedRow = function (url, type, uuid) {
        switch (type) {
            case "NOAUTH":
                type = "HTTP: NO AUTHORIZATION"
                break
            case "USERNAMEANDPASSWORD":
                type = "HTTP: USERNAME AND PASSWORD"
                break
            case "SSHKEY":
                type = "SSH: SSH KEY"
                break
            default:
                type = "UNDEFINED"

        }
        var rowTemplate =
            `
           
             <td>` + url + `</td>
                <td>` + type + `</td>
                <td>
                    <ul class="menu">
                        <li>
                            <a id="repository-list_edit_button" style="color: grey; background-color: white" class="aui-icon aui-icon-small aui-iconfont-edit"></a>

                            <!--<button id="repository-list_edit_button" style="color: #6495ed; background-color: white" class="aui-icon aui-icon-small aui-iconfont-configure"></button>-->
                        </li>
                        <li>
                            <a id="repository-list_remove_button" style="color: grey; background-color: white" class="aui-icon aui-icon-small aui-iconfont-delete" ></a>
                            <!--<button id="repository-list_remove_button" style="color: red; background-color: white" class="aui-icon aui-icon-small aui-iconfont-remove"></button>-->
                        </li>
                    </ul>
                </td>
            
            `

        var result = document.createElement("tr")
        result.innerHTML = rowTemplate


        result.getElementsByTagName("a")[0].addEventListener("click", () => {
            editPredefined(uuid, url, type)
        }, false)
        result.getElementsByTagName("a")[1].addEventListener("click", () => {
            AJS.dialog2(getRemoveRepositoryPopUpInstance(uuid, url, type)).show()
        }, false)

        AJS.$('#repository-list_edit_button').tooltip({
            title: function () {
                return "Click here to edit repository.";
            }
        });
        AJS.$('#repository-list_remove_button').tooltip({
            title: function () {
                return "Click here to remove repository. Associated macros will not be available anymore.";
            }
        });

        $("#predefined_repo_table_body").append(result)

    };


    var getGlobsRow = function (name, glob, uuid) {
        var rowTemplate =
            `
           
             <td>` + name + `</td>
                <td>` + glob + `</td>
                <td>
                    <ul class="menu">                       
                        <li>
                            <a id="glob-list_remove_button" style="color: grey; background-color: white" class="aui-icon aui-icon-small aui-iconfont-delete" ></a>
                        </li>
                    </ul>
                </td>
            
            `

        var result = document.createElement("tr")
        result.innerHTML = rowTemplate

        result.getElementsByTagName("a")[0].addEventListener("click", () => {
            AJS.dialog2(getRemoveGlobPopUpInstance(uuid, glob, name)).show()
        }, false)



        AJS.$('#glob-list_remove_button').tooltip({
            title: function () {
                return "Click here to remove glob";
            }
        });

        $("#globs_repo_table_body").append(result)

    };


    var getCreateOrEditPredefinedRepositoryDialogInstance = function () {
        if (createPredefinedRepositoryDialogInstance === null) {
            // region var html = ...;
            var html =
                `
                    <section role="dialog" id="doc_macroDialog" class="aui-layer aui-dialog2 aui-dialog2-medium" aria-hidden="true">
                       <header class="aui-dialog2-header">
                           <h2 class="aui-dialog2-header-main">Git4C macro parameters</h2>
                           <a class="aui-dialog2-header-close">
                               <span class="aui-icon aui-icon-small aui-iconfont-close-dialog">Close</span>
                           </a>
                       </header> 
                       <div class="aui-dialog2-content" style="padding: 0">
                       `
                +
                errors.map(e => {
                    return `
                                <div class="aui-message aui-message-error" id="doc_macro_` + e.id + `">
                                    <p class="title">
                                        <strong>Error!</strong>
                                    </p>
                                ` + e.text + `
                                </div>
                                `
                }).join("")
                +
                `
                            <div id="doc_macro-login-form">
                            <div id="action-messages"></div>
                            <form class="aui">
                              <div class="field-group">
                                <label for="doc_macro-repo_url">Repository url</label>
                                 <input class="text" type="text" id="doc_macro-repo_url" placeholder="git clone URL">
                                 <div class="description">Please type your repository\s git fle url</div>
                              </div>
                              <div class="field-group">
                                  <label for="doc_macro-auth_type">Connection and Authorization type</label>
                                  <select class="select" id="doc_macro-auth_type">
                                      <option id="doc_macro-auth_type-usernameandpasswordOption" value="USERNAMEPASSWORD">Http: Username + Password</option>
                                      <option id="doc_macro-auth_type-noAuthOption" value="NOAUTH">Http: No Authorization</option>
                                      <option id="doc_macro-auth_type-sshOption" value="SSHKEY">SSH: Private Key</option>                                
                                  </select>
                              </div>
                              <div class="field-group" id="div_doc_macro-login_email">
                                <label for="doc_macro-login_email">Username</label>
                                 <input class="text" type="text" id="doc_macro-login_email" placeholder="username">
                                 <div class="description">Please type your username to given git</div>
                              </div>
                              <div class="field-group" id="div_doc_macro-login_password">
                                <label for="doc_macro-login_password">Password</label>
                                 <input class="password" type="password" id="doc_macro-login_password" placeholder="password">
                                 <div class="description">Please type your password to given git</div>
                              </div>
                              <div class="field-group" id="div_doc_macro-ssh_key">
                                <label for="doc_macro-ssh_key">SSH key</label>
                                 <textarea class="text" style="height: auto" rows="10" id="doc_macro-ssh_key" placeholder="key" />
                                 <div class="description">Please paste your SSH key</div>
                               </div>
                           </form>
                          </div>
                       <footer class="aui-dialog2-footer">
                           <div class="aui-dialog2-footer-actions">
                               <button id="doc_macroDialogSaveButton" class="aui-button aui-button-primary" disabled="true">Save</button>
                               <button id="doc_macroDialogCloseButton" class="aui-button aui-button-link">Close</button>
                           </div>
                       </footer>
                    </section>
                    `;
            // endregion

            createPredefinedRepositoryDialogInstance = $(html).appendTo("body");

            urlField().keyup(() => collectDataFromCreateOrEditPredefinedRepositoryDialog("url_field"))
            usernameField().keyup(() => collectDataFromCreateOrEditPredefinedRepositoryDialog("username_field"))
            passwordField().keyup(() => collectDataFromCreateOrEditPredefinedRepositoryDialog("password_field"))
            authSelect().change(() => collectDataFromCreateOrEditPredefinedRepositoryDialog("auth_field"))
            sshField().keyup(() => collectDataFromCreateOrEditPredefinedRepositoryDialog("ssh_field"))

            createPredefinedRepositoryDialogInstance.find("#doc_macroDialogCloseButton").click(function () {
                //@ts-ignore
                AJS.dialog2("#doc_macroDialog").hide();
            });

            createPredefinedRepositoryDialogInstance.find("#doc_macroDialogSaveButton").click(function () {
                savePredefinedRepository();
            });
        }
        return createPredefinedRepositoryDialogInstance;
    };


    var getCreatePredefinedGlobDialogInstance = function () {
        if (createPredefinedGlobDialogInstance === null) {
            // region var html = ...;
            var html =
                `
                    <section role="dialog" id="doc_macroGlobsDialog" class="aui-layer aui-dialog2 aui-dialog2-medium" aria-hidden="true">
                       <header class="aui-dialog2-header">
                           <h2 class="aui-dialog2-header-main">Predefine Glob</h2>
                           <a class="aui-dialog2-header-close">
                               <span class="aui-icon aui-icon-small aui-iconfont-close-dialog">Close</span>
                           </a>
                       </header> 
                       <div class="aui-dialog2-content" style="padding: 0">
                       `
                +
                errors.map(e => {
                    return `
                                <div class="aui-message aui-message-error" id="doc_macro_globs` + e.id + `">
                                    <p class="title">
                                        <strong>Error!</strong>
                                    </p>
                                ` + e.text + `
                                </div>
                                `
                }).join("")
                +
                `
                           <div id="doc_macro-create-predefined-glob-form">
                            <div id="action-messages"></div>
                            <form class="aui">
                              <div class="field-group">
                                <label for="doc_macro-glob_name">Glob name</label>
                                 <input class="text" type="text" id="doc_macro-glob_name" placeholder="Enter glob name">
                                 <div class="description">Please type glob name</div>
                              </div>                             
                              <div class="field-group" id="div_doc_macro-glob_pattern">
                                <label for="doc_macro-glob_pattern">Glob pattern</label>
                                 <input class="text" type="text" id="doc_macro-glob_pattern" placeholder="Enter glob pattern">
                                 <div class="description">Please type glob pattern (ex. **.java)</div>
                              </div>                                                      
                           </form>
                          </div>
                       <footer class="aui-dialog2-footer">
                           <div class="aui-dialog2-footer-actions">
                               <button id="doc_macroGlobsDialogSaveButton" class="aui-button aui-button-primary" disabled="true">Save</button>
                               <button id="doc_macroGlobsDialogCloseButton" class="aui-button aui-button-link">Close</button>
                           </div>
                       </footer>
                    </section>
                    `;
            // endregion

            createPredefinedGlobDialogInstance = $(html).appendTo("body");

            globNameField().keyup(() => collectDataFromCreatePredefinedGlobDialog("glob_name"))
            globPatternField().keyup(() => collectDataFromCreatePredefinedGlobDialog("glob_pattern"))

            createPredefinedGlobDialogInstance.find("#doc_macroGlobsDialogCloseButton").click(function () {
                //@ts-ignore
                AJS.dialog2("#doc_macroGlobsDialog").hide();
            });

            createPredefinedGlobDialogInstance.find("#doc_macroGlobsDialogSaveButton").click(function () {
                savePredefinedGlob();
            });
        }
        return createPredefinedGlobDialogInstance;
    };


    var getCleanAllDataPopUpInstance = function () {
        if (cleanDataPopUpInstance === null) {
            var html =
                `                
                   <section role="dialog" id="demo-dialog" class="aui-layer aui-dialog2 aui-dialog2-warning aui-dialog2-small" aria-hidden="true">
                   <!-- Dialog header -->
                   <header class="aui-dialog2-header">
                       <!-- The dialog's title -->
                       <h2 class="aui-dialog2-header-main">WARNING</h2>
                       <!-- Close icon -->
                   </header>
                   <!-- Main dialog content -->
                   <div class="aui-dialog2-content">
                       <p style="text-align: center">You are about to permamently remove all data.</p>
                       <p style="text-align: center"><b>All Git Viewer for Confluence macros will stop working.</b></p>
                   </div>
                   <!-- Dialog footer -->
                   <footer class="aui-dialog2-footer">
                       <!-- Actions to render on the right of the footer -->
                       <div class="aui-dialog2-footer-actions">
                           <button id="popup-ok-button"id="popup-ok-button"id="popup-ok-button" class="aui-button aui-button-primary">Clean</button>
                           <button id="popup-cancel-button"id="popup-cancel-button"id="popup-cancel-button" class="aui-button aui-button-link">Cancel</button>
                       </div>
                       <!-- Hint text is rendered on the left of the footer -->
                   </footer>
                   </section>`;

            cleanDataPopUpInstance = $(html).appendTo("body");

            cleanDataPopUpInstance.find("#popup-ok-button").click(function () {
                $.ajax({
                    url: restUrl,
                    type: 'DELETE',
                }).done(function () {
                    AJSC.flag({
                        type: "info",
                        title: "Data removal completed successfully"
                    })
                    refreshPredefinedRepositoriesTable()
                    AJS.dialog2(cleanDataPopUpInstance).hide()
                })
            })


            cleanDataPopUpInstance.find("#popup-cancel-button").click(function () {
                AJS.dialog2(cleanDataPopUpInstance).hide()
            })
        }
        return cleanDataPopUpInstance
    }

    var getRemoveRepositoryPopUpInstance = function (uuid, url, type) {
        if (removeRepositoryPopUpInstance === null) {
            var html =
                `                
                   <section role="dialog" id="demo-dialog" class="aui-layer aui-dialog2 aui-dialog2-warning aui-dialog2-small" aria-hidden="true">
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
                           <button id="popup-ok-button" class="aui-button aui-button-primary">Remove</button>
                           <button id="popup-cancel-button" class="aui-button aui-button-link">Cancel</button>
                       </div>
                       <!-- Hint text is rendered on the left of the footer -->
                   </footer>
                   </section>`;

            removeRepositoryPopUpInstance = $(html).appendTo("body");

            removeRepositoryPopUpInstance.find("#popup-ok-button").click(function () {
                removePredefined(uuid, url, type)
                AJS.dialog2(removeRepositoryPopUpInstance).hide()
            })


            removeRepositoryPopUpInstance.find("#popup-cancel-button").click(function () {
                AJS.dialog2(removeRepositoryPopUpInstance).hide()
            })
        }
        return removeRepositoryPopUpInstance
    }


    var getRemoveGlobPopUpInstance = function (uuid, glob, name) {
        if (removeGlobPopUpInstance === null) {
            var html =
                `                
                   <section role="dialog" id="demo-dialog" class="aui-layer aui-dialog2 aui-dialog2-warning aui-dialog2-small" aria-hidden="true">
                   <!-- Dialog header -->
                   <header class="aui-dialog2-header">
                       <!-- The dialog's title -->
                       <h2 class="aui-dialog2-header-main">WARNING</h2>
                       <!-- Close icon -->
                   </header>
                   <!-- Main dialog content -->
                   <div class="aui-dialog2-content">
                       <p style="font-size: 13px">You are about to remove a predefined glob.</p>
                       <p style="text-align: center"><b>Glob will not be available anymore in a list of globs shown to user as a template</b>.</p>
                   </div>
                   <!-- Dialog footer -->
                   <footer class="aui-dialog2-footer">
                       <!-- Actions to render on the right of the footer -->
                       <div class="aui-dialog2-footer-actions">
                           <button id="popup-glob-ok-button" class="aui-button aui-button-primary">Remove</button>
                           <button id="popup-glob-cancel-button" class="aui-button aui-button-link">Cancel</button>
                       </div>
                       <!-- Hint text is rendered on the left of the footer -->
                   </footer>
                   </section>`;

            removeGlobPopUpInstance = $(html).appendTo("body");

            removeGlobPopUpInstance.find("#popup-glob-ok-button").click(function () {
                removeGlob(uuid, glob, name)
                AJS.dialog2(removeGlobPopUpInstance).hide()
            })


            removeGlobPopUpInstance.find("#popup-glob-cancel-button").click(function () {
                AJS.dialog2(removeGlobPopUpInstance).hide()
            })
        }
        return removeGlobPopUpInstance
    }


    var savePredefinedRepository = function () {


        var loginForm = $("#doc_macro-login-form");

        saveButton().prop("disabled", true)
        saveButton().text("Saving...")

        clearErrorsAtCreateOrEditPredefinedRepositoryDialog();
        var password = loginForm.find("#doc_macro-login_password").val();
        var sshkey = loginForm.find("#doc_macro-ssh_key").val();

        /** @type {Credentials} */
        var credentials = null

        switch (authSelect().val()) {
            case "USERNAMEPASSWORD":
                credentials = new UsernamePasswordCredentials(usernameField().val(), password)
                break;
            case "SSHKEY":
                credentials = new SSHKeyCredentials(sshkey)
                break;
            case "NOAUTH":
                credentials = new NoAuthCredentials()
                break;
            default:
                console.log("Unknown authtype: " + authType().val())
        }
        var url = restUrl + "/predefine"

        if (editing)
            url += ("/" + currentEditingUuid)


        $.ajax({
            type: "POST",
            url: url,
            data: JSON.stringify({
                "sourceRepositoryUrl": urlField().val(),
                credentials
            }),
            contentType: "application/json",
            dataType: "json",
            success: function (data, status, jqXHR) {
                saveButton().text("Save")
                clearDialog()
                editing = false
                saveButton().removeAttr("disabled")
                refreshPredefinedRepositoriesTable()
                AJS.dialog2(getCreateOrEditPredefinedRepositoryDialogInstance()).hide();
            },
            error: function (jqXHR, status) {
                saveButton().text("Save")
                saveButton().removeAttr("disabled")
                showError(jqXHR.responseText)
            }
        });
    };

    var savePredefinedGlob = function () {


        var globForm = $("#doc_macro-create-predefined-glob-form");

        saveGlobsButton().prop("disabled", true)
        saveGlobsButton().text("Saving...")

        clearErrorsAtCreatePredefinedGlobDialog();
        var name = globForm.find("#doc_macro-glob_name").val();
        var glob = globForm.find("#doc_macro-glob_pattern").val();

        var url = restUrl + "/glob"

        $.ajax({
            type: "POST",
            url: url,
            data: JSON.stringify({
                "name": name,
                "glob": glob
            }),
            contentType: "application/json",
            dataType: "json",
            success: function (data, status, jqXHR) {
                saveGlobsButton().text("Save")
                clearGlobDialog()
                saveGlobsButton().removeAttr("disabled")
                refreshPredefinedGlobsTable()
                AJS.dialog2(getCreatePredefinedGlobDialogInstance()).hide();
            },
            error: function (jqXHR, status) {
                saveGlobsButton().text("Save")
                saveGlobsButton().removeAttr("disabled")
                showGlobsError(jqXHR.responseText)
            }
        });
    };


    const callback = function (changedId, formState) {

        saveButton().prop("disabled", true)


        if (changedId == "url_field" || changedId == "start") {
            if (formState.url.startsWith("http")) {
                sshOption().prop("disabled", true)
                noAuthOption().removeAttr("disabled")
                usernameandpasswordOption().removeAttr("disabled")
                formState.connectionType = "USERNAMEPASSWORD"
                callback("auth_field", formState)
            }
            else if (formState.url.startsWith("ssh") || formState.url.startsWith("git@gitlab")) {
                sshOption().removeAttr("disabled")
                usernameandpasswordOption().prop("disabled", true)
                noAuthOption().prop("disabled", true)
                formState.connectionType = "SSHKEY"
                callback("auth_field", formState)
            }
            else {
                noAuthOption().removeAttr("disabled")
                sshOption().removeAttr("disabled")
                usernameandpasswordOption().removeAttr("disabled")
            }
        }


        if (changedId == "auth_field" || changedId == "start") {
            switch (formState.connectionType) {
                case "USERNAMEPASSWORD":
                    showAll(loginPasswordDivs())
                    hideAll(sshDivs())
                    authSelect().val(usernameandpasswordOption().val())
                    break
                case "SSHKEY":
                    hideAll(loginPasswordDivs())
                    showAll(sshDivs())
                    authSelect().val(sshOption().val())
                    break
                case "NOAUTH":
                    hideAll(sshDivs())
                    hideAll(loginPasswordDivs())
                    authSelect().val(noAuthOption().val())
                    break
            }
        }

        if (allFilled(formState))
            saveButton().removeAttr("disabled")

    }

    const callbackGlob = function (changedId, formState) {

        saveGlobsButton().prop("disabled", true)


        if (allFilledForGlob(formState))
            saveGlobsButton().removeAttr("disabled")

    }


    const clearDialog = function () {
        urlField().val("")
        authSelect().val(usernameandpasswordOption().val())
        usernameField().val("")
        passwordField().val("")
        sshField().val("")
        clearErrorsAtCreateOrEditPredefinedRepositoryDialog()
    }

    const clearGlobDialog = function () {
        globNameField().val("")
        globPatternField().val("")
        clearErrorsAtCreatePredefinedGlobDialog()
    }

    const setDialog = function (url, type) {
        urlField().val(url)
        switch (type) {
            case "HTTP: NO AUTHORIZATION":
                authSelect().val(noAuthOption().val())
                collectDataFromCreateOrEditPredefinedRepositoryDialog("auth_field")
                break
            case "HTTP: USERNAME AND PASSWORD":
                authSelect().val(usernameandpasswordOption().val())
                collectDataFromCreateOrEditPredefinedRepositoryDialog("auth_field")
                break
            case "SSH: SSH KEY":
                authSelect().val(sshOption().val())
                collectDataFromCreateOrEditPredefinedRepositoryDialog("auth_field")
                break
            default:
                authSelect().val(usernameandpasswordOption().val())
                collectDataFromCreateOrEditPredefinedRepositoryDialog("auth_field")
        }


    }

    const collectDataFromCreateOrEditPredefinedRepositoryDialog = function (changeId) {


        const url = urlField().val()
        const username = usernameField().val()
        const password = passwordField().val()
        const auth = authSelect().val()
        const ssh = sshField().val()

        const formState = new FormState(url, auth, username, password, ssh)


        callback(changeId, formState)

    }

    const collectDataFromCreatePredefinedGlobDialog = function (changeId) {


        const name = globNameField().val()
        const pattern = globPatternField().val()

        const formState = new GlobFormState(name, pattern)


        callbackGlob(changeId, formState)

    }


    function clearErrorsAtCreateOrEditPredefinedRepositoryDialog() {
        errors.map(e => $("#doc_macro_" + e.id)).forEach(e => e.fadeOut(0))
    }

    function clearErrorsAtCreatePredefinedGlobDialog() {
        errors.map(e => $("#doc_macro_globs" + e.id)).forEach(e => e.fadeOut(0))
    }

    function showError(error) {

        clearErrorsAtCreateOrEditPredefinedRepositoryDialog()

        const errorDiv = errors.find(e => e.serverError.indexOf(error) > -1)

        if (errorDiv) {
            $("#doc_macro_" + errorDiv.id).fadeIn(0)
        } else {
            $("#doc_macro_" + serverError.id).fadeIn(0)
        }

    }

    const allFilled = function (formState) {
        var filled = true;
        var arr = [];
        switch (formState.connectionType) {
            case "USERNAMEPASSWORD":
                arr = loginPasswordInputs()
                break
            case "SSHKEY":
                arr = sshInputs()
                break
            case "NOAUTH":
                arr = noAuthInputs()
                break
        }

        arr.forEach(function (e) {
            if (e.val() == '')
                filled = false;
        });
        return filled;
    }

    const allFilledForGlob = function (formState) {
        var filled = true;
        var arr = globsInputs();

        arr.forEach(function (e) {
            if (e.val() == '')
                filled = false;
        });
        return filled;
    }

    const showAll = function (arr) {
        arr.forEach(e => e.show())
    }
    const hideAll = function (arr) {
        arr.forEach(e => e.hide())
    }


    const serverError = new Error("server_error", [], "<p>Server error</p>")

    const errors = [
        new Error("server_error", [], "<p>Server error</p>"),
        new Error("invalid_url", ["SOURCE_NOT_FOUND", "WRONG_URL"], "<p>Invalid url</p>"),
        new Error("wrong_credentials", ["WRONG_CREDENTIALS"], "<p>Invalid repositoryDetails</p>"),
        new Error("wrong_key_format", ["WRONG_KEY_FORMAT"], `<p>SSH key should have following format:</p>
        <pre>-----BEGIN RSA PRIVATE KEY-----
         ....
        -----END RSA PRIVATE KEY-----</pre>`),
        new Error("captcha_required", ["CAPTCHA_REQUIRED"], `<p>Your Git repository account has been locked. To unlock it and log in again you must solve a CAPTCHA. This is typically caused by too many attempts to login with an incorrect password. The account lock prevents your SCM client from accessing repository and its mirrors until it is solved, even if you enter your password correctly</p>
        <p>If you are currently logged in to Git repository via a browser you may need to logout and then log back in in order to solve the CAPTCHA.</p>`),
    ]


})(AJS.$ || jQuery);
