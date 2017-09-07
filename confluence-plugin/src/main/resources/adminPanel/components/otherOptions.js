var Git4COtherOptions = {
    getComponent: function (Events) {


        const baseUrl = AJS.contextPath() + "/rest/doc/1.0";
        const restUrl = baseUrl + "/documentation";


        return{
            template:
                `                
                   <div style="margin-top: 80px;">
                        <hr/>
                        <h3 style="margin-bottom: 15px; margin-top:0">Other Settings</h3>
                        <div id="remove_data-div">
                            <button id="remove_data-button" v-on:click="cleanDataRequest" class="aui-button aui-button-primary" >Clean data</button>
                            <a id="remove_data-button-hint" style="margin-left: 10px;" class="aui-icon aui-icon-small aui-iconfont-error"></a>
                        </div>
                        <div id="restore_default_globs-div" style="margin-top: 10px;">
                            <button id="restore_default_globs-button" v-on:click="restoreDefaultGlobsRequest" class="aui-button aui-button-primary" >Restore default filters</button>
                            <a id="restore_default_globs-button-hint" style="margin-left: 10px;" class="aui-icon aui-icon-small aui-iconfont-error"></a>
                        </div>
                    </div>
                `,
            methods:{
                cleanDataRequest(){
                    this.$emit("cleanDataRequest")
                },
                restoreDefaultGlobsRequest(){
                    this.$emit("restoreDefaultGlobsRequest")
                },
                cleanData(){
                    Vue.http.delete(restUrl).then(()=>{
                        this.$emit("refreshRequest")
                    })

                },
                restoreDefaultGlobs(){
                    Vue.http.head(restUrl + "/glob").then(()=>{
                        this.$emit("refreshRequest")
                    })
                },
                setTooltips(){
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
                }
            },
            mounted(){
                this.setTooltips()
            }

        }
    }
}