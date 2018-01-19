Vue.component("nav-collapse", {
    template: "#nav-collapse",
    data: function () {
        return {
            isExpanded: true
        };
    },
    mounted: function() {
        const vm = this
        Events.$on("toggleSideBar", function () {
            vm.expandedtoggle()
        })
        $($(this.$el).find("button")[0]).tooltip()
    },
    methods: {
        expandedtoggle: function () {
            //Fix FF bug
            console.log("Toggle")
            this.isExpanded ? this.collapse() : this.expand();
        },
        expand: function () {
            this.isExpanded = true;
            Events.$emit("StickyToolbarToogled")
            setTimeout(function(){
                Events.$emit('navExpand');
            }, 100)

        },
        collapse: function () {
            this.isExpanded = false;
            Events.$emit('navCollapse');
            setTimeout(function(){
                Events.$emit("StickyToolbarToogled")
            }, 500)
        }
    }
});