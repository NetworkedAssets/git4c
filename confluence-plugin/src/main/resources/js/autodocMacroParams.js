// @ts-check
(function ($) {

    class FormState {

        /**
         * 
         * @param {string} url 
         * @param {string} branch 
         * @param {"USERNAMEPASSWORD" | "SSHKEY" | "NOAUTH"} connectionType 
         * @param {string} username 
         * @param {string} password 
         * @param {string} sshkey 
         * @param {string} glob
         * @param (string) defaultDocItem
         */
        constructor(url, branch, connectionType, username, password, sshkey, glob, defaultDocItem) {
            this.url = url
            this.branch = branch
            this.connectionType = connectionType
            this.username = username
            this.password = password
            this.sshkey = sshkey
            this.glob = glob
            this.defaultDocItem = defaultDocItem
        }

    }

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

    const serverError = new Error("server_error", [], "<p>Server error</p>")

    const errors = [
        serverError,
        new Error("invalid_url", ["SOURCE_NOT_FOUND", "WRONG_URL"], "<p>Invalid url</p>"),
        new Error("invalid_branch", ["WRONG_BRANCH"], "<p>Invalid branch name</p>"),
        new Error("wrong_credentials", ["WRONG_CREDENTIALS"], "<p>Invalid credentials</p>"),
        new Error("wrong_key_format", ["WRONG_KEY_FORMAT"], `<p>SSH key should have following format:</p>
<pre>-----BEGIN RSA PRIVATE KEY-----
....
-----END RSA PRIVATE KEY-----</pre>`),
        new Error("captcha_required", ["CAPTCHA_REQUIRED"], `<p>Your Git repository account has been locked. To unlock it and log in again you must solve a CAPTCHA. This is typically caused by too many attempts to login with an incorrect password. The account lock prevents your SCM client from accessing repository and its mirrors until it is solved, even if you enter your password correctly</p>
                                                             <p>If you are currently logged in to Git repository via a browser you may need to logout and then log back in in order to solve the CAPTCHA.</p>`),
    ]

    var vm = {
        macro: null
    };
    var dialogInstance = null;

    var restUrl;

    AJS.toInit(function () {
        var baseUrl = AJS.params.baseUrl;

        restUrl = baseUrl + "/rest/doc/1.0/documentation";
    })



    var save = function () {
        var macroName = "Git4C";

        /** @type {Params} */
        var currentParams = $.extend({}, vm.macro.params);

        var loginForm = $("#doc_macro-login-form");

        lockSaveButton();
        clearErrors();

        currentParams.type = "MARKUP";
        currentParams.url = loginForm.find("#doc_macro-repo_url").val();
        currentParams.branch = loginForm.find("#doc_macro-repo_branch").val();
        currentParams.username = loginForm.find("#doc_macro-login_email").val();
        currentParams.glob = loginForm.find("#doc_macro-glob").val();
        currentParams.authType = loginForm.find("#doc_macro-auth_type").val();
        currentParams.defaultDocItem = loginForm.find("#doc_macro-default_Doc_Item").val();
        var password = loginForm.find("#doc_macro-login_password").val();
        var sshkey = loginForm.find("#doc_macro-ssh_key").val();

        /** @type {Credentials} */
        var credentials = null

        switch (currentParams.authType) {
            case "USERNAMEPASSWORD":
                credentials = new UsernamePasswordCredentials(currentParams.username, password)
                break;
            case "SSHKEY":
                credentials = new SSHKeyCredentials(sshkey)
                break;
            case "NOAUTH":
                credentials = new NoAuthCredentials()
                break;
            default:
                console.log("Unknown authtype: " + vm.macro.params.authType)
        }

        $.ajax({
            type: "POST",
            url: restUrl,
            data: JSON.stringify({
                "sourceRepositoryUrl": currentParams.url,
                "branch": currentParams.branch,
                "glob": currentParams.glob,
                "defaultDocItem": currentParams.defaultDocItem,
                credentials
            }),
            contentType: "application/json",
            dataType: "json",
            success: function (data, status, jqXHR) {
                unlockSaveButton();
                currentParams.uuid = data.uuid;
                //@ts-ignore
                tinymce.confluence.macrobrowser.macroBrowserComplete({
                    "name": macroName,
                    "bodyHtml": undefined,
                    "params": currentParams
                });
                //@ts-ignore
                AJS.dialog2(getDialogInstance()).hide();
            },
            error: function (jqXHR, status) {
                unlockSaveButton();
                showError(jqXHR.responseText)
            }
        });

        function unlockSaveButton() {
            var saveButton = $('#doc_macroDialogSaveButton');
            saveButton.removeAttr('disabled');
            saveButton.text("Save");
        }

        function lockSaveButton() {
            var saveButton = $('#doc_macroDialogSaveButton');
            saveButton.prop("disabled", true);
            saveButton.text("Saving");
        }
    };


    var getDialogInstance = function () {
        if (dialogInstance === null) {
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
                   <div class="aui-dialog2-content">
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
                            <label for="doc_macro-repo_branch">SSH key</label>
                             <textarea class="text" style="height: auto" rows="10" id="doc_macro-ssh_key" placeholder="key" />
                             <div class="description">Please paste your SSH key</div>
                           </div>
                          <div class="field-group">
                            <label for="doc_macro-repo_branch">Repository branch</label>
                             <select class="select" id="doc_macro-repo_branch">
                             </select>
                          </div>
                          <div class="field-group">
                            <label for="doc_macro-repo_glob">GLOB</label>
                             <input class="text" type="text" id="doc_macro-glob" placeholder="glob">
                             <div class="description">Please type glob pattern (Optional)</div>
                          </div>
                          <div class="field-group">
                            <label for="doc_macro-default_Doc_Item">Default File</label>
                             <input class="text" type="text" id="doc_macro-default_Doc_Item" placeholder="Readme.md">
                             <div class="description">Please type name of default Document File (Optional)</div>
                          </div>
                         </form>
                       </div>
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

            dialogInstance = $(html).appendTo("body");

            dialogInstance.find("#doc_macroDialogCloseButton").click(function () {
                //@ts-ignore
                AJS.dialog2("#doc_macroDialog").hide();
            });

            dialogInstance.find("#doc_macroDialogSaveButton").click(function () {
                save();
            });
        }
        return dialogInstance;
    };

    const urlFieldId = "doc_macro-repo_url"
    const branchSelectId = "doc_macro-repo_branch"
    const usernameFieldId = "doc_macro-login_email"
    const passwordFieldId = "doc_macro-login_password"
    const authSelectId = "doc_macro-auth_type"
    const sshFieldId = "doc_macro-ssh_key"
    const globFieldId = "doc_macro-glob"
    const defaultDocItemFieldId = "doc_macro-default_Doc_Item"

    const loginForm = () => { return $("#doc_macro-login-form") }

    const urlField = () => { return $("#" + urlFieldId) }

    const branchSelect = () => { return $("#" + branchSelectId) }

    const usernameField = () => { return $("#" + usernameFieldId) }
    const usernameDiv = () => { return $("#div_" + usernameFieldId) }

    const passwordField = () => { return $("#" + passwordFieldId) }
    const passwordDiv = () => { return $("#div_" + passwordFieldId) }

    const authSelect = () => { return $("#" + authSelectId) }

    const sshField = () => { return $("#" + sshFieldId) }
    const sshDiv = () => { return $("#div_" + sshFieldId) }

    const globField = () => { return $("#" + globFieldId) }
    const defaultDocItemField = () => { return $("#" + defaultDocItemFieldId) }

    const loginPasswordDivs = () => { return [usernameDiv(), passwordDiv()] }
    const sshDivs = () => { return [sshDiv()] }

    const loginPasswordInputs = () => { return [usernameField(), passwordField(), urlField()] }
    const sshInputs = () => { return [sshField(), urlField()] }
    const noAuthInputs = () => { return [urlField()] }

    const usernameandpasswordOption = () => { return $("#doc_macro-auth_type-usernameandpasswordOption") }
    const sshOption = () => { return $("#doc_macro-auth_type-sshOption") }
    const noAuthOption = () => { return $("#doc_macro-auth_type-noAuthOption") }

    const saveButton = () => { return $('#doc_macroDialogSaveButton') }



    /**
     * 
     * @param {string} changedId
     * @param {FormState} formState 
     */
    const callback = function (changedId, formState) {


        if (changedId == "auth_field" ||
            changedId == "username_field" ||
            changedId == "password_field" ||
            changedId == "url_field" ||
            changedId == "ssh_field" ||
            changedId == "start"
        ) {
            saveButton().prop("disabled", true)
        }

        if (changedId == "url_field" || changedId == "start") {
            if (formState.url.startsWith("http")) {
                sshOption().prop("disabled", true)
                noAuthOption().removeAttr("disabled")
                usernameandpasswordOption().removeAttr("disabled")
                formState.connectionType = "USERNAMEPASSWORD"
                callback("auth_field", formState)
            }
            else if (formState.url.startsWith("ssh")) {
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

        if (allFilled(formState)) {
            if (changedId != "branch_select" && changedId != "defaultDocItem_field" && changedId != "glob_field")
                timeoutId = setTimeout(() => downloadBranches(), 2000)
        }
    }


    const downloadBranches = function () {
        clearErrors()
        branchSelect().prop("disabled", true)
        var item = document.createElement('option')
        item.innerHTML = "Getting branch list..."
        branchSelect().children().remove()
        branchSelect().append(item)
        getBranches()
    }

    /**
     * 
     * @param {FormState} formState 
     */
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


    let timeoutId = null

    /**
     * 
     * @param {string} changeId 
     */
    const collectData = function (changeId) {

        if (timeoutId) {
            clearTimeout(timeoutId)
        }
        const url = urlField().val()
        const branch = branchSelect().val()
        const username = usernameField().val()
        const password = passwordField().val()
        /** @type {"USERNAMEPASSWORD" | "SSHKEY" | "NOAUTH"} */
        const auth = authSelect().val()
        const ssh = sshField().val()
        const glob = globField().val()
        const defaultDocItem = defaultDocItemField().val()

        const formState = new FormState(url, branch, auth, username, password, ssh, glob, defaultDocItem)


        callback(changeId, formState)

    }

    /**
     * 
     * @param {array} arr 
     */
    const hideAll = function (arr) {
        arr.forEach(e => e.hide())
    }

    /**
     * 
     * @param {array} arr 
     */
    const showAll = function (arr) {
        arr.forEach(e => e.show())
    }

    const setInitialState = function () {

        /** @type {Params} */
        var currentParams = $.extend({}, vm.macro.params);

        urlField().val(currentParams.url)
        usernameField().val(currentParams.username)
        authSelect().val(currentParams.authType)
        globField().val(currentParams.glob)
        defaultDocItemField().val(currentParams.defaultDocItem)
    }

    var load = function () {

        urlField().keyup(() => collectData("url_field"))
        branchSelect().change(() => collectData("branch_select"))
        usernameField().keyup(() => collectData("username_field"))
        passwordField().keyup(() => collectData("password_field"))
        authSelect().change(() => collectData("auth_field"))
        sshField().keyup(() => collectData("ssh_field"))
        globField().keyup(() => collectData("glob_field"))
        defaultDocItemField().keyup(() => collectData("defaultDocItem_field"))
        clearErrors()
        setInitialState()
        collectData("start")
    };

    var initFields = function (loginForm) {
        if (vm.macro.params) {
            loginForm.find("#doc_macro-repo_url").val(vm.macro.params.url);
            loginForm.find("#doc_macro-repo_branch").val(vm.macro.params.branch);
            loginForm.find("#doc_macro-login-email").val(vm.macro.params.username);
            const sel = loginForm.find("#doc_macro_auth_type")
            if (vm.macro.params.authType) {
                sel[0].value = vm.macro.params.authType
                $(sel[0]).trigger("change")
            }
            loginForm.find("#doc_macro-glob").val(vm.macro.params.glob)
        }
    };

    var getBranches = function () {
        var loginForm = $("#doc_macro-login-form");
        var branches = $("#doc_macro-repo_branch");
        var username = loginForm.find("#doc_macro-login_email").val();
        var password = loginForm.find("#doc_macro-login_password").val();
        var sshkey = loginForm.find("#doc_macro-ssh_key").val();
        var url = loginForm.find("#doc_macro-repo_url").val();

        /** @type {Credentials} */
        var credentials = null
        var saveButton = $("#doc_macroDialogSaveButton");



        switch (loginForm.find("#doc_macro-auth_type").val()) {
            case "USERNAMEPASSWORD":
                credentials = new UsernamePasswordCredentials(username, password)
                break;
            case "SSHKEY":
                credentials = new SSHKeyCredentials(sshkey)
                break;
            case "NOAUTH":
                credentials = new NoAuthCredentials()
                break;
            default:
                console.log("Unknown authtype: " + vm.macro.params.authType)
        }

        $.ajax({
            type: "POST",
            url: restUrl + "/getBranches",
            data: JSON.stringify({
                "sourceRepositoryUrl": url,
                credentials
            }),
            contentType: "application/json",
            dataType: "json",
            success: function (data, status, jqXHR) {
                saveButton.removeAttr("disabled")
                branches.children().remove()
                branchSelect().removeAttr("disabled")
                var refs = data.allBranches
                for (var i = 0; i < refs.length; i++) {
                    var item = document.createElement('option')
                    item.value = refs[i]
                    item.innerHTML = refs[i]
                    branches.append(item)
                }
            },
            error: function (jqXHR, status) {
                branches.children().remove()
                branchSelect().removeAttr("disabled")
                saveButton.prop("disabled", true)
                showError(jqXHR.responseText)
            }
        });
    }

    function clearErrors() {
        errors.map(e => $("#doc_macro_" + e.id)).forEach(e => e.fadeOut(0))
    }

    /**
     * 
     * @param {string} error 
     */
    function showError(error) {

        clearErrors()

        const errorDiv = errors.find(e => e.serverError.indexOf(error) > -1)

        if (errorDiv) {
            $("#doc_macro_" + errorDiv.id).fadeIn(0)
        } else {
            $("#doc_macro_" + serverError.id).fadeIn(0)
        }

    }

    //@ts-ignore
    AJS.MacroBrowser.setMacroJsOverride("Git4C", {
        "opener": function (macro) {
            vm.macro = macro;
            //@ts-ignore
            AJS.dialog2(getDialogInstance()).show();
            load();
        }
    });
    class Params {
        /**
         *
         * @param {string} type
         * @param {string} url
         * @param {string} branch
         * @param {string} username
         * @param {string} glob
         * @param {string} uuid
         * @param {string} authType
         * @param (string) defaultDocItem
         *
         * SHOULD NOT BE USED - ONLY FOR VSCODE COMPLETION/TYPE CHECKING
         */
        constructor(type, url, branch, username, glob, uuid, authType, defaultDocItem) {
            this.type = type
            this.url = url
            this.branch = branch
            this.username = username
            this.glob = glob
            this.uuid = uuid
            this.authType = authType
            this.defaultDocItem = defaultDocItem
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


    //@ts-ignore
})(AJS.$);