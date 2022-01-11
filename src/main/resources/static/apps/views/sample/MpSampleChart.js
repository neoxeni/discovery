const MpSampleChart =  {
    name: 'mp-sample-chart',
    mixins: [],
    components: {},
    template: `
    <v-card outlined>
        <v-row>
            <v-col cols="12" md="3">
                <line-chart :chart-data="chart.data" :options="chart.options" :height="400"></line-chart>
            </v-col>
            <v-col cols="12" md="3">
                <word-cloud :chart-data="chart.wordCloud.data" :options="chart.wordCloud.options" :height="400"></word-cloud>
            </v-col>
            <v-col cols="12" md="3">
                <line-chart :chart-data="chart.data" :options="chart.options" :height="200"></line-chart>
            </v-col>
            <v-col cols="12" md="3">
                <line-chart :chart-data="chart.data" :options="chart.options" :height="400"></line-chart>
            </v-col>
        </v-row>
        
        <mp-button color="primary" label="DataSet Data 변경" icon="mdi-sync" @click="changeDataSetData"></mp-button>
        <mp-button color="primary" label="DataSet 변경" icon="mdi-sync" @click="changeDataSet"></mp-button>
        <mp-button color="primary" label="ChatData 변경" icon="mdi-sync" @click="changeChartData"></mp-button>
    </v-card>
    `,
    props: {},
    data: function () {
        return {
            chart: {
                data: {
                    labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
                    datasets: [
                        {
                            label: 'Data One',
                            backgroundColor: '#f87979',
                            data: [40, 39, 10, 40, 39, 80, 40]
                        }
                    ]
                },
                options: {responsive: true, maintainAspectRatio: false},

                wordCloud: {
                    data: {
                        labels: ['word', 'sprite', 'layout', 'algorithm'],
                        datasets: [
                            {
                                label: "Data One",
                                data: [10+10*3, 10+2*3, 10+3*3, 10+1]
                            }
                        ]
                    },
                    options: {
                        title: {
                            display: true,
                            text: "Chart.js Word Cloud"
                        },
                        plugins: {
                            legend: {
                                display: false
                            }
                        }
                    }
                }
            }
        }
    },
    computed: {},
    watch: {},
    created() {

    },
    methods: {
        getRandomArray(){
            return [
                parseInt(Math.random()*100),
                parseInt(Math.random()*100),
                parseInt(Math.random()*100),
                parseInt(Math.random()*100),
                parseInt(Math.random()*100),
                parseInt(Math.random()*100),
                parseInt(Math.random()*100)
            ]
        },

        changeDataSetData(){
            this.chart.data.datasets[0].data = this.getRandomArray()
        },

        changeDataSet(){
            this.chart.data.datasets.push({
                label: 'Data One',
                backgroundColor: '#f87979',
                data: this.getRandomArray()
            })
        },
        changeChartData(){
            Object.assign(this.chart.data, {
                labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
                datasets: [
                    {
                        label: 'Data One',
                        backgroundColor: '#f87979',
                        data: this.getRandomArray()
                    }
                ]
            })
        }
    }
};

export default MpSampleChart;