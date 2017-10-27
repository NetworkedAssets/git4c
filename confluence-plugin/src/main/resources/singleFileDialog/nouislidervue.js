//Taken from https://vuejs.org/v2/examples/select2.html
Vue.component('git4cnouislider', {

    template:
    '<div>'+
    '    <div ref="slider"></div>'+
    '    <div style="margin-top: 20px">'+
    '        <input ref="startinput" style="width: 47%" type="number" name="start" value="10">'+
    '        <input ref="endinput" style="width: 47%" type="number" name="end" value="30">'+
    '    </div>'+
    '</div>',
    props: ['numberOfLines'],
    mounted: function () {

        var vm = this
        // this.$el.

        const slider = this.$refs.slider;

        const startInput = this.$refs.startinput;
        const endInput = this.$refs.endinput;

        var numberOfLines = this.numberOfLines;

        if (!numberOfLines || numberOfLines === 1) {
            numberOfLines = 2
        }

        const update = function () {
            vm.$emit('input', [startInput.value, endInput.value])
        }

        noUiSlider.create(slider, {
            start: [(numberOfLines) / 4, (numberOfLines * 3) / 4],
            connect: true,
            step: 1,

            range: {
                'min': 1,
                'max': numberOfLines
            },
            format: wNumb({
                decimals: 0
            })
        });

        slider.noUiSlider.on('update', function (values, handle) {
            const start = values[0]
            const end = values[1]

            if (!isNaN(start) && !isNaN(end)) {
                startInput.value = start
                endInput.value = end
                update()
            }

        })

        slider.getElementsByClassName('noUi-connect')[0].style.background = '#3572b0'

        startInput.addEventListener('change', function () {
            slider.noUiSlider.set([this.value, null])
            update()
        })

        endInput.addEventListener('change', function () {
            slider.noUiSlider.set([null, this.value])
            update()
        })

    },
    watch: {
        numberOfLines: function (numberOfLines) {
            const slider = this.$refs.slider;

            slider.noUiSlider.updateOptions({
                start: [(numberOfLines) / 4, (numberOfLines * 3) / 4],
                range: {
                    'min': 1,
                    'max': numberOfLines
                }
            })
        }
    }

})