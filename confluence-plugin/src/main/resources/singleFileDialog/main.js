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
        <section role="dialog" id="singlefiledoc_macroDialog" class="aui-layer aui-dialog2 aui-dialog2-xlarge" aria-hidden="true">
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
                 <div id="git4c-single-file-dialog-content">
                 <div class="aui-group">
                 <div class="aui-item">
                    <div class="field-group">
                        <label for="doc_macro-predefined_repository">Repository</label>
                        <select class="select" v-show="(repositories && repositories.length!=0) || customRepository" v-model="repository"  style="max-width: 64%">
                            <option v-if="customRepository" v-bind:value="customRepository">
                                 {{ customRepository.repositoryName }}
                            </option>
                            <option v-for="repo in repositories" v-bind:value="repo">
                                 {{ repo.name }}
                            </option>
                        </select>
                        <select class="select" v-show="(!repositories || repositories.length==0) && !customRepository" :disabled="true"  style="max-width: 64%">
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
                    <div class="field-group git4c-file-select-container">
                       <label for="doc_macro-repo_file">File</label>
                       <select class="select" v-show="downloadingFiles == true" :disabled="true" style="max-width: 208px">
                          <option>Downloading files</option>
                       </select>
                       <span v-show="downloadingFiles === false">
                           <git4cselect2single class="select" v-model="file">
                              <option v-for="file in files">
                                 {{ file }}
                              </option>
                           </git4cselect2single>
                       </span>
                       
                       <span v-show="files">
                           <button v-on:click="showFileTree()" @click.prevent href="javascript:void(0)" class="aui-button aui-button-primary"><span class="aui-icon aui-icon-small aui-iconfont-nav-children-large">Show file tree</span></button>
                       </span>
                       <span v-show="!files">
                           <button @click.prevent href="javascript:void(0)" class="aui-button aui-button-primary" aria-disabled="true"><span class="aui-icon aui-icon-small aui-iconfont-nav-children-large">Show file tree</span></button>
                       </span>
                       
                          <!-- <button v-if="files" v-on:click="showFileTree()" @click.prevent href="javascript:void(0)" class="aui-button aui-button-primary"><span class="aui-icon aui-icon-small aui-iconfont-nav-children-large">Show file tree</span></button>
                           -->
                    </div>
                    <div class="field-group" v-if="hasMethods">
                       <label for="doc_macro-repo_branch">Methods in file</label>
                       <select class="select" v-show="downloadingMethods == true" :disabled="true">
                          <option>Downloading methods</option>
                       </select>
                       <select class="select" v-show="downloadingMethods == false" v-model="method">
                          <option>All</option>
                          <option v-for="method in methods">
                             {{ method.name }}
                          </option>
                       </select>
                    </div>
                    <div class="field-group">
                       <label>Show top bar</label>
                       <div class="checkbox">
                          <input class="checkbox" type="checkbox" v-model="showTopBar">
                       </div>      
                    </div>
                    <div class="field-group" v-if="showTopBar">
                       <label>Collapsible</label>
                       <div class="checkbox">
                          <input class="checkbox" type="checkbox" v-model="collapsible">
                       </div>      
                    </div>
                    <div class="field-group" v-if="showTopBar && collapsible">
                       <label>Collapse by default</label>
                       <div class="checkbox">
                          <input class="checkbox" type="checkbox" v-model="collapseByDefault">
                       </div>      
                    </div>
                    <div class="field-group" v-if="haveLineNumbers">
                       <label>Show line numbers</label>
                       <div class="checkbox">
                          <input :disabled="downloadingFile" class="checkbox" type="checkbox" v-model="showLineNumbers">
                       </div>      
                    </div>
                 </div>
                 <div class="aui-item" id="dialog-code-content">
                     <div style="color: gray; margin-bottom: 10px">File preview (will show after macro is loaded)</div>
                     <!-- <hr /> -->
                     <div id="git4c-single-dialog-code-holder" v-html="fileContent" v-bind:class="{ 'git4c-single-dialog-no-line-numbers': haveLineNumbers && !showLineNumbers }">
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
           <custom-repository-dialog ref="custom_repository_dialog"></custom-repository-dialog>
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
    AJS.MacroBrowser.setMacroJsOverride("Git4C Single File", {
        "opener": function (macro) {

            //Default values
            let dataModel = {
                customRepository: undefined,
                customRepositoryAuthType: undefined,
                customRepositoryName: undefined,
                customRepositoryUrl: undefined,

                repository: undefined,

                existingRepositoryUuid: undefined,

                branch: undefined,
                branches: undefined,
                downloadingBranches: false,
                branchesReq: undefined,

                files: undefined,
                file: undefined,
                downloadingFiles: false,
                filesReq: undefined,
                downloadingFile: false,

                methods: undefined,
                method: undefined,
                hasMethods: undefined,
                downloadingMethods: false,
                methodsReq: undefined,
                originalContent: undefined,

                currentError: undefined,
                fileTree: undefined,
                fileContent: undefined,
                // currentError: "SOURCE_NOT_FOUND",
                errors: errors,
                repositories: undefined,
                isPredefined: true,
                saving: false,
                isFilled: false,
                showLineNumbers: true,
                haveLineNumbers: false,
                collapsible: true,
                showTopBar: true,
                collapseByDefault: false
            }

            const data = $.extend(dataModel, macro.params);

            //Needs testing
            // data.isPredefined = (data.isPredefined === "true")

            data.collapsible = (typeof(data.collapsible) === "boolean") ? data.collapsible : (data.collapsible === "true")
            data.showTopBar = (typeof(data.showTopBar) === "boolean") ? data.showTopBar : (data.showTopBar === "true")
            data.showLineNumbers = (typeof(data.showLineNumbers) === "boolean") ? data.showLineNumbers : (data.showLineNumbers === "true")
            data.collapseByDefault = (typeof(data.collapseByDefault) === "boolean") ? data.collapseByDefault : (data.collapseByDefault === "true")

            // AJS.dialog2(dialogInstance).on("show", function () {
            //     console.log("Dialog shown")
            //     var closeButton = dialogInstance.find("#dialog-close-button")[0]
            //     $("#dialog-close-button").click(function(e) {
            //         e.preventDefault()
            //         //Clean data
            //     })
            // })

            const hide = (uuid) => {
                console.log("Closing")

                data.branches = undefined
                data.errors = undefined
                data.currentError = undefined
                data.downloadingBranches = undefined
                data.downloadingFiles = undefined
                data.saving = undefined
                data.isFilled = undefined
                data.files = undefined
                data.fileTree = undefined
                data.fileContent = undefined
                data.isPredefined = undefined
                data.repositories = undefined
                data.filesReq = undefined
                data.branchesReq = undefined
                data.uuid = uuid
                data.methods = undefined
                data.downloadingMethods = undefined
                data.methodsReq = undefined
                data.hasMethods = undefined
                data.originalContent = undefined
                data.haveLineNumbers = undefined
                data.customRepository = undefined
                data.repository = undefined
                data.downloadingFile = undefined

                tinymce.confluence.macrobrowser.macroBrowserComplete({
                    "name": "Git4C Single File",
                    "bodyHtml": undefined,
                    "params": JSON.parse(JSON.stringify(data))
                });
                AJS.dialog2(dialogInstance).hide();
            }

            AJS.dialog2(dialogInstance).on("hide", function () {
                // console.log("Data", data)
                // console.log("JSON", JSON.stringify(data))
                //Remove all dialog instances from DOM
                $("#singlefiledoc_macroDialog").remove()
                dialogInstance = $(html).appendTo("body")
            });

            AJS.dialog2(dialogInstance).show();

            const Bus = new Vue({});

            new Vue({
                el: '#singlefiledoc_macroDialog',
                data: function () {
                    return data
                },
                components: {
                    customRepositoryDialog: Git4CCustomRepositoryDialog.getComponent(Bus),
                },
                watch: {
                    repository: function () {
                        this.checkIfPredefined()
                        this.$nextTick(() => {
                            this.getBranches()
                        })
                    },
                    branch: function () {
                        this.getFiles()
                        this.isFilled = this.checkIfFilled()
                    },
                    file: function () {
                        this.getFile()
                        this.isFilled = this.checkIfFilled()
                    },
                    method: function () {
                        this.handleMethodChange()
                    },
                    fileContent: function () {
                        this.handleFileContentChange()
                    },
                    showLineNumbers: function () {
                        this.$nextTick(() => {
                            this.resizeCodeDiv()
                        })
                    }
                },
                methods: {
                    handleFileContentChange: function () {
                        this.$nextTick(function () {
                            $(this.$el).find("pre code.git4c-highlightjs-code").each(function (i, block) {
                                hljs.highlightBlock(block);
                            });
                            $(this.$el).find("code.git4c-prismjs-code").each(function (i, block) {
                                Prism.highlightElement(block)
                            });
                            this.$nextTick(function () {
                                this.resizeCodeDiv();
                            })
                        })
                    },
                    resizeCodeDiv: function () {
                        $(this.$el).find("#git4c-single-dialog-code-holder").css({width: ''})
                        $(this.$el).find("pre.line-numbers").css({'margin-right': ''})

                        let trueWidth = 0
                        try {
                            trueWidth = $(this.$el).find("pre.line-numbers").get()[0].scrollWidth
                        } catch (err) {
                            trueWidth = 0
                        }

                        // console.log("Width", trueWidth)
                        if (trueWidth) {
                            $(this.$el).find("#git4c-single-dialog-code-holder").css({width: trueWidth + 40})
                            $(this.$el).find("pre.line-numbers").css({'margin-right': 20})
                        }
                    },
                    handleMethodChange: function () {

                        if(this.method === "All") {
                            this.fileContent = this.originalContent
                        } else {
                            this.fileContent = _.find(this.methods, (m) => m.name === this.method).content
                        }

                    },
                    getBranches: function () {

                        this.branches = undefined;
                        this.files = undefined;
                        this.fileContent = undefined

                        if (this.repository) {
                            this.getBranchesDebounce()
                        }
                    },
                    getBranchesDebounce: _.debounce(
                        function () {

                            const vm = this;

                            vm.downloadingBranches = true
                            vm.currentError = undefined
                            vm.files = undefined
                            vm.file = undefined

                            let promise = undefined

                            let before = {
                                before(request) {
                                    vm.branchesReq = request
                                }
                            }
                            if (this.existingRepositoryUuid && !this.isPredefined) {
                                const uuid = this.existingRepositoryUuid
                                promise = vm.$http.get(restUrl + "/repository/" + uuid + "/branches")
                            } else {
                                if (vm.isPredefined) {
                                    const uuid = this.repository.uuid
                                    promise = vm.$http.get(restUrl + "/predefine/" + uuid + "/branches", before)
                                } else {

                                    const credentials = vm.getCredentials()

                                    const toSend = {
                                        sourceRepositoryUrl: this.repository.sourceRepositoryUrl,
                                        credentials: credentials
                                    };

                                    if(toSend.sourceRepositoryUrl) {
                                        promise = vm.$http.post(restUrl + "/repository/branches", toSend, before)
                                    }
                                }
                            }

                            if(promise) {
                                promise.then(response => {
                                    const branches = response.body.allBranches
                                    vm.branches = branches

                                    const masterId = branches.indexOf("master")
                                    const developId = branches.indexOf("develop")

                                    if (masterId !== -1) {
                                        vm.branch = branches[masterId]
                                    } else if (developId !== -1) {
                                        vm.branch = branches[developId]
                                    } else {
                                        vm.branch = branches[0]
                                    }

                                    vm.downloadingBranches = false

                                    //So downloadingBranches will be set
                                    this.$nextTick(() => {
                                        vm.getFiles()
                                    })
                                }, error => {
                                    error.text().then(text => {
                                        vm.showError(text)
                                        // console.log(text)
                                        vm.currentError = text
                                        vm.downloadingBranches = false
                                    })
                                    // console.log(errorText)
                                })
                            }
                        },
                        200
                    ),
                    getFiles: function () {

                        this.fileContent = undefined
                        if (this.downloadingBranches) {
                            return
                        }

                        this.getFilesDebounce()

                    },
                    getFilesDebounce: _.debounce(
                        function () {

                            const vm = this

                            // console.log("Getfile debounce")

                            let promise = undefined
                            vm.downloadingFiles = true
                            vm.currentError = undefined

                            let before = {
                                before(request) {
                                    vm.filesReq = request
                                }
                            }
                            if (this.existingRepositoryUuid && !this.isPredefined) {
                                const uuid = this.existingRepositoryUuid

                                const toSend = {
                                    branch: vm.branch
                                };

                                promise = vm.$http.post(restUrl + "/repository/" + uuid + "/files", toSend, before)
                            } else {
                                if (this.isPredefined) {

                                    const uuid = this.repository.uuid

                                    const toSend = {
                                        branch: vm.branch
                                    };

                                    promise = vm.$http.post(restUrl + "/predefine/" + uuid + "/files", toSend, before)
                                } else {

                                    const credentials = vm.getCredentials()

                                    const toSend = {
                                        sourceRepositoryUrl: this.repository.sourceRepositoryUrl,
                                        credentials: credentials,
                                        branch: vm.branch
                                    };
                                    if (toSend.sourceRepositoryUrl) {
                                        promise = vm.$http.post(restUrl + "/repository/files", toSend, before)
                                    }
                                }
                            }

                            if(promise) {
                                promise.then(response => {
                                    vm.files = response.body.files
                                    vm.fileTree = response.body.tree
                                    vm.file = response.body.files[0]
                                    vm.downloadingFiles = false
                                }, error => {
                                    error.text().then(text => {
                                        vm.showError(text)
                                        vm.downloadingFiles = false
                                    })
                                })
                            }
                        },
                        200
                    ),
                    getFile: function () {
                        // const file = vm.file

                        $(this.$el).find("#git4c-single-dialog-code-holder").css({width: '', 'margin-right': ''})
                        $(this.$el).find("pre.line-numbers").css({'margin-right': ''})

                        if (!this.file) {
                            return
                        }

                        this.downloadingFile = true

                        this.$nextTick(() => {
                            this.haveLineNumbers = Git4CUtils.hasLines(this.file)
                        })

                        this.hasMethods = false
                        this.haveLineNumbers = false

                        let promise;
                        if (this.existingRepositoryUuid && !this.isPredefined) {
                            const uuid = this.existingRepositoryUuid

                            const toSend = {
                                branch: this.branch,
                                file: this.file
                            };

                            promise = this.$http.post(restUrl + "/repository/" + uuid + "/file", toSend, {})
                        } else {
                            if (this.isPredefined) {

                                const uuid = this.repository.uuid

                                const toSend = {
                                    branch: this.branch,
                                    file: this.file
                                };

                                promise = this.$http.post(restUrl + "/predefine/" + uuid + "/file", toSend, {})

                            } else {

                                const credentials = this.getCredentials()

                                const toSend = {
                                    sourceRepositoryUrl: this.repository.sourceRepositoryUrl,
                                    credentials: credentials,
                                    branch: this.branch,
                                    file: this.file
                                };
                                if (toSend.sourceRepositoryUrl){
                                    promise = this.$http.post(restUrl + "/repository/file", toSend, {})
                                }
                            }
                        }
                        this.fileContent = "<div>Loading file</div>"

                        if(promise) {
                            promise.then(response => {
                                this.downloadingFile = false
                                const content = response.body.content
                                this.originalContent = content
                                if (content) {
                                    this.fileContent = content
                                } else {
                                    this.fileContent = "<div>Cannot open file</div>"
                                }
                                if (this.file.endsWith("feature") || this.file.endsWith("java")) {
                                    this.hasMethods = true
                                    this.getMethods()
                                }
                            }, error => {
                                error.text().then(text => {
                                    this.showError(text)
                                })
                            })
                        }
                    },
                    getMethods: function () {

                        if (!this.file) {
                            return
                        }

                        this.downloadingMethods = true

                        let promise
                        if (this.existingRepositoryUuid && !this.isPredefined) {
                            const uuid = this.existingRepositoryUuid
                            const toSend = {
                                branch: this.branch,
                                file: this.file
                            };
                            promise = this.$http.post(restUrl + "/repository/" + uuid + "/files", toSend, {})
                        } else {
                            if (this.isPredefined) {

                                const uuid = this.repository.uuid

                                const toSend = {
                                    branch: this.branch,
                                    file: this.file
                                };

                                promise = this.$http.post(restUrl + "/predefine/" + uuid + "/methods", toSend, {})


                            } else {

                                const credentials = this.getCredentials()

                                const toSend = {
                                    sourceRepositoryUrl: this.repository.sourceRepositoryUrl,
                                    credentials: credentials,
                                    branch: this.branch,
                                    file: this.file
                                };
                                if (toSend.sourceRepositoryUrl) {
                                    promise = this.$http.post(restUrl + "/repository/file/methods", toSend, {})
                                }
                            }
                        }
                        if(promise) {
                            promise.then(response => {
                                this.methods = response.body.methods
                                this.method = "All"
                                this.downloadingMethods = false
                            }, error => {
                                error.text().then(text => {
                                    this.showError(text)
                                })
                            })
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
                    save: function () {
                        this.saving = true
                        const credentials = this.getCredentials()

                        const method = this.hasMethods && this.method !== "All" ? this.method : undefined

                        let toSend = undefined
                        if (this.existingRepositoryUuid && !this.isPredefined) {
                            toSend = {
                                repositoryDetails: {
                                    repository: {
                                        type: "EXISTING",
                                        uuid: this.existingRepositoryUuid
                                    }
                                },
                                branch: this.branch,
                                // file: this.file
                                glob: [this.file],
                                defaultDocItem: "",
                                method
                            };
                        } else {
                            if (!this.isPredefined) {
                                toSend = {
                                    repositoryDetails: {
                                        repository: {
                                            type: "CUSTOM",
                                            url: this.repository.sourceRepositoryUrl,
                                            credentials: credentials
                                        }
                                    },
                                    branch: this.branch,
                                    // file: this.file
                                    glob: [this.file],
                                    defaultDocItem: "",
                                    method
                                };
                            }
                            else {
                                const uuid = this.repository.uuid
                                toSend = {
                                    repositoryDetails: {
                                        repository: {
                                            type: "PREDEFINED",
                                            uuid
                                        }
                                    },
                                    branch: this.branch,
                                    // file: this.file
                                    glob: [this.file],
                                    defaultDocItem: "",
                                    method
                                }
                                this.clearCustomFields()
                            }
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

                        // hide()
                        // console.log("Hide dialog")
                        // AJS.dialog2(dialogInstance).hide()
                    },
                    closeDialog: function () {


                        // hide()
                        // console.log("Hide dialog")
                        AJS.dialog2(dialogInstance).hide()
                    },
                    setChoosenFile: function (file) {
                        if (file) {
                            this.file = file
                        }
                    },
                    showFileTree: function () {

                        // const f = this.setChoosenFile()
                        //
                        createFileTreeDialog(this.fileTree, this.setChoosenFile, this)

                        // alert("SHOW DIALOG")
                    },
                    getCredentials: function () {
                        return this.repository.credentials
                    },
                    openCustomRepositoryDialog: function () {
                        AJS.dialog2(this.$refs.custom_repository_dialog.$el).show()
                    },
                    closeCustomRepositoryDialog: function () {
                         AJS.dialog2(this.$refs.custom_repository_dialog.$el).hide()
                    },
                    processRepository: function (repository) {
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
                    getPredefinedList: function () {
                        const vm = this;

                        vm.$http.get(restUrl + "/predefine", {}).then(response => {
                            const repos = response.data
                            vm.repositories = repos
                            vm.$nextTick(() => {
                                if(!vm.repository) {
                                    vm.repository = repos[0]
                                    vm.$nextTick(() => {
                                        vm.getBranches()
                                    })
                                }
                            })
                        }, error => {
                            error.text().then(text => {
                                vm.showError(text)
                            })
                        })
                    },
                    checkIfFilled: function () {
                            if (this.branch && this.file && this.repository) {
                                return true
                            }

                        return false
                    },
                    checkIfPredefined: function () {
                      this.isPredefined = !this.customRepository ? true : this.repository != this.customRepository
                    },
                    clearCustomFields: function () {
                        this.existingRepositoryUuid = undefined
                        this.customRepositoryUrl = undefined
                        this.customRepositoryAuthType = undefined
                        this.customRepositoryName = undefined
                    },
                    init: function(){
                        if(data.uuid){
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
                mounted: function () {
                    this.init()
                    this.getPredefinedList()
                    Bus.$on("closeCustomRepositoryDialog", ()=>{this.closeCustomRepositoryDialog()})
                    Bus.$on("repositoryDefined", (repository)=>{this.processRepository(repository)})
                }
            })
        }
    });

    const fileTreeDialog = `
           <section role="dialog" id="singlefiledoc_filetree_macroDialog" class="aui-layer aui-dialog2 aui-dialog2-medium" aria-hidden="true">
               <header class="aui-dialog2-header">
                  <h2 class="aui-dialog2-header-main">Select file</h2>
                  <a class="aui-dialog2-header-close" v-on:click="hideDialog()">
                  <span class="aui-icon aui-icon-small aui-iconfont-close-dialog">Close</span>
                  </a>
               </header>
               <div class="aui-dialog2-content" style="margin: 0; padding: 0; display: flex;"> 
                   <div id="git4c-single-dialog-tree-div" style="width: 200px; padding: 20px; overflow: auto;" ref="fileTree">
                       <git4c-filetree :data="fileTree"></git4c-filetree>
                   </div>
                   <div style="width: 400px; border-left: 1px solid #ccc; overflow: auto; display: flex; flex-direction: column">
                       <div style="margin-left: 20px; margin-top: 8px; margin-bottom: 8px">File preview</div>
                       <hr style="margin: 0" />
                       <div v-if="fileContent" v-bind:class="{ 'git4c-single-dialog-tree-code-holder-markdown ': !singleFile }" id="git4c-single-dialog-tree-code-holder" v-html="fileContent"></div>
                       <overlay v-else></overlay>
                    </div>
               </div>
               <footer class="aui-dialog2-footer">
                    <!-- Actions to render on the right of the footer -->
                    <div class="aui-dialog2-footer-actions">
                        <button v-on:click="passFile()" id="dialog-next-button" class="aui-button">Select</button>
                        <button v-on:click="hideDialog()" id="dialog-close-button" class="aui-button aui-button-link">Cancel</button>
                    </div>
               </footer>
           </section>
    `

    const createFileTreeDialog = function (tree, callback, originalData) {
        const dialogInstance = $(fileTreeDialog).appendTo("body");

        let vue

        AJS.dialog2(dialogInstance).on("hide", function () {
            if (vue) {
                vue.$destroy()
            }
            $("#singlefiledoc_filetree_macroDialog").remove()
        })

        AJS.dialog2(dialogInstance).show();

        const hide = function (file) {
            AJS.dialog2(dialogInstance).hide()
            callback(file)
        }

        const Bus = new Vue({});

        let maxCodeHeight = 20

        const resizeFun = () => {
            maxCodeHeight = Math.max($("#singlefiledoc_filetree_macroDialog").height(), 231) - 75
            Bus.$emit("WindowResized")
        }

        vue = new Vue({
            el: '#singlefiledoc_filetree_macroDialog',
            data: function () {
                return {
                    fileTree: tree,
                    fileContent: "<div>Please select file from tree</div>",
                    singleFile: false,
                    currentFile: undefined
                }
            },
            components: {
                "git4c-filetree": Git4CFileTree.getComponent(Bus),
                "overlay": Git4COverlay.getLoaderAlone(Bus)
            },
            methods: {
                hideDialog: function () {
                    AJS.dialog2(dialogInstance).hide()
                },
                passFile: function () {
                    if (this.currentFile) {
                        hide(this.currentFile.toString())
                    } else {
                        hide(undefined)
                    }
                }
            },
            mounted: function () {

                Bus.$on('selectedFile', (file) => {

                    this.currentFile = file
                    this.singleFile = false
                    this.fileContent = ""

                    // console.log("asd")

                    let promise

                    if (originalData.existingRepositoryUuid && !originalData.isPredefined) {
                        const uuid = originalData.existingRepositoryUuid

                        const toSend = {
                            branch: originalData.branch,
                            file: originalData.file
                        };

                        promise = this.$http.post(restUrl + "/repository/" + uuid + "/file", toSend, [])
                    } else if (originalData.isPredefined) {

                        const uuid = originalData.repository.uuid

                        const toSend = {
                            branch: originalData.branch,
                            file: file
                        };

                        promise = this.$http.post(restUrl + "/predefine/" + uuid + "/file", toSend, {})

                    } else {

                        const credentials = originalData.getCredentials()

                        const toSend = {
                            sourceRepositoryUrl: originalData.repository.sourceRepositoryUrl,
                            credentials: credentials,
                            branch: originalData.branch,
                            file: file
                        };

                        promise = this.$http.post(restUrl + "/repository/file", toSend, {})

                    }

                    promise.then(response => {
                        this.fileContent = response.body.content
                        this.$nextTick(() => {
                            const vm = this
                            this.singleFile = false
                            $(this.$el).find("pre code.git4c-highlightjs-code").each(function (i, block) {
                                hljs.highlightBlock(block);
                            });
                            $(this.$el).find("code.git4c-prismjs-code").each(function (i, block) {
                                vm.singleFile = true
                                Prism.highlightElement(block)
                            });
                            this.$nextTick(function () {

                                $(this.$root.$el).find("pre.line-numbers").css({"max-height": maxCodeHeight})

                                const tree = this.$refs.fileTree
                                const pre = $(this.$root.$el).find("pre.line-numbers")[0]

                                console.log("Pre", pre)

                                if (pre) {
                                    const preHeight = $(pre).height()
                                    const destHeight = tree.clientHeight - 36

                                    console.log("Pre height", preHeight)
                                    console.log("Dest height", destHeight)

                                    if (preHeight < destHeight) {
                                        $(pre).height(destHeight)
                                    }
                                }
                            })
                        })
                    })
                })

                Bus.$on("WindowResized", () =>{
                    $(this.$root.$el).find("pre.line-numbers").css({"max-height": maxCodeHeight})
                })

                resizeFun()

                window.addEventListener('resize', resizeFun)

            },
            destroyed: function () {
                window.removeEventListener("resize", resizeFun)
            }
        })
    }

})(AJS.$);
