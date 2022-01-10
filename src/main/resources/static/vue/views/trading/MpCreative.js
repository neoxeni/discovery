const baseHelper = Vuex.createNamespacedHelpers('base');

const MpCreative = {
    name: 'mp-creative',
    mixins: [],
    template: `
    <v-card outlined>
        <mp-search>
            <div class="w-190px">
                <v-select v-model="clients.select" label="광고주" @change="changeClients" :items="mercury.base.util.concatArray(clients.items,[{text:'직접입력',value:''}], false)" 
                          return-object outlined dense hide-details></v-select>
            </div>
            <div v-show="clients.select === ''">
                <v-text-field v-model="search.advertiser_id" label="Advertiser ID" @keyup.enter="fetchData(true)" outlined dense hide-details></v-text-field>
            </div>
            <div class="search-form-btns">
                <mp-button color="primary" label="조회" icon="mdi-magnify" @click="fetchData(true)"></mp-button>
            </div>
        </mp-search>
        <v-data-table dense fixed-header :height="$settings.datatable.rows20" :footer-props="$settings.datatable.footer20" :headers="tbl.headers" :items="tbl.items">
            <template v-slot:item.io_id="{ item }">
                <span :title="item['io_id']">{{item['io_name']}}</span>
            </template>
            <template v-slot:item.li_id="{ item }">
                <span :title="item['li_id']">{{item['li_name']}}</span>
            </template>
            <template v-slot:item.creative_id="{ item }">
                <span :title="item['creative_id']">{{item['creative_name']}}</span>
            </template>
        </v-data-table>
    </v-card>
    `,
    props: {
        user: {
            type: Object
        }
    },
    data: function () {
        return {
            url: '/LittleJoe/trading',
            search: {
                advertiser_id: '',
                include_inactive_li: false,
                include_inactive_io: false,
                include_paging_creative: false
            },
            tbl: {
                headers: [
                    {text: 'IO', value: 'io_id'},
                    {text: 'LI', value: 'li_id'},
                    {text: '소재명', value: 'creative_id'},
                    {text: '랜딩 URL', value: 'landing_url'}
                ],
                items: [],
                paging: 'client',
                total: 1,
                options: {}
            },
            clients: {
                select: '',
                items: []
            }
        }
    },
    computed: {
        ...baseHelper.mapGetters([
            'getClients',
        ])
    },
    created() {
        this.xAjax({
            url: this.url+ '/get_dv360_clients',
            data: this.search
        }).then(resp=>{
            this.clients.items.push(...resp)
        })
    },
    methods: {
        fetchData() {
            this.setOverlay(true);
            this.xAjax({
                url: this.url+ '/creatives',
                data: this.search
            }).then(resp=>{
                this.tbl.items = resp;
                this.tbl.total = resp.length;
            }).finally(() => {
                this.setOverlay(false);
            });
        },
        changeClients(item) {
            if(item.value !== ''){
                this.search.advertiser_id = item.value
                this.fetchData()
            }else{
                this.search.advertiser_id = ''
            }
        },
        ...baseHelper.mapActions([
            "setOverlay"
        ])
    }
};

export default MpCreative;
