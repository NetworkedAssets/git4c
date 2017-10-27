Vue.component("update-now", {
    template:
        '<button @click="update()" class="aui-button" :disabled="working" title="Update documentation">'+
        '    <span v-if="!working"><span class="fa fa-refresh"></span></span>'+
        '    <span v-else><span class="fa fa-refresh fa-spin"></span></span>'+
        '</button>',
    data: function () {
        return {
            working: false
        };
    },
    methods: {
        update: function () {
            const vm = this
            this.working = true;
            Events.$emit('updateStart');
            MarkupService.updateDocumentation().then(function(response){
                if (response.ok) {
                    Events.$emit('updateComplete');
                } else {
                    Events.$emit('updateError');
                }
                this.working = false;
            },
                function(err)  {
                    console.log(err);
                    this.working = false;
                    Events.$emit('updateError');
                });
        }
    }
});