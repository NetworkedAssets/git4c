var Git4CCustomRepositoryDialog = {
    getComponent: function (Events) {

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

        class Credentials{
            /**
             * @param {string} type
             */
            constructor(type){
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

        const serverError = new Error("server_error", ["SERVER_ERROR"], "<p>Server error</p>")

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
                    currentError: undefined
                }
            },
            watch: {
                name: function(){
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
                authType: function() {
                    this.checkIfFilled()
                }
            },
            methods: {
                closeDialog() {
                  if(Events){
                      Events.$emit("closeCustomRepositoryDialog")
                  }
                  this.$emit("closeCustomRepositoryDialog")
                },
                checkIfFilled: function() {
                    if(!this.name)
                    {
                        this.isFilled = false
                    } else if( !this.url ){
                        this.isFilled = false
                    } else if( this.authType == "NOAUTH" ) {
                        this.isFilled = true
                    } else if(this.authType == "SSHKEY") {
                        !this.sshKey ? this.isFilled = false : this.isFilled = true
                    } else if (this.authType == "USERNAMEPASSWORD") {
                                if(!this.password) {
                                    this.isFilled = false
                                } else {
                                    !this.username ? this.isFilled = false : this.isFilled = true
                                  }
                    } else {
                        isFilled = true
                    }
                },
                processUrlChange: function () {


                    if(!this.url) {
                        this.urlType = "EMPTY"
                    } else if ( this.url.startsWith("http") ) {
                        this.urlType = "HTTP"
                    } else if (this.url.startsWith("ssh") || this.url.startsWith("git@gitlab")) {
                        this.urlType = "SSH"
                    } else {
                        this.urlType = "UNKNOWN"
                    }

                    switch(this.urlType){
                        case 'HTTP': {
                            if(this.authType!== "NOAUTH")
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
                defineRepository: function() {
                    this.saving = true;
                    switch (this.authType){
                        case "NOAUTH": {
                            const repository = {
                                repositoryName : this.name,
                                sourceRepositoryUrl: this.url,
                                credentials: new NoAuthCredentials()
                            };
                            if(Events){
                                Events.$emit("repositoryDefined", repository)
                            }
                            this.$emit("repositoryDefined", repository);
                            break;
                        }
                        case "USERNAMEPASSWORD": {
                            const repository = {
                                repositoryName : this.name,
                                sourceRepositoryUrl: this.url,
                                credentials: new UsernamePasswordCredentials(this.username, this.password)
                            };
                            if(Events){
                                Events.$emit("repositoryDefined", repository)
                            }
                            this.$emit("repositoryDefined", repository);
                            break;
                        }
                        case "SSHKEY" : {
                            const repository = {
                                repositoryName : this.name,
                                sourceRepositoryUrl: this.url,
                                credentials: new SSHKeyCredentials(this.sshKey)
                            };
                            if(Events){
                                Events.$emit("repositoryDefined", repository)
                            }
                            this.$emit("repositoryDefined", repository);
                            break;
                        }
                    }

                },
                showError: function (text) {
                    const errors = _.flatMap(this.errors, (e) => e.serverError)
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
                    this.isFilled = false
                    this.saving = false
                    this.currentError = undefined
                },
                initFields: function(repositoryInfo) {
                    this.name = repositoryInfo.name
                    this.url = repositoryInfo.sourceRepositoryUrl
                    this.authType = repositoryInfo.authType
                }

            },
            template: `
                    <section role="dialog" id="custom_repository_dialog" class="aui-layer aui-dialog2 aui-dialog2-medium" aria-hidden="true">
                    <header class="aui-dialog2-header">
                       <h2 class="aui-dialog2-header-main">Git4C macro parameters</h2>
                       <a class="aui-dialog2-header-close" v-on:click="closeDialog()">
                       <span class="aui-icon aui-icon-small aui-iconfont-close-dialog">Close</span>
                       </a>
                    </header>
                    <div class="aui-dialog2-content">
                       <div v-for="error in errors" >
                          <div v-for="serverError in error.serverError" class="aui-message aui-message-error" v-show="serverError == currentError">
                             <p class="title">
                                <strong>Error!</strong>
                             </p>
                             <p v-html="error.text">
                             </p>
                          </div>
                       </div>
                       <!--</div>-->
                       <form class="aui">
                          <div id="git4c-custom-repository-dialog-content">
                          <div class="aui-group">
                          <div class="aui-item">
                                 <div class="field-group">
                                    <label for="doc_macro-repo_name">Name</label>
                                    <input v-model="name" class="text" type="text" placeholder="custom">
                                    <div class="description">Please type your repository\s name</div>
                                 </div>
                                 <div class="field-group">
                                    <label for="doc_macro-repo_url">Repository url</label>
                                    <input v-model="url" class="text" type="text" placeholder="git clone URL">
                                    <div class="description">Please type your repository\s git fle url</div>
                                 </div>
                                 <div class="field-group">
                                     <label for="doc_macro-auth_type">Connection and Authorization type</label>
                                     <select class="select" v-model="authType">
                                         <option v-bind:disabled="(urlType !== 'HTTP' && urlType !== 'EMPTY' && urlType != undefined && urlType !== 'UNKNOWN')" value="USERNAMEPASSWORD">Http: Username + Password</option>
                                         <option v-bind:disabled="(urlType !== 'HTTP' && urlType !== 'EMPTY' && urlType != undefined && urlType !== 'UNKNOWN')" value="NOAUTH">Http: No Authorization</option>
                                         <option v-bind:disabled="(urlType !== 'SSH' && urlType !== 'EMPTY' && urlType != undefined && urlType !== 'UNKNOWN')" value="SSHKEY">SSH: Private Key</option>                            
                                     </select>
                                 </div>
                                 <div class="field-group" v-if="authType === 'USERNAMEPASSWORD'">
                                    <label for="doc_macro-login_email">Username</label>
                                    <input v-model="username" class="text" type="text" placeholder="username">
                                    <div class="description">Please type your username to given git</div>
                                 </div>
                                 <div class="field-group" v-if="authType === 'USERNAMEPASSWORD'">
                                    <label for="doc_macro-login_password">Password</label>
                                    <input v-model="password" class="password" type="password" placeholder="password">
                                    <div class="description">Please type your password to given git</div>
                                 </div>
                                 <div class="field-group" v-if="authType === 'SSHKEY'">
                                   <label for="doc_macro-repo_branch">SSH key</label>
                                   <textarea v-model="sshKey" class="text" style="height: auto" rows="10" placeholder="key" />
                                   <div class="description">Please paste your SSH key</div>
                                 </div>
        
                          </div>
                          </div>
                          </div>
                       </form>
                    </div>
                    <footer class="aui-dialog2-footer">
                       <!-- Actions to render on the right of the footer -->
                       <div class="aui-dialog2-footer-actions">
                          <button id="custom_repository_dialog-close-button" v-on:click="defineRepository()" class="aui-button aui-button-primary" v-bind:disabled="!isFilled" v-show="saving == false">Save</button>
                          <button id="custom_repository_dialog-close-button" class="aui-button aui-button-primary" disabled=true v-show="saving == true">Saving...</button>
                       </div>
                       <!-- Hint text is rendered on the left of the footer -->
                       <!--<div class="aui-dialog2-footer-hint">this is a hint</div>-->
                    </footer>
                    </section>
                    `
        }


    }
}
