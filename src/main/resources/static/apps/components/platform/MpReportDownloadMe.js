const baseHelper = Vuex.createNamespacedHelpers('base');

const MpReportDownloadMeList = {
    name: 'mp-report-download-me-list',
    template: `
        <v-card outlined class="mb-2">
            <v-card-title>
                {{platformSource}}
                <v-spacer></v-spacer>
                <template v-if="hasReport">
                    <v-btn :color="getDownloadColor()" @click="sendTodayReport()" ><v-icon>mdi-email</v-icon></v-btn>
                    <v-btn :color="getDownloadColor()" @click="downloadTodayReport()" class="ml-2"><v-icon>mdi-download-circle</v-icon></v-btn>  
                </template>
                <template v-else>
                    설정된 Report가 없습니다.
                </template>
            </v-card-title>
            <v-card-text v-if="hasReport">
                <v-tabs v-model="tab" >
                    <v-tab v-for="(client, client_id) in items" :key="client_id">
                        <v-chip :color="getClientColor(client)" small>{{ client['client_name'] }}</v-chip>
                    </v-tab>
                </v-tabs>
                <v-tabs-items v-model="tab" style="width:100%">
                    <v-tab-item v-for="(client, client_id) in items" :key="client_id">
                        <v-data-table dense fixed-header :hide-default-footer="true" :headers="tbl.headers" :items="getItems(client)">
                            <template v-slot:item.data_success="{ item }">
                                <v-chip :color="Number(item['data_process']) === 0 ? 'green': 'red'" small>{{getDataCount(item)}}</v-chip>
                            </template>
                            <template v-slot:item.data_updated_at="{ item }">
                                <v-chip :color="item['data_updated']?'green':'red'" small>{{ item['data_updated_at'] }}</v-chip>
                            </template>
                            <template v-slot:item.dsp_updated_at="{ item }">
                                <v-chip :color="item['dsp_updated']?'green':'orange'" small>{{ item['dsp_updated_at'] }}</v-chip>
                            </template>
                            <template v-slot:item.conversion_updated_at="{ item }">
                                <v-chip :color="item['conversion_updated']?'green':'orange'" small>{{ item['conversion_updated_at'] }}</v-chip>
                            </template>
                        </v-data-table>
                    </v-tab-item>
                </v-tabs-items>
            </v-card-text>
        </v-card>
    `,
    props: {
        platformSource: String,
        user: Object,
        items: Object,
    },
    data: function () {
        return {
            url: '/LittleJoe/api/users/',
            tab: undefined,
            tbl: {
                headers: [
                    {text: '플랫폼', value: 'platform', width: 140},
                    {text: '데이터 수집 현황', value: 'data_success', width: 100, align: 'right'},
                    {text: '데이터 수집 완료 시간', value: 'data_updated_at', width: 140},
                    {text: 'PLATFORM 동기화 시간', value: 'dsp_updated_at', width: 140},
                    {text: 'CONVERSION 동기화 시간', value: 'conversion_updated_at', width: 140},
                ]
            },
            status: {
                data: true,
                dsp: true,
                conversion: true
            },
        }
    },
    computed: {
        hasReport() {
            return this.items && Object.keys(this.items).length > 0 && Object.getPrototypeOf(this.items) === Object.prototype
        },
    },
    methods: {
        getDataCount(item) {
            const process = item['data_process'] !== undefined ? Number(item['data_process']) : 0;
            const success = item['data_success'] !== undefined ? Number(item['data_success']) : 0;
            return success + '/' + (success + process)
        },
        getItems(client) {
            const results = []
            for (const [key, obj] of Object.entries(client['platforms'])) {
                results.push(obj);
            }
            return results;
        },
        getClientColor(client) {
            let data_updated = true;
            let dsp_updated = true;
            let conversion_updated = true;
            for (const [key, obj] of Object.entries(client['platforms'])) {
                if (!obj['data_updated']) {
                    data_updated = false;
                    this.status.data = false
                }

                if (!obj['dsp_updated']) {
                    dsp_updated = false;
                    this.status.dsp = false
                }

                if (!obj['conversion_updated']) {
                    conversion_updated = false;
                    this.status.conversion = false
                }
            }

            if (data_updated) {
                if (dsp_updated && conversion_updated) {
                    return 'green'
                } else {
                    return 'orange'
                }
            } else {
                return 'red';
            }
        },
        downloadTodayReport() {
            if (this.status.data) {
                if (this.status.dsp && this.status.conversion) {
                    this.innerDownload();
                } else {
                    this.mercury.base.lib.confirm('ITEM 동기화가 완료되지 않았습니다.<br/>그래도 다운 받으시겠습니까?').then(result => {
                        if (result.isConfirmed) {
                            this.innerDownload();
                        }
                    });
                }
            } else {
                this.mercury.base.lib.confirm('데이터 수집이 완료되지 않았습니다.<br/> 그래도 다운 받으시겠습니까?').then(result => {
                    if (result.isConfirmed) {
                        this.innerDownload();
                    }
                });
            }
        },
        sendTodayReport() {
            if (this.status.data) {
                if (this.status.dsp && this.status.conversion) {
                    this.innerSendEmail();
                } else {
                    this.mercury.base.lib.confirm('ITEM 동기화가 완료되지 않았습니다.<br/>그래도 이메일로 전송하시겠습니까?').then(result => {
                        if (result.isConfirmed) {
                            this.innerSendEmail();
                        }
                    });
                }
            } else {
                this.mercury.base.lib.confirm('데이터 수집이 완료되지 않았습니다.<br/> 그래도 이메일로 전송하시겠습니까?').then(result => {
                    if (result.isConfirmed) {
                        this.innerSendEmail();
                    }
                });
            }

        },
        getDownloadColor() {
            if (this.status.data) {
                if (this.status.dsp && this.status.conversion) {
                    return 'green'
                } else {
                    return 'orange'
                }
            } else {
                return 'red';
            }
        },
        innerSendEmail() {
            this.setOverlay(true);
            this.xAjaxJson({
                url: this.url+'user_report_email/',
                method: 'GET',
                data: 'user_id='+this.user['id'] + '&email=' + this.user['email'] +'&platform_source='+this.platformSource
            }).then((resp) => {
                this.mercury.base.lib.notify(resp);
            }).finally(() => {
                this.setOverlay(false);
            });
        },
        innerDownload() {
            this.setOverlay(true);
            this.mercury.base.lib.download({
                url: this.url + 'user_report_download/',
                method: 'GET',
                data: 'user_id=' + this.user['id'] + '&email=' + this.user['email'] + '&is_new=true&platform_source='+this.platformSource
            }).finally(() => {
                this.setOverlay(false);
            });
        },
        ...baseHelper.mapActions([
            "setOverlay"
        ])
    }
}

