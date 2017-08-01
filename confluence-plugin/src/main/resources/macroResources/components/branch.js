Vue.component("branch", {

    template: "#branch",

    data: function () {
        return {
            branches: [],
            currentBranch: null,
            selectedBranch: ""
        }
    },

    mounted: function() {
        MarkupService.getBranches().then((branches) => {
            const allBranches = branches.allBranches
            const currentBranch = branches.currentBranch ? branches.currentBranch : "master"

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
            var newBranch = {
                branch: this.selectedBranch
            }
            Events.$emit('branchChanging')
            MarkupService.createTemporary(newBranch).then((id) => {
                Events.$emit('branchChanged', id)
            });
        }
    }
})