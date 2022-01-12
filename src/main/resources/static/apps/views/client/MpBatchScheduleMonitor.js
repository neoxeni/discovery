const baseHelper = Vuex.createNamespacedHelpers('base');
import MpBaseGroupCombo from "/static/apps/components/ui/MpBaseGroupCombo.js";
import MpAdvertiserSelect from "/static/apps/components/ui/MpAdvertiserSelect.js";

const MpBatchScheduleMonitor = {
    name: 'mp-batch-schedule-monitor',
    mixins: [],
    components: {
        MpBaseGroupCombo,
        MpAdvertiserSelect
    },
    template: `
    <v-card outlined>
        <mp-search>
            <div class="w-190px">
                <mp-date-range-picker label="기간" :start.sync="search.start_date" :end.sync="search.end_date" format="datetime"></mp-date-range-picker>
            </div>
            <div class="w-200px">
                <mp-advertiser-select label="광고주" v-model="search.client_id" @change="fetchData(true)" :add-items="[{text:'전체',value:''}]" outlined dense hide-details></mp-advertiser-select>
            </div>
            <div class="w-120px">
                <v-select label="타입" v-model="search.type" @change="fetchData(true)" :items="mercury.base.util.concatArray(code.type,[{text:'전체',value:''}], false)" outlined dense hide-details></v-select>
            </div>
            <div class="w-100px">
                <v-select label="상태" v-model="search.status" @change="fetchData(true)" :items="mercury.base.util.concatArray(code.status,[{text:'전체',value:''}], false)" outlined dense hide-details></v-select>
            </div>
            <div class="w-190px">
                <v-select label="플랫폼" v-model="search.platform" @change="fetchData(true)" :items="mercury.base.util.concatArray(code.platform,[{text:'전체',value:''}], false)" outlined dense hide-details></v-select>
            </div>
            <div class="w-150px">
                <v-text-field v-model="search.platform_id" label="플랫폼 아이디" @keyup.enter="fetchData(true)" outlined dense hide-details></v-text-field>
            </div>
            <div class="search-form-btns">
                <mp-button color="primary" label="조회" icon="mdi-magnify" @click="fetchData(true)"></mp-button>
                <mp-button color="info" label="새로입력" icon="mdi-plus" @click="newItem()"></mp-button>
            </div>
        </mp-search>

        <v-data-table dense fixed-header :height="$settings.datatable.rows20" :footer-props="$settings.datatable.footer20" :headers="tbl.headers" :items="tbl.items" :options.sync="tbl.options" :server-items-length="tbl.total" >
            <template v-slot:item.actions="{ item }">
                <mp-button mode="icon" v-if="item.status !== 'DONE'" color="warning" label="체크" icon="mdi-source-branch-check" 
                @click="checkBatchReportSchedule(item)" :loading="item.loading.PULL" :disabled="item.loading.PULL"></mp-button>
            </template>
            <template v-slot:item.status="{ item }">
                <v-chip :color="getStatusColor(item.status)" small :title="item['message']">{{item['status_label']}}</v-chip>
            </template>
            <template v-slot:item.client_id="{ item }">
                <div class="ellipsis"><div class="ellipsis" :title="item.client_id"> {{client_map[item.client_id] || item.client_id}}</div></div>
            </template>
            <template v-slot:item.report_id="{ item }">
                <div class="ellipsis"><div class="ellipsis" :title="item['report_id']"> {{item['report_id']}}</div></div>
            </template>
            <template v-slot:item.report_file_id="{ item }">
                <div class="ellipsis"><div class="ellipsis" :title="item['report_file_id']"> {{item['report_file_id']}}</div></div>
            </template>
            <template v-slot:item.message="{ item }">
                <div class="ellipsis"><div class="ellipsis" :title="item['message']"> {{item['message']}}</div></div>
            </template>
            <template v-slot:item.actions2="{ item }">
                <mp-button mode="icon" v-if="item.status !== 'DONE'" color="warning" label="재생성" icon="mdi-restore-alert" 
                @click="recreateBatchReportSchedule(item)" :loading="item.loading.PULL" :disabled="item.loading.PULL"></mp-button>
                <mp-button mode="icon" color="danger" label="삭제" icon="mdi-delete" @click="deleteItem(item)"></mp-button>
            </template>
        </v-data-table>
        
        <mp-view :view.sync="view" v-if="view.show">
            <template v-slot:body="{edit}">
                <v-form class="input-form" ref="form" v-model="form.valid">
                    <v-row>
                        <v-col cols="12" md="6">
                            <mp-date-range-picker label="기간" :start.sync="form.data.start_date" :end.sync="form.data.end_date" shape="underline"></mp-date-range-picker>
                        </v-col>
                        <v-col cols="12" md="6">
                            <mp-advertiser-select label="광고주" v-model="form.data.client_id" @change="fetchData(true)" :add-items="[{text:'전체',value:''}]" :rules="mercury.base.rule.required"></mp-advertiser-select>
                        </v-col>
                        <v-col cols="12" md="12">
                            <mp-base-group-combo label="플랫폼" v-model="form.data.platforms" :items="mercury.base.util.concatArray(code.platform,[{text:'전체',value:'ALL'}], false)" :rules="mercury.base.rule.required"></mp-base-group-combo>
                        </v-col>
                        <v-col cols="12" md="12">
                            <v-textarea label="비고" v-model="form.data.desc" auto-grow clearable rows="5" row-height="30" :rules="mercury.base.rule.required"></v-textarea>
                        </v-col>
                        <v-col cols="12" md="12"></v-col>
                        <v-col cols="12" md="12"></v-col>
                    </v-row>
                </v-form>
            </template>
            <template v-slot:control="{edit}">
                <mp-button mode="text" color="warning" label="요청" icon="mdi-text-box-check" @click="requestItem()"></mp-button>
            </template>
        </mp-view>
    </v-card>
    `,
    data: function () {
        return {
            url: '/LittleJoe/api/batchreportschedule/',
            search: {
                client_id: undefined,
                type: undefined,
                status: 'PROCESSING',
                platform_id: undefined,
                platform: undefined,
                start_date: moment().format('YYYY-MM-DD'),
                end_date: moment().format('YYYY-MM-DD')
            },
            view: {
                width: 700,
                mode: 'dialog',  //sheet || dialog
                title: '데이터 가져오기 요청',
                show: false,
                edit: false
            },
            tbl: {
                headers: [
                    {text: '액션', value: 'actions', width: 80, sortable: false},
                    {text: '시작일', value: 'start_date', width: 100},
                    {text: '종료일', value: 'end_date', width: 100},
                    {text: '단계', value: 'schedule_step', width: 80},
                    {text: '타입', value: 'schedule_type', width: 80},
                    {text: '상태', value: 'status', width: 80},
                    {text: '광고주아이디', value: 'client_id', width: 110},
                    {text: '플랫폼', value: 'platform', width: 140},
                    {text: '플랫폼아이디', value: 'platform_id', width: 160},
                    {text: '레포트타입', value: 'report_type', width: 120},
                    {text: '레포트유형', value: 'report_template', width: 160},
                    {text: '레포트아이디', value: 'report_id', width: 120},
                    {text: '파일아이디', value: 'report_file_id', width: 120},
                    {text: '생성일시', value: 'created_at', width: 160, align: 'center'},
                    {text: '수정일시', value: 'updated_at', width: 160, align: 'center'},
                    {text: '메시지', value: 'message', width: 200},
                    {text: '액션', value: 'actions2', width: 80, sortable: false},
                ],
                items: [],
                total: 10,
                options: {}
            },
            form: {
                init: {
                    client_id: undefined,
                    start_date: moment().subtract(1,'days').format('YYYY-MM-DD'),
                    end_date: moment().subtract(1,'days').format('YYYY-MM-DD'),
                    platforms: [],
                    desc: undefined
                },
                data: {

                },
                valid: false
            },
            code: {
                platform: mercury.base.code['CODES']['API_Platform'],
                type: mercury.base.code['CODES']['API_BatchScheduleType'],
                status: mercury.base.code['CODES']['API_BatchScheduleStatus'],
            },
            client_map:{}
        }
    },
    created(){
        this.getClients.forEach(item=>{ //join 되지 않은 광고주 아이디를 이름으로 보여주기 위함
            this.client_map[item.value] = item.text
        })
    },
    computed: {
        ...baseHelper.mapGetters([
            'getUser',
            'getClients',
        ])
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
                return this.mercury.base.util.dataTablesReset(this.tbl.options);
            }

            const data = this.mercury.base.util.dataTablesParam(this.tbl.options, this.search, []);

            this.xAjax({
                url: this.url,
                method: 'GET',
                data: data
            }).then(response => {
                response['results'].forEach(item=>{
                    item.loading = {PULL: false};
                })

                this.tbl.items = response['results'];
                this.tbl.total = response['count'];
            });
        },
        recreateBatchReportSchedule(item){
            const data = this.mercury.base.util.deepCopy(item);
            delete data['message'];
            item.loading['PULL'] = true;

            data['schedule_id'] = data['id'];
            this.xAjaxJson({
                url: this.url + 're_create/',
                method: 'POST',
                data: data
            }).then(resp => {
                this.mercury.base.lib.notify(resp);
                if (resp.type === 'success') {
                    item.status = 'PROCESSING';
                    item.status_label = '진행중';
                }
            }).finally(()=>{
                item.loading['PULL'] = false;
            });

        },
        checkBatchReportSchedule(item) {
            const data = this.mercury.base.util.deepCopy(item);
            delete data['message'];
            data['schedule_id'] = data['id'];
            item.loading['PULL'] = true;
            if(item['status'] === 'ERROR' && item['schedule_step'] === 'CREATE'){
                this.xAjaxJson({
                    url: this.url + 're_create/',
                    method: 'POST',
                    data: data
                }).then(resp => {
                    this.mercury.base.lib.notify(resp);
                    if (resp.type === 'success') {
                        item.status = 'PROCESSING';
                        item.status_label = '진행중';
                    }
                }).finally(()=>{
                    item.loading['PULL'] = false;
                });
            }else{
                this.xAjaxJson({
                    url: this.url + 'check/',
                    method: 'POST',
                    data: data
                }).then(resp => {
                    this.mercury.base.lib.notify(resp);
                    if (resp.type === 'success') {
                        item.status = 'DONE';
                        item.status_label = '완료';
                    }
                }).finally(()=>{
                    item.loading['PULL'] = false;
                });
            }
        },
        getStatusColor(status) {
            if (status === 'DONE') {
                return 'green';
            } else if (status === 'ERROR') {
                return 'red';
            } else {
                return 'gray';
            }
        },
        newItem() {
            this.view.edit = false;
            this.view.show = true;
            this.resetItem();
        },
        resetItem: function () {//입력폼 초기화
            this.form.data = Object.assign({}, this.form.init); //init에 설정된 값으로 data를 모두 초기화
            this.form.data.platforms.splice(0);
            if (this.$refs.form !== undefined) {
                this.$refs.form.resetValidation();  //form에 표시된 모든 error validation 표시를 제거
            }
        },
        requestItem() {
            if (!this.form.valid && this.$refs.form !== undefined) {
                return this.$refs.form.validate();//form validation 수행하여 화면에 에러 메시지 표시
            }

            this.setOverlay(true);
            this.xAjaxJson({
                url: '/LittleJoe/api/batchreportschedule/sync_data/',
                data: this.form.data,
                method: 'POST'
            }).then(resp => {
                this.mercury.base.lib.notify(resp);
                this.view.show = false;
                this.fetchData(false);
            }).finally(() => {
                this.setOverlay(false);
            });
        },
        deleteItem(item) {
            this.mercury.base.lib.confirm('삭제 하시겠습니까?').then(result => {
                if (result.isConfirmed) {
                    this.xAjaxJson({
                        url: this.url + item.id+'/',
                        method: 'DELETE',
                        data: this.form.data
                    }).then((resp) => {
                        this.mercury.base.lib.notify(resp);
                        this.fetchData(false);
                    });
                }
            });
        },
        ...baseHelper.mapActions([
            "setOverlay"
        ])
    }
};

export default MpBatchScheduleMonitor;