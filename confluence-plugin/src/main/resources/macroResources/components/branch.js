Vue.component("branch", {

    template:
'       <div class="git4c-branch" style="display: flex; justify-content: center">'+
'           <div class="git4c-full-width" v-show="loading">'+
'               <a disabled class="aui-button aui-style-default aui-dropdown2-trigger git4c-branches-dropdown git4c-white-aui-dropdown2 git4c-full-width" aria-owns="git4c-branches" style="color: black">'+
'                   <span class="aui-icon aui-icon-small aui-iconfont-devtools-branch">Branch</span>'+
'                   Loading branches'+
'               </a>'+
'           </div> '+
'           <div class="git4c-full-width" v-show="!loading">'+
'               <a v-show="branches.length == 0" aria-owns="git4c-branches" aria-haspopup="true" class="aui-button aui-style-default aui-dropdown2-trigger git4c-branches-dropdown git4c-white-aui-dropdown2 git4c-full-width" style="color: black; margin-left: 0;">'+
'                   <span class="aui-icon aui-icon-small aui-iconfont-devtools-branch">Branch</span>'+
'                   No Branches Present'+
'               </a>'+
'               <a ref="branches_dropdown" v-show="branches.length > 0" href="" aria-controls="git4c-branches" aria-haspopup="true" class="aui-button aui-style-default aui-dropdown2-trigger git4c-branches-dropdown git4c-white-aui-dropdown2" style="color: black; margin-left: 0">'+
'                   <div class="git4c-current-branch-div">'+
'                       <span class="aui-icon aui-icon-small aui-iconfont-devtools-branch">Branch</span>'+
'                       {{currentBranch}}'+
'                   </div>'+
'               </a>'+
'               <div id="git4c-branches" class="aui-style-default aui-dropdown2" style="max-height: 300px; overflow: auto">'+
'                   <ul class="aui-list-truncate" >'+
'                       <li v-for="branch in branches">'+
'                           <a v-on:click="onChange(branch)"'+
'                               v-bind:class="{checked: branch == currentBranch, \'aui-dropdown2-checked\': branch == currentBranch}"'+
'                               class="aui-dropdown2-radio">{{branch}}</a>'+
'                       </li>'+
'                   </ul>'+
'               </div>'+
'           </div>'+
'       </div>',

    data: function () {
        return {
            branches: [],
            currentBranch: null,
            selectedBranch: "",
            loading: true
        }
    },

    mounted: function () {
        const vm = this
        MarkupService.getBranches().then(function (branches) {
            vm.loading = false
            if (branches.allBranches.length == 0) {
                Events.$emit("errorOccured", "no_branches")
            } else {
                const allBranches = branches.allBranches
                const currentBranch = vm.$route.query.branch ? vm.$route.query.branch : branches.currentBranch ? branches.currentBranch : "master"

                vm.$nextTick(function() {

                    const dropdown = $(vm.$refs["branches_dropdown"])

                    dropdown.tooltip('destroy');

                    //TODO: Replace with refs (which doesn't work for some reason) - they don't work because of v-if
                    dropdown
                        .tooltip({
                            title: function () {
                                return currentBranch
                            }
                        })

                })

                allBranches.sort()
                vm.branches = allBranches
                vm.currentBranch = currentBranch
                vm.selectedBranch = currentBranch
            }
        }, function(error) {
            if(error.status == 404){
                Events.$emit("errorOccured", "repository_removed")
            }
            else {
                error.text().then(function () {
                        Events.$emit("errorOccured", "no_branches")
                })
            }
        })

        Events.$on('branchChangeRequest', function(branch) {
            if (vm.branches.includes(branch) && branch !== vm.selectedBranch) {
                vm.onChange(branch)
            }
        })
    },
    methods: {
        onChange: function (selected) {
            const vm =this
            vm.selectedBranch = selected
            vm.currentBranch = selected
            vm.$root.pushBranch(selected)
            Events.$emit('branchChanging')
            MarkupService.temporary(vm.selectedBranch).then(function(id)  {
                Events.$emit('branchChanged', id)
            });

            vm.$nextTick(function()  {
                AJS.$(vm.$el).find(".git4c-branches-dropdown")
                    .tooltip('destroy');

                    AJS.$(vm.$el).find(".git4c-branches-dropdown")
                        .tooltip({
                            title: function () {
                                return selected
                            }
                        })
                })
        }
    }
})