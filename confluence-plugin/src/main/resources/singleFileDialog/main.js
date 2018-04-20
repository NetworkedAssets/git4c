(function ($) {


    /**
     * @param {string} id
     * @param {string[]} serverError
     * @param {string} text
     */
    const Error = function (id, serverError, text) {
        return {
            id: id,
            serverError: serverError,
            text: text
        }
    }

    const serverError = Error("server_error", ["SERVER_ERROR"], "<p>Server error</p>")

    const errors = [
        serverError,
        Error("last_repository_removed", ["REPOSITORY_REMOVED"], "<p>Previously selected repository has been deleted by the Administrator.</p>"),
        Error("repository_removed", ["REMOVED"], "<p>Selected repository has been deleted by the Administrator.</p>"),
        Error("invalid_url", ["SOURCE_NOT_FOUND", "WRONG_URL"], "<p>Invalid url</p>"),
        Error("invalid_branch", ["WRONG_BRANCH"], "<p>Invalid branch name</p>"),
        Error("wrong_credentials", ["WRONG_CREDENTIALS"], "<p>Invalid credentials</p>"),
        Error("wrong_key_format", ["WRONG_KEY_FORMAT"], '<p>SSH key should have following format:</p>'+
            '<pre>-----BEGIN RSA PRIVATE KEY-----'+
            '....'+
            '-----END RSA PRIVATE KEY-----</pre>'),
        Error("captcha_required", ["CAPTCHA_REQUIRED"], '<p>Your Git repository account has been locked. To unlock it and log in again you must solve a CAPTCHA. This is typically caused by too many attempts to login with an incorrect password. The account lock prevents your SCM client from accessing repository and its mirrors until it is solved, even if you enter your password correctly</p>'+
            '<p>If you are currently logged in to Git repository via a browser you may need to logout and then log back in in order to solve the CAPTCHA.</p>'),
    ]

    const html =
        '<section role="dialog" id="singlefiledoc_macroDialog" class="aui-layer aui-dialog2 aui-dialog2-xlarge" aria-hidden="true">'+
        '   <header class="aui-dialog2-header">'+
        '      <h2 class="aui-dialog2-header-main">Git4C macro parameters</h2>'+
        '      <a class="aui-dialog2-header-close" id="git4c_single_file_dialog_header_close" v-on:click="closeDialog()">'+
        '      <span class="aui-icon aui-icon-small aui-iconfont-close-dialog">Close</span>'+
        '      </a>'+
        '   </header>'+
        '   <div class="aui-dialog2-content" style="margin: 0; padding: 0; min-height: 60%; height:1px; display: flex; flex-direction: column; overflow-x: hidden;">'+
        '      <div>'+
        '          <div v-for="error in errors" >'+
        '             <div v-for="serverError in error.serverError" class="aui-message aui-message-error" v-show="serverError == currentError">'+
        '                <p class="title">'+
        '                   <strong>Error!</strong>'+
        '                </p>'+
        '                <p v-html="error.text">'+
        '                </p>'+
        '             </div>'+
        '          </div>'+
        '      </div>'+
        '      <div style="margin: 0; padding: 0; display: flex; flex-direction: row; height: 100%;">'+
        '          <form class="aui">'+
        '             <div id="git4c-single-file-dialog-content" style="overflow: auto; width: 400px; margin: 10px;">'+
        '               <div class="aui-group">'+
        '                 <div class="aui-item">'+
        '                    <div class="field-group">'+
        '                        <label for="doc_macro-predefined_repository">Repository</label>'+
        '                        <select class="select" v-show="((repositories && repositories.length!=0) || customRepository ||(recentlyUsedRepositories && recentlyUsedRepositories.length!=0)) && !loadingRepositories" v-model="repository"  style="max-width: 81%">'+
        '                           <optgroup v-if="customRepository" label="Chosen repository">'+
        '                              <option v-bind:value="customRepository">'+
        '                                 {{ customRepository.repositoryName }}'+
        '                              </option>'+
        '                           </optgroup>'+
        '                           <optgroup v-show="!forcedPredefined" label="Recently used repositories">'+
        '                               <option  v-for="repo in recentlyUsedRepositories" v-bind:value="repo">'+
        '                                 {{ repo.repositoryName }}'+
        '                               </option>'+
        '                           </optgroup>'+
        '                           <optgroup label="Predefined repositories">'+
        '                               <option  v-for="repo in repositories" v-bind:value="repo">'+
        '                                 {{ repo.name }}'+
        '                               </option>'+
        '                           </optgroup>'+
        '                        </select>'+
        '                        <select class="select" v-show="((!repositories || repositories.length==0) && !customRepository && !(recentlyUsedRepositories && recentlyUsedRepositories.length!=0)) && !loadingRepositories" :disabled="true"  style="max-width: 64%">'+
        '                            <option>No Repositories Available</option>'+
        '                        </select>'+
        '                        <select class="select" v-show="loadingRepositories" :disabled="true"  style="width: 81%">'+
        '                            <option>Loading repositories</option>'+
        '                        </select>'+
        '                        <button v-bind:disabled="forcedPredefined || loadingRepositories" ref="custom_repository_button" @click.prevent id="git4c-single_file_dialog-add_repository-button" v-on:click="openCustomRepositoryDialog" class="aui-button aui-button-primary">'+
        '                            <span class="aui-icon aui-icon-small aui-iconfont-add"/>'+
        '                        </button>'+
        '                    </div>'+
        '                    <div class="field-group">'+
        '                       <label for="doc_macro-repo_branch">Repository branch</label>'+
        '                       <select class="select" v-show="downloadingBranches == true" :disabled="true">'+
        '                          <option>Downloading branches</option>'+
        '                       </select>'+
        '                       <select class="select" v-show="!branches && downloadingBranches == false" :disabled="true">'+
        '                          <option>No Branches Available</option>'+
        '                       </select>'+
        '                       <span v-show="downloadingBranches == false && branches">'+
        '                           <git4cselect2single ref="git4c_branch_select" class="select" v-model="branch">'+
        '                              <option v-for="br in branches">{{ br }}</option>'+
        '                           </git4cselect2single>'+
        '                       </span>'+
        '                    </div>'+
        '                    <div class="field-group git4c-file-select-container">'+
        '                       <label for="doc_macro-repo_file">File</label>'+
        '                       <span>'+
        '                           <input v-show="downloadingFiles" placeholder="Downloading files" style="max-width: 81%" class="text" type="text" disabled></input>'+
        '                           <input v-show="!downloadingFiles" style="max-width: 81%" v-model="file" class="text" type="text" disabled id="git4c_singlefiledialog_select_file"></input>'+
        '                       </span>'+
        '                       '+
        '                       <span>'+
        '                           <button v-on:click="showFileTree()" @click.prevent href="javascript:void(0)" v-bind:disabled="downloadingFiles || !fileTree" class="aui-button aui-button-primary"><span class="aui-icon aui-icon-small aui-iconfont-nav-children-large">Show file tree</span></button>'+
        '                       </span>'+
        '                    </div>'+
        '                    <div class="field-group" >'+
        '                       <label>Show top bar by default</label>'+
        '                       <div class="checkbox">'+
        '                          <input class="checkbox" type="checkbox" v-model="showTopBar">'+
        '                       </div>      '+
        '                    </div>'+
        '                    <div class="field-group" v-show="hasToc">'+
        '                       <label>Table of contents</label>'+
        '                       <div class="checkbox">'+
        '                          <input id="git4c_singlefiledialog_checkbox_toc" class="checkbox" type="checkbox" v-model="toc">'+
        '                       </div>      '+
        '                    </div>'+
        '                    <div class="field-group">'+
        '                       <label>Collapsible document</label>'+
        '                       <div class="checkbox">'+
        '                          <input class="checkbox" type="checkbox" v-model="collapsible">'+
        '                       </div>      '+
        '                    </div>'+
        '                    <div class="field-group" v-if="showTopBar && collapsible">'+
        '                       <label>Collapse by default</label>'+
        '                       <div class="checkbox">'+
        '                          <input class="checkbox" type="checkbox" v-model="collapseByDefault">'+
        '                       </div>      '+
        '                    </div>'+
        '                    <div class="field-group" v-if="fileContent && haveLineNumbers">'+
        '                       <label>Show line numbers</label>'+
        '                       <div class="checkbox">'+
        '                          <input :disabled="downloadingFile" class="checkbox" type="checkbox" v-model="showLineNumbers">'+
        '                       </div>      '+
        '                    </div>'+
        '                    <div id="show_type_group" class="field-group" v-if="fileContent && (hasMethods || haveLineNumbers)">'+
        '                       <label>Show</label>'+
        '                       <select class="select" v-model="showType">'+
        '                            <option value="all">All lines</option>'+
        '                            <option value="lines" v-if="haveLineNumbers">Lines Range</option>'+
        '                            <option value="method" v-if="hasMethods">Methods</option>'+
        '                        </select> '+
        '                    </div>'+
        '                    <div class="field-group" v-if="hasMethods && showType === \'method\'">'+
        '                          <label for="doc_macro-repo_branch">Methods in file</label>'+
        '                       <select class="select" v-show="downloadingMethods == true" :disabled="true">'+
        '                          <option>Downloading methods</option>'+
        '                       </select>'+
        '                       <select class="select" v-show="downloadingMethods == false" v-model="method">'+
        '                       <option>All</option>'+
        '                          <option v-for="method in methods">{{ method.name }}</option>'+
        '                       </select>      '+
        '                    </div>'+
        '                    <div class="field-group" v-show="showType === \'lines\' && haveLineNumbers && numberOfLines !== 1">'+
        '                        <label>Line number selection</label>'+
        '                        <div style="max-width: 250px; margin-right: 20px; margin-left: 17px">'+
        '                            <git4cnouislider :number-of-lines="numberOfLines" ref="line_slider" v-model="lines"></git4cnouislider>'+
        '                        </div> '+
        '                    </div>'+
        '                    '+
        '                 </div>'+
        '               </div>'+
        '             </div>'+
        '          </form>'+
        '          <div class="aui-item" id="dialog-code-content" style="display: flex; flex-direction: column; overflow: auto; width: 560px; border-left: 1px solid #ccc;">'+
        '                     <div style="color: gray; margin: 10px">File preview (will show after macro is loaded)</div>'+
        '                     <hr style="margin: 0;" />'+
        '                     <filepreview id="git4c-single-dialog-code-holder" :show-line-numbers="showLineNumbers" :show-toc="hasToc && toc" :toc="tocObject" v-show="fileContent" :content="fileContent"></filepreview>'+
        '                     <div v-show="!fileContent" style="position: relative; top: 50%; ">'+
        '                        <overlay v-if="file && files"></overlay>'+
        '                     </div>'+
        '          </div>'+
        '      </div>'+
        '   </div>'+
        '   <footer class="aui-dialog2-footer">'+
        '      <div class="aui-dialog2-footer-actions">'+
        '         <button id="dialog-save-button" v-on:click="save()" class="aui-button aui-button-primary" v-bind:disabled="!isFilled" v-show="saving == false">Save</button>'+
        '         <button id="dialog-close-button" class="aui-button aui-button-primary" disabled=true v-show="saving == true">Saving...</button>'+
        '      </div>'+
        '   </footer>'+
        '   <custom-repository-dialog ref="custom_repository_dialog"></custom-repository-dialog>'+
        '</section>'



    var dialogInstance
    var restUrl;

    AJS.toInit(function () {
        dialogInstance = $(html).appendTo("body");
        const baseUrl = AJS.params.baseUrl;
        restUrl = baseUrl + "/rest/doc/1.0/documentation";
    });

    //@ts-ignore
    AJS.MacroBrowser.setMacroJsOverride("Git4C Single File", {
        "opener": function (macro) {

            //Default values
            const dataModel = {
                customRepository: undefined,
                customRepositoryAuthType: undefined,
                customRepositoryName: undefined,
                customRepositoryUrl: undefined,

                prevBranch: undefined,
                prevFile: undefined,
                prevMethod: undefined,

                repository: undefined,

                recentlyUsedRepositories: undefined,
                predefinedRepositoryUuid: undefined,

                branch: undefined,
                branches: undefined,
                downloadingBranches: false,
                branchesReq: undefined,

                files: undefined,
                file: undefined,
                fileReq: undefined,
                downloadingFiles: false,
                filesReq: undefined,
                downloadingFile: false,

                methods: undefined,
                method: undefined,
                hasMethods: undefined,
                downloadingMethods: false,
                methodsReq: undefined,
                originalContent: undefined,

                singleFile: undefined,

                showType: "all",
                prevShowType: undefined,

                forcedPredefined: true,

                hasToc: false,
                toc: true,
                tocObject: undefined,

                currentError: undefined,
                fileTree: undefined,
                fileContent: undefined,
                // currentError: "SOURCE_NOT_FOUND",
                errors: errors,
                repositories: undefined,
                saving: false,
                isFilled: false,
                showLineNumbers: true,
                haveLineNumbers: false,
                collapsible: true,
                showTopBar: true,
                collapseByDefault: false,

                loadingRepositories: true,

                selectLineNumbers: undefined,
                numberOfLines: 100,
                lines: undefined,
                startLine: undefined,
                endLine: undefined
            }

            const data = $.extend(dataModel, macro.params);

            //Needs testing
            // data.isPredefined = (data.isPredefined === "true")

            data.collapsible = (typeof(data.collapsible) === "boolean") ? data.collapsible : (data.collapsible === "true")
            data.showTopBar = (typeof(data.showTopBar) === "boolean") ? data.showTopBar : (data.showTopBar === "true")
            data.showLineNumbers = (typeof(data.showLineNumbers) === "boolean") ? data.showLineNumbers : (data.showLineNumbers === "true")
            data.collapseByDefault = (typeof(data.collapseByDefault) === "boolean") ? data.collapseByDefault : (data.collapseByDefault === "true")

            if (!data.showTopBar) {
                data.collapseByDefault = false
            }

            // AJS.dialog2(dialogInstance).on("show", function () {
            //     console.log("Dialog shown")
            //     var closeButton = dialogInstance.find("#dialog-close-button")[0]
            //     $("#dialog-close-button").click(function(e) {
            //         e.preventDefault()
            //         //Clean data
            //     })
            // })

            const hide = function(uuid) {
                console.log("Closing")

                data.forcedPredefined = undefined
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
                data.downloadingFile = undefined,
                data.lines = undefined,
                data.fileReq = undefined
                data.singleFile = undefined
                data.numberOfLines = undefined
                data.showType = undefined
                data.hasToc = undefined
                data.tocObject = undefined
                data.recentlyUsedRepositories = undefined
                data.loadingRepositories = undefined


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
                    toc: Git4CToc.getComponent(),
                    customRepositoryDialog: Git4CCustomRepositoryDialog.getComponent(Bus, true),
                    git4cselect2single: Git4CSelect2Single.getComponent(),
                    overlay: Git4COverlay.getLoaderAlone(Bus),
                    filepreview: Git4CFilePreview.getComponent()
                },
                watch: {
                    showTopBar: function () {
                        if(!this.showTopBar) {
                            this.collapseByDefault = false
                        }
                    },
                    repository: function () {
                        if(this.isRecentlyUsed(this.repository)) {
                            this.customRepository = {
                                uuid: this.repository.uuid,
                                repositoryName: this.repository.repositoryName
                            }
                        }
                        this.$nextTick(function () {
                            this.getBranches()
                        })
                    },
                    branch: function () {
                        this.fileTree = undefined
                        if(this.branch) {
                            this.prevBranch = this.branch
                        }
                        this.getFiles()
                        this.isFilled = this.checkIfFilled()
                    },
                    file: function () {
                        $(this.$el).find("#git4c-single-dialog-code-holder").css("height","")
                        $(this.$el).find("#git4c-single-dialog-code-holder").css("background-color","")
                        if(this.file) {
                            this.prevFile = this.file
                        }
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
                        this.$nextTick(function () {
                            this.resizeCodeDiv()
                        })
                    },
                    lines: function () {
                      this.handleLinesChange();
                    },
                    showType: function () {

                        //Remove highlight
                        this.removeHighlight()

                        //Reset content
                        this.fileContent = ""
                        this.fileContent = this.originalContent


                        if(this.showType == "method"){
                            this.handleMethodChange()
                        }
                        if(this.showType == "lines"){
                            this.handleLinesChange()
                        }
                    }
                },
                methods: {
                    isRecentlyUsed: function (repo) {
                        var includes = false
                        if(this.recentlyUsedRepositories) {
                            this.recentlyUsedRepositories.forEach(function (it) {
                                if(it.repositoryName === repo.repositoryName && it.uuid === repo.uuid)
                                {
                                    includes = true
                                }
                            })
                        }

                        return includes
                    },
                    removeHighlight: function () {
                        $(this.$el).find("div.git4c-prismjs-div pre").each(function (i, block) {
                            const el = $(block)

                            $(el).find("div.line-highlight").each(function (i, block) {
                                block.remove()
                            })

                        })
                    },
                    handleLinesChange: function () {
                        this.$nextTick(function () {

                            if(this.showType != "lines"){
                                this.removeHighlight()
                                return
                            }

                            var lines = this.lines
                            if(!lines) {
                                lines = [0, this.fileContent.split("\n").length - 6]
                            }
                            $(this.$el).find("div.git4c-prismjs-div pre").each(function (i, block) {
                                const el = $(block)

                                //TODO: Waiting for UI
                                el.attr("data-line", lines[0] + "-" + lines[1])

                                $(el).find("code.git4c-prismjs-code").each(function (i, block) {
                                    Prism.highlightElement(block)
                                });
                            })

                            this.$nextTick(function () {
                                this.resizeCodeDiv();
                            })
                        })
                    },
                    handleFileContentChange: function () {
                        const vm = this
                        this.$nextTick(function () {
                            if(this.fileContent) {
                                vm.singleFile = false

                                this.numberOfLines = this.fileContent.split("\n").length - 6

                                $(this.$el).find("pre code.git4c-highlightjs-code").each(function (i, block) {
                                    hljs.highlightBlock(block);
                                });

                                $(this.$el).find("code.git4c-prismjs-code").each(function (i, block) {
                                    $(vm.$el).find("#git4c-single-dialog-code-holder").css("height","100%")
                                    $(vm.$el).find("#git4c-single-dialog-code-holder").css("background-color","#f5f2f0")
                                    vm.singleFile = true
                                    Prism.highlightElement(block)
                                });

                                this.$nextTick(function () {
                                    this.resizeCodeDiv();
                                })
                            }
                        })
                    },
                    resizeCodeDiv: function () {
                        $(this.$el).find("#git4c-single-dialog-code-holder").css({width: ''})
                        $(this.$el).find("pre.line-numbers").css({'margin-right': ''})

                        var trueWidth = 0
                        try {
                            trueWidth = $(this.$el).find("pre.line-numbers").get()[0].scrollWidth
                        } catch (err) {
                            trueWidth = 0
                        }

                        // console.log("Width", trueWidth)
                        if (trueWidth) {
                            $(this.$el).find("#git4c-single-dialog-code-holder").css({width: trueWidth + 40})
                        }
                    },
                    handleMethodChange: function () {
                        const vm = this
                        if(this.method) {
                            this.prevMethod = this.method
                        }
                        if(this.method === "All") {
                            this.fileContent = this.originalContent
                        } else {
                            this.fileContent = this.methods.find( function(m) { return m.name === vm.method} ).content
                        }

                    },
                    getBranches: function () {

                        this.tocObject = undefined
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

                            if (vm.branchesReq) {
                                vm.branchesReq.stop()
                            }

                            vm.downloadingBranches = true
                            if(vm.currentError !== "REPOSITORY_REMOVED") {
                                vm.currentError = undefined
                            }
                            vm.files = undefined
                            vm.file = undefined
                            vm.branch = undefined

                            var promise = undefined

                            if (this.customRepository && this.customRepository.uuid && !this.isPredefined()) {
                                const uuid = this.customRepository.uuid
                                promise = Git4CApi.getBranches.forCustomRepository(uuid)
                            } else {
                                if (vm.isPredefined()) {
                                    const uuid = this.repository.uuid
                                    promise = Git4CApi.getBranches.forPredefinedRepository(uuid)
                                } else {

                                    const credentials = vm.getCredentials()

                                    const repositoryUrl = this.repository.sourceRepositoryUrl

                                    if(repositoryUrl) {
                                        promise = Git4CApi.getBranches.forRepository(repositoryUrl, credentials)
                                    }
                                }
                            }

                            vm.branchesReq = promise

                            if(promise) {
                                promise
                                    .then(function(response) {
                                        const branches = response.allBranches
                                        vm.branches = branches

                                        if (branches.length == 0){
                                            vm.branches = undefined
                                            vm.downloadingBranches = false
                                            return
                                        }

                                        const masterId = branches.indexOf("master")
                                        const developId = branches.indexOf("develop")
                                        const prevBranchId = branches.indexOf(vm.prevBranch)
                                        if(prevBranchId !== -1){
                                            vm.branch = branches[prevBranchId]
                                        } else {
                                            if (masterId !== -1) {
                                                vm.branch = branches[masterId]
                                            } else if (developId !== -1) {
                                                vm.branch = branches[developId]
                                            } else {
                                                vm.branch = branches[0]
                                            }
                                            vm.prevBranch = vm.branch
                                        }
                                        vm.downloadingBranches = false
                                    })
                                    .catch(function(error) {
                                        error.text().then(function(text) {
                                            vm.showError(text)
                                            vm.currentError = text
                                            vm.downloadingBranches = false
                                        })
                                    })
                            }
                        },
                        200
                    ),
                    getFiles: function () {

                        this.tocObject = undefined

                        if (!this.branch) {
                            return
                        }

                        this.fileContent = undefined
                        if (this.downloadingBranches) {
                            return
                        }

                        this.getFilesDebounce()

                    },
                    getFilesDebounce: _.debounce(
                        function () {

                            const vm = this

                            var promise = undefined

                            vm.downloadingFiles = true
                            if(vm.currentError !== "REPOSITORY_REMOVED") {
                                vm.currentError = undefined
                            }
                            if (this.customRepository && this.customRepository.uuid && !this.isPredefined()) {
                                const uuid = this.customRepository.uuid
                                const branch = vm.branch

                                promise = Git4CApi.getFilesTree.forCustomRepository(uuid, branch)

                            } else {
                                if (this.isPredefined()) {

                                    const uuid = this.repository.uuid
                                    const branch = vm.branch

                                    promise = Git4CApi.getFilesTree.forPredefinedRepository(uuid, branch)
                                } else {

                                    const credentials = vm.getCredentials()

                                    const branch = vm.branch
                                    const repositoryUrl = this.repository.sourceRepositoryUrl

                                    if (repositoryUrl) {
                                        promise = Git4CApi.getFilesTree.forRepository(repositoryUrl, credentials, branch)
                                    }
                                }
                            }

                            vm.filesReq = promise

                            if(promise) {
                                promise.then(function(response) {
                                    vm.files = response.files
                                    vm.fileTree = response.tree
                                    const prevFileId = response.files.indexOf(vm.prevFile)

                                    if(prevFileId !== -1){
                                        vm.file = response.files[prevFileId]
                                    }else {
                                        vm.file = response.files[0]
                                    }

                                    vm.downloadingFiles = false
                                }, function(error) {
                                    error.text().then(function(text) {
                                        vm.showError(text)
                                        vm.downloadingFiles = false
                                    })
                                })
                            }
                        },
                        200
                    ),
                    getFile: function () {

                        this.tocObject = undefined

                        const vm = this

                        $(this.$el).find("#git4c-single-dialog-code-holder").css({width: '', 'margin-right': ''})
                        $(this.$el).find("pre.line-numbers").css({'margin-right': ''})

                        if (!this.file) {
                            return
                        }

                        this.showType = "all"

                        if(this.prevShowType){
                            this.showType = this.prevShowType
                            this.prevShowType = undefined
                        }

                        this.downloadingFile = true

                        this.$nextTick(function() {
                            this.haveLineNumbers = Git4CUtils.hasLines(this.file)
                        })

                        this.hasToc = false
                        this.hasMethods = false
                        this.haveLineNumbers = false

                        if (vm.fileReq) {
                            vm.fileReq.stop()
                        }

                        const before = {
                            before: function(request) {

                                if (vm.fileReq) {
                                    vm.fileReq.abort()
                                }

                                vm.fileReq = request
                            }
                        }

                        var promise;
                        if (this.customRepository && this.customRepository.uuid && !this.isPredefined()) {
                            const uuid = this.customRepository.uuid

                            const branch = this.branch
                            const file = this.file

                            promise = Git4CApi.getFile.forCustomRepository(uuid, branch, file)

                        } else {
                            if (this.isPredefined()) {

                                const uuid = this.repository.uuid

                                const branch = this.branch
                                const file = this.file

                                promise = Git4CApi.getFile.forPredefinedRepository(uuid, branch, file)

                            } else {

                                const credentials = this.getCredentials()

                                const branch = this.branch
                                const file = this.file

                                const repositoryUrl = this.repository.sourceRepositoryUrl

                                if (repositoryUrl){
                                    promise = Git4CApi.getFile.forRepository(repositoryUrl, credentials, branch, file)
                                }
                            }
                        }
                        this.fileContent = undefined

                        this.fileReq = promise

                        if(promise) {
                            promise.then(function(response) {
                                vm.downloadingFile = false
                                const content = response.content
                                vm.originalContent = content
                                if (content) {
                                    vm.fileContent = content
                                    vm.tocObject = response.tableOfContents
                                    vm.numberOfLines = content.split("\n").length - 6
                                    vm.$nextTick(function () {
                                        if(vm.startLine && vm.endLine){
                                            vm.$refs.line_slider.updateSlider(vm.startLine, vm.endLine)

                                            vm.startLine = undefined
                                            vm.endLine = undefined
                                    }
                                    })
                                } else {
                                    vm.fileContent = "<div>Cannot open file</div>"
                                }
                                if (vm.file.endsWith("feature") || vm.file.endsWith("java")) {
                                    vm.hasMethods = true
                                    vm.getMethods()
                                }
                                if (vm.file.endsWith("md") || vm.file.endsWith("adoc")) {
                                    vm.hasToc = true
                                } else {
                                    vm.hasToc = false
                                }
                            }, function(error) {
                                if (error.status === 0) {
                                    return
                                }
                                error.text().then(function(text) {
                                    vm.showError(text)
                                })
                            })
                        }
                    },
                    getMethods: function () {
                        const vm = this

                        if (!this.file) {
                            return
                        }

                        this.downloadingMethods = true

                        var promise
                        if (this.customRepository && this.customRepository.uuid && !this.isPredefined()) {
                            const uuid = this.customRepository.uuid

                            const branch = this.branch
                            const file = this.file

                            promise = Git4CApi.getMethods.forCustomRepository(uuid, branch, file)

                        } else {
                            if (this.isPredefined()) {

                                const uuid = this.repository.uuid

                                const branch = this.branch
                                const file = this.file

                                promise = Git4CApi.getMethods.forPredefinedRepository(uuid, branch, file)

                            } else {

                                const credentials = this.getCredentials()

                                const branch = this.branch
                                const file = this.file

                                const repositoryUrl = this.repository.sourceRepositoryUrl

                                if (repositoryUrl) {
                                    promise = Git4CApi.getMethods.forRepository(repositoryUrl, credentials, branch, file)
                                }
                            }
                        }
                        if (promise) {
                            promise.then(function (response) {
                                const methods = response.methods
                                vm.methods = methods
                                const prevMethodId = methods.indexOf(vm.prevMethod)
                                if (prevMethodId !== -1) {
                                    vm.method = vm.methods[prevMethodId]
                                }
                                else {
                                    vm.method = "All"
                                }
                                vm.downloadingMethods = false
                            }, function (error) {
                                if (error.status === 0) {
                                    return
                                }
                                error.text().then(function (text) {
                                    vm.showError(text)
                                })
                            })
                        }

                    },
                    showError: function (text) {
                        const errors = _.flatMap(this.errors, function(e) { return e.serverError })
                        if (_.includes(errors, text)) {
                            this.currentError = text
                        } else {
                            this.currentError = "SERVER_ERROR"
                        }

                    },
                    save: function () {

                        const vm = this

                        this.saving = true
                        const credentials = this.getCredentials()

                        const method = this.method

                        var extractor

                        if (this.showType === "lines" ) {
                            this.startLine = this.lines[0]
                            this.endLine = this.lines[1]
                            this.prevShowType = "lines"
                            extractor = this.haveLineNumbers ? {
                                type: "LINENUMBERS",
                                start: Math.round(this.lines[0]),
                                end: Math.round(this.lines[1]),
                            } : undefined

                        } else if(this.showType === "method") {
                            this.prevShowType = "method"
                            extractor = this.hasMethods ? {
                                type: "METHOD",
                                method: method
                            } : undefined
                        }

                        if(this.isPredefined()){
                            this.customRepository = undefined
                            this.customRepositoryUrl = undefined
                            this.customRepositoryAuthType = undefined
                            this.customRepositoryName = undefined
                            this.predefinedRepositoryUuid = this.repository.uuid;
                        } else {
                            this.predefinedRepositoryUuid = undefined
                        }

                        var toSend = undefined

                        if(this.customRepository && this.customRepository.uuid){
                            vm.customRepositoryName = this.repository.repositoryName
                            toSend = {
                                repositoryDetails: {
                                    repository: {
                                        type: "EXISTING",
                                        uuid: this.customRepository.uuid
                                    }
                                },
                                repositoryName: this.repository.repositoryName,
                                branch: this.branch,
                                // file: this.file
                                glob: [this.file],
                                defaultDocItem: "",
                                extractor: extractor
                            };
                        } else {
                            if (!this.isPredefined()) {
                                toSend = {
                                    repositoryDetails: {
                                        repository: {
                                            type: "CUSTOM",
                                            url: this.repository.sourceRepositoryUrl,
                                            credentials: credentials
                                        }
                                    },
                                    repositoryName: this.repository.repositoryName,
                                    branch: this.branch,
                                    // file: this.file
                                    glob: [this.file],
                                    defaultDocItem: "",
                                    extractor: extractor
                                };
                            }
                            else {
                                const uuid = this.repository.uuid
                                toSend = {
                                    repositoryDetails: {
                                        repository: {
                                            type: "PREDEFINED",
                                            uuid: uuid
                                        }
                                    },
                                    repositoryName: this.repositories.filter( function(it) { return (it.uuid === vm.repository.uuid) } )[0].name,
                                    branch: this.branch,
                                    // file: this.file
                                    glob: [this.file],
                                    defaultDocItem: "",
                                    extractor: extractor
                                }
                                this.clearCustomFields()
                            }
                        }


                        Git4CApi.createMacro(toSend)
                            .then(function(response) {
                                const uuid = response.uuid
                                hide(uuid)
                                vm.saving = false
                            })
                            .catch(function(error) {
                                if (error.status === 0) {
                                    return
                                }
                                vm.saving = false
                                error.text().then(function(text) {
                                    vm.showError(text)
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
                    setChosenFile: function (file) {
                        if (file) {
                            this.file = file
                        }
                    },
                    showFileTree: function () {

                        // const f = this.setChosenFile()
                        //
                        createFileTreeDialog(this.fileTree, this.setChosenFile, this.toc, this)

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

                        const vm = this

                        const credentials = repository.credentials
                        const repositoryUrl = repository.sourceRepositoryUrl

                        Git4CApi.verifyRepository(repositoryUrl, credentials)
                            .then(function(data) {
                                if (data.ok == false) {
                                    throw Error("",[""],data.status);
                                }
                                vm.postProcessCustomRepository(repository)
                            })
                            .catch(function(error) {
                                error.text().then(function(text) {
                                        vm.$refs.custom_repository_dialog.saving = false
                                        vm.$refs.custom_repository_dialog.showError(text)
                                    }
                                )
                            })

                    },
                    postProcessCustomRepository: function (repository) {
                        this.$refs.custom_repository_dialog.saving = false
                        this.closeCustomRepositoryDialog()
                        this.customRepository = repository
                        this.repository = this.customRepository
                        this.customRepositoryAuthType = this.repository.credentials.sshKey ? "SSHKEY" : this.repository.credentials.username ? "USERNAMEPASSWORD" : "NOAUTH"
                        this.customRepositoryName = this.repository.repositoryName
                        this.customRepositoryUrl = this.repository.sourceRepositoryUrl
                    },
                    getRepositoryList: function () {
                        const vm = this;

                        var downloaded = 0

                        const registerDownload = function () {
                            downloaded++
                            if (downloaded === 2) {
                                vm.loadingRepositories = false
                            }
                        }

                        Git4CApi.getRepositoryUsages()
                            .then(function(usages) {
                                registerDownload()
                                vm.recentlyUsedRepositories = usages.reverse()
                            })
                            .catch(function(error) {
                                registerDownload()
                                error.text().then(function(text) {
                                    vm.showError(text)
                                })
                            })

                        Git4CApi.getPredefinedRepositories()
                            .then(function(repos) {
                                registerDownload()
                                vm.repositories = repos
                                vm.$nextTick(function() {
                                    if(this.predefinedRepositoryUuid)
                                    {
                                        var list = vm.repositories.filter( function(it) { return (it.uuid === vm.predefinedRepositoryUuid) } )
                                        if(list.length !== 0){
                                            vm.repository = list[0]
                                        }else{
                                            vm.predefinedRepositoryUuid = null
                                        }
                                    }
                                    if(!vm.repository && !vm.customRepositoryName) {
                                        vm.repository = repos[0]
                                    }
                                })
                            })
                            .catch(function(error) {
                                registerDownload()
                                error.text().then(function(text) {
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
                    isPredefined: function () {
                        return !this.customRepository ? true : (this.repository.uuid !== this.customRepository.uuid) &&  (this.repository.repositoryName !== this.customRepository.repositoryName)
                    },
                    clearCustomFields: function () {
                        this.customRepositoryUrl = undefined
                        this.customRepositoryAuthType = undefined
                        this.customRepositoryName = undefined
                    },
                    init: function(){
                        const vm = this

                        if(vm.customRepositoryName) {
                            vm.customRepository = {
                                repositoryName: vm.customRepositoryName
                            }
                            vm.repository = vm.customRepository
                        }

                        Git4CApi.getPredefinedRepositoriesForceSetting()
                            .then(function(response) {
                                if(response.forced === true) {
                                    vm.forcedPredefined = true
                                    vm.$refs.custom_repository_button.title = "Administrator blocked custom repositories"

                                }
                                else {
                                    vm.forcedPredefined = false
                                }

                            }, function(error) {
                                error.text().then(function(text) {
                                    vm.showError(text)
                                })
                            })

                        if(data.uuid){
                            if(vm.prevShowType){
                                vm.showType = vm.prevShowType
                            }

                            Git4CApi.getMacroInformation(data.uuid)
                                .then(function(response) {
                                    if(vm.customRepositoryName) {
                                        vm.customRepository = {
                                            uuid: response.repositoryUuid,
                                            repositoryName: vm.customRepositoryName
                                        }
                                        vm.repository = vm.customRepository
                                        vm.getBranches()
                                    }
                                }, function(error) {
                                    if(error.statusText == "Not Found")
                                    {
                                        vm.showError("REPOSITORY_REMOVED")
                                    }else {
                                        error.text().then(function(text) {
                                            vm.showError(text)
                                        })
                                    }
                                })
                            if(this.customRepositoryName) {
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
                    const vm = this
                    this.init()
                    this.getRepositoryList()
                    Bus.$on("closeCustomRepositoryDialog", function() {vm.closeCustomRepositoryDialog()})
                    Bus.$on("repositoryDefined", function(repository) {vm.processRepository(repository)})
                }
            })
        }
    });

    const fileTreeDialog =
'           <section role="dialog" id="singlefiledoc_filetree_macroDialog" class="aui-layer aui-dialog2 aui-dialog2-medium" aria-hidden="true">'+
'               <header class="aui-dialog2-header">'+
'                  <h2 class="aui-dialog2-header-main">Select file</h2>'+
'                  <a class="aui-dialog2-header-close" v-on:click="hideDialog()">'+
'                  <span class="aui-icon aui-icon-small aui-iconfont-close-dialog">Close</span>'+
'                  </a>'+
'               </header>'+
'               <div class="aui-dialog2-content" style="height: 80%; margin: 0; padding: 0; display: flex;"> '+
'                   <input style="height: 20px; width: 33%; position: absolute; margin: 10px;"  placeholder="search..." class="text" v-model="query" type="text" title="search">'+
'                   <div id="git4c-single-dialog-tree-div" style="width: 200px; padding: 20px; margin-top: 30px; overflow: auto;" ref="fileTree">'+
'                       <git4c-filetree ref="fileTreeRef" :data="fileTree"></git4c-filetree>'+
'                   </div>'+
'                   <div id="git4c-single-dialog-right-column" style="width: 400px; border-left: 1px solid #ccc; overflow: auto; display: flex; flex-direction: column">'+
'                       <div style="margin-left: 20px; margin-top: 8px; margin-bottom: 8px">File preview</div>'+
'                       <hr ref="previewbar" style="margin: 0" />'+
'                       <div v-if="fileContent" style="height: 100%;" v-bind:class="{ \'git4c-single-dialog-tree-code-holder-markdown \': !singleFile }">' +
'                           <preview id="git4c-single-dialog-tree-code-holder" :show-toc="toc" :toc="tocObject" :content="fileContent"></preview>'+
'                       </div>'+
'                       <overlay v-else></overlay>'+
'                    </div>'+
'               </div>'+
'               <footer class="aui-dialog2-footer">'+
'                    <!-- Actions to render on the right of the footer -->'+
'                    <div class="aui-dialog2-footer-actions">'+
'                        <button v-on:click="passFile()" id="dialog-next-button" class="aui-button">Select</button>'+
'                        <button v-on:click="hideDialog()" id="dialog-close-button" class="aui-button aui-button-link">Cancel</button>'+
'                    </div>'+
'               </footer>'+
'           </section>'

    const createFileTreeDialog = function (tree, callback, toc, originalData) {
        const dialogInstance = $(fileTreeDialog).appendTo("body");

        var vue

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

        vue = new Vue({
            el: '#singlefiledoc_filetree_macroDialog',
            data: function () {
                return {
                    fileTree: tree,
                    originalTree: tree,
                    fileContent: "<div>Please select file from tree</div>",
                    singleFile: false,
                    currentFile: undefined,
                    request: undefined,
                    tocObject: undefined,
                    toc: toc,
                    query: undefined
                }
            },
            watch: {
                query: function () {
                    const vm = this
                    const filteredTree = this.filterTree(this.originalTree, this.query)
                    this.fileTree = filteredTree === false ? undefined : filteredTree
                    if(this.fileTree) {
                        this.$nextTick(function () {
                            vm.$refs.fileTreeRef.closeTree()
                            if (vm.countElements(vm.fileTree) < 20) {
                                vm.$refs.fileTreeRef.openTree()
                            }
                        })
                    }
                }
            },
            components: {
                "toc": Git4CToc.getComponent(),
                "git4c-filetree": Git4CFileTree.getComponent(Bus),
                "overlay": Git4COverlay.getLoaderAlone(Bus),
                "preview": Git4CFilePreview.getComponent()
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
                },
                resizeCodeDiv: function () {
                    $(this.$el).find("#git4c-single-dialog-tree-code-holder").css({width: ''})

                    var trueWidth = 0
                    try {
                        trueWidth = $(this.$el).find("pre.line-numbers").get()[0].scrollWidth
                    } catch (err) {
                        trueWidth = 0
                    }
                    if (trueWidth) {
                        $(this.$el).find("#git4c-single-dialog-tree-code-holder").css({width: trueWidth + 40})
                        $(this.$refs.previewbar).css({width: trueWidth + 40})
                    }
                },
                filterTree: function (tree, needle) {
                    const vm = this
                    if (tree.fullName.includes(needle)){
                        //deep copy of item
                        return JSON.parse(JSON.stringify(tree))
                    }else {
                        var t = {fullName: tree.fullName, name: tree.name, type: tree.type, children: []}
                        tree.children.forEach(function (child) {
                            var c = vm.filterTree(child, needle)
                            if (c) t.children.push(c)
                        })
                        if (t.children.length !== 0) return t
                        return false
                    }
                },
                countElements: function (tree) {
                    const vm = this
                    if (tree.children.size > 0){
                        return 1
                    }
                    else{
                        var i = 0
                        tree.children.forEach(function (it) {
                            i += vm.countElements(it)
                        })
                        return i+1;
                    }
                }
            },


            mounted: function () {

                const vm = this

                Bus.$on('selectedFile', function(file) {

                    $(vm.$refs.previewbar).css({width: "100%"})
                    $(vm.$el).find("#git4c-single-dialog-tree-code-holder").css({width: "100%"})

                    vm.currentFile = file
                    vm.singleFile = false
                    vm.fileContent = ""

                    const before = {
                        before: function(req) {
                            if (vm.request) {
                                vm.request.abort()
                            }
                            vm.request = req
                        }
                    }

                    var promise

                    if (originalData.customRepository && originalData.customRepository.uuid && !originalData.isPredefined()) {
                        const uuid = originalData.customRepository.uuid

                        const branch = originalData.branch

                        promise = Git4CApi.getFile.forCustomRepository(uuid, branch, file)

                    } else if (originalData.isPredefined()) {

                        const uuid = originalData.repository.uuid

                        const branch = originalData.branch

                        promise = Git4CApi.getFile.forPredefinedRepository(uuid, branch, file)

                    } else {

                        const credentials = originalData.getCredentials()

                        const sourceRepositoryUrl = originalData.repository.sourceRepositoryUrl
                        const branch = originalData.branch

                        promise = Git4CApi.getFile.forRepository(sourceRepositoryUrl, credentials, branch, file)

                    }

                    promise.then(function(response) {

                        vm.fileContent = response.content
                        vm.tocObject = response.tableOfContents
                        vm.$nextTick(function() {

                            vm.singleFile = false
                            $(vm.$el).find("pre code.git4c-highlightjs-code").each(function (i, block) {
                                hljs.highlightBlock(block);
                            });

                            $(vm.$el).find("code.git4c-prismjs-code").each(function (i, block) {
                                vm.singleFile = true
                                $(vm.$el).find("#git4c-single-dialog-tree-code-holder").css("height","100%")
                                $(vm.$el).find("#git4c-single-dialog-tree-code-holder").css("background-color","#f5f2f0")
                                Prism.highlightElement(block)
                            });

                            vm.resizeCodeDiv()
                        })
                    })
                })

            }
        })
    }

})(AJS.$);
