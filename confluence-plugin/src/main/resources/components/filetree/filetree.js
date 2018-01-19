var Git4CFileTree = {

    getComponent: function (Bus) {

        return {
            name: "git4c-filetree",
            props: {
                dirclickable: {
                    type: Boolean
                },
                level: {
                    default: 0,
                    type: Number
                },
                data: {
                    type: Object
                }
            },
            data: function () {
                return {
                    open: this.level === 0,
                    selected: false
                }
            },
            template:
        '<span v-if="data">'+
        '    <li style="list-style-type:none" v-if="data.name">'+
        '           <div style="white-space: nowrap;">'+
        '               <a v-if="data.children.length > 0" href="javascript:void(0)">'+
        '                    <i style="color: #b4b4b4;" class="icon" v-bind:class="{\'icon-section-opened\': open, \'icon-section-closed\': !open}" v-on:click="triggerToggle()" ></i>'+
        '               </a>'+
        '               <i v-else style="color: #b4b4b4;" class="aui-icon aui-icon-small aui-iconfont-custom-bullet"></i> '+
        '               <a v-if="data.children.length > 0 && !dirclickable" style="color: #000000;" href="javascript:void(0)" v-on:click="triggerToggle()" v-html="data.name"></a>'+
        '               <a v-else style="color: #000000;" v-bind:class="{\'git4c-filetree-selected\': selected}" href="javascript:void(0)" v-on:click="selectFile()" v-html="data.name"></a>'+
        '           </div>'+
        '    </li>'+
        '    <ol v-show="open" style="margin: 0; padding-left: 10px" v-bind:class="{ \'git4c-first-ul\': level === 0 }">'+
        '        <git4c-filetree v-for="t in data.children" :data="t" :level="level+1" :dirclickable="dirclickable"></git4c-filetree>'+
        '    </ol>'+
        '</span>',
            methods: {
                triggerToggle: function() {
                    this.open = !this.open
                },
                anchor: function (id) {
                    const top = document.getElementsByName(id)[0].offsetTop;
                    window.scrollTo(0, top);
                },
                selectFile: function() {
                    // alert("From file tree " + this.data.fullName)
                    if (!this.selected) {
                        const selectedFile = this.data.fullName
                        Bus.$emit("selectedFile", selectedFile)
                        this.selected = true
                    }
                },
                openTree: function () {
                    this.open = true
                    this.$children.forEach(function (it) {
                        it.openTree()
                    })
                },
                closeTree: function () {
                    if(this.level !== 0) {
                        this.open = false
                    }
                    this.$children.forEach(function (it) {
                        it.closeTree()
                    })

                }
            },
            mounted: function () {
                const vm = this
                Bus.$on("selectedFile", function() {
                    vm.selected = false
                })
            }
        }
    }
}