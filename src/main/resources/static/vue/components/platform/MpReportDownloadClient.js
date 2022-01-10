const baseHelper = Vuex.createNamespacedHelpers('base');
const MpReportDownloadClient = {
    name: 'mp-report-download-client',
    template: `
    <v-card outlined>
        <mp-search>
            <div>
                <v-text-field v-model="search.name" label="이름" @keyup.enter="fetchData(true)" outlined dense hide-details></v-text-field>
            </div>
            <div class="w-150px">
                <v-select label="상태" v-model="search.status" @change="fetchData(true)" :items="mercury.base.util.concatArray(code.status,[{text:'전체',value:''}], false)" outlined dense hide-details></v-select>
            </div>
            <div class="search-form-btns">
                <mp-button color="primary" label="조회" icon="mdi-magnify" @click="fetchData(true)"></mp-button>
            </div>
        </mp-search>

        <v-data-table dense fixed-header :height="$settings.datatable.rows20" :footer-props="$settings.datatable.footer20" :headers="tbl.headers" :items="tbl.items" :options.sync="tbl.options" :server-items-length="tbl.total" >
            <template v-slot:item.name="{ item }">
                <div class="ellipsis"><div class="ellipsis" :title="item.name">{{item.name}}</div></div>
            </template>
            <template v-slot:item.eng_name="{ item }">
                <div class="ellipsis"><div class="ellipsis" :title="item.eng_name">{{item.eng_name}}</div></div>
            </template>
            <template v-slot:item.status="{ item }">
                <v-chip :color="item.status === 'ACTIVE' ? 'green' : 'gray'" small>{{item['status_label']}}</v-chip>
            </template>
            <template v-slot:item.actions="{ item }">
                <mp-button mode="icon" color="green" label="오늘 레포트 다운로드" icon="mdi-download-box" @click="downloadTodayReport(item)"></mp-button>
            </template>
        </v-data-table>
    </v-card>
    `,
    data: function () {
        return {
            url: '/LittleJoe/api/clients/',
            search: {
                name: '',
                status: 'ACTIVE'
            },
            tbl: {
                headers: [
                    {text: 'ID', value: 'id', width: 50, align: 'right'},
                    {text: '이름', value: 'name', width: 140},
                    {text: '영문이름', value: 'eng_name', width: 140},
                    {text: '설명', value: 'desc'},
                    {text: '업종', value: 'industry_code_label', width: 80},
                    {text: '상태', value: 'status', width: 80},
                    {text: '액션', value: 'actions', width: 80, sortable: false},
                ],
                items: [],
                total: 20,
                options: {}
            },
            form: {
                init: {
                    name: undefined,
                    status: 'ACTIVE',
                    desc: undefined,
                    industry_code: undefined,
                },
                data: {},
                valid: false
            },
            code: {
                status: mercury.base.code['CODES']['API_StatusShort'],
                industry_code: mercury.base.code['CODES']['API_IndustryCode']
            }
        }
    },
    watch: {
        'tbl.options': {
            handler: 'fetchData',
            deep: true
        }
    },
    methods: {
        fetchData(isSearchFirst) {
            if (isSearchFirst === true) {//1페이지 조회
                this.mercury.base.util.dataTablesReset(this.tbl.options);
                return Promise.resolve({})
            }

            return this.xAjax({
                url: this.url,
                method: 'GET',
                data: this.mercury.base.util.dataTablesParam(this.tbl.options, this.search, [])
            }).then(resp => {
                if(typeof resp === 'object'){
                    if (this.mercury.base.util.isArray(resp)) {//array 인 경우는 클라이언트 페이징
                        this.tbl.items = resp;
                        this.tbl.total = resp.length;
                    }else{
                        this.tbl.items = resp['results'];
                        this.tbl.total = resp['count'];
                    }
                }

                return Promise.resolve(resp)
            });
        },
        downloadTodayReport(item){
            this.mercury.base.lib.confirm(item['name']+' 광고주의 오늘자 report를 다운로드하시겠습니까?').then(result => {
                if (result.isConfirmed) {
                    const id = item['id']
                    this.setOverlay(true);
                    this.mercury.base.lib.download({
                        url: '/LittleJoe/api/reportgroups/advertiser_excel/',
                        method: 'GET',
                        data: 'id='+id+'&name='+item['name']
                    }).finally(() => {
                        this.setOverlay(false);
                    });
                }
            });
        },
        ...baseHelper.mapActions([
            "setOverlay"
        ])
    }
};

export default MpReportDownloadClient;