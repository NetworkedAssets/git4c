//noinspection JSValidateTypes
Vue.component('treeview', {
    template:
            '<div class="treeview">'+
            '    <div class="node-data" v-for="(node,index) in model">'+
            '        <!--<div class="node" v-bind:class="{active: isSelectedFile(index)}" @click.prevent="select(index, node[valuename])">-->'+
            '        <div class="node" v-bind:class="{active: node.fullName === selectedFile}">'+
            '            <i @click.prevent="select(index)" class="aui-icon aui-icon-small" '+
            '                v-bind:class="{'+
            '                    \'aui-iconfont-expanded\': isDirectory(node) && isExpanded[index],'+
            '                    \'aui-iconfont-collapsed\': isDirectory(node) && !isExpanded[index],'+
            '                    \'aui-iconfont-custom-bullet\':!isDirectory(node)'+
            '                }">'+
            '            </i>'+
            '            <span @click.prevent="selectAndOpen(index)">'+
            '                <label>{{node.name}}</label>'+
            '            </span>'+
            '        </div>'+
            '        <div v-if="isDirectory(node)" class="children" v-show="isExpanded[index]">'+
            '            <div class="margin"></div>'+
            '            <div class="nodes">'+
            '                <treeview :model="node.children"></treeview>'+
            '            </div>'+
            '        </div>'+
            '    </div>'+
            '</div>'
    ,
    data: function () {
        return {
            isExpanded: this.model.map(function() {
                return false
            }),
            selectedFile: undefined
        };
    },
    props: {
        model: {
            Type: Array,
            default: function () {
                return [];
            }
        },
    },

    mounted: function () {
        const vm = this
        Events.$on('navCollapse', function () {
            $("#content-nav").removeClass("expanded").addClass("collapsed");
        });
        Events.$on('navExpand', function () {
            $("#content-nav").removeClass("collapsed").addClass("expanded");
        });
        Events.$on("fileSelected", function (file) {
            vm.selectedFile = file
        })
        Events.$on("select", function (file) {
            vm.model.forEach(function (item, index) {
                if(item.fullName === file) {
                    if (item.type === "DOCITEM") {
                        vm.selectedFile = item.fullName
                    } else {
                        Vue.set(vm.isExpanded, index, true)
                    }
                }
            })
        })

        Events.$on("TreeviewInvalidate", function () {
            vm.invalidate()
        })
    },

    methods: {

        invalidate: function () {
            const fullName = this.$route.params.fullName
            this.selectedFile = fullName
            const vm = this
            //Open "Route" to this file
            this.model.forEach(function (item, index) {
                if (fullName.startsWith(item.fullName)) {
                    vm.expandSelect(index)
                }
            })
        },

        expandSelect: function (index) {
            const item = this.model[index]
            if (item.type === "DOCITEM") {
                this.selectedFile = item.fullName
            } else {
                Vue.set(this.isExpanded, index, true)
            }
        },

        select: function (index) {
            this.toggle(index)
            if (this.model[index].type === "DOCITEM") {
                this.$router.push('/' + encodeURIComponent(this.model[index].fullName) + "&" + encodeURIComponent(this.$route.params.branch));
            }
        },

        selectAndOpen: function (index) {
            this.toggle(index)
            const item = this.model[index]
            if (item.type === "DOCITEM") {
                this.$router.push('/' + encodeURIComponent(this.model[index].fullName) + "&" + encodeURIComponent(this.$route.params.branch));
            }
            if(item.type === "DIR" && this.isExpanded[index]){
                if(item.children.length != 0){
                    const child = item.children.filter(function(it) { return it.type === "DOCITEM"})[0]
                    if(child) {
                        if (child.type === "DOCITEM") {
                            Events.$emit("fileSelected", child.fullName)
                            this.$router.push('/' + encodeURIComponent(child.fullName) + "&" + encodeURIComponent(this.$route.params.branch));
                        }
                    }
                }
            }

        },

        toggle: function (index) {
            const item = this.model[index]
            if (item.type === "DOCITEM") {
                Events.$emit("fileSelected", item.fullName)
            } else {
                Vue.set(this.isExpanded, index, !this.isExpanded[index])
            }
        },

        isDirectory: function (node) {
            return this.areValidNodes(node.children)
        },

        /**
         * Returns flag indicating if nodes are valid or not.
         * @param array nodes Nodes to validate.
         */
        areValidNodes: function (nodes) {
            return nodes != undefined
                && Object.prototype.toString.call(nodes) === '[object Array]'
                && nodes.length > 0;
        }
    }
});
