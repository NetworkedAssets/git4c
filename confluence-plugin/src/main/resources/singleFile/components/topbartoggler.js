var Git4CTopBarToggler = {

    getComponent: function (Events) {

        return {

            template:
               ' <div style="position: absolute; width: 100%">'+
               '     <div id="buttonholder" style="z-index: 90; display: flex; flex-direction: row-reverse; margin: 20px">'+
               '         <button class="aui-button" ref="button" v-on:click="toggleTopBar">'+
               '             <span class="aui-icon aui-icon-small aui-iconfont-configure" v-bind:class="{ \'git4c-red-text\': editBranch }">'+
               '                Show topbar'+
               '             </span>'+
               '         </button>'+
               '     </div>'+
               ' </div>',

            props: {
                editBranch: {
                    type: Boolean
                }
            },

            mounted: function () {
                $(this.$refs.button).tooltip({
                    title: function () {
                        return "Show toolbar"
                    }
                })
            },

            methods: {
                toggleTopBar: function () {
                    Events.$emit("toggleTopbar")
                }
            }
        }
    }
}