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
                container: {
                    type: String,
                    default: undefined
                },
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
                '           <a style="color: #000000;" href="javascript:void(0)" v-on:click="anchor(data.anchorName)" v-html="data.name"></a>' +
                '       </span>' +
                '    </li>' +
                '    <ol v-show="data.children.length > 0 && open" style="margin: 0; padding-left: 15px;" v-bind:class="{ \'git4c-toc-first-ul\': level === 0 }">' +
                '        <toc v-for="t in data.children" :parent="parent" :data="t" :container="container" :level="level+1" ></toc>' +
                '    </ol>' +
                '</div>'
            ,
            methods: {
                triggerToggle: function () {
                    this.open = !this.open
                },
                anchor: function (id) {
                    if (Events) {
                        //Used only on multi file macro
                        Events.$emit("pushAnchor", id)
                    }

                    if (this.container) {
                        //Used in dialog preview
                        const el = $(this.container)[0];
                        const topPos = $(el).find("[name='" + id + "']")[0].offsetTop;
                        el.scrollTop = topPos - 10
                    } if (this.parent) {
                        //Used in single page macro
                        const el = $(this.parent)[0];
                        const topPos = $(el).find("[name='" + id + "']")[0].offsetTop;
                        window.scrollTo(0, el.offsetTop + topPos)
                    } else {
                        const top = document.getElementsByName(id)[0].offsetTop;
                        window.scrollTo(0, top);
                    }
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