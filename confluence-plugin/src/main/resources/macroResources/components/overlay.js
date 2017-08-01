Vue.component("overlay", {
    template: "#overlay",
    data: function () {
        return {
            //It's intentional - first event isn't fast enough
            loading: true
        };
    },

    mounted() {
        Events.$on('updateStart', () => {
            this.loading = true;
        });
        Events.$on('treeLoaded', () => {
            this.loading = false;
        });
        Events.$on('updateComplete', () => {
            this.loading = false;
        });
        Events.$on('branchChanging', () => {
            this.loading = true;
        });
        Events.$on('updateError', () => {
            this.loading = false;
        });
    }
});