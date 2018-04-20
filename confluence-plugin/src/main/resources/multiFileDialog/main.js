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
        Error("invalid_url", ["SOURCE_NOT_FOUND", "WRONG_URL"], "<p>Invalid url</p>"),
        Error("last_repository_removed", ["REPOSITORY_REMOVED"], "<p>Previously selected repository has been deleted by the Administrator.</p>"),
        Error("repository_removed", ["REMOVED"], "<p>Selected repository has been deleted by the Administrator.</p>"),
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
        '<section role="dialog" id="multifiledoc_macroDialog" class="aui-layer aui-dialog2 aui-dialog2-medium" aria-hidden="true">'+
        '   <header class="aui-dialog2-header">'+
        '      <h2 class="aui-dialog2-header-main">Git4C macro parameters</h2>'+
        '      <a class="aui-dialog2-header-close" v-on:click="closeDialog()">'+
        '      <span class="aui-icon aui-icon-small aui-iconfont-close-dialog">Close</span>'+
        '      </a>'+
        '   </header>'+
        '   <div class="aui-dialog2-content">'+
        '      <div v-for="error in errors" >'+
        '         <div v-for="serverError in error.serverError" class="aui-message aui-message-error" v-show="serverError == currentError">'+
        '            <p class="title">'+
        '               <strong>Error!</strong>'+
        '            </p>'+
        '            <p v-html="error.text">'+
        '            </p>'+
        '         </div>'+
        '      </div>'+
        '      <!--</div>-->'+
        '      <form class="aui">'+
        '         <div id="git4c-multi-file-dialog-content">'+
        '         <div class="aui-group">'+
        '         <div class="aui-item">'+
        '            <div class="field-group">'+
        '                <label for="doc_macro-predefined_repository">Repository</label>'+
        '                <select class="select" v-show="((repositories && repositories.length!=0) || customRepository || (recentlyUsedRepositories && recentlyUsedRepositories.length!=0)) && !loadingRepositories" v-model="repository"  style="max-width: 50%">'+
        '                   <optgroup v-if="customRepository" label="Chosen repository">'+
        '                      <option v-bind:value="customRepository">'+
        '                         {{ customRepository.repositoryName }}'+
        '                      </option>'+
        '                   </optgroup>'+
        '                   <optgroup v-show="!forcedPredefined" label="Recently used repositories">'+
        '                       <option  v-for="repo in recentlyUsedRepositories" v-bind:value="repo">'+
        '                         {{ repo.repositoryName }}'+
        '                       </option>'+
        '                   </optgroup>'+
        '                   <optgroup label="Predefined repositories">'+
        '                       <option  v-for="repo in repositories" v-bind:value="repo">'+
        '                         {{ repo.name }}'+
        '                       </option>'+
        '                   </optgroup>'+
        '                </select>'+
        '                <select class="select" v-show="((!repositories || repositories.length==0) && !customRepository && !(recentlyUsedRepositories && recentlyUsedRepositories.length!=0)) && !loadingRepositories" :disabled="true"  style="width: 50%">'+
        '                    <option>No Repositories Available</option>'+
        '                </select>'+
        '                <select class="select" v-show="loadingRepositories" :disabled="true"  style="width: 50%">'+
        '                     <option>Loading repositories</option>'+
        '                </select>'+
        '                <button v-bind:disabled="forcedPredefined || loadingRepositories" ref="custom_repository_button" id="git4c-multi_file_dialog-add_repository-button" @click.prevent v-on:click="openCustomRepositoryDialog" class="aui-button aui-button-primary">'+
        '                     <span class="aui-icon aui-icon-small aui-iconfont-add"/>'+
        '                </button>'+
        '            </div>'+
        '            <div class="field-group">'+
        '               <label for="doc_macro-repo_branch">Repository branch</label>'+
        '               <select class="select" v-show="downloadingBranches == true" :disabled="true">'+
        '                  <option>Downloading branches</option>'+
        '               </select>'+
        '               <select class="select" v-show="!branches && downloadingBranches == false" :disabled="true">'+
        '                  <option>No Branches Available</option>'+
        '               </select>'+
        '               <span v-show="downloadingBranches == false && branches">'+
        '                   <git4cselect2single class="select" v-model="branch">'+
        '                      <option v-for="br in branches">{{ br }}</option>'+
        '                   </git4cselect2single>'+
        '               </span>'+
        '            </div>'+
        '            <div class="field-group">'+
        '              <label for="doc_macro-repo_glob">Filter</label>'+
        '               <input class="text" type="text" ref="doc_macro-glob" placeholder="pattern"></input>'+
        '               <div class="description">Please enter your <a ref="pattern_tooltip" target="_blank" href="https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob">pattern</a> (Optional)</div>'+
        '            </div>'+
        '            <div class="field-group">'+
        '              <label for="doc_macro-repo_root_directory">Root Directory</label>'+
        '               <input style="max-width: 50%" v-model="rootDirectory" class="text" type="text" disabled ref="doc_macro-root_directory"></input>'+
        '                    <button v-bind:style="[downloadingFiles? {width: \'9%\'} : {}]" id="git4c-multi_file_dialog-select_root_dir_button" v-bind:disabled="downloadingFiles" @click.prevent="showFileTreeDialog()" class="aui-button aui-button-primary">'+
        '                        <span v-bind:style="[downloadingFiles? {margin: \'0\'} : {}]" v-bind:class="{\'aui-icon aui-icon-wait\': downloadingFiles, \'aui-iconfont-nav-children-large\': !downloadingFiles}" class="aui-icon aui-icon-small">Show file tree</span>'+
        '                    </button>'+
        '            </div>'+
        '            <div class="field-group">'+
        '               <label for="doc_macro-default_Doc_Item">Default File</label>'+
        '               <input v-model="defaultDocItem" class="text" type="text" id="doc_macro-default_Doc_Item" placeholder="Readme.md">'+
        '               <div class="description">Please enter location of default document (Optional)</div>'+
        '            </div>'+
        '         </div>'+
        '         </div>'+
        '         </div>'+
        '      </form>'+
        '   </div>'+
        '   <footer class="aui-dialog2-footer">'+
        '      <!-- Actions to render on the right of the footer -->'+
        '      <div class="aui-dialog2-footer-actions">'+
        '      '+
        '         <button id="dialog-close-button" v-on:click="save()" class="aui-button aui-button-primary" v-bind:disabled="!isFilled" v-show="saving == false">Save</button>'+
        '         <button id="dialog-close-button" class="aui-button aui-button-primary" disabled=true v-show="saving == true">Saving...</button>'+
        '      </div>'+
        '      <!-- Hint text is rendered on the left of the footer -->'+
        '      <!--<div class="aui-dialog2-footer-hint">this is a hint</div>-->'+
        '   </footer>'+
        '<div id="custom_repository_dialog-div">'+
        '    <custom-repository-dialog ref="custom_repository_dialog"></customrepositorydialog>'+
        '</div>'+
        '</section>'


    var dialogInstance
    var restUrl;

    AJS.toInit(function () {
        dialogInstance = $(html).appendTo("body");
        const baseUrl = AJS.params.baseUrl;
        restUrl = baseUrl + "/rest/doc/1.0/documentation";
    });

    //@ts-ignore
    AJS.MacroBrowser.setMacroJsOverride("Git4C", {
        "opener": function (macro) {

            //Default values
            var dataModel = {
                repositories: undefined,
                recentlyUsedRepositories: undefined,
                repository: undefined,

                customRepository: undefined,
                customRepositoryAuthType: undefined,
                customRepositoryName: undefined,
                customRepositoryUrl: undefined,

                prevBranch: undefined,

                existingRepositoryUuid: undefined,

                predefinedRepositoryUuid: undefined,

                branch: undefined,
                branches: undefined,
                downloadingBranches: false,

                fileTree: undefined,
                customRoot: undefined,

                glob: undefined,
                globList: Array(),
                defaultDocItem: "",

                rootDirectory: "",

                forcedPredefined: true,

                downloadingFiles: false,

                currentError: undefined,
                errors: errors,

                loadingRepositories: true,

                saving: false,
                isFilled: false,

                branchDownloadPromise: undefined

            }

            const data = $.extend(dataModel, macro.params);

            const hide = function(uuid) {
                console.log("Closing")

                data.branches = undefined
                data.errors = undefined
                data.currentError = undefined

                data.glob = JSON.stringify(data.glob)


                data.globList = undefined

                data.downloadingBranches = undefined

                data.forcedPredefined = undefined

                data.saving = undefined
                data.isFilled = undefined

                data.branch = undefined
                data.repository = undefined
                data.customRepository = undefined
                data.existingRepositoryUuid = undefined
                data.repositories = undefined
                data.uuid = uuid

                data.loadingRepositories = undefined

                data.downloadingFiles = undefined

                data.fileTree = undefined
                data.recentlyUsedRepositories = undefined

                data.branchDownloadPromise = undefined


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
                    customRepositoryDialog: Git4CCustomRepositoryDialog.getComponent(Bus, true),
                    git4cselect2single: Git4CSelect2Single.getComponent()
                },
                data: function () {
                    return data
                },
                watch: {
                    repository: function () {
                        if(this.isRecentlyUsed(this.repository)) {
                            this.customRepository = {
                                uuid: this.repository.uuid,
                                repositoryName: this.repository.repositoryName
                            }
                        }
                        this.fileTree = null
                        this.getBranches()
                        this.checkIfFilled()
                    },
                    branch: function () {
                        if(this.branch) {
                            this.prevBranch = this.branch
                            this.fileTree = null
                        }
                        this.checkIfFilled()
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
                    closeDialog: function(){
                        this.$refs.custom_repository_dialog.clearFields()
                        AJS.dialog2(dialogInstance).hide()
                    },
                    openCustomRepositoryDialog: function(){
                        AJS.dialog2(this.$refs.custom_repository_dialog.$el).show()
                    },
                    closeCustomRepositoryDialog: function(){
                        AJS.dialog2(this.$refs.custom_repository_dialog.$el).hide()
                    },
                    processCustomRepository: function(repository){
                        const vm = this
                        Git4CApi.verifyRepository(repository.sourceRepositoryUrl, repository.credentials)
                            .then(function(data) {
                                if (data.ok === false) {
                                    throw Error("",[""], data.status);
                                }
                                vm.postProcessCustomRepository(repository)
                            }, function(error) {
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
                    isPredefined: function () {
                        return !this.customRepository ? true : (this.repository.uuid !== this.customRepository.uuid) &&  (this.repository.repositoryName !== this.customRepository.repositoryName)
                    },
                    getBranches: function () {
                        const vm = this
                        this.downloadingBranches = true;
                        if(this.repository) {

                            if (this.branchDownloadPromise) {
                                this.branchDownloadPromise.stop()
                            }

                            this.branches = undefined;
                            const isPredefined = this.isPredefined()
                            var promise = undefined
                            if (this.customRepository && this.customRepository.uuid && !isPredefined) {
                                const uuid = this.customRepository.uuid
                                promise = Git4CApi.getBranches.forCustomRepository(uuid)
                            } else {
                                if (isPredefined) {
                                    const uuid = this.repository.uuid
                                    promise = Git4CApi.getBranches.forPredefinedRepository(uuid)
                                } else {
                                    const repositoryUrl = this.customRepository.sourceRepositoryUrl
                                    const credentials = this.customRepository.credentials
                                    if(repositoryUrl) {
                                        promise = Git4CApi.getBranches.forRepository(repositoryUrl, credentials)
                                    }
                                }

                            }

                            this.branchDownloadPromise = promise

                            if(promise) {
                                promise
                                    .then(function(response) {
                                        vm.downloadingBranches = false;
                                        const branches = response.allBranches

                                        if (branches.length == 0){
                                            vm.branches = undefined
                                            vm.downloadingBranches = false
                                            return
                                        }

                                        const masterId = branches.indexOf("master")
                                        const developId = branches.indexOf("develop")
                                        const prevBranchId = branches.indexOf(vm.prevBranch)
                                        if (prevBranchId !== -1) {
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
                                        vm.branches = branches
                                    })
                                    .catch(function(error) {
                                        error.text().then(function(text) {
                                                vm.showError(text)
                                                vm.currentError = text
                                                vm.downloadingBranches = false
                                            }
                                        )
                                    })
                            }
                        }

                    },

                    getFileTree: function () {
                        const vm = this
                        vm.downloadingFiles = true
                        if(this.repository) {
                            const isPredefined = this.isPredefined()
                            var promise
                            const branch = this.branch
                            if (this.customRepository && this.customRepository.uuid && !isPredefined) {
                                const uuid = this.customRepository.uuid
                                promise = Git4CApi.getFilesTree.forCustomRepository(uuid, branch)
                            } else {
                                if (isPredefined) {
                                    const uuid = this.repository.uuid
                                    promise = Git4CApi.getFilesTree.forPredefinedRepository(uuid, branch)
                                } else {
                                    const repositoryUrl = this.customRepository.sourceRepositoryUrl
                                    const credentials = this.customRepository.credentials

                                    if (repositoryUrl) {
                                        promise = Git4CApi.getFilesTree.forRepository(repositoryUrl, credentials, branch)
                                    }
                                }
                            }
                            if(promise) {
                                promise
                                    .then(function(response) {
                                        var tree = response.tree
                                        vm.fileTree = vm.filterFileTreeNodes(tree, function(node) {return node.type === "DIR"} )
                                        vm.downloadingFiles = false
                                        createFileTreeDialog(vm.fileTree, vm.processSelectedRootFilter, vm)
                                    })
                                    .catch(function(error) {
                                        vm.downloadingFiles = false
                                        error.text().then(function(text) {
                                            vm.showError(text)
                                        })
                                    })
                            }
                        }
                    },

                    filterFileTreeNodes: function (tree, predicate){
                        const vm = this
                        if(tree && predicate(tree)){
                            return {
                                fullName: tree.fullName,
                                name: tree.name,
                                type: tree.type,
                                children: (tree.children.map(function(it) { return vm.filterFileTreeNodes(it, predicate) })).filter(function(it) {return it!== undefined})
                            }
                        }
                    },

                    save: function () {
                        const vm = this

                        this.saving = true
                        const isPredefined = this.isPredefined()
                        if(isPredefined){
                            this.customRepository = undefined
                            this.customRepositoryUrl = undefined
                            this.customRepositoryAuthType = undefined
                            this.customRepositoryName = undefined

                            this.predefinedRepositoryUuid = this.repository.uuid;
                        } else {
                            this.predefinedRepositoryUuid = undefined
                        }

                        var toSend = undefined
                        var repositoryDetails = undefined

                        const globToSave = this.glob ? this.glob.split(',') : []
                        const defaultDocItemToSave = this.defaultDocItem ? this.defaultDocItem : ""

                        var repositoryName = null
                        if(this.customRepository && this.customRepository.uuid){
                            repositoryName = this.repository.repositoryName
                            vm.customRepositoryName = repositoryName
                            repositoryDetails = {
                                repository: {
                                    type: "EXISTING",
                                    uuid: this.repository.uuid
                                }
                            }
                        } else{
                            if (!isPredefined) {
                                repositoryName = this.repository.repositoryName
                                repositoryDetails = {
                                    repository: {
                                        type: "CUSTOM",
                                        url: this.repository.sourceRepositoryUrl,
                                        credentials: this.repository.credentials

                                    }
                                }
                            } else {
                                repositoryName = this.repositories.filter( function(it) { return (it.uuid === vm.repository.uuid) } )[0].name
                                repositoryDetails = {
                                    repository: {
                                        type: "PREDEFINED",
                                        uuid: this.repository.uuid

                                    }
                                }
                            }
                        }

                        var rd

                        if (this.rootDirectory) {
                            rd = this.rootDirectory + "/**"
                        } else {
                            rd = null
                        }

                        toSend = {
                            repositoryDetails: repositoryDetails,
                            repositoryName: repositoryName,
                            branch: this.branch,
                            glob:  globToSave,
                            defaultDocItem: defaultDocItemToSave,
                            rootDirectory: rd
                        }

                        Git4CApi.createMacro(toSend)
                            .then(function(response) {
                                const uuid = response.uuid
                                hide(uuid)
                                vm.saving = false
                            })
                            .catch(function(error) {
                                vm.saving = false
                                error.text().then(function(text) {
                                    vm.showError(text)
                                })
                            })

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
                            .then(function (usages) {
                                registerDownload()
                                vm.recentlyUsedRepositories = usages.reverse()
                            })
                            .catch(function (error) {
                                registerDownload()
                                error.text().then(function(text) {
                                    vm.showError(text)
                                })
                            })

                        Git4CApi.getPredefinedRepositories()
                            .then(function(repositories) {
                                registerDownload()
                                if(vm.predefinedRepositoryUuid){
                                    var list = repositories.filter( function(it) { return (it.uuid === vm.predefinedRepositoryUuid) } )
                                    if(list.length !== 0){
                                        vm.repository = list[0]
                                    }else{
                                        vm.predefinedRepositoryUuid = null
                                    }
                                }
                                vm.repositories = repositories
                                if(!vm.repository && !vm.customRepositoryName) {
                                    vm.repository = repositories[0]
                                }
                            })
                            .catch(function(error) {
                                registerDownload()
                                error.text().then(function(text) {
                                    vm.showError(text)
                                })
                            })

                    },
                    getGlobList: function () {
                        const vm = this;

                        Git4CApi.getGlobs()
                            .then(function(globs) {
                                const globList = globs.map(function (data) {
                                    return {id: data.glob, text: data.glob + " (" + data.name + ")"}
                                })

                                const select2 = $(vm.$refs['doc_macro-glob'])

                                select2.auiSelect2({
                                    tags: globList,
                                    tokenSeparators: [",", " "]
                                }).on('change', function (e) {
                                    if (e.val)
                                        vm.glob = e.val.join();
                                    else {
                                        const value = $(this).val();
                                        vm.glob = value;
                                    }
                                });

                                const currentGlobs = vm.glob

                                if (currentGlobs) {
                                    select2.auiSelect2("val", currentGlobs.split(","));
                                }

                                vm.globList = globList

                            })
                            .catch(function(error) {
                                error.text().then(function(text) {
                                    vm.showError(text)
                                })
                            })
                    },
                    checkIfFilled: function () {
                        !this.repository ? this.isFilled = false : !this.branch ? this.isFilled = false : this.isFilled = true
                    },
                    showError: function (text) {
                        const errors = _.flatMap(this.errors, function(e) {return e.serverError})
                        if (_.includes(errors, text)) {
                            this.currentError = text
                        } else {
                            this.currentError = "SERVER_ERROR"
                        }

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

                            })
                            .catch(function(error) {
                                error.text().then(function(text) {
                                    vm.showError(text)
                                })
                            })

                        if(data.uuid){
                            if(this.glob){
                                if(this.glob == "undefined"){
                                    this.glob = null
                                }else{
                                    this.glob = this.glob.substring(1,this.glob.length-1)
                                }
                            }

                            Git4CApi.getMacroInformation(data.uuid)
                                .then(function(response) {
                                    if(vm.customRepositoryName) {
                                        vm.customRepository = {
                                            uuid: response.repositoryUuid,
                                            repositoryName: vm.customRepositoryName
                                        }
                                        vm.repository = vm.customRepository
                                    }
                                })
                                .catch(function(error) {
                                    if(error.statusText == "Not Found")
                                    {
                                        vm.showError("REPOSITORY_REMOVED")
                                    }else {
                                        error.text().then(function(text) {
                                            vm.showError(text)
                                        })
                                    }
                                })
                            if(this.customRepositoryName && this.customRepositoryUrl) {
                                const repoInfo = {
                                    name: this.customRepositoryName,
                                    sourceRepositoryUrl: this.customRepositoryUrl,
                                    authType: this.customRepositoryAuthType
                                }
                                this.$refs.custom_repository_dialog.initFields(repoInfo)
                            }
                        }
                    },
                    showFileTreeDialog: function() {
                        if(this.fileTree) {
                            createFileTreeDialog(this.fileTree, this.processSelectedRootFilter, this)
                        }
                        else{
                            this.getFileTree()
                        }
                    },
                    processSelectedRootFilter: function(file) {
                        if (file) {
                            this.rootDirectory = file
                        }
                    }

                },
                mounted: function(){
                    const vm = this
                    this.init()
                    this.getRepositoryList()

                    Bus.$on("closeCustomRepositoryDialog", function() {vm.closeCustomRepositoryDialog()})
                    Bus.$on("repositoryDefined", function(repository) {vm.processCustomRepository(repository)})

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



    const fileTreeDialog =
           '<section role="dialog" id="multifiledoc_filetree_macroDialog" class="aui-layer aui-dialog2 aui-dialog2-small" aria-hidden="true">'+
           '    <header class="aui-dialog2-header">'+
           '       <h2 class="aui-dialog2-header-main">Select root directory</h2>'+
           '       <a class="aui-dialog2-header-close" v-on:click="hideDialog()">'+
           '       <span class="aui-icon aui-icon-small aui-iconfont-close-dialog">Close</span>'+
           '       </a>'+
           '    </header>'+
           '    <div class="aui-dialog2-content" style="margin: 0; padding: 0; display: flex;">'+
           '        <div id="git4c-single-dialog-tree-div" style="width: 200px; padding: 20px; overflow: auto;" ref="fileTree">'+
           '            <git4c-filetree :data="fileTree" :dirclickable="true" ></git4c-filetree>'+
           '        </div>'+
           '    </div>'+
           '    <footer class="aui-dialog2-footer">'+
           '         <!-- Actions to render on the right of the footer -->'+
           '         <div class="aui-dialog2-footer-actions">'+
           '             <button v-on:click="passFile()" id="dialog-next-button" class="aui-button">Select</button>'+
           '             <button v-on:click="hideDialog()" id="dialog-close-button" class="aui-button aui-button-link">Cancel</button>'+
           '         </div>'+
           '    </footer>'+
           '</section>'



    const createFileTreeDialog = function (tree, callback, originalData) {
        const dialogInstance = $(fileTreeDialog).appendTo("body");

        var vue

        AJS.dialog2(dialogInstance).on("hide", function () {
            if (vue) {
                vue.$destroy()
            }
            $("#multifiledoc_filetree_macroDialog").remove()
        })

        AJS.dialog2(dialogInstance).show();

        const hide = function (file) {
            AJS.dialog2(dialogInstance).hide()
            callback(file)
        }

        const Bus = new Vue({});


        vue = new Vue({
            el: '#multifiledoc_filetree_macroDialog',
            data: function () {
                return {
                    fileTree: tree,
                    currentFile: undefined
                }
            },
            components: {
                "git4c-filetree": Git4CFileTree.getComponent(Bus),
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
                const vm = this
                Bus.$on('selectedFile', function(file)  {
                    vm.currentFile = file
                })
            }
        })
    }

})(AJS.$);
