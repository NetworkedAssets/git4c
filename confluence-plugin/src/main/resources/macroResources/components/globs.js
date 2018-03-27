Vue.component("globs", {
    template:
        '<div v-if="hasGlobs" style="width: 210px;">'+
        '    <h3 class="header" style="margin-bottom: 0; margin-top: 20px">Displaying only</h3> '+
        '    <span class="git4c-globs-container">'+
        '        <span v-for="glob in globs">'+
        '            <span class="aui-lozenge">{{ glob.prettyName }}</span> '+
        '            <span>&nbsp;</span>'+
        '        </span>'+
        '    <span>'+
        '</div>'
    ,
    data: function () {
        return {
            globs: []
        }
    },
    computed: {
        hasGlobs: function () {
            return this.globs && this.globs.length > 0
        }
    },
    mounted: function () {
        MarkupService.getGlobs().then(function (globs) {
            this.globs = globs
        })
    }
})
