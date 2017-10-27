Vue.component("fullscreen-trigger", {
    template: "#fullscreen-trigger",
    data: function () {
        return {
            isFullScreen: false
        };
    },
    mounted: function() {
        const vm = this
        Events.$on("FullscreenModeDisable", function (){
            vm.toggle()
        })

        Events.$on('navCollapse', function (){
            $(vm.$el).css("display", "none")
        });

        Events.$on('navExpand', function (){
            $(vm.$el).css("display", "block")
        });

    },
    methods: {
        toggle: function () {
            const sidebar = $(".ia-fixed-sidebar")[0]
            if(sidebar) {
                var sidebarCollapsed = $(sidebar).hasClass("collapsed")
                this.isFullScreen = !this.isFullScreen
                if(this.isFullScreen !== sidebarCollapsed) {
                    Confluence.Sidebar.toggle()
                }
                Events.$emit("FullscreenModeToggled", this.isExpanded)
            }
        }
    }
});