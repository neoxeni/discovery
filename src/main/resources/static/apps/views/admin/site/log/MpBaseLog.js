import crudMixIn from "/static/apps/mixin/crudMixIn.js";
export default {
    name: 'mp-base-log',
    mixins: [crudMixIn],
    template: `
    <v-card outlined>
        <mp-search>
            <div class="w-190px">
                <mp-date-range-picker label="기간" :start.sync="search.startedAt" :end.sync="search.endedAt" format="datetime"></mp-date-range-picker>
            </div>
            <div>
                <v-select v-model="search.divCd" label="구분" @change="fetchData(true)" :items="[{text:'전체',value:''},{text:'관리자',value:'ADMIN'},{text:'사용자',value:'USER'},{text:'개인정보',value:'SECU'}]" outlined dense hide-details></v-select>
            </div>
            <div>
                <v-text-field v-model="search.empNm" label="사용자명" @keyup.enter="fetchData(true)" outlined dense hide-details></v-text-field>
            </div>
            <div>
                <v-text-field v-model="search.userId" label="사용자아이디" @keyup.enter="fetchData(true)" outlined dense hide-details></v-text-field>
            </div>
            <div>
                <v-text-field v-model="search.ip" label="접속아이피" @keyup.enter="fetchData(true)" outlined dense hide-details></v-text-field>
            </div>
            <div class="search-form-btns">
                <mp-button color="primary" label="조회" icon="mdi-magnify" @click="fetchData(true)"></mp-button>
                <mp-button color="green" label="엑셀" icon="mdi-file-excel" @click="excelData(true)"></mp-button>
            </div>
        </mp-search>

        <v-data-table dense fixed-header item-key="seqNo" :height="$settings.datatable.rows10" :footer-props="$settings.datatable.footer10" :headers="tbl.headers" :items="tbl.items" :options.sync="tbl.options" :server-items-length="tbl.total" :items-per-page="10">
            <template v-slot:item.name="{ item }">
                <div class="ellipsis" :title="item.username">{{item.name}}</div>
            </template>
            <template v-slot:item.action_url="{ item }">
                <div class="ellipsis"><a :href="item.actionUrl" class="ellipsis" :title="item.actionUrl">{{item.actionUrl}}</a></div>
            </template>
            <template v-slot:item.actions="{ item }">
                <mp-button mode="icon" color="info" label="상세" icon="mdi-eye" @click="selectItem(item)"></mp-button>
            </template>
        </v-data-table>

        <mp-view :view.sync="view">
            <template v-slot:body="{edit}">
                <v-form class="input-form" ref="form" v-model="form.valid">
                    <v-row>
                        <v-col cols="12" md="3">
                            <v-text-field label="아이디" v-model="form.data.username" readonly></v-text-field>
                        </v-col>
                        <v-col cols="12" md="3">
                            <v-text-field label="사용자" v-model="form.data.name" readonly></v-text-field>
                        </v-col>
                        <v-col cols="12" md="3">
                            <v-text-field label="접속아이피" v-model="form.data.ip" readonly></v-text-field>
                        </v-col>
                        <v-col cols="12" md="3">
                            <v-text-field label="국가" v-model="form.data.language" readonly></v-text-field>
                        </v-col>
                        
                        <v-col cols="12" md="3">
                            <v-text-field label="등록일시" v-model="form.data.createdAt" readonly></v-text-field>
                        </v-col>
                        <v-col cols="12" md="3">
                            <v-text-field label="구분" v-model="form.data.divCd" readonly></v-text-field>
                        </v-col>
                        <v-col cols="12" md="3">
                            <v-text-field label="메뉴" v-model="form.data.menu" readonly></v-text-field>
                        </v-col>
                        <v-col cols="12" md="3">
                            <v-text-field label="서브메뉴" v-model="form.data.subMenu" readonly></v-text-field>
                        </v-col>
                        
                        <v-col cols="12" md="3">
                            <v-text-field label="행위" v-model="form.data.action" readonly></v-text-field>
                        </v-col>
                        <v-col cols="12" md="9">
                            <v-text-field label="경로" v-model="form.data.actionUrl" readonly></v-text-field>
                        </v-col>
                        
                        <v-col cols="12" md="3">
                            <v-text-field label="기타1" v-model="form.data.etc1" readonly></v-text-field>
                        </v-col>
                        <v-col cols="12" md="3">
                            <v-text-field label="기타2" v-model="form.data.etc2" readonly></v-text-field>
                        </v-col>
                        <v-col cols="12" md="2">
                            <v-text-field label="기타3" v-model="form.data.etc3" readonly></v-text-field>
                        </v-col>
                        <v-col cols="12" md="2">
                            <v-text-field label="기타4" v-model="form.data.etc4" readonly></v-text-field>
                        </v-col>
                        <v-col cols="12" md="2">
                            <v-text-field label="기타5" v-model="form.data.etc5" readonly></v-text-field>
                        </v-col>
                        <v-col cols="12" md="12">
                            <pre>{{form.data.inputVal}}</pre>
                        </v-col>
                    </v-row>
                </v-form>
            </template>
        </mp-view>
    </v-card>
    `,

    data: function () {
        return {
            url: '/base/logs/actions',
            pk: 'id',
            search: {
                divCd: undefined,
                name: undefined,
                username: undefined,
                ip: undefined,
                startedAt: moment().subtract(1, 'days').format('YYYY-MM-DD'),
                endedAt: moment().format('YYYY-MM-DD')
            },
            view: {
                mode: 'sheet',
                title: '관리자로그조회',
                show: false,
                edit: false
            },
            tbl: {
                headers: [
                    {text: 'SEQ', value: 'id', width: 80, align: 'right'},
                    {text: '등록일시', value: 'createdAt', width: 165, align: 'center' },
                    {text: '사용자', value: 'name', width: 100 },
                    {text: '접속아이피', value: 'ip', width: 130},
                    {text: '국가', value: 'language', width: 70},
                    {text: '메뉴', value: 'menu', width: 160 },
                    {text: '서브메뉴', value: 'subMenu', width: 120 },
                    {text: '행위', value: 'action', width: 140 },
                    {text: '경로', value: 'action_url', cellClass: 'miw-100px' },
                    {text: '상세', value: 'actions', width: 100, align: 'center', sortable: false}
                ]
            }
        };
    }
};
