(function ($) {

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

    const html = `
        <section role="dialog" id="multifiledoc_macroDialog" class="aui-layer aui-dialog2 aui-dialog2-medium" aria-hidden="true">
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
                 <div id="git4c-multi-file-dialog-content">
                 <div class="aui-group">
                 <div class="aui-item">
                    <div class="field-group">
                        <label for="doc_macro-predefined_repository">Repository</label>
                        <select class="select" v-show="(repositories && repositories.length!=0) || customRepository" v-model="repository"  style="max-width: 50%">
                            <option v-if="customRepository" v-bind:value="customRepository">
                                 {{ customRepository.repositoryName }}
                            </option>
                            <option v-for="repo in repositories" v-bind:value="repo">
                                 {{ repo.name }}
                            </option>
                        </select>
                        <select class="select" v-show="(!repositories || repositories.length==0) && !customRepository" :disabled="true"  style="max-width: 50%">
                            <option>No Repositories Available</option>
                        </select>
                        <button @click.prevent v-on:click="openCustomRepositoryDialog" class="aui-button aui-button-primary">
                            <span class="aui-icon aui-icon-small aui-iconfont-add"/>
                        </button>
                    </div>
               
                    <div class="field-group">
                       <label for="doc_macro-repo_branch">Repository branch</label>
                       <select class="select" v-show="downloadingBranches == true" :disabled="true">
                          <option>Downloading branches</option>
                       </select>
                       <select class="select" v-show="!branches && downloadingBranches == false" :disabled="true">
                          <option>No Branches Available</option>
                       </select>
                       <select class="select" v-show="downloadingBranches == false && branches" v-model="branch">
                          <option v-for="br in branches">
                             {{ br }}
                          </option>
                       </select>
                    </div>
                    <div class="field-group">
                      <label for="doc_macro-repo_glob">Filter</label>
                       <input v-model="glob" class="text" type="text" ref="doc_macro-glob" placeholder="pattern">
                       <div class="description">Please type your <a ref="pattern_tooltip" href="https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob">pattern</a> (Optional)</div>
                    </div>
                    <div class="field-group">
                      <label for="doc_macro-default_Doc_Item">Default File</label>
                       <input v-model="defaultDocItem" class="text" type="text" id="doc_macro-default_Doc_Item" placeholder="Readme.md">
                       <div class="description">Please type name of default Document File (Optional)</div>
                    </div>
                 </div>
                 </div>
                 </div>
              </form>
           </div>
           <footer class="aui-dialog2-footer">
              <!-- Actions to render on the right of the footer -->
              <div class="aui-dialog2-footer-actions">
              
                 <button id="dialog-close-button" v-on:click="save()" class="aui-button aui-button-primary" v-bind:disabled="!isFilled" v-show="saving == false">Save</button>
                 <button id="dialog-close-button" class="aui-button aui-button-primary" disabled=true v-show="saving == true">Saving...</button>

              </div>
              <!-- Hint text is rendered on the left of the footer -->
              <!--<div class="aui-dialog2-footer-hint">this is a hint</div>-->
           </footer>
        <div id="custom_repository_dialog-div">
            <custom-repository-dialog ref="custom_repository_dialog"></customrepositorydialog>
        </div>
        </section>
    `

    let dialogInstance
    let restUrl;

    AJS.toInit(function () {
        dialogInstance = $(html).appendTo("body");
        const baseUrl = AJS.params.baseUrl;
        restUrl = baseUrl + "/rest/doc/1.0/documentation";
    });

    //@ts-ignore
    AJS.MacroBrowser.setMacroJsOverride("Git4C", {
        "opener": function (macro) {

            //Default values
            let dataModel = {
                repositories: undefined,
                repository: undefined,

                customRepository: undefined,
                customRepositoryAuthType: undefined,
                customRepositoryName: undefined,
                customRepositoryUrl: undefined,

                existingRepositoryUuid: undefined,

                branch: undefined,
                branches: undefined,
                downloadingBranches: false,

                glob: undefined,
                globList: Array(),
                defaultDocItem: "",

                currentError: undefined,
                errors: errors,


                saving: false,
                isFilled: false,

            }

            const data = $.extend(dataModel, macro.params);

            const hide = (uuid) => {
                console.log("Closing")

                data.branches = undefined
                data.errors = undefined
                data.currentError = undefined

                data.glob = JSON.stringify(data.glob)


                data.globList = undefined

                data.downloadingBranches = undefined

                data.saving = undefined
                data.isFilled = undefined

                data.branch = undefined
                data.repository = undefined
                data.customRepository = undefined
                data.existingRepositoryUuid = undefined
                data.repositories = undefined
                data.uuid = uuid


                tinymce.confluence.macrobrowser.macroBrowserComplete({
                    "name": "Git4C",
                    "bodyHtml": undefined,
                    "params": JSON.parse(JSON.stringify(data))
                });
                AJS.dialog2(dialogInstance).hide();

            }

            AJS.dialog2(dialogInstance).on("hide", function () {
                $("#multifiledoc_macroDialog").remove()
                dialogInstance = $(html).appendTo("body")
            });

            AJS.dialog2(dialogInstance).show();

            const Bus = new Vue({})
            new Vue({
                el: '#multifiledoc_macroDialog',
                components:{
                    customRepositoryDialog: Git4CCustomRepositoryDialog.getComponent(Bus),
                },
                data: function () {
                    return data
                },
                watch: {
                    repository: function () {
                        this.getBranches()
                        this.checkIfFilled()
                    },
                    branch: function () {
                        this.checkIfFilled()
                    }

                },
                methods: {
                    closeDialog(){
                        this.$refs.custom_repository_dialog.clearFields()
                        AJS.dialog2(dialogInstance).hide()
                    },
                    openCustomRepositoryDialog(){
                        AJS.dialog2(this.$refs.custom_repository_dialog.$el).show()
                    },
                    closeCustomRepositoryDialog(){
                        AJS.dialog2(this.$refs.custom_repository_dialog.$el).hide()
                    },
                    processCustomRepository(repository){
                        const response = Vue.http.post(restUrl + "/repository/verify", {sourceRepositoryUrl: repository.sourceRepositoryUrl, credentials: repository.credentials}).then((response) => {
                            if (response.data.ok == false) {
                                throw new Error("","",response.data.status);
                            }
                            this.postProcessCustomRepository(repository)
                        }, error => {
                            error.text().then(text => {
                                    this.$refs.custom_repository_dialog.saving = false
                                    this.$refs.custom_repository_dialog.showError(text)
                                }
                            )
                        })

                    },
                    postProcessCustomRepository: function (repository) {
                        this.$refs.custom_repository_dialog.saving = false
                        this.closeCustomRepositoryDialog()
                        this.customRepository = repository
                        this.repository = this.customRepository
                        this.existingRepositoryUuid = undefined
                        this.customRepositoryAuthType = this.repository.credentials.sshKey ? "SSHKEY" : this.repository.credentials.username ? "USERNAMEPASSWORD" : "NOAUTH"
                        this.customRepositoryName = this.repository.repositoryName
                        this.customRepositoryUrl = this.repository.sourceRepositoryUrl

                    },
                    getBranches: function () {
                        this.downloadingBranches = true;
                        if(this.repository) {
                            this.branches = undefined;
                            const isPredefined = !this.customRepository ? true : this.repository != this.customRepository

                            if (this.existingRepositoryUuid && !isPredefined) {
                                const uuid = this.existingRepositoryUuid
                                Vue.http.get(restUrl + "/repository/" + uuid + "/branches").then((response) => {
                                    this.downloadingBranches = false;
                                    this.branches = response.body.allBranches

                                    const masterId = this.branches.indexOf("master")
                                    const developId = this.branches.indexOf("develop")

                                    if (masterId !== -1) {
                                        this.branch = this.branches[masterId]
                                    } else if (developId !== -1) {
                                        this.branch = this.branches[developId]
                                    } else {
                                        this.branch = this.branches[0]
                                    }
                                }, error => {
                                    error.text().then(text => {
                                            this.showError(text)
                                            this.currentError = text
                                            this.downloadingBranches = false
                                        }
                                    )
                                })
                            } else {
                                if (isPredefined) {
                                    const uuid = this.repository.uuid
                                    Vue.http.get(restUrl + "/predefine/" + uuid + "/branches").then((response) => {
                                        this.downloadingBranches = false;
                                        this.branches = response.body.allBranches

                                        const masterId = this.branches.indexOf("master")
                                        const developId = this.branches.indexOf("develop")

                                        if (masterId !== -1) {
                                            this.branch = this.branches[masterId]
                                        } else if (developId !== -1) {
                                            this.branch = this.branches[developId]
                                        } else {
                                            this.branch = this.branches[0]
                                        }
                                    }, error => {
                                        error.text().then(text => {
                                                this.showError(text)
                                                this.currentError = text
                                                this.downloadingBranches = false
                                            }
                                        )
                                    })

                                } else {
                                    const repositoryForBranches = {
                                        sourceRepositoryUrl: this.customRepository.sourceRepositoryUrl,
                                        credentials: this.customRepository.credentials
                                    }
                                    if(repositoryForBranches.sourceRepositoryUrl)
                                    Vue.http.post(restUrl + "/repository/branches", repositoryForBranches).then((response) => {
                                        this.branches = response.body.allBranches
                                        this.downloadingBranches = false;


                                        const masterId = this.branches.indexOf("master")
                                        const developId = this.branches.indexOf("develop")

                                        if (masterId !== -1) {
                                            this.branch = this.branches[masterId]
                                        } else if (developId !== -1) {
                                            this.branch = this.branches[developId]
                                        } else {
                                            this.branch = this.branches[0]
                                        }
                                    }, error => {
                                        error.text().then(text => {
                                                this.showError(text)
                                                this.currentError = text
                                                this.downloadingBranches = false
                                            }
                                        )
                                    })
                                }

                            }
                        }
                    },
                    save: function () {

                        this.saving = true
                        const isPredefined = !this.customRepository ? true : this.repository != this.customRepository
                        if(isPredefined){
                            this.existingRepositoryUuid = undefined
                            this.customRepositoryUrl = undefined
                            this.customRepositoryAuthType = undefined
                            this.customRepositoryName = undefined
                        }

                        let toSend = undefined
                        let repositoryDetails = undefined

                        const globToSave = this.glob ? this.glob.split(',') : []
                        const defaultDocItemToSave = this.defaultDocItem ? this.defaultDocItem : ""

                        if(!this.existingRepositoryUuid){
                            if (!isPredefined) {
                                repositoryDetails = {
                                        repository: {
                                            type: "CUSTOM",
                                            url: this.repository.sourceRepositoryUrl,
                                            credentials: this.repository.credentials

                                    }
                                }
                            } else {
                                repositoryDetails = {
                                        repository: {
                                            type: "PREDEFINED",
                                            uuid: this.repository.uuid

                                    }
                                }
                            }
                        } else{
                            repositoryDetails = {
                                    repository: {
                                        type: "EXISTING",
                                        uuid: this.existingRepositoryUuid

                                }
                            }
                        }
                        toSend = {
                            repositoryDetails,
                            branch: this.branch,
                            glob:  globToSave,
                            defaultDocItem: defaultDocItemToSave
                        }
                        this.$http.post(restUrl, toSend, {}).then(response => {
                            const uuid = response.body.uuid
                            hide(uuid)
                            this.saving = false
                        }, error => {
                            this.saving = false
                            error.text().then(text => {
                                this.showError(text)
                            })
                        })

                    },
                    getRepositoryList: function () {
                        const vm = this;

                        vm.$http.get(restUrl + "/predefine", {}).then(response => {
                            this.repositories = response.data
                            if(!this.repository) {
                                this.repository = this.repositories[0]
                            }
                        }, error => {
                            error.text().then(text => {
                                vm.showError(text)
                            })
                        })
                    },
                    getGlobList: function () {
                        const vm = this;

                        vm.$http.get(restUrl + "/glob", {}).then(response => {
                            this.globList = response.data.globs.map(function (data) {
                                return {id: data.glob, text: data.glob + " (" + data.name + ")"}
                            })
                            $(this.$refs['doc_macro-glob']).auiSelect2({
                                tags: this.globList,
                                tokenSeparators: [",", " "]
                            }).on('change', function (e) {
                                let val = $(this).val();
                                vm.glob = val;
                            });

                        }, error => {
                            error.text().then(text => {
                                vm.showError(text)
                            })
                        })
                    },
                    checkIfFilled: function () {
                        !this.repository ? this.isFilled = false : !this.branch ? this.isFilled = false : this.isFilled = true
                    },
                    showError: function (text) {
                        const errors = _.flatMap(this.errors, (e) => e.serverError)
                        if (_.includes(errors, text)) {
                            this.currentError = text
                        } else {
                            this.currentError = "SERVER_ERROR"
                        }

                    },
                    init: function(){
                        if(data.uuid){
                            if(this.glob){
                                if(this.glob == "undefined"){
                                    this.glob = null
                                }else{
                                this.glob = this.glob.substring(1,this.glob.length-1)
                                }
                            }

                            Vue.http.get(restUrl + "/" + data.uuid).then(response => {
                                this.existingRepositoryUuid = response.body.repositoryUuid
                                this.getBranches()

                            }, error => {
                                error.text().then(text => {
                                    this.showError(text)
                                })
                            })
                            if(this.customRepositoryName) {
                                this.customRepository = {repositoryName: this.customRepositoryName}
                                this.repository = this.customRepository
                                const repoInfo = {
                                    name: this.customRepositoryName,
                                    sourceRepositoryUrl: this.customRepositoryUrl,
                                    authType: this.customRepositoryAuthType
                                }
                                this.$refs.custom_repository_dialog.initFields(repoInfo)
                            }


                        }
                    }

                },
                mounted: function(){
                    this.init()
                    this.getRepositoryList()

                    Bus.$on("closeCustomRepositoryDialog", ()=>{this.closeCustomRepositoryDialog()})
                    Bus.$on("repositoryDefined", (repository)=>{this.processCustomRepository(repository)})

                    AJS.$(this.$refs.pattern_tooltip).tooltip({
                        title: function () {
                            return "Git4C uses Glob patterns to filter files."
                        }
                    })

                    this.getGlobList()

                }
            })
        }
    });

    //https://developer.mozilla.org/en-US/docs/Web/API/WindowBase64/Base64_encoding_and_decoding#The_.22Unicode_Problem.22
    function b64EncodeUnicode(str) {
        // first we use encodeURIComponent to get percent-encoded UTF-8,
        // then we convert the percent encodings into raw bytes which
        // can be fed into btoa.
        return btoa(encodeURIComponent(str).replace(/%([0-9A-F]{2})/g,
            function toSolidBytes(match, p1) {
                return String.fromCharCode('0x' + p1);
            }));
    }

    function b64DecodeUnicode(str) {
        // Going backwards: from bytestream, to percent-encoding, to original string.
        return decodeURIComponent(atob(str).split('').map(function (c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));
    }

})(AJS.$);
