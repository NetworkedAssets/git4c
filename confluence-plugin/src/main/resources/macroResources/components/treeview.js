//noinspection JSValidateTypes
Vue.component('treeview', {
    template: '#treeview',
    data: function () {
        return {
            isExpanded: true
        };
    },
    props: {
        /**
         * Unique identifier for treeview.
         * @var string
         */
        id: {
            Type: String,
            default: 'tv_' + Math.ceil(Math.random() * 100000)
        },
        /**
         * Value of the selected node in the tree.
         * @var mixed
         */
        value: [String, Number],
        /**
         * Initial tree composition.
         * @var array
         */
        model: {
            Type: Array,
            default: function () {
                return [];
            }
        },
        /**
         * Additional CSS class to apply to component.
         * @var string
         */
        class: {
            Type: String,
            default: ''
        },
        /**
         * Name of the child nodes property.
         * @var string
         */
        children: {
            Type: String,
            default: 'nodes'
        },
        /**
         * Name of the property holding the node name.
         * @var string
         */
        labelname: {
            Type: String,
            default: 'label'
        },
        /**
         * Name of the property holding the node value.
         * @var string
         */
        valuename: {
            Type: String,
            default: 'value'
        },
        /**
         * Parent node model index.
         * @var int
         */
        parent: {
            Type: Number,
            default: undefined
        }
    },

    mounted() {
        Events.$on('navCollapse', () => {
            $("#content-nav").removeClass("expanded").addClass("collapsed");
            this.isExpanded = false;
        });
        Events.$on('navExpand', () => {
            $("#content-nav").removeClass("collapsed").addClass("expanded");
            this.isExpanded = true;
        });
    },


    methods: {
        /**
         * Selects a node from tree view.
         * @param int   index Tree index selected.
         * @param mixed value Value selected.
         */
        select: function (index, value) {
            // Unselect from current level, children and parents
            this.toggleOpen(index);
            this.value = value;
            if (!this.areValidNodes(this.model[index][this.children])) {
                this.$router.push('/' + encodeURIComponent(this.model[index][this.valuename]));
            }
        },


        /**
         * Toggles open / close node.
         * @param int index Index to open
         */
        toggleOpen: function (index) {
            // Return if no children
            if (!this.areValidNodes(this.model[index][this.children]))
                return;
            // Init
            if (this.model[index].isOpened == undefined)
                Vue.set(this.model[index], 'isOpened', this.hasSelectedChild(index));
            // General
            Vue.set(this.model[index], 'isOpened', !this.model[index].isOpened);
        },
        /**
         * Returns flag indicating if nodes are valid or not.
         * @param array nodes Nodes to validate.
         */
        areValidNodes: function (nodes) {
            return nodes != undefined
                && Object.prototype.toString.call(nodes) === '[object Array]'
                && nodes.length > 0;
        },
        /**
         * Returns flag indicating if tree view has a node selected.
         * @return bool
         */
        hasSelected: function () {
            // Check children
            for (var i in this.model) {
                if (this.isSelected(i) || this.hasSelectedChild(i))
                    return true;
            }
            return false;
        },
        /**
         * Returns flag indicating if node at specified index has a child selected or not.
         * @param int index Index to check
         * @return bool
         */

        hasSelectedChild: function (index) {
            for (var i in this.$children) {
                if (this.$children[i].parent == index
                    && this.$children[i].hasSelected()
                )
                    return true;
            }
            return false;
        },

        /**
         * Returns flag indicating if node at specified index is selected or not.
         * @param int index Index to check
         * @return bool
         */
        isSelected: function (index) {
            return this.value && this.model[index][this.valuename] == this.value;
        },

        isSelectedFile: function (index) {
            return "/" + this.model[index][this.valuename] == decodeURIComponent(this.$route.path);
        },

        /**
         * Returns flag indicating if node is opened or not.
         * @param int index Index to check
         * @return bool
         */
        isOpened: function (index) {
            return (this.model[index].isOpened != undefined && this.model[index].isOpened)
                || this.hasSelectedChild(index);
        }
    }
});
