const baseHelper = Vuex.createNamespacedHelpers('base');
const MpManagement = {
    name: 'mp-management',
    template: `
    <v-card outlined>
        <v-expansion-panels focusable>
            <v-expansion-panel>
                <v-expansion-panel-header>SYNC</v-expansion-panel-header>
                <v-expansion-panel-content>
                    <v-row>
                        <v-col cols="12" md="4">
                            <v-select label="광고주" v-model="sync.client" :items="getClients" outlined dense hide-details>
                                <template v-slot:item="data">
                                    <v-chip class="mr-2" x-small label :color="data.item.status === 'ACTIVE'? 'green': 'gray'" v-if="data.item.value !== ''">{{ data.item.value }}</v-chip> {{data.item.text}}
                                </template>
                                <template v-slot:append-outer>
                                    <v-btn icon small color="green" title="광고주 아이템 SYNC" @click="requestSync('sync_units_by_client', sync.client)"><v-icon>mdi-database-sync</v-icon></v-btn>
                                </template>
                            </v-select>
                        </v-col>
                        <v-col cols="12" md="4">
                            <v-select label="플랫폼" v-model="sync.platform" :items="code.platform" outlined dense hide-details>
                                <template v-slot:append-outer>
                                    <v-btn icon small color="green" title="플랫폼 아이템 SYNC" @click="requestSync('sync_units_by_platform', sync.platform)"><v-icon>mdi-database-sync</v-icon></v-btn>
                                </template>
                            </v-select>
                        </v-col>
                        <v-col cols="12" md="4">
                            <v-btn color="orange" @click="requestManagement('batch_all_client_api_platform_ad_unit_and_conversion')" style="width:100%"><v-icon>mdi-database-sync</v-icon> 모든 광고주 아이템 SYNC</v-btn>
                        </v-col>
                    </v-row>
                </v-expansion-panel-content>
            </v-expansion-panel>
            <v-expansion-panel>
                <v-expansion-panel-header>긴급</v-expansion-panel-header>
                <v-expansion-panel-content>
                    <v-row>
                        <v-col cols="12" md="2">
                            <v-btn color="error" @click="requestManagement('batch_all_client_api_data_10days_ago')" style="width:100%">10일치 데이터 요청(API)</v-btn>
                        </v-col>
                        <v-col cols="12" md="2">
                            <v-btn color="error" @click="requestManagement('batch_all_client_api_data_yesterday')" style="width:100%">어제 데이터 요청(API)</v-btn>
                        </v-col>
                        <v-col cols="12" md="2">
                            <v-btn color="error" @click="requestManagement('batch_all_client_spreadsheet_data')" style="width:100%">데이터 요청(SPREADSHEET)</v-btn>
                        </v-col>
                        <v-col cols="12" md="1">
                            <v-btn color="warning" @click="requestManagement('batch_short_fetch_data')" style="width:100%" title="GA, SA360, FACEBOOK, GADS">SHORT 수집</v-btn>
                        </v-col>
                        <v-col cols="12" md="1">
                            <v-btn color="warning" @click="requestManagement('batch_long_fetch_data')" style="width:100%" title="DV360, CM">LONG 수집</v-btn>
                        </v-col>
                        <v-col cols="12" md="2">
                            <v-btn color="warning" @click="requestManagement('batch_spreadsheet_fetch_data')" style="width:100%" title="SPREADSHEET">SPREADSHEET 수집</v-btn>
                        </v-col>
                        <v-col cols="12" md="2">
                            <v-btn color="warning" @click="requestManagement('batch_all_fetch_data')" style="width:100%" title="모든 플랫폼">ALL 수집</v-btn>
                        </v-col>
                    </v-row>
                    <v-row>
                        <v-col cols="12" md="4">
                            <v-btn color="error" @click="requestManagement('batch_daily_report_email_all')" style="width:100%">모두에게 레포트 이메일(ALL)</v-btn>
                        </v-col>
                        <v-col cols="12" md="4">
                            <v-btn color="error" @click="requestManagement('batch_daily_report_email_api')" style="width:100%">모두에게 레포트 이메일(API)</v-btn>
                        </v-col>
                        <v-col cols="12" md="4">
                            <v-btn color="error" @click="requestManagement('batch_daily_report_email_spreadsheet')" style="width:100%">모두에게 레포트 이메일(SPREADSHEET)</v-btn>
                        </v-col>
                    </v-row>
                </v-expansion-panel-content>
            </v-expansion-panel>
        </v-expansion-panels>
    </v-card>
    `,
    props: {
        user: {
            type: Object
        }
    },
    data: function () {
        return {
            sync:{
                client: undefined,
                platform: undefined,
            },
            code: {
                platform: mercury.base.code['CODES']['API_Platform']
            }
        }
    },
    computed: {
        ...baseHelper.mapGetters([
            'getUser',
            'getClients',
        ])
    },
    methods: {
        requestSync(job, data){
            if (job === 'sync_units_by_client'){
                if (data === undefined || data === ''){
                    this.mercury.base.lib.notify({message:'동기화할 광고주를 선택해주세요.', type:'warning'});
                    return;
                }

                this.mercury.base.lib.confirm('['+data+']광고주의 모든 플랫폼을 동기화 하시겠습니까?').then(result => {
                    if (result.isConfirmed) {
                        this.setOverlay(true);
                        this.xAjax({
                            url: '/LittleJoe/management',
                            method: 'GET',
                            data: 'job='+job+'&client_id='+data
                        }).then((resp) => {
                            this.mercury.base.lib.notify(resp);
                        }).finally(() => {
                            this.setOverlay(false);
                        });
                    }
                });
            }else if(job === 'sync_units_by_platform'){
                if (data === undefined || data === ''){
                    this.mercury.base.lib.notify({message:'동기화할 플랫폼을 선택해주세요.', type:'warning'});
                    return;
                }

                this.mercury.base.lib.confirm('모든 광고주의 플랫폼['+data+']을 동기화 하시겠습니까?').then(result => {
                    if (result.isConfirmed) {
                        this.setOverlay(true);
                        this.xAjax({
                            url: '/LittleJoe/management',
                            method: 'GET',
                            data: 'job='+job+'&platform='+data
                        }).then((resp) => {
                            this.mercury.base.lib.notify(resp);
                        }).finally(() => {
                            this.setOverlay(false);
                        });
                    }
                });
            }
        },
        requestManagement(job){
            this.mercury.base.lib.confirm('[경고] 위급한 상황에만...<br/>'+job+' ?').then(result => {
                if (result.isConfirmed) {
                    this.setOverlay(true);
                    this.xAjax({
                        url: '/LittleJoe/management',
                        method: 'GET',
                        data: 'job='+job
                    }).then((resp) => {
                        this.mercury.base.lib.notify(resp);
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

export default MpManagement;
