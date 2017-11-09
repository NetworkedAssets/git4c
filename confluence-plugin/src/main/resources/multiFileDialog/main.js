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
        '                <select class="select" v-show="(repositories && repositories.length!=0) || customRepository" v-model="repository"  style="max-width: 50%">'+
        '                    <option v-if="customRepository" v-bind:value="customRepository">'+
        '                         {{ customRepository.repositoryName }}'+
        '                    </option>'+
        '                    <option v-for="repo in repositories" v-bind:value="repo">'+
        '                         {{ repo.name }}'+
        '                    </option>'+
        '                </select>'+
        '                <select class="select" v-show="(!repositories || repositories.length==0) && !customRepository" :disabled="true"  style="max-width: 50%">'+
        '                    <option>No Repositories Available</option>'+
        '                </select>'+
        '                <button  v-bind:disabled="forcedPredefined" ref="custom_repository_button"  id="git4c-multi_file_dialog-add_repository-button" @click.prevent v-on:click="openCustomRepositoryDialog" class="aui-button aui-button-primary">'+
        '                    <span class="aui-icon aui-icon-small aui-iconfont-add"/>'+
        '                </button>'+
        '            </div>'+
        '       '+
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
        '               <input style="max-width: 50%" class="text" type="text" ref="doc_macro-glob" placeholder="pattern"></input>'+
        '                    <button style="position: absolute; margin-left: 1%;" v-bind:disabled="!fileTree" @click.prevent="showFileTreeDialog()" class="aui-button aui-button-primary">'+
        '                        <span class="aui-icon aui-icon-small aui-iconfont-nav-children-large">Show file tree</span>'+
        '                    </button>'+
        '               <div class="description">Please type your <a ref="pattern_tooltip" href="https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob">pattern</a> (Optional)</div>'+
        '            </div>'+
        '            <div class="field-group">'+
        '               <label for="doc_macro-default_Doc_Item">Default File</label>'+
        '               <input v-model="defaultDocItem" class="text" type="text" id="doc_macro-default_Doc_Item" placeholder="Readme.md">'+
        '               <div class="description">Please type the name of the default Document File (Optional)</div>'+
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

                forcedPredefined: true,

                currentError: undefined,
                errors: errors,


                saving: false,
                isFilled: false,

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

                data.fileTree = undefined



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
                    git4cselect2single: Git4CSelect2Single.getComponent()
                },
                data: function () {
                    return data
                },
                watch: {
                    repository: function () {
                        this.fileTree = null
                        this.getBranches()
                        this.checkIfFilled()
                    },
                    branch: function () {
                        if(this.branch) {
                            this.prevBranch = this.branch
                            this.fileTree = null
                            this.$nextTick(function(){
                                this.getFileTree()
                            })

                        }
                        this.checkIfFilled()
                    }

                },
                methods: {
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
                        const response = Vue.http.post(restUrl + "/repository/verify", {sourceRepositoryUrl: repository.sourceRepositoryUrl, credentials: repository.credentials}).then(function(response) {
                            if (response.data.ok == false) {
                                throw Error("",[""],response.data.status);
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
                        this.existingRepositoryUuid = undefined
                        this.customRepositoryAuthType = this.repository.credentials.sshKey ? "SSHKEY" : this.repository.credentials.username ? "USERNAMEPASSWORD" : "NOAUTH"
                        this.customRepositoryName = this.repository.repositoryName
                        this.customRepositoryUrl = this.repository.sourceRepositoryUrl

                    },
                    getBranches: function () {
                        const vm = this
                        this.downloadingBranches = true;
                        if(this.repository) {
                            this.branches = undefined;
                            const isPredefined = !this.customRepository ? true : this.repository != this.customRepository
                            var promise = undefined
                            if (this.existingRepositoryUuid && !isPredefined) {
                                const uuid = this.existingRepositoryUuid
                                promise = Vue.http.get(restUrl + "/repository/" + uuid + "/branches")
                            } else {
                                if (isPredefined) {
                                    const uuid = this.repository.uuid
                                    promise = Vue.http.get(restUrl + "/predefine/" + uuid + "/branches")

                                } else {
                                    const repositoryForBranches = {
                                        sourceRepositoryUrl: this.customRepository.sourceRepositoryUrl,
                                        credentials: this.customRepository.credentials
                                    }
                                    if(repositoryForBranches.sourceRepositoryUrl)
                                    promise = Vue.http.post(restUrl + "/repository/branches", repositoryForBranches)
                                }

                            }
                            if(promise) {
                                promise.then(function(response) {
                                    vm.downloadingBranches = false;
                                    vm.branches = response.body.allBranches

                                    const masterId = vm.branches.indexOf("master")
                                    const developId = vm.branches.indexOf("develop")
                                    const prevBranchId = vm.branches.indexOf(vm.prevBranch)
                                    if (prevBranchId !== -1) {
                                        vm.branch = vm.branches[prevBranchId]
                                    } else {
                                        if (masterId !== -1) {
                                            vm.branch = vm.branches[masterId]
                                        } else if (developId !== -1) {
                                            vm.branch = vm.branches[developId]
                                        } else {
                                            vm.branch = vm.branches[0]
                                        }
                                        vm.prevBranch = vm.branch
                                    }
                                    vm.$nextTick(function(){
                                        vm.getFileTree()
                                    })

                                }, function(error) {
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
                        if(this.repository) {
                            const isPredefined = !this.customRepository ? true : this.repository != this.customRepository
                            var promise
                            const branch = {
                                branch: this.branch
                            }
                            if (this.existingRepositoryUuid && !isPredefined) {
                                const uuid = this.existingRepositoryUuid
                                promise = Vue.http.post(restUrl + "/repository/" + uuid + "/files", branch)
                            } else {
                                if (isPredefined) {
                                    const uuid = this.repository.uuid
                                    promise = Vue.http.post(restUrl + "/predefine/" + uuid + "/files", branch)

                                } else {
                                    const repositoryForFiles = {
                                        sourceRepositoryUrl: this.customRepository.sourceRepositoryUrl,
                                        credentials: this.customRepository.credentials,
                                        branch: branch.branch
                                    }
                                    if (repositoryForFiles.sourceRepositoryUrl) {
                                        promise = Vue.http.post(restUrl + "/repository/files", repositoryForFiles)
                                    }
                                }
                            }
                            if(promise) {
                                promise.then(function(response) {
                                    var tree = response.data.tree
                                    vm.fileTree = vm.filterFileTreeNodes(tree, function(node) {return node.type === "DIR"} )
                            }, function(error) {
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
                        const isPredefined = !this.customRepository ? true : this.repository != this.customRepository
                        if(isPredefined){
                            this.existingRepositoryUuid = undefined
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
                            repositoryDetails: repositoryDetails,
                            branch: this.branch,
                            glob:  globToSave,
                            defaultDocItem: defaultDocItemToSave
                        }
                        this.$http.post(restUrl, toSend, {}).then(function(response) {
                            const uuid = response.body.uuid
                            hide(uuid)
                            vm.saving = false
                        }, function(error) {
                            vm.saving = false
                            error.text().then(function(text) {
                                vm.showError(text)
                            })
                        })

                    },
                    getRepositoryList: function () {
                        const vm = this;

                        vm.$http.get(restUrl + "/predefine", {}).then(function(response) {
                            vm.repositories = response.data
                            if(vm.predefinedRepositoryUuid){
                                var list = vm.repositories.filter( function(it) { return (it.uuid === vm.predefinedRepositoryUuid) } )
                                if(list.length != 0){
                                    vm.repository = list[0]
                                }else{
                                    vm.predefinedRepositoryUuid = null
                                }
                            }
                            if(!vm.repository) {
                                vm.repository = vm.repositories[0]
                            }
                        }, function(error) {
                            error.text().then(function(text) {
                                vm.showError(text)
                            })
                        })
                    },
                    getGlobList: function () {
                        const vm = this;

                        vm.$http.get(restUrl + "/glob", {}).then(function(response) {
                            vm.globList = response.data.globs.map(function (data) {
                                return {id: data.glob, text: data.glob + " (" + data.name + ")"}
                            })

                            const select2 = $(vm.$refs['doc_macro-glob'])

                            select2.auiSelect2({
                                tags: vm.globList,
                                tokenSeparators: [",", " "]
                            }).on('change', function (e) {
                                vm.glob = e.val.join();
                            });

                            select2.auiSelect2("val", vm.glob.split(","));

                        }, function(error) {
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

                        Vue.http.get(restUrl + "/settings/repository/predefine/force").then(function(response) {
                            if(response.body.forced === true) {
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
                            if(this.glob){
                                if(this.glob == "undefined"){
                                    this.glob = null
                                }else{
                                    this.glob = this.glob.substring(1,this.glob.length-1)
                                }
                            }

                            Vue.http.get(restUrl + "/" + data.uuid).then(function(response) {
                                vm.existingRepositoryUuid = response.body.repositoryUuid
                                if(vm.customRepositoryName) {
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
                    },
                    showFileTreeDialog: function() {
                        createFileTreeDialog(this.fileTree, this.processSelectedRootFilter, this)
                    },
                    processSelectedRootFilter: function(file) {
                        const newGlobValue = this.glob? this.glob.concat("," + file + "/**") : file + "/**"
                        if (file) {
                            $(this.$refs['doc_macro-glob']).val(newGlobValue).trigger("change")
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
