const baseHelper = Vuex.createNamespacedHelpers('base');

const MpUser = {
    name: 'mp-user',
    components: {},
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
            <template v-slot:item.status="{ item }">
                <v-chip :color="item.status === 'ACTIVE' ? 'green' : 'gray'" small>{{item['status_label']}}</v-chip>
            </template>
            <template v-slot:item.is_active="{ item }">
                <v-chip :color="item['is_active'] ? 'green' : 'gray'" small>{{item['is_active'] ? '활성' : '비활성'}}</v-chip>
            </template>
            <template v-slot:item.actions="{ item }">
                <mp-button mode="icon" color="green" label="패스워드 초기화" icon="mdi-security" @click="resetPassword(item)"></mp-button>
            </template>
        </v-data-table>
    </v-card>
    `,
    data: function () {
        return {
            url: '/LittleJoe/api/users/',
            search: {
                name: '',
                status: undefined
            },
            tbl: {
                headers: [
                    {text: 'SEQ', value: 'id', width: 60, align: 'right'},
                    {text: '아이디', value: 'username', width: 140},
                    {text: '이메일', value: 'email'},
                    {text: '이름', value: 'first_name', width: 80},
                    {text: '상태', value: 'is_active', width: 80},
                    {text: '마지막로그인', value: 'last_login', width: 160, align: 'center'},
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
        resetPassword(item){
            this.mercury.base.lib.confirm(item['username']+'님의 패스워드를 초기화 하시겠습니까?').then(result => {
                if (result.isConfirmed) {
                    this.setOverlay(true);
                    this.xAjaxJson({
                        url: this.url+'reset_change/',
                        method: 'PUT',
                        data: item
                    }).then((resp) => {
                        this.mercury.base.lib.notify(resp);
                        this.fetchData(false);
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

export default MpUser;