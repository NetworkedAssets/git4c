Vue.component("nav-collapse", {
    template: "#nav-collapse",
    data: function () {
        return {
            isExpanded: true
        };
    },
    mounted() {
    },
    methods: {
        toggle: function () {
            //Fix FF bug
            $("#git4c-nav-collapse-span").css("position", "absolute");
            this.isExpanded ? this.collapse() : this.expand();
        },
        expand: function () {
            this.isExpanded = true;
            Events.$emit('navExpand');

        },
        collapse: function () {
            this.isExpanded = false;
            Events.$emit('navCollapse');
        }
    }
});