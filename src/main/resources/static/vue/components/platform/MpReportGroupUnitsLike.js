const MpReportGroupUnitsLike = {
    name: 'mp-report-group-units-like',
    template: `
    <v-card outlined>
        <v-expansion-panels focusable>
            <v-expansion-panel>
                <v-expansion-panel-header>사용법</v-expansion-panel-header>
                <v-expansion-panel-content>
                    <v-simple-table dense>
                        <template v-slot:default>
                            <thead>
                                <tr>
                                    <th class="text-left" style="width:150px">LIKE Operator</th>
                                    <th class="text-left">Description</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td class="text-left">a%</td>
                                    <td class="text-left">Finds any values that start with "a"</td>
                                </tr>
                                <tr>
                                    <td class="text-left">%a</td>
                                    <td class="text-left">Finds any values that end with "a"</td>
                                </tr>
                                <tr>
                                    <td class="text-left">%a%</td>
                                    <td class="text-left">Finds any values that have "a" in any position</td>
                                </tr>
                                <tr>
                                    <td class="text-left">_a%</td>
                                    <td class="text-left">Finds any values that have "a" in the second position</td>
                                </tr>
                                <tr>
                                    <td class="text-left">a_%</td>
                                    <td class="text-left">Finds any values that start with "a" and are at least 2 characters in length</td>
                                </tr>
                                <tr>
                                    <td class="text-left">a__%</td>
                                    <td class="text-left">Finds any values that start with "a" and are at least 3 characters in length</td>
                                </tr>
                                <tr>
                                    <td class="text-left">a%o</td>
                                    <td class="text-left">Finds any values that start with "a" and ends with "o"</td>
                                </tr>
                            </tbody>
                        </template>
                    </v-simple-table>
                </v-expansion-panel-content>
            </v-expansion-panel>
        </v-expansion-panels>
        <v-row>
            <v-col cols="12">
                <v-text-field v-model="unit.report_group_ad_unit_item[0].item_id" label="AD ITEM 검색식" outlined dense hide-details @keyup.enter="check">
                    <template v-slot:append>
                        <v-btn icon small color="red" @click="check" title="확인"><v-icon>mdi-magnify</v-icon></v-btn>
                    </template>
                </v-text-field>
            </v-col>
        </v-row>
        <v-row>
            <v-col cols="12">
                <v-card>
                    <v-data-table dense fixed-header :height="$settings.datatable.rows10" :footer-props="$settings.datatable.footer10" :headers="tbl.headers" :items="tbl.items" :options.sync="tbl.options">
                        <template v-slot:item.text="{ item, index}">
                            <v-chip x-small chip>{{item.value}}</v-chip> {{item.text}}
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
                    {text: '선택 될 AD ITEM', value: 'text'},
                ],
                items: [],
                options: {}
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
    },
    mounted(){
        if(this.mercury.base.util.hasValue(this.unit.report_group_ad_unit_item[0].item_id)){
            this.check()
        }
    },
    methods: {
        check(){
            if(!this.mercury.base.util.hasValue(this.unit.report_group_ad_unit_item[0].item_id)){
                this.mercury.base.lib.notify({message:'AD ITEM 검색식을 입력해주세요.', type:'warning'});
                return false;
            }

            const data = {
                client_id : this.form.data.client,
                platform : this.form.data.platform,
                platform_id : this.form.data.platform_id,
                type: this.unit.type,
                text: this.unit.report_group_ad_unit_item[0].item_id
            }

            this.xAjax({
                url:'/LittleJoe/api/reportgroups/get_like_expression_units/',
                method:'GET',
                data: data
            }).then((resp)=>{
                this.tbl.items = resp;
            })
        }
    }
};

export default MpReportGroupUnitsLike;