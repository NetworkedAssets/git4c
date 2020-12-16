var Git4CToc = {

    /**
     * When adding new props remember to also add them to #45 (this component is recursive)
     */
    getComponent: function(Events) {

        return {
            name: "toc",
            props: {
                level: {
                    default: 0,
                    type: Number
                },
                data: {
                    type: Object
                },
                // container: {
                //     type: String,
                //     default: undefined
                // },
                parent: {
                    type: String,
                    default: undefined
                }
            },
            data: function () {
                return {
                    open: this.level !== 1
                }
            },
            template:
                '<div v-if="data">' +
                '    <h1 v-if="level === 0 && data.children.length > 0" style="margin-bottom: 10px">Table of Contents</h1>' +
                '    <li style="list-style-type:none" v-if="data.name">' +
                '       <span class="git4c-toc-li-content">' +
                '           <a v-if="data.children.length > 0" href="javascript:void(0)">' +
                '                <i style="color: #b4b4b4;" class="aui-icon aui-icon-small" v-bind:class="{\'aui-iconfont-expanded\': open, \'aui-iconfont-collapsed\': !open}" v-on:click="triggerToggle(this)" ></i>' +
                '           </a>' +
                '           <i v-else style="color: #b4b4b4;" class="aui-icon aui-icon-small aui-iconfont-custom-bullet"></i> ' +
                '           <a style="color: #000000;" href="javascript:void(0)" v-on:click="emitAnchor(data.anchorName)" v-html="data.name"></a>' +
                '       </span>' +
                '    </li>' +
                '    <ol v-show="data.children.length > 0 && open" style="margin: 0; padding-left: 15px;" v-bind:class="{ \'git4c-toc-first-ul\': level === 0 }">' +
                '        <toc @anchor="anchor" v-for="t in data.children" :parent="parent" :data="t" :level="level+1" v-bind:key="t.anchorName"></toc>' +
                '    </ol>' +
                '</div>'
            ,
            methods: {
                triggerToggle: function () {
                    this.open = !this.open
                },
                anchor: function (data) {
                    this.$emit('anchor', data)
                },
                emitAnchor: function (id) {

                    this.$emit("anchor", id)

                    // if (Events) {
                    //     //Used only on multi file macro
                    //     Events.$emit("pushAnchor", id)
                    // }
                    //
                    // let element
                    //
                    // if (this.container) {
                    //     //Used in dialog preview
                    //     const el = $(this.container)[0];
                    //     element = Git4CUtils.findElementToScrollTo(el, id);
                    //     // el.scrollTop = topPos - 10
                    // } if (this.parent) {
                    //     //Used in single page macro
                    //     const el = $(this.parent)[0];
                    //     element = Git4CUtils.findElementToScrollTo(el, id);
                    //     // window.scrollTo(0, el.offsetTop + topPos)
                    // } else {
                    //     element = Git4CUtils.findElementToScrollTo(document, id)
                    //     // Git4CUtils.scrollTo(document, id)
                    // }
                    //
                    // element.scrollIntoView()
                    //
                    // const scrolledY = window.scrollY;
                    //
                    // if(scrolledY){
                    //     window.scroll(0, scrolledY - 50);
                    // }

                }
            },
            watch: {
                data: function () {
                    this.open = this.level !== 1
                }
            }
        }
    }

}
