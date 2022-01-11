const baseHelper = Vuex.createNamespacedHelpers('base');

const MpReportSpreadSheetList = {
    name: 'mp-report-spread-sheet-list',
    template: `
    <v-card outlined>
        <mp-search>
            <div>
                <v-text-field v-model="search.username" label="사용자 아이디" @keyup.enter="fetchData()" outlined dense hide-details></v-text-field>
            </div>
            <div class="search-form-btns">
                <mp-button color="primary" label="조회" icon="mdi-magnify" @click="fetchData()"></mp-button>
                <mp-button color="info" label="새로입력" icon="mdi-plus" @click="newItem()"></mp-button>
            </div>
        </mp-search>

        <v-data-table dense fixed-header :height="$settings.datatable.rows10" :footer-props="$settings.datatable.footer10" :headers="tbl.headers" :items="tbl.items">
            <template v-slot:item.advertising="{ item }">
                <v-chip :color="item['advertising'] ? 'green' : 'gray'" small>{{item['advertising'] ? '활성' : '비활성'}}</v-chip>
            </template>
            <template v-slot:item.sheet_nm="{ item }">
                <div class="ellipsis"><a href="#" @click.stop.prevent="selectItem(item)" class="ellipsis" :title="item['sheet_nm']">{{ item['sheet_nm'] }}</a></div>
            </template>
            <template v-slot:item.last_update_date="{ item }">
                <v-chip :color="item['last_update_date'] === today ? 'green' : 'gray'" small>{{item['last_update_date']}}</v-chip>
            </template>
        </v-data-table>
    
        <mp-view :view.sync="view">
            <template v-slot:body="{edit}">
                <v-row>
                    <v-col cols="12" md="12">
                        <v-text-field label="시트명" v-model="sheet_name" :rules="mercury.base.rule.required"></v-text-field>
                    </v-col>
                </v-row>
            </template>
            <template v-slot:control="{edit}">
                <template v-if="edit">
                    <mp-button mode="text" color="warning" label="저장" icon="mdi-file-document-edit" @click="saveItem('PUT')"></mp-button>
                </template>
                <template v-else>
                    <mp-button mode="text" color="warning" label="저장" icon="mdi-text-box-plus" @click="saveItem('POST')"></mp-button>
                </template>
            </template>
        </mp-view>
    </v-card>
    `,
    props: {
        form: {
            type: Object
        }
    },
    data: function () {
        return {
            url: '/LittleJoe/api/reportgroups/',
            search: {
                report_group_id: undefined,
                username: undefined
            },
            sheet_name: this.form.data.name,
            view: {
                title: 'Sheet',
                show: false
            },
            tbl: {
                headers: [
                    {text: 'sheetId', value: 'sheet_id', width: 120},
                    {text: 'sheetName', value: 'sheet_nm', width: 120},
                    {text: 'sheetTemplate', value: 'sheet_template', width: 60},
                    {text: 'startDate', value: 'start_date', width: 120},
                    {text: 'endDate', value: 'end_date', width: 120},
                    {text: 'advertising', value: 'advertising', width: 60},
                    {text: 'lastUpdateDate', value: 'last_update_date', width: 120},
                ],
                items: [],
                paging: 'client'
            },
            today: this.moment().format('YYYY-MM-DD')
        }
    },
    mounted() {
        this.fetchData();
    },
    methods: {
        fetchData() {
            this.setOverlay(true);
            this.xAjax({
                url: '/LittleJoe/api/reportgroups/spreadsheet_info?platform_id='+this.form.data.platform_id
            }).then(resp=>{
                this.tbl.items = resp;
                this.tbl.total = resp.length;
            }).finally(() => {
                this.setOverlay(false);
            });
        },
        selectItem(item){
            this.form.data.report_id = item['sheet_id']
            this.$emit('close')
        },
        saveItem(){
            if(!this.mercury.base.util.hasValue(this.sheet_name)){
                this.mercury.base.lib.notify({message:'시트명을 입력해주세요.', type:'error'});
                return;
            }

            this.xAjax({
                url: '/LittleJoe/api/reportgroups/create_sheet/',
                method: 'POST',
                data: {
                    'platform_id': this.form.data.platform_id,
                    'sheet_name': this.sheet_name,
                    'start_date': this.form.data.start_date,
                    'end_date': this.form.data.end_date
                }
            }).then(resp=>{
                this.fetchData();
            })
        },
        newItem(){
            this.view.edit = false;
            this.view.show = true;
        },
        ...baseHelper.mapActions([
            "setOverlay"
        ])
    }
};

export default MpReportSpreadSheetList;