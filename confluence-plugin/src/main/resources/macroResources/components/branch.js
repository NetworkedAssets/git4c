Vue.component("branch", {

    template: "#branch",

    data: function () {
        return {
            branches: [],
            currentBranch: null,
            selectedBranch: ""
        }
    },

    mounted: function () {
        MarkupService.getBranches().then((branches) => {
            const allBranches = branches.allBranches
            const currentBranch = this.$route.params.branch ? this.$route.params.branch : branches.currentBranch? branches.currentBranch : "master"

            allBranches.sort()
            this.branches = allBranches
            this.currentBranch = currentBranch
            this.selectedBranch = currentBranch
        })
    },
    methods: {
        onChange: function (selected) {
            this.selectedBranch = selected
            this.currentBranch = selected
            this.$root.pushBranch(selected)
            var newBranch = {
                branch: this.selectedBranch
            }
            Events.$emit('branchChanging')
            MarkupService.temporary(newBranch).then((id) => {
                Events.$emit('branchChanged', id, newBranch.branch)
            });
        }
    }
})