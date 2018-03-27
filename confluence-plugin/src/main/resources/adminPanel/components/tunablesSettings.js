var Git4CTunablesSettings = {

    getComponent: function (Events) {

        const baseUrl = AJS.contextPath() + "/rest/doc/1.0";
        const restUrl = baseUrl + "/documentation";

        return {
            template:
            '<div>'+
            '    <h5>Threading settings</h5>'+
            '    <div style="margin-bottom: 10px; margin-top: 10px">Values below depend on the number of CPU cores installed in the system and should be adjusted with caution as they might cause instability of the Confluence server.</div>'+
            '    <div>'+
            '        <form class="aui">'+
            '            <div class="field-group">'+
            '                <label for="comment-input">'+
            '                    Revision checking'+
            '                </label>'+
            '                <input v-model="revision" class="text medium-field" type="number">'+
            '                <div class="description medium-field">The maximum number of CPU threads dedicated to repository revision check operations.</div>'+
            '            </div> '+
            '            <div class="field-group">'+
            '                <label for="comment-input">'+
            '                    Repository pulling'+
            '                </label>'+
            '                <input v-model="repository" class="text medium-field" type="number">'+
            '                <div class="description medium-field">The maximum number of CPU threads dedicated to repository pull operations.</div>'+
            '            </div>'+
            '            <div class="field-group">'+
            '                <label for="comment-input">'+
            '                    File Converting'+
            '                </label>'+
            '                <input v-model="converter" class="text medium-field" type="number">'+
            '                <div class="description medium-field">The maximum number of CPU threads dedicated document conversion and visualization.</div>'+
            '            </div>'+
            '            <div class="field-group">'+
            '                <label for="comment-input">'+
            '                    I/O Operations'+
            '                </label>'+
            '                <input v-model="confluenceQuery" class="text medium-field" type="number">'+
            '                <div class="description medium-field">The maximum number of CPU threads dedicated to macro indexing operations.</div>'+
            '            </div>'+
            '        </form>'+
            '        <button v-on:click="save()" class="aui-button">Save</button>'+
            '    </div>'+
            '</div>',

            data: function () {
                return {
                    revision: 0,
                    repository: 0,
                    converter: 0,
                    confluenceQuery: 0,
                    downloaded: false
                }
            },

            methods: {

                save: function () {

                    const obj = {
                        revisionCheckExecutor: this.revision,
                        repositoryPullExecutor: this.repository,
                        converterExecutor: this.converter,
                        confluenceQueryExecutor: this.confluenceQuery
                    }

                    Git4CApi.setExecutorSettings(obj)
                        .then(function (res) {

                            AJSC.flag({
                                type: 'success',
                                close: "manual",
                                body: 'Settings saved'
                            })

                        })
                        .catch(function (err) {

                            AJSC.flag({
                                type: 'error',
                                close: "manual",
                                body: 'Settings saving failed'
                            })

                        })


                }

            },

            mounted: function () {

                const vm = this

                Git4CApi.getExecutorsSettings()
                    .then(function (threads) {

                        vm.revision = threads.revisionCheckExecutor
                        vm.repository = threads.repositoryPullExecutor
                        vm.converter = threads.converterExecutor
                        vm.confluenceQuery = threads.confluenceQueryExecutor

                        vm.downloaded = true
                    })
                    .catch(function (err) {
                        console.log(err)

                    })

            }


        }

    }

}
