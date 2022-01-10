const MpReportGroupColumnPlatform = {
    name: 'mp-report-group-column-platform',
    template: `
    <v-card outlined>
        <v-row>
            <v-col cols="12" md="6">
                <v-card-title class="mp-check-box-inline">
                    <v-checkbox v-model="check_all.dimension" @change="checkAllReportColumn('dimension',$event)"></v-checkbox>
                    DIMENSION
                </v-card-title>
                <v-list>
                    <v-list-item v-for="item in platform_columns['dimension']" :key="item.value">
                        <template v-slot:default="{ active }">
                            <v-list-item-action>
                                <v-checkbox v-model="item.selected" @change="changeReportColumn(item,$event)"></v-checkbox>
                            </v-list-item-action>
                            <v-list-item-content>
                                <v-list-item-title>{{item.text}}</v-list-item-title>
                                <v-list-item-subtitle>{{item.value}}</v-list-item-subtitle>
                            </v-list-item-content>
                        </template>
                    </v-list-item>
                </v-list>
            </v-col>
            <v-col cols="12" md="6">
                <v-card-title class="mp-check-box-inline">
                    <v-checkbox v-model="check_all.performance" @change="checkAllReportColumn('performance',$event)"></v-checkbox>
                    METRICS
                </v-card-title>
                <v-list>
                    <v-list-item v-for="item in platform_columns['performance']" :key="item.value">
                        <template v-slot:default="{ active }">
                            <v-list-item-action>
                                <v-checkbox v-model="item.selected" @change="changeReportColumn(item,$event)"></v-checkbox>
                            </v-list-item-action>
                            <v-list-item-content>
                                <v-list-item-title>{{item.text}}</v-list-item-title>
                                <v-list-item-subtitle>{{item.value}}</v-list-item-subtitle>
                            </v-list-item-content>
                        </template>
                    </v-list-item>
                </v-list>
            </v-col>
            <!-- <v-col cols="12" md="4">
                <v-card-title class="mp-check-box-inline">
                    <v-checkbox v-model="check_all.conversion" @change="checkAllReportColumn('conversion',$event)"></v-checkbox>
                    CONVERSION
                </v-card-title>
                <v-list>
                    <v-list-item v-for="item in platform_columns['conversion']" :key="item.value">
                        <template v-slot:default="{ active }">
                            <v-list-item-action>
                                <v-checkbox v-model="item.selected" @change="changeReportColumn(item,$event)"></v-checkbox>
                            </v-list-item-action>
                            <v-list-item-content>
                                <v-list-item-title>{{item.text}}</v-list-item-title>
                                <v-list-item-subtitle>{{item.value}}</v-list-item-subtitle>
                            </v-list-item-content>
                        </template>
                    </v-list-item>
                </v-list>
            </v-col>-->
        </v-row>
    </v-card>
    `,
    props: {
        form: {
            type: Object
        },
        columns: {
            type: Array
        },
        dialog: {
            type: Object
        }
    },
    computed: {
        selectableDimensionItems:  function(){
             const rc_map = {};
             this.form.data.report_columns.filter(rc=> rc.type === 'DSP').forEach(item=>{
                 rc_map[item.type+'_'+item.value] = item;
             });

             this.columns.forEach(item=>{
                 this.$set(item, 'selected', rc_map[item.type+'_'+item.value] !== undefined)
             });

            return this.columns;
        },
    },
    data: function () {
        return {
            platform_columns:{
                dimension: [],
                performance: [],
                conversion: []
            },
            check_all:{
                dimension: false,
                performance: false,
                conversion: false
            }
        }
    },
    created() {
        const rc_map = {};
        this.form.data.report_columns.filter(rc=> rc.side === 'DSP').forEach(item=>{
            rc_map[item.type+'_'+item.value] = item;
        });

        this.columns.forEach(item=>{
            this.$set(item, 'selected', rc_map[item.type+'_'+item.value] !== undefined)
        })

        this.columns.forEach(item=>{
            if(item.type === 'D'){
                this.platform_columns.dimension.push(item);
            }else if(item.type === 'M_P'){
                this.platform_columns.performance.push(item);
            }else if(item.type === 'M_C'){
                this.platform_columns.conversion.push(item);
            }
        });
    },
    mounted() {

    },
    methods: {
        saveItem() {
            if(this.dialog){
                this.dialog.close() // mp-dialog 로 쓰인경우 닫기 위해
            }
        },
        checkAllReportColumn(type, checked){
            this.form.data.report_columns = this.form.data.report_columns.filter(item=>{
                if (type === 'dimension'){
                    if (item.type !== 'D'){
                        return true
                    }else{
                        item.selected = false
                        return false
                    }
                }else if(type === 'performance'){
                    if (item.type !== 'M_P'){
                        return true
                    }else{
                        item.selected = false
                        return false
                    }
                }else if(type === 'conversion'){
                    if (item.type !== 'M_C'){
                        return true
                    }else{
                        item.selected = false
                        return false
                    }
                }
            });

            if(checked){
                const columns = this.platform_columns[type];
                for( let i  in columns){
                    if (Object.prototype.hasOwnProperty.call(columns, i)){
                        columns[i].selected= true
                        this.form.data.report_columns.push(columns[i])
                    }
                }
            }
        },
        changeReportColumn(item, checked){
            if(checked){
                this.form.data.report_columns.push(item)
            }else{
                this.removeReportColumn(item)
            }
        },
        removeReportColumn(item){
            const idx = this.form.data.report_columns.findIndex(column=>{
                return column.type === item.type && column.value === item.value;
            });
            if(idx > -1){
                this.form.data.report_columns.splice(idx,1)
            }
        },
    }
};

export default MpReportGroupColumnPlatform;