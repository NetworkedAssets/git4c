var Git4CPagesWithMacroList = {
    getComponent: function (Events) {

        const baseUrl = AJS.contextPath() + "/rest/doc/1.0";
        const restUrl = baseUrl + "/documentation";

        const contextPath = AJS.contextPath()

        const PRE_DOWNLOADING = "PRE_DOWNLOADING"
        const DOWNLOADING = "DOWNLOADING"
        const DOWNLOADING_ENDED = "DOWNLOADING_ENDED"

        return {
            data: function () {
                return {
                    spaces: undefined,
                    macros: undefined,
                    statuses: undefined,
                    contextPath: contextPath,
                    shown: false,
                    loading: true,
                    downloadingState: PRE_DOWNLOADING
                }
            },
            template:
                "<div>" +
                '    <div>'+
                '    <h3 style="margin-top: 20px; display: flex; justify-content: space-between">'+
                '        Macros in system'+
                '        <div style="display: flex">'+
                '            <div style="width: 32px; height: 32px;" ref="spinner" class="button-spinner">'+
                '            </div>'+
                '            <button v-on:click="toggleShow" class="aui-button">{{buttonText}}</button>'+
                '        </div>'+
                '    </h3>'+
                '    </div>'+
                '    <div v-show="shown">' +
                    '<table id="delayedSortedTable" ref="table" class="aui" v-show="listAvailable">' +
                        "<thead>" +
                            '<tr>'+
                                "<th>Space</th>" +
                                "<th>Page</th>" +
                                "<th>Type</th>" +
                                "<th>Status</th>" +
                            '</tr>'+
                        "</thead>" +
                        "<tbody>" +
                            '<tr v-for="(macro, index) in macros" >' +
                                '<td><a :href="contextPath + macro.spaceUrl">{{macro.spaceName}}</a></td>' +
                                '<td><a :href="contextPath + macro.pageUrl">{{macro.pageName}}</a></td>' +
                                "<td>" +
                                    "<span v-if=\"macro.macroType === 'SINGLEFILEMACRO'\" :title=\"'Single page macro: ' + macro.macroFile\" class='aui-icon aui-icon-small aui-iconfont-doc tooltipable'>Single page macro</span>" +
                                    "<span v-if=\"macro.macroType === 'MULTIFILEMACRO'\" title='Multi file macro' class='aui-icon aui-icon-small aui-iconfont-nav-children tooltipable'>Multi file macro</span>" +
                                "</td>" +
                                "<td>" +
                                    "<span v-if=\"statuses[index] == 'UNKNOWN'\" class=\"aui-icon aui-icon-wait\">Loading...</span>" +
                                    "<span v-else>" +
                                        "<span v-if=\"statuses[index] == 'PASSED'\" class=\"aui-icon aui-icon-small aui-iconfont-approve\">Tests passed</span>" +
                                        "<span v-else title=\"Error occured. Go to macro to see error details\" class=\"aui-icon aui-icon-small aui-iconfont-error tooltipable\">Error</span>" +
                                    "</span>" +
                                "</td>" +
                            "</tr>" +
                        "</tbody>" +
                    "</table>" +
                '    <div v-show="!loading && !listAvailable" class="aui-message aui-message-info">' +
                '        <p class="title">' +
                '            <strong>No items</strong>' +
                '        </p>' +
                '    </div>' +
                    "</div>" +
                "</div>",
            watch: {
                spaces: function () {
                   this.handleSpacesChange()
                }
            },
            computed: {
                buttonText: function () {
                    if (this.shown) {
                        return "Hide"
                    } else {
                        return "Show"
                    }
                },
                listAvailable: function(){
                    return this.macros && this.macros.length
                }
            },
            methods: {
                handleSpacesChange: function () {
                    const spaces = this.spaces

                    const vm = this

                    if (spaces) {

                        const macros = [];
                        const statuses = []

                        spaces.forEach(function(space) {
                            space.pages.forEach(function(page) {
                                page.macros.forEach(function(macro) {

                                    macros.push({
                                        spaceName: space.name,
                                        spaceUrl: space.url,
                                        pageName: page.name,
                                        pageUrl: page.url,
                                        macroId: macro.id,
                                        macroType: macro.type,
                                        macroFile: macro.file
                                    })

                                    statuses.push("UNKNOWN")

                                })
                            })
                        })
                        // return macros
                        this.macros = macros
                        this.statuses = statuses

                        macros.forEach(function(macro, index) {

                            Git4CApi.verifyMacro(macro.macroId)
                                .then(function () {
                                    Vue.set(vm.statuses, index, "PASSED")
                                })
                                .catch(function () {
                                    Vue.set(vm.statuses, index, "ERROR")
                                    vm.$nextTick(function() {
                                        $(vm.$el).find(".tooltipable").tooltip()
                                    })
                                })

                        })

                    }

                },
                toggleShow: function () {

                    this.shown = !this.shown

                    const vm = this

                    if (this.shown && this.downloadingState === PRE_DOWNLOADING) {

                        AJS.$(this.$refs["spinner"]).spin()

                        this.downloadingState = DOWNLOADING

                        Git4CApi.getAllSpaces()
                            .then(function (spaces) {
                                vm.spaces = spaces
                                vm.loading = false
                                vm.$nextTick(function () {
                                    AJS.tablessortable.setTableSortable(AJS.$(vm.$refs["table"]));
                                    $(vm.$el).find(".tooltipable").tooltip()
                                    vm.downloadingState = DOWNLOADING_ENDED
                                    AJS.$(vm.$refs["spinner"]).spinStop()
                                })
                            })
                            .catch(function(err) {
                                console.log("PagesWithMacroList error: " + err)
                            })

                    }

                }
            }
        }
    }
}
