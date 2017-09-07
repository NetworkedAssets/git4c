var Git4CCustomGlobDialog = {
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

        const serverError = new Error("server_error", ["SERVER_ERROR"], "<p>Server error</p>")
        const errors = [
            serverError
        ]


        return {
            data: function () {
                return {
                    name: undefined,
                    pattern: undefined,
                    isFilled: false,
                    errors: errors,
                    currentError: undefined,
                    saving: false
                }
            },
            watch: {
                name: function () {
                    this.processInputChange()
                },
                pattern: function() {
                    this.processInputChange()
                }
            },
            methods: {
                closeDialog() {
                    this.$emit("closeCustomGlobDialog")
                },
                processInputChange: function () {
                    !this.name ? this.isFilled = false : !this.pattern ? this.isFilled = false : this.isFilled = true
                },
                defineGlob: function() {
                    const glob = {
                        name: this.name,
                        glob: this.pattern
                    }

                    this.saving = true;
                    this.$emit("globDefined", glob);

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
                    this.pattern = undefined
                    this.isFilled = false
                    this.saving = false
                    this.currentError = undefined
                }

            },
            template: `
                    <section role="dialog" id="custom_glob_dialog" class="aui-layer aui-dialog2 aui-dialog2-medium" aria-hidden="true">
                    <header class="aui-dialog2-header">
                       <h2 class="aui-dialog2-header-main">Create new filter</h2>
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
                          <div id="git4c-custom-glob-dialog-content">
                          <div class="aui-group">
                          <div class="aui-item">
                                 <div class="field-group">
                                    <label for="doc_macro-repo_url">Name</label>
                                    <input v-model="name" class="text" type="text" placeholder="Filter name">
                                    <div class="description">Please type your filter name</div>
                                 </div>
                                 <div class="field-group">
                                    <label for="doc_macro-repo_url">Pattern</label>
                                    <input v-model="pattern" class="text" type="text" placeholder="Filter pattern">
                                    <div class="description">Please type your <a id="new-glob-dialog-tooltip" href="https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob">pattern</a></div>
                                 </div>
                          </div>
                          </div>
                          </div>
                       </form>
                    </div>
                    <footer class="aui-dialog2-footer">
                       <!-- Actions to render on the right of the footer -->
                       <div class="aui-dialog2-footer-actions">
                          <button id="custom_glob_dialog-close-button" v-on:click="defineGlob()" class="aui-button aui-button-primary" v-bind:disabled="!isFilled" v-show="saving == false">Save</button>
                          <button id="custom_glob_dialog-close-button" class="aui-button aui-button-primary" disabled=true v-show="saving == true">Saving...</button>
                       </div>
                       <!-- Hint text is rendered on the left of the footer -->
                       <!--<div class="aui-dialog2-footer-hint">this is a hint</div>-->
                    </footer>
                    </section>
                    `
        }


    }
}