const MpReportDownloadMe = {
    name: 'mp-report-download-me',
    components: {
        MpReportDownloadMeList
    },
    template: `
    <v-card outlined>
        <v-card-text>
            <v-select label="사용자" v-model="user" @change="changeUser" :items="getUsers" item-value="id" :item-text="item => item['first_name'] +' ('+ item['username']+')'" return-object outlined dense hide-details>
                
                <template v-if="getUser['is_superuser']" v-slot:append-outer>
                    <v-btn icon small color="warning" title="오늘 전송된 레포트" @click="downloadTodayReport"><v-icon>mdi-download-circle</v-icon></v-btn>
                </template>
                
            </v-select>
            <v-tabs v-model="tab" >
                <v-tab>전체</v-tab>
                <v-tab>분리</v-tab>
            </v-tabs>
            <v-tabs-items v-model="tab" style="width:100%">
                <v-tab-item>
                    <mp-report-download-me-list :items="splits['ALL']" :user="user" platform-source="ALL"></mp-report-download-me-list>
                </v-tab-item>
                <v-tab-item>
                    <template v-for="item in code.platformSource">
                        <mp-report-download-me-list :items="splits[item.value]" :user="user" :platform-source="item.value"></mp-report-download-me-list>
                    </template>
                </v-tab-item>
            </v-tabs-items>
        </v-card-text>
    </v-card>
    `,
    data: function () {
        return {
            tab: undefined,
            url: '/LittleJoe/api/users/',
            splits: {},
            user: {},
            code: {
                platformSource: mercury.base.code['CODES']['API_PlatformSource'],
            },
        }
    },
    computed: {
        ...baseHelper.mapGetters([
            'getUser',
            'getUsers'
        ])
    },
    watch: {},
    mounted() {
        this.user = this.getUser
        this.getUserReportStatus(this.user)
    },
    methods: {
        getUserReportStatus(user){
                this.xAjaxJson({
                url: this.url + 'user_report_status/',
                method: 'GET',
                data: 'data_format=separate&user_id=' + user['id'] + '&email=' + user['email']
            }).then((resp) => {
                this.splits = resp['splits']
                this.splits['ALL'] = resp['all']
            });
        },
        changeUser(user){
            this.user = user
            this.getUserReportStatus(user)
        },
        downloadTodayReport(){
            this.mercury.base.lib.confirm(this.user['username']+'님의 오늘자 전송된 report를 다운로드하시겠습니까?').then(result => {
                if (result.isConfirmed) {
                    this.setOverlay(true);
                    this.mercury.base.lib.download({
                        url: this.url+'user_report_download/',
                        method: 'GET',
                        data: 'user_id='+this.user['id']+'&email='+this.user['email']+'&is_new=false'
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

export default MpReportDownloadMe;