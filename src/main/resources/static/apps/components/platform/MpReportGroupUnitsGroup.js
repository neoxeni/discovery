const MpReportGroupUnitsGroup = {
    name: 'mp-report-group-units-group',
    template: `
    <v-card outlined>
        <mp-search>
            <div class="w-200px">
                <v-select label="유형" v-model="search.level" :rules="mercury.base.rule.required" :items="form.group.dsp.level[form.data.platform+platformReportType]" outlined dense hide-details></v-select>
            </div>
            <div class="w-150px">
                <v-text-field v-model="search.name" label="AD ITEM 검색" clearable outlined dense hide-details></v-text-field>
            </div>
        </mp-search>
        <v-row>
            <v-col cols="12" md="6">
                <v-card>
                    <v-data-table dense fixed-header :height="$settings.datatable.rows10" :footer-props="$settings.datatable.footer10" :headers="tbl.headers" :items="selectableItems" :options.sync="tbl.options">
                        <template v-slot:item.text="{ item, index}">
                            <v-chip x-small chip>{{item.value}}</v-chip> {{item.text}}
                        </template>
                        <template v-slot:item.actions="{ item, index}">
                            <mp-button mode="icon" color="green" label="추가" icon="mdi-arrow-right-circle" @click="addItem(item, index)"></mp-button>
                        </template>
                        <template v-slot:header.actions="{ header }">
                            <mp-button mode="icon" color="green" label="전체 추가" icon="mdi-arrow-right-circle" @click="addAllItem()"></mp-button>
                        </template>
                    </v-data-table>                
                </v-card>
            </v-col>
            <v-col cols="12" md="6">
                <v-card>
                    <v-data-table dense fixed-header :height="$settings.datatable.rows10" :footer-props="$settings.datatable.footer10" :headers="tbl2.headers" :items="tbl2.items" :options.sync="tbl2.options">
                        <template v-slot:item.text="{ item, index}">
                            <v-chip x-small chip>{{item.report_group_ad_unit_item[0].value}}</v-chip> {{item.report_group_ad_unit_item[0].text}}
                        </template>
                        <template v-slot:item.actions="{ item, index }">
                            <mp-button mode="icon" color="danger" label="삭제" icon="mdi-arrow-left-circle" @click="deleteItem(item, index)"></mp-button>
                        </template>
                        <template v-slot:header.actions="{ header }">
                            <mp-button mode="icon" color="danger" label="전체 삭제" icon="mdi-arrow-left-circle" @click="deleteAllItem()"></mp-button>
                        </template>
                    </v-data-table>                
                </v-card>
            </v-col>
        </v-row>
    </v-card>
    `,

    props: {
        form: {
            type: Object
        },
        unit: {
            type: Object
        }
    },
    data: function () {
        return {
            search: {
                name: '',
                level: this.unit.type
            },
            tbl: {
                headers: [
                    {text: '선택 가능한 AD ITEM', value: 'text'},
                    {text: '', value: 'actions', width: 30, sortable:false},
                ],
                options: {}
            },
            tbl2: {
                headers: [
                    {text: '', value: 'actions', width: 30, sortable:false},
                    {text: '선택한 AD ITEM', value: 'text'},
                ],
                items: this.form.data.report_group_ad_unit,
                options: {}
            },
            code: {
                platform: mercury.base.code['CODES']['API_Platform'],
                platform_unique: mercury.base.code['CODES']['API_Platform']
            }
        }
    },
    computed: {
        platformReportType: function(){
            let report_type = this.form.data.report_type;
            if(report_type === undefined || report_type === null){
                report_type = '';
            }else{
                report_type = '_'+report_type
            }

            return report_type;
        },
        selectedItemsMap: function(){
            const map = {};
            this.form.data.report_group_ad_unit.forEach((item=>{
                const unit = this.getFirstUnit(item)
                if (unit !== undefined){
                    map[String(unit.value)] = unit.text
                }
            }));

            return map;
        },
        selectableItems: function(){
            const items = this.form.group.dsp.items[this.search.level] || [];
            return items.filter(item=>{
                let isTrue = this.selectedItemsMap[String(item.value)] === undefined
                if(isTrue &&  this.mercury.base.util.hasValue(this.search.name)){
                    isTrue = item.text.toUpperCase().indexOf(this.search.name.toUpperCase()) > -1 || item.value.indexOf(this.search.name) > -1
                }

                return isTrue;
            });
        }
    },
    methods: {
        getFirstUnit(item){
            if (item.report_group_ad_unit_item !== undefined && item.report_group_ad_unit_item.length > 0){
                return item.report_group_ad_unit_item[0]
            }
            return undefined
        },
        makeAdUnit(item, idx){
            return {
                id: new Date().getTime()+''+idx,
                name: item.text,
                type: this.search.level,
                search_type: 'ITEM',
                report_group_id: this.form.data.id,
                report_group_ad_unit_item: [item]
            }
        },
        addAllItem(){
            const items = []
            this.selectableItems.forEach((item, idx)=>{
                items.push(this.makeAdUnit(item, idx))
            });

            this.form.data.report_group_ad_unit.splice(0)
            this.form.data.report_group_ad_unit.push(...items)
        },
        addItem(item, idx){
            this.form.data.report_group_ad_unit.push(this.makeAdUnit(item, idx))
        },
        deleteAllItem(){
            this.form.data.report_group_ad_unit.splice(0)
        },
        deleteItem(item, idx){
            const pageIdx = this.tbl2.options.itemsPerPage * (this.tbl2.options.page -1)
            this.form.data.report_group_ad_unit.splice(pageIdx+idx,1)
        }
    }
};

export default MpReportGroupUnitsGroup;