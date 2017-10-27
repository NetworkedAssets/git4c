Vue.component("sticky-toolbar-trigger", {
    template: "#sticky-toolbar-trigger",
    data: function () {
        return {
            sticky: false
        };
    },
    mounted: function(){
        Events.$on('navCollapse', function(){
            $(this.$el).css("display", "none")
        });

        Events.$on('navExpand', function(){
            $(this.$el).css("display", "block")
        });
    },
    methods: {
        toggle: function () {
            Events.$emit("StickyToolbarToogled", this.isExpanded)
        }
    }
});