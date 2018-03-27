var Git4CAdvancedSettings = {

    getComponent: function () {

        return {
            template:
               ' <div>'+
               '     <h3 style="margin-top: 20px; display: flex; justify-content: space-between">'+
               '         Advanced settings'+
               '         <div style="display: flex">'+
               '             <div style="width: 32px; height: 32px;" ref="spinner" class="button-spinner">'+
               '             </div>'+
               '             <button v-on:click="toggleShow" class="aui-button">{{buttonText}}</button>'+
               '         </div>'+
               '     </h3>'+
               '     <div v-show="shown">'+
               '         <slot></slot>'+
               '     </div>'+
               ' </div>',


            data: function () {
                return {
                    shown: false
                }
            },

            computed: {
                buttonText: function () {
                    if (this.shown) {
                        return "Hide"
                    } else {
                        return "Show"
                    }
                }
            },

            methods: {
                toggleShow: function () {
                    this.shown = !this.shown
                }
            }

        }

    }

}