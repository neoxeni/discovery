export default {
    name: 'mp-base-log-group',
    template: `
        <v-card outlined>
            <mp-search>
                <mp-calendar mode="range" :model.sync="search.range" label="기간"></mp-calendar>
                <div>
                    <v-select label="그룹" v-model="search.grpNo" :items="group.items" item-text="name" item-value="grpNo" outlined dense hide-details clearable>
                        <template v-slot:selection="data">
                            {{ data.item.name }}
                        </template>
                        <template v-slot:item="data">
                            <v-chip class="ma-2" x-small label>{{ data.item.code }}</v-chip> {{ data.item.name }}
                        </template>
                    </v-select>
                </div>
                <div>
                    <v-select v-model="search.action" label="행위" @change="fetchData(true)" :items="[{text:'전체',value:''},{text:'추가',value:'C'},{text:'삭제',value:'D'}]" outlined dense hide-details></v-select>
                </div>
                <div>
                    <v-select v-model="search.dataGbn" label="대상구분" @change="fetchData(true)" :items="[{text:'전체',value:''},{text:'부서',value:'D'},{text:'직원',value:'E'}]" outlined dense hide-details></v-select>
                </div>
                <div>
                    <v-text-field v-model="search.dataNm" label="대상" @keyup.enter="fetchData(true)" outlined dense hide-details></v-text-field>
                </div>
                <div>
                    <v-text-field v-model="search.regEmpNm" label="변경자" @keyup.enter="fetchData(true)" outlined dense hide-details></v-text-field>
                </div>
        
                <div class="search-form-btns">
                    <mp-button color="primary" label="조회" icon="mdi-magnify" @click="fetchData(true)"></mp-button>
                    <mp-button color="green" label="엑셀" icon="mdi-file-excel" @click="excelData(true)"></mp-button>
                </div>
            </mp-search>
        
            <v-data-table dense fixed-header item-key="seqNo" height="70vh" :headers="tbl.headers" :items="tbl.items" :options.sync="tbl.options" :server-items-length="tbl.total" :items-per-page="10">
                <template v-slot:item.action="{ item }">
                    <v-chip color="green" v-if="item.action === 'C'" small>추가</v-chip>
                    <v-chip color="warning" v-else-if="item.action === 'D'" small>삭제</v-chip>
                    <v-chip color="gray" v-else small>{{item.action}}</v-chip>
                </template>
            </v-data-table>
        </v-card>
    `,
    props: {},
    data: function() {
        return {
            search: {
                grpNo: undefined,
                action: undefined,
                dataGbn: undefined,
                dataNm: undefined,
                regEmpNm: undefined,
                range: {
                    start: moment().subtract(1, 'days').toDate(),
                    end: moment().toDate()
                }
            },
            tbl: {
                headers: [{
                    text: '그룹',
                    value: 'name'
                }, {
                    text: '대상',
                    value: 'dataNm',
                    width: 150
                }, {
                    text: '변경자',
                    value: 'regEmpNm',
                    width: 100
                }, {
                    text: '행위',
                    value: 'action',
                    width: 100,
                    align: 'center'
                }, {
                    text: '변경일시',
                    value: 'createdAt',
                    width: 165,
                    align: 'center'
                }, {
                    text: '접속아이피',
                    value: 'regIp',
                    width: 130
                }],
                items: [],
                total: 0,
                options: {}
            },
            group: {
                items: [],
                item: {}
            }
        };
    },
    computed: {},
    watch: {
        'tbl.options': {
            handler: 'fetchData',
            deep: true
        }
    },
    created() {
        xAjax({
            url: '/base/groups'
        }).then(resp => {
            this.group.items = resp;
        });
    },
    mounted() {},
    methods: {
        fetchData: function(isSearchFirst) {
            if (isSearchFirst === true) {
                //1페이지 조회
                return mercury.base.vue.dataTablesReset(this.tbl.options);
            }
            this.search.dateRange = mercury.base.vue.getDateRangeFormat(this.search.range);
            let query = mercury.base.vue.dataTablesParam(this.tbl.options, this.search);
            xAjax({
                url: '/base/groups/mappings/histories',
                method: 'GET',
                data: query
            }).then(response => {
                this.tbl.items = response.data;
                this.tbl.total = response.recordsTotal;
            });
        },
        excelData: function() {
            this.search.dateRange = mercury.base.vue.getDateRangeFormat(this.search.range);
            let query = mercury.base.vue.dataTablesParam(this.tbl.options, this.search);
            mercury.base.lib.download({
                url: '/base/groups/mappings/histories/excel',
                method: 'GET',
                data: query
            }).then(response => {
                console.log(response);
            });
        }
    }
}