import crudMixIn from "/static/vue/mixin/crudMixIn.js";

const MpSampleCrudMixIn = {
    name: 'mp-sample-crud-mix-in',
    template: `
    <v-card outlined>
        <mp-search>
            <div class="w-190px">
                <mp-date-range-picker label="기간" :start.sync="search.start_date" :end.sync="search.end_date" format="datetime"></mp-date-range-picker>
            </div>
            <div>
                <v-text-field v-model="search.name" label="이름" @keyup.enter="fetchData(true)" outlined dense hide-details></v-text-field>
            </div>
            <div class="w-150px">
                <v-select label="상태" v-model="search.status" @change="fetchData(true)" :items="mercury.base.util.concatArray(code.status,[{text:'전체',value:''}], false)" outlined dense hide-details></v-select>
            </div>
            <div class="search-form-btns">
                <mp-button color="primary" label="조회" icon="mdi-magnify" @click="fetchData(true)"></mp-button>
                <mp-button color="green" label="엑셀" icon="mdi-file-excel" @click="excelData()"></mp-button>
                <mp-button color="info" label="새로입력" icon="mdi-plus" @click="newItem()"></mp-button>
            </div>
        </mp-search>

        <v-data-table dense fixed-header :height="$settings.datatable.rows10" :footer-props="$settings.datatable.footer10" :headers="tbl.headers" :items="tbl.items" :options.sync="tbl.options" :server-items-length="tbl.total" >
            <template v-slot:item.name="{ item }">
                <div class="ellipsis"><a href="#" @click.stop.prevent="selectItem(item)" class="ellipsis">{{item.name}}</a></div>
            </template>
            <template v-slot:item.status="{ item }">
                <v-chip :color="item.status === 'ACTIVE' ? 'green' : 'gray'" small>{{item['status_label']}}</v-chip>
            </template>
            <template v-slot:item.actions="{ item }">
                <mp-button mode="icon" color="warning" label="플랫폼" icon="mdi-sitemap" @click="selectItem(item)"></mp-button>
            </template>
        </v-data-table>

        <mp-view :view.sync="view">
            <template v-slot:body="{edit}">
                <v-form class="input-form" ref="form" v-model="form.valid">
                    <v-row>
                        <v-col cols="12" md="6">
                            <v-text-field label="이름" v-model="form.data.name" :rules="mercury.base.rule.required"></v-text-field>
                        </v-col>
                        <v-col cols="12" md="6">
                            <v-select label="상태" v-model="form.data.status" :items="code.status"></v-select>
                        </v-col>
                        <v-col cols="12" md="12">
                            <v-textarea label="비고" v-model="form.data.desc" auto-grow clearable rows="2" row-height="30"></v-textarea>
                        </v-col>
                        <v-col cols="12" md="4">
                            <mp-date-picker label="기간달력" v-model="form.data.calRange" type="range"></mp-date-picker>
                        </v-col>
                        <v-col cols="12" md="4">
                            <mp-date-picker label="일달력" v-model="form.data.calDay" type="date"></mp-date-picker>
                        </v-col>
                        <v-col cols="12" md="4">
                            <mp-date-picker label="월달력" v-model="form.data.calMonth" type="month"></mp-date-picker>
                        </v-col>
                    </v-row>
                </v-form>
            </template>
            <template v-slot:control="{edit}">
                <template v-if="edit">
                    <mp-button mode="text" color="warning" label="저장" icon="mdi-file-document-edit" @click="saveItem('PUT')"></mp-button>
                    <mp-button mode="text" color="danger" label="삭제" icon="mdi-delete" @click="deleteItem(form.data)"></mp-button>
                </template>
                <template v-else>
                    <mp-button mode="text" color="warning" label="저장" icon="mdi-text-box-plus" @click="saveItem('POST')"></mp-button>
                    <mp-button mode="text" color="secondary" label="초기화" icon="mdi-refresh" @click="resetItem()"></mp-button>
                </template>
            </template>
        </mp-view>
    </v-card>
    `,
    mixins: [crudMixIn],
    data: () => ({
        url: '/LittleJoe/api/advertisers/',
        search: {
            name: '',
            status: undefined,
            start_date: moment().subtract(1, 'days').format('YYYY-MM-DD'),
            end_date: moment().format('YYYY-MM-DD')
        },
        view: {
            title: 'Advertiser'
        },
        tbl: {
            headers: [
                {text: 'id', value: 'id', width: 60, align: 'right'},
                {text: '이름', value: 'name', width: 140},
                {text: '소개', value: 'desc'},
                {text: '상태', value: 'status', width: 80},
                {text: '액션', value: 'actions', width: 80, sortable: false},
                {text: '수정자', value: 'user_name', width: 120, align: 'center'},
                {text: '생성일시', value: 'created_at', width: 160, align: 'center'},
                {text: '수정일시', value: 'updated_at', width: 160, align: 'center'}
            ]
        },
        form: {
            init: {
                name: undefined,
                status: 'ACTIVE',
                desc: undefined,
                calDay: moment().format('YYYY-MM-DD'),
                calMonth: moment().format('YYYY-MM'),
                calRange: moment().subtract(10, 'days').format('YYYY-MM-DD') + ' ~ ' + moment().format('YYYY-MM-DD')
            }
        },
        code: {
            status: mercury.base.code['CODES']['API_StatusShort']
        }
    }),
    methods: {

    }
};

export default MpSampleCrudMixIn;