var Git4CInfoDialog = {

    getComponent: function () {

        const random = Math.random().toString(36).substring(10);

        const id = "git4c-info-dialog-" + random
        const jId = "#" + id

        return {

            template:
                '   <section role="dialog" id="' + id + '" class="aui-layer aui-dialog2 aui-dialog2-medium" aria-hidden="true">' +
                '       <header class="aui-dialog2-header">' +
                '           <div style="display: flex; align-items: center; height: 100%;">' +
                '               <img style="height: 28px; margin-right: 5px;" v-bind:src="logoLocation" />' +
                '               <h2 style="margin-top: 0">' +
                '               macro properties' +
                '               </h2>' +
                '           </div>' +
                '           <a class="aui-dialog2-header-close">' +
                '               <span class="aui-icon aui-icon-small aui-iconfont-close-dialog">Close</span>' +
                '           </a>' +
                '       </header>' +
                '       <div class="aui-dialog2-content">' +
                '           <div ref="logocage" class="git4clogocage"></div>' +
                '           <table class="aui">' +
                '               <tbody>' +
                '                   <tr>' +
                '                       <td><b>File</b></td>' +
                '                       <td>{{fileLocation}}</td>' +
                '                   </tr>' +
                '                   <tr>' +
                '                       <td><b>Repository</b></td>' +
                '                       <td>{{repositoryName}}</td>' +
                '                   </tr>' +
                '                   <tr>' +
                '                       <td><b>Current branch</b></td>' +
                '                       <td>{{branchName}}</td>' +
                '                   </tr>' +
                '                   <tr v-if="originalBranchName !== branchName">' +
                '                       <td><b>Source branch</b></td>' +
                '                       <td>{{originalBranchName}}</td>' +
                '                   </tr>' +
                '               </tbody>' +
                '           </table>' +
                '       </div>' +
                '       <footer class="aui-dialog2-footer">' +
                '           <div class="aui-dialog2-footer-actions">' +
                '               <button v-on:click="hide" id="dialog-close-button" class="aui-button aui-button-link">Close</button>' +
                '           </div>' +
                '       </footer>' +
                '   </section>',

            data: function() {
                return {
                    logoLocation: undefined
                }
            },

            props: {
                branchName: {
                    type: String
                },
                originalBranchName: {
                    type: String
                },
                editBranch: {
                    type: Boolean
                },
                fileLocation: {
                    type: String
                },
                repositoryName: {
                    type: String
                }
            },

            mounted: function () {

                const image = $(this.$refs.logocage).css("background-image")
                //To remove url("")
                this.logoLocation = image.substring(5, image.length - 2)

            },

            methods: {

                show: function () {
                    AJS.dialog2(jId).show();
                },

                hide: function () {
                    AJS.dialog2(jId).hide();
                }
            }

        }

    }

}