var Git4CThreadNumberSettings = {

    getComponent: function (Events) {

        const baseUrl = AJS.contextPath() + "/rest/doc/1.0";
        const restUrl = baseUrl + "/documentation";

        return {
            template:
            '<div v-show="downloaded">'+
            '    <h3>Thread settings</h3>'+
            '    <div>'+
            '        <form class="aui">'+
            '            <div class="field-group">'+
            '                <label for="comment-input">'+
            '                    Revision check'+
            '                    <span ref="revision_check_tooltip" class="aui-icon aui-icon-small aui-iconfont-help">Insert meaningful text here for accessibility</span>'+
            '                </label>'+
            '                <input v-model="revision" class="text medium-field" type="number">'+
            '            </div> '+
            '            <div class="field-group">'+
            '                <label for="comment-input">'+
            '                    Repository pull'+
            '                    <span ref="repository_pull_tooltip" class="aui-icon aui-icon-small aui-iconfont-help">Insert meaningful text here for accessibility</span>'+
            '                </label>'+
            '                <input v-model="repository" class="text medium-field" type="number">'+
            '            </div>'+
            '            <div class="field-group">'+
            '                <label for="comment-input">'+
            '                    Files Convert'+
            '                    <span ref="converter_tooltip" class="aui-icon aui-icon-small aui-iconfont-help">Insert meaningful text here for accessibility</span>'+
            '                </label>'+
            '                <input v-model="converter" class="text medium-field" type="number">'+
            '            </div>'+
            '            <div class="field-group">'+
            '                <label for="comment-input">'+
            '                    I/O Operations'+
            '                    <span ref="confluence_query_tooltip" class="aui-icon aui-icon-small aui-iconfont-help">Insert meaningful text here for accessibility</span>'+
            '                </label>'+
            '                <input v-model="confluenceQuery" class="text medium-field" type="number">'+
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

                    this.$http.post(restUrl + "/executors", obj)
                        .then(function (res) {

                            AJSC.flag({
                                type: 'success',
                                close: "manual",
                                body: 'Settings saved'
                            })

                        }, function (err) {

                            AJSC.flag({
                                type: 'error',
                                close: "manual",
                                body: 'Settings saving failed'
                            })

                        })


                }

            },

            mounted: function () {

                AJS.$(this.$refs.revision_check_tooltip).tooltip({
                    title: function () {
                        return "Number of threads used for revision checking (light IO operation)"
                    }
                });

                AJS.$(this.$refs.repository_pull_tooltip).tooltip({
                    title: function () {
                        return "Number of threads used for downloading repositories (heavy IO operation)"
                    }
                });

                AJS.$(this.$refs.converter_tooltip).tooltip({
                    title: function () {
                        return "Number of threads used for converting documents (IO and Computation operation)"
                    }
                });

                AJS.$(this.$refs.confluence_query_tooltip).tooltip({
                    title: function () {
                        return "Numbers of threads used in Confluence related operations (like indexing macros). Heavy IO (mostly database) operation"
                    }
                })

                this.$http.get(restUrl + "/executors")
                    .then(function (res) {
                        const threads = res.body

                        this.revision = threads.revisionCheckExecutor
                        this.repository = threads.repositoryPullExecutor
                        this.converter = threads.converterExecutor
                        this.confluenceQuery = threads.confluenceQueryExecutor

                        this.downloaded = true

                        // console.log(threads)
                    }, function (err) {
                        console.log(err)

                    })

            }


        }

    }

}
