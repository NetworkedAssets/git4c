var Git4CCustomRepositoryDialog = {
    getComponent: function (Events, isEditableOptionAvailable) {

        const Error = function (id, serverError, text) {
            return {
                id: id,
                serverError: serverError,
                text: text
            }
        }

        const UsernamePasswordCredentials = function (username, password){
            return {
                username: username,
                password: password,
                type: "USERNAMEPASSWORD"
            }
        }

        const SSHKeyCredentials = function(sshKey) {
            return {
                sshKey: sshKey,
                type: "SSHKEY"
            }
        }

        const NoAuthCredentials = function() {
            return {
                type: "NOAUTH"
            }
        }

        const serverError =  Error("server_error", ["SERVER_ERROR"], "<p>Server error</p>")

        const errors = [
            serverError,
            Error("invalid_url", ["SOURCE_NOT_FOUND", "WRONG_URL"], "<p>Invalid url</p>"),
            Error("invalid_branch", ["WRONG_BRANCH"], "<p>Invalid branch name</p>"),
            Error("wrong_credentials", ["WRONG_CREDENTIALS"], "<p>Invalid credentials</p>"),
            Error("wrong_key_format", ["WRONG_KEY_FORMAT"], '<p>Could not parse the provided SSH key. It may be in wrong format. The key should look like:</p>' +
            '<p>-----BEGIN RSA PRIVATE KEY-----</p>'+
            '<p>....</p>' +
            '<p>-----END RSA PRIVATE KEY-----</p>'),
            Error("captcha_required", ["CAPTCHA_REQUIRED"],
               '<p>Your Git repository account has been locked. To unlock it and log in again you must solve a CAPTCHA. This is typically caused by too many attempts to login with an incorrect password. The account lock prevents your SCM client from accessing repository and its mirrors until it is solved, even if you enter your password correctly</p>' +
               '<p>If you are currently logged in to Git repository via a browser you may need to logout and then log back in in order to solve the CAPTCHA.</p>'),
            Error("access_denied", ["ACCESS_DENIED"], "<p>Could not find repository, or permission has been denied.</p>"),
            Error("unknown_host", ["UNKNOWN_HOST"], "<p>Could not find host.</p>")

        ]


        return {
            data: function () {
                return {
                    name: undefined,
                    url: undefined,
                    username: undefined,
                    password: undefined,
                    sshKey: undefined,
                    urlType: "EMPTY",
                    authType: "USERNAMEPASSWORD",
                    isFilled: false,
                    saving: false,
                    errors: errors,
                    currentError: undefined,
                    editable: false,
                    editableOptionAvailable: isEditableOptionAvailable
                }
            },
            watch: {
                name: function () {
                    this.checkIfFilled()
                },
                url: function () {
                    this.processUrlChange()
                },
                username: function () {
                    this.checkIfFilled()
                },
                password: function () {
                    this.checkIfFilled()
                },
                sshKey: function () {
                    this.checkIfFilled()
                },
                authType: function () {
                    this.checkIfFilled()
                }
            },
            methods: {
                closeDialog: function () {
                    this.currentError = null
                    if (Events) {
                        Events.$emit("closeCustomRepositoryDialog")
                    }
                    this.$emit("closeCustomRepositoryDialog")
                },
                checkIfFilled: function () {
                    if (!this.name) {
                        this.isFilled = false
                    } else if (!this.url) {
                        this.isFilled = false
                    } else if (this.authType == "NOAUTH") {
                        this.isFilled = true
                    } else if (this.authType == "SSHKEY") {
                        !this.sshKey ? this.isFilled = false : this.isFilled = true
                    } else if (this.authType == "USERNAMEPASSWORD") {
                        if (!this.password) {
                            this.isFilled = false
                        } else {
                            !this.username ? this.isFilled = false : this.isFilled = true
                        }
                    } else {
                        isFilled = true
                    }
                },
                processUrlChange: function () {
                    if (!this.url) {
                        this.urlType = "EMPTY"
                    } else if (this.url.startsWith("http")) {
                        this.urlType = "HTTP"
                    } else if (this.url.startsWith("ssh") || this.url.startsWith("git@gitlab")) {
                        this.urlType = "SSH"
                    } else {
                        this.urlType = "UNKNOWN"
                    }

                    switch (this.urlType) {
                        case 'HTTP': {
                            if (this.authType !== "NOAUTH")
                                this.authType = "USERNAMEPASSWORD"
                            break;
                        }
                        case 'SSH': {
                            this.authType = "SSHKEY"
                            break;
                        }
                    }
                    this.checkIfFilled()
                },
                defineRepository: function () {
                    this.saving = true;
                    switch (this.authType) {
                        case "NOAUTH": {
                            const repository = {
                                repositoryName: this.name,
                                sourceRepositoryUrl: this.url,
                                credentials: new NoAuthCredentials(),
                                editable: this.editableOptionAvailable && this.editable
                            };
                            if (Events) {
                                Events.$emit("repositoryDefined", repository)
                            }
                            this.$emit("repositoryDefined", repository);
                            break;
                        }
                        case "USERNAMEPASSWORD": {
                            const repository = {
                                repositoryName: this.name,
                                sourceRepositoryUrl: this.url,
                                credentials: new UsernamePasswordCredentials(this.username, this.password),
                                editable: this.editableOptionAvailable && this.editable
                            };
                            if (Events) {
                                Events.$emit("repositoryDefined", repository)
                            }
                            this.$emit("repositoryDefined", repository);
                            break;
                        }
                        case "SSHKEY" : {
                            const repository = {
                                repositoryName: this.name,
                                sourceRepositoryUrl: this.url,
                                credentials: new SSHKeyCredentials(this.sshKey),
                                editable: this.editableOptionAvailable && this.editable
                            };
                            if (Events) {
                                Events.$emit("repositoryDefined", repository)
                            }
                            this.$emit("repositoryDefined", repository);
                            break;
                        }
                    }

                },
                showError: function (text) {
                    const errors = _.flatMap(this.errors, function(e) {return e.serverError})
                    if (_.includes(errors, text)) {
                        this.currentError = text
                    } else {
                        this.currentError = "SERVER_ERROR"
                    }

                },
                clearFields: function () {
                    this.name = undefined
                    this.url = undefined
                    this.username = undefined
                    this.password = undefined
                    this.sshKey = undefined
                    this.urlType = "EMPTY"
                    this.authType = "USERNAMEPASSWORD"
                    this.editable = false
                    this.isFilled = false
                    this.saving = false
                    this.currentError = undefined
                },
                initFields: function (repositoryInfo) {
                    this.name = repositoryInfo.name
                    this.url = repositoryInfo.sourceRepositoryUrl
                    this.authType = repositoryInfo.authType
                    this.editable = repositoryInfo.editable
                }

            },
            template:
                    '<section role="dialog" id="custom_repository_dialog" class="aui-layer aui-dialog2 aui-dialog2-medium" aria-hidden="true">'+
                    '<header class="aui-dialog2-header">'+
                    '   <h2 class="aui-dialog2-header-main">Git4C macro parameters</h2>'+
                    '   <a class="aui-dialog2-header-close" v-on:click="closeDialog()">'+
                    '   <span class="aui-icon aui-icon-small aui-iconfont-close-dialog">Close</span>'+
                    '   </a>'+
                    '</header>'+
                    '<div class="aui-dialog2-content">'+
                    '   <div v-for="error in errors" >'+
                    '      <div v-for="serverError in error.serverError" class="aui-message aui-message-error" v-show="serverError == currentError">'+
                    '         <p class="title">'+
                    '            <strong>Error!</strong>'+
                    '         </p>'+
                    '         <p v-html="error.text">'+
                    '         </p>'+
                    '      </div>'+
                    '   </div>'+
                    '   <!--</div>-->'+
                    '   <form class="aui">'+
                    '      <div id="git4c-custom-repository-dialog-content">'+
                    '      <div class="aui-group">'+
                    '      <div class="aui-item">'+
                    '             <div class="field-group">'+
                    '                <label for="doc_macro-repo_name">Name</label>'+
                    '                <input id="doc_macro-repo_name" v-model="name" class="text" type="text" placeholder="custom">'+
                    '                <div class="description">Please type the name for your repository</div>'+
                    '             </div>'+
                    '             <div class="field-group">'+
                    '                <label for="doc_macro-repo_url">Repository url</label>'+
                    '                <input id="doc_macro-repo_url" v-model="url" class="text" type="text" placeholder="git clone URL">'+
                    '                <div class="description">Please type your repository\'s git file\'s url</div>'+
                    '             </div>'+
                    '             <div class="field-group">'+
                    '                 <label for="doc_macro-auth_type">Connection and Authorization type</label>'+
                    '                 <select id="doc_macro-auth_type" class="select" v-model="authType">'+
                    '                     <option v-bind:disabled="(urlType !== \'HTTP\' && urlType !== \'EMPTY\' && urlType != undefined && urlType !== \'UNKNOWN\')" value="USERNAMEPASSWORD">Http: Username + Password</option>'+
                    '                     <option v-bind:disabled="(urlType !== \'HTTP\' && urlType !== \'EMPTY\' && urlType != undefined && urlType !== \'UNKNOWN\')" value="NOAUTH">Http: No Authorization</option>'+
                    '                     <option v-bind:disabled="(urlType !== \'SSH\' && urlType !== \'EMPTY\' && urlType != undefined && urlType !== \'UNKNOWN\')" value="SSHKEY">SSH: Private Key</option>                            '+
                    '                 </select>'+
                    '             </div>'+
                    '             <div class="field-group" v-if="authType === \'USERNAMEPASSWORD\'">'+
                    '                <label for="doc_macro-username">Username</label>'+
                    '                <input id="doc_macro-username" v-model="username" class="text" type="text" placeholder="username">'+
                    '                <div class="description">Please type the username for the given git</div>'+
                    '             </div>'+
                    '             <div class="field-group" v-if="authType === \'USERNAMEPASSWORD\'">'+
                    '                <label for="doc_macro-password">Password</label>'+
                    '                <input id="doc_macro-password" v-model="password" class="password" type="password" placeholder="password">'+
                    '                <div class="description">Please type the password to the given git</div>'+
                    '             </div>'+
                    '             <div class="field-group" v-if="authType === \'SSHKEY\'">'+
                    '               <label for="doc_macro-sshkey">SSH key</label>'+
                    '               <textarea id="doc_macro-sshkey" v-model="sshKey" class="text" style="height: auto" rows="10" placeholder="key" />'+
                    '               <div class="description">Please paste your SSH key</div>'+
                    '             </div>'+
                    '             <div class="field-group" v-if="editableOptionAvailable">'+
                    '                  <label for="iseditable">Editable</label>'+
                    '                  <div class="checkbox"> ' +
                    '                      <input id="iseditable" class="checkbox" type="checkbox" v-model="editable" />'+
                    '                      <div class="description">Define if files can be edited in this repository via Git4C macro</div> ' +
                    '                  </div>'+
                    '             </div>'+
                    '      </div>'+
                    '      </div>'+
                    '      </div>'+
                    '   </form>'+
                    '</div>'+
                    '<footer class="aui-dialog2-footer">'+
                    '   <!-- Actions to render on the right of the footer -->'+
                    '   <div class="aui-dialog2-footer-actions">'+
                    '      <button id="custom_repository_dialog-close-button" v-on:click="defineRepository()" class="aui-button aui-button-primary" v-bind:disabled="!isFilled" v-show="saving == false">Save</button>'+
                    '      <button id="custom_repository_dialog-close-button" class="aui-button aui-button-primary" disabled=true v-show="saving == true">Saving...</button>'+
                    '   </div>'+
                    '   <!-- Hint text is rendered on the left of the footer -->'+
                    '   <!--<div class="aui-dialog2-footer-hint">this is a hint</div>-->'+
                    '</footer>'+
                    '</section>'
        }


    }
}
