//Taken from https://vuejs.org/v2/examples/select2.html
var Git4CSelect2Single = {
    getComponent: function () {
       return {
           props: ['options', 'value'],
           template:
           '<select>'+
           '    <slot></slot>'+
           '</select>'
           ,
           mounted: function () {
               var vm = this
               $(this.$el)
               // init select2
                   .auiSelect2({ data: this.options })
                   .val(this.value)
                   .trigger('change')
                   // emit event on change.
                   .on('change', function () {
                       vm.$emit('input', this.value)
                   })
           },
           watch: {
               value: function (value) {
                   // update value
                   this.$nextTick(function() {
                       // alert(value)
                       $(this.$el).auiSelect2("val", value);
                   })
               },
               options: function (options) {
                   // update options
                   $(this.$el).select2({ data: options })
               }
           },
           destroyed: function () {
               $(this.$el).off().auiSelect2('destroy')
           }
       }
    }
}
