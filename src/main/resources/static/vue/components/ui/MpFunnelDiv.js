const MpFunnelDivDetail = {
    name: 'mp-funnel-div-detail',
    template: `
        <div class="mp-funnel-div-detail">
            <div class="funnel-div-detail-name">{{data.name}}</div>
            <div class="funnel-div-detail-graph">
                <div :class="'funnel-item ' + data.className" :style="calcStyleWidthWithPrev">{{data.value | comma}}</div>
                <div class="funnel-divider" :style="calcStyleWidthWithPrev">
                    <div class="diagonal-left" :style="calcWingWithNext"></div>
                    <div class="diagonal-center" :style="calcWidthWithNext"><div class="diagonal-center-value">{{ratioPerNext ? ratioPerNext+'%':''}}</div></div>
                    <div class="diagonal-right" :style="calcWingWithNext"></div>
                </div>
            </div>
            <div class="funnel-div-detail-value">{{ratioPerBase}}%</div>
        </div>
    `,
    props: {
        index: {
            type: Number
        },
        base: {
            type: Object
        },
        data: {
            type: Object
        },
        prev: {
            type: Object
        },
        next: {
            type: Object
        }
    },
    computed: {
        ratioPerBase() {
            if (this.index === 0) {
                return '100.00';
            }

            let width = (this.data.value / this.base.value) * 100;
            return width.toFixed(2);
        },
        ratioPerNext() {
            if (this.next !== undefined) {
                let width = (this.next.value / this.data.value) * 100;
                return width.toFixed(2);
            }
        },
        calcStyleWidthWithPrev() {
            return 'width:' + this.ratioPerBase + '%';
        },
        calcWidthWithNext() {
            return this.ratioPerNext ? 'width:' + (this.ratioPerNext) + '%' : ''
        },
        calcWingWithNext() {
            return this.ratioPerNext ? 'width:' + ((100 - this.ratioPerNext) / 2) + '%' : ''
        }
    },
    data: function () {
        return {}
    },
}

const MpFunnelDiv = {
    name: 'mp-funnel-div',
    components: {
        MpFunnelDivDetail
    },
    template: `
        <v-card outlined class="ma-2">
            <v-card-title>
                <span class="headline">{{title}}</span>
                <v-spacer></v-spacer>
                <v-btn icon @click="innerClose()" v-show="index !== 0">
                    <v-icon>mdi-close</v-icon>
                </v-btn>
            </v-card-title>
            <v-card-text>
                <mp-funnel-div-detail v-for="(item, index) in data" :key="index"
                                      :data="item" :base="data[0]" :prev="data[index-1]" :next="data[index+1]" :index="index" ></mp-funnel-div-detail>
            </v-card-text>
        </v-card>
    `,
    props: {
        title: {
            type: String
        },
        data: {
            type: Array,
            default() {
                return [
                    {name: 'Sent', value: 5676, className: 'platform'},
                    {name: 'Viewed', value: 3872, className: 'platform'},
                    {name: 'Clicked', value: 1668, className: 'platform'},
                    {name: 'Add to Cart', value: 610, className: 'platform'},
                    {name: 'Purchased', value: 565, className: 'platform'}
                ]
            }
        },
        index: {
            type: Number
        }
    },
    data: function () {
        return {}
    },
    methods:{
        innerClose(){
            this.$emit('close',this.title, this.data, this.index)
        }
    }
}

export default MpFunnelDiv;