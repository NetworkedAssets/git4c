var Git4COtherOptions = {
    getComponent: function (Events) {


        const baseUrl = AJS.contextPath() + "/rest/doc/1.0";
        const restUrl = baseUrl + "/documentation";


        return{
            template:

                   '<div style="margin-top: 80px;">'+
                   '     <hr/>'+
                   '     <h3 style="margin-bottom: 15px; margin-top:0">Other Settings</h3>'+
                   '     <div id="remove_data-div">'+
                   '         <button id="remove_data-button" v-on:click="cleanDataRequest" class="aui-button aui-button-primary" >Clean data</button>'+
                   '         <a id="remove_data-button-hint" style="margin-left: 10px;" class="aui-icon aui-icon-small aui-iconfont-error"></a>'+
                   '     </div>'+
                   '     <div id="restore_default_globs-div" style="margin-top: 10px;">'+
                   '         <button id="restore_default_globs-button" v-on:click="restoreDefaultGlobsRequest" class="aui-button aui-button-primary" >Restore default filters</button>'+
                   '         <a id="restore_default_globs-button-hint" style="margin-left: 10px;" class="aui-icon aui-icon-small aui-iconfont-error"></a>'+
                   '     </div>'+
                   '     <div>'+
                   '         <button v-on:click="cleanUnusedDataRequest" style="margin-top: 10px" class="aui-button aui-button-primary">Clean unused data</button>'+
                   '         <a id="clean_unused_data_hint" style="margin-left: 10px;" class="aui-icon aui-icon-small aui-iconfont-error"></a>'+
                   '     </div>'+
                   '</div>'
                ,
            methods:{
                cleanDataRequest: function(){
                    this.$emit("cleanDataRequest")
                },
                restoreDefaultGlobsRequest: function(){
                    this.$emit("restoreDefaultGlobsRequest")
                },
                cleanUnusedDataRequest: function() {
                    this.$emit("cleanUnusedDataRequest")
                },
                cleanData: function(){
                    this.$http.delete(restUrl).then(function(){
                        this.$emit("refreshRequest")
                    })
                },
                restoreDefaultGlobs: function(){
                    this.$http.head(restUrl + "/glob").then(function () {
                        this.$emit("refreshRequest")
                    })
                },
                cleanUnusedData: function () {
                    Vue.http.delete(restUrl + "/unused").then(function() {
                        AJSC.flag({
                            type: 'success',
                            title: "Removal successful",
                            close: 'auto',
                            persistent: false,
                            body: '<p>Cleaning unused data was finished successfully</p>'
                        });
                    }, function() {
                        AJSC.flag({
                            type: 'error',
                            title: "Error",
                            close: 'auto',
                            persistent: false,
                            body: '<p>Error occurred during cleaning unused data</p>'
                        });
                    })
                },
                setTooltips: function(){
                    AJS.$("#remove_data-button-hint").tooltip({
                        title: function () {
                            return "click here clean all data";
                        }
                    });
                    AJS.$("#restore_default_globs-button-hint").tooltip({
                        title: function () {
                            return "click here to restore default filters";
                        }
                    });
                    AJS.$("#clean_unused_data_hint").tooltip({
                        title: function () {
                            return "Click here to remove unused data";
                        }
                    });
                }
            },
            mounted: function(){
                this.setTooltips()
            }

        }
    }
}