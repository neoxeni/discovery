const baseHelper = Vuex.createNamespacedHelpers('base');

import MpFunnelDiv from "/static/vue/components/ui/MpFunnelDiv.js";
import MpAdvertiserSelect from "/static/vue/components/ui/MpAdvertiserSelect.js";
const MpAnalyticsAdvertiser = {
    name: 'mp-analytics-advertiser',
    components: {
        MpFunnelDiv,
        MpAdvertiserSelect
    },
    template: `
    <v-card outlined>
        <mp-search>
            <div class="w-200px">
                <mp-advertiser-select label="광고주" v-model="search.client" @change="fetchData(true)" :add-items="[{text:'전체',value:''}]" outlined dense hide-details></mp-advertiser-select>
            </div>
            <div>
                <mp-date-picker label="월달력" v-model="search.month" type="month"></mp-date-picker>
            </div>
            <div class="search-form-btns">
                <mp-button color="primary" label="조회" icon="mdi-magnify" @click="fetchData(true)"></mp-button>
            </div>
        </mp-search>
        
        <v-row>
            <v-col cols="12" md="3" v-for="(item, index) in data" :key="index">
                <mp-funnel-div :title="item.title" :data="item.data" :index="index" @close="close"></mp-funnel-div>  
            </v-col>
        </v-row>
    </v-card>
    `,
    data: function () {
        return {
            url: '/LittleJoe/api/clients/',
            search: {
                client: undefined,
                name: '',
                status: undefined,
                month: undefined
            },
            data: [
                {
                    title: '2021년-10월',
                    data: [
                        {name: 'Impression', value: 2826572, className: 'platform'},
                        {name: 'Unique Impression Reach', value: 1963951, className: 'platform'},
                        {name: 'Click+View', value: 1317726, className: 'platform'},
                        {name: 'Unique Click Reach', value: 802545, className: 'platform'},
                        {name: '상품페이지', value: 85624, className: 'conversion'},
                        {name: '장바구니', value: 3528, className: 'conversion'},
                        {name: '주문완료', value: 2700, className: 'conversion'},
                    ]
                },
                {
                    title: '2021년-9월',
                    data: [
                        {name: 'Impression', value: 2826572, className: 'platform'},
                        {name: 'Unique Impression Reach', value: 1963951, className: 'platform'},
                        {name: 'Click+View', value: 1317726, className: 'platform'},
                        {name: 'Unique Click Reach', value: 802545, className: 'platform'},
                        {name: '상품페이지', value: 85624, className: 'conversion'},
                        {name: '장바구니', value: 3528, className: 'conversion'},
                        {name: '주문완료', value: 2700, className: 'conversion'},
                    ]
                },
                {
                    title: '2021년-8월',
                    data: [
                        {name: 'Impression', value: 2826572, className: 'platform'},
                        {name: 'Unique Impression Reach', value: 1963951, className: 'platform'},
                        {name: 'Click+View', value: 1317726, className: 'platform'},
                        {name: 'Unique Click Reach', value: 802545, className: 'platform'},
                        {name: '상품페이지', value: 85624, className: 'conversion'},
                        {name: '장바구니', value: 3528, className: 'conversion'},
                        {name: '주문완료', value: 2700, className: 'conversion'},
                    ]
                },
                {
                    title: '2021년-7월',
                    data: [
                        {name: 'Impression', value: 2826572, className: 'platform'},
                        {name: 'Unique Impression Reach', value: 1963951, className: 'platform'},
                        {name: 'Click+View', value: 1317726, className: 'platform'},
                        {name: 'Unique Click Reach', value: 802545, className: 'platform'},
                        {name: '상품페이지', value: 85624, className: 'conversion'},
                        {name: '장바구니', value: 3528, className: 'conversion'},
                        {name: '주문완료', value: 2700, className: 'conversion'},
                    ]
                },
                {
                    title: '2021년-6월',
                    data: [
                        {name: 'Impression', value: 2826572, className: 'platform'},
                        {name: 'Unique Impression Reach', value: 1963951, className: 'platform'},
                        {name: 'Click+View', value: 1317726, className: 'platform'},
                        {name: 'Unique Click Reach', value: 802545, className: 'platform'},
                        {name: '상품페이지', value: 85624, className: 'conversion'},
                        {name: '장바구니', value: 3528, className: 'conversion'},
                        {name: '주문완료', value: 2700, className: 'conversion'},
                    ]
                },
                {
                    title: '2021년-5월',
                    data: [
                        {name: 'Impression', value: 2826572, className: 'platform'},
                        {name: 'Unique Impression Reach', value: 1963951, className: 'platform'},
                        {name: 'Click+View', value: 1317726, className: 'platform'},
                        {name: 'Unique Click Reach', value: 802545, className: 'platform'},
                        {name: '상품페이지', value: 85624, className: 'conversion'},
                        {name: '장바구니', value: 3528, className: 'conversion'},
                        {name: '주문완료', value: 2700, className: 'conversion'},
                    ]
                }
            ]
        }
    },
    computed: {
        ...baseHelper.mapGetters([
            'getClients',
        ])
    },
    methods: {
        fetchData() {

        },
        close(title, item, index) {
            this.data.splice(index, 1)
        }
    }
};

export default MpAnalyticsAdvertiser;