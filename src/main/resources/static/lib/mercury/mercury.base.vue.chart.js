/**
 * ChartJs vue component
 *
 * https://jeongwooahn.medium.com/vue-js-에서-사용할-차트-추천-4390f704bc7b
 *
 * https://www.chartjs.org/
 * https://github.com/chartjs/awesome
 *
 * https://github.com/apertureless/vue-chartjs
 *
 * */

(function (Vue, VueChartJs) {

    Vue.component('line-chart', {
        extends: VueChartJs.Line,
        props: ['chartData', 'options'],
        mounted() {
            this['renderChart'](this.chartData, this.options)
        },
        watch: {
            "chartData": {
                handler: function () {
                    console.log('cccc')
                    this.$data['_chart'].update()
                },
                deep: true
            },
        }
    })

    Vue.component('word-cloud', {
        props: ['chartData', 'options', 'height'],
        template: `
            <div>
                <canvas ref="canvas" :height="height"></canvas>
            </div>
        `,
        mounted() {
            this['renderChart'](this.chartData, this.options)
        },
        watch: {
            "chartData": {
                handler: function () {
                    this.$data['_chart'].update()
                },
                deep: true
            },
        },
        data() {
            return {
                _chart: undefined
            }
        },
        methods: {
            renderChart(chartData, options){
                this._chart = new Chart(this.$refs['canvas'].getContext("2d"), {
                    type: "wordCloud",
                    data: chartData,
                    options: options
                });
            }
        }
    })


})(window.Vue, window['VueChartJs']);