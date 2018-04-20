var Git4COverlay = {
    getComponent: function (Events) {
        return {
            template:
            ' <div style="position: relative; flex: 1; align-content: stretch;" v-bind:class="{ \'git4c-component-overlay-loading\': loading }">' +
            '    <div v-if="loading" class="git4c-component-overlay">' +
            '        <div style="transform:translate(-50%,-50%);position: absolute; top:50%; left: 50%; width: 100%;">' +
            '            <div id="spinner1" class="spinner6" style="width:60px; margin: 0 auto;">' +
            '                <div class="rect1" style="background-color: rgb(204, 214, 7);"></div>' +
            '                <div class="rect2" style="background-color: rgb(204, 214, 7);"></div>' +
            '                <div class="rect3" style="background-color: rgb(204, 214, 7);"></div>' +
            '                <div class="rect4" style="background-color: rgb(204, 214, 7);"></div>' +
            '                <div class="rect5" style="background-color: rgb(204, 214, 7);"></div>' +
            '            </div>' +
            '            <div>' +
            '                <p style="font-family: Arial; color: rgb(204, 214, 7); font-weight: bold; font-size: 20pt; text-align: center;">' +
            '                    Loading content from git</p>' +
            '            </div>' +
            '            <div>' +
            '                <p style="font-family: Arial; color: rgb(112, 112, 112); font-weight: normal; font-size: 14pt; text-align: center;">' +
            '                   It shouldn\'t take more than one minute</p>' +
            '            </div>' +
            '        </div>' +
            '    </div>' +
            '    <div>' +
            '        <slot></slot>' +
            '    </div>' +
            '    <div v-if="error == true" class="git4c-component-overlay">' +
            '        <img v-bind:src="dragonLocation" style="max-width: 100%; max-height:90%" id="dragon_image">' +
            '        <div style="margin:0">' +
            '           <p v-if="repositoryRemoved == true"> Repository has been removed by the administrator.</p>' +
            '           <p v-if="noBranches == true">There are no banches available on this repository.</p>' +
            '           <p v-if="nonExistingBranch == true"> The branch you requested doesn\'t exist. You can return to default branch by clicking <a href="javascript:void(0)" v-on:click="returnToDefaultBranch" >here</a>.</p>' +
            '           <p v-if="defaultError == true">Ooops... Something went wrong. Try refreshing your Git4C macro or ask your administrator for help.</p>' +
            '           <p v-if="noFiles == true">There are no files present on branch. Check if filter is set correctly or if there are any files at branch.</p>' +
            '        </div>' +
            '    </div>' +
            '    <div ref="dragoncage" class="dragonCage"></div>' +
            '</div>',

            data: function () {
                return {
                    //It's intentional - first event isn't fast enough
                    loading: true,
                    error: false,
                    repositoryRemoved: false,
                    nonExistingBranch: false,
                    noBranches: false,
                    noFiles: false,
                    defaultError: false,
                    dragonLocation: undefined
                };
            },
            methods: {
                returnToDefaultBranch: function () {
                    MarkupService.getDefaultBranch().then(function (promise) {
                        Events.$emit("branchChangeRequest", promise.currentBranch)
                    })
                },
                detectError: function (error) {
                    if (error == "repository_removed") {
                        this.repositoryRemoved = true
                    }
                    else if (error == "non_existing_branch") {
                        this.nonExistingBranch = true
                    }
                    else if (error == "no_branches") {
                        this.noBranches = true
                    }
                    else if (error == "no_files") {
                        this.noFiles = true
                    }
                    else {
                        this.defaultError = true
                    }
                },
                clearErrors: function () {
                    this.error = false
                    this.repositoryRemoved = false
                    this.nonExistingBranch = false
                    this.noBranches = false
                    this.noFiles = false
                    this.defaultError = false
                }
            },

            mounted: function () {
                const vm = this
                Events.$on("errorOccured", function (error) {
                    vm.clearErrors()
                    vm.loading = false
                    vm.error = true
                    vm.detectError(error)
                })
                Events.$on("OverlayChange", function (loading) {
                    vm.loading = loading
                })
                Events.$on("branchChanging", function () {
                    vm.clearErrors()
                    vm.loading = true
                })
                const image = $(this.$refs.dragoncage).css("background-image")
                //To remove url("")
                this.dragonLocation = image.substring(5, image.length - 2)
            }
        }
    },

    getLoaderAlone: function (Events) {
        return {
            template: '<div style="position: relative; flex: 1; align-content: stretch;">' +
            '    <div v-if="loading" class="git4c-component-overlay">' +
            '        <div style="transform:translate(-50%,-50%);position: absolute; top:50%; left: 50%; width: 100%;">' +
            '            <div id="spinner1" class="spinner6" style="width:60px; margin: 0 auto;">' +
            '                <div class="rect1" style="background-color: rgb(204, 214, 7);"></div>' +
            '                <div class="rect2" style="background-color: rgb(204, 214, 7);"></div>' +
            '                <div class="rect3" style="background-color: rgb(204, 214, 7);"></div>' +
            '                <div class="rect4" style="background-color: rgb(204, 214, 7);"></div>' +
            '                <div class="rect5" style="background-color: rgb(204, 214, 7);"></div>' +
            '            </div>' +
            '        </div>' +
            '    </div>' +
            '    <div>' +
            '        <slot></slot>' +
            '    </div>' +
            '</div>'
            ,
            data: function () {
                return {
                    //It's intentional - first event isn't fast enough
                    loading: true
                };
            },

            mounted: function () {
                const vm = this
                Events.$on("OverlayChange", function (loading) {
                    vm.loading = loading
                })
            }
        }
    }

};
