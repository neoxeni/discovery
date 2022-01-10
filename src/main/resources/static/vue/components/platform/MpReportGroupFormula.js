const MpReportGroupFormula = {
    name: 'mp-report-group-formula',
    template: `
    <v-card outlined>
        <div class="d-flex report-formula">
            <v-card class="formulize-custom-platform-box">
                <div ref="formulize-custom-platform-box" style="height: 450px;overflow-y: auto">
                    <v-list dense>
                        <v-list-item v-for="item in dspColumns" :key="item.value">
                            <v-list-item-content>
                                <span class="mr-1 mb-1 v-chip v-chip--removable v-size--small green formulize-custom"
                                    :data-side="item.side" 
                                    :data-platform="item.platform"
                                    :data-type="item.type" 
                                    :data-value="item.value" 
                                    :data-text="item.text"
                                    :data-label="item.label"
                                    @click="insertItem(item)"
                                >
                                    <span class="v-chip__content">
                                        <span :title="item.value" class="ellipsis">{{item.text}}</span>
                                        <!--<button type="button" aria-label="Close" class="v-icon notranslate v-chip__close v-icon&#45;&#45;link v-icon&#45;&#45;right mdi mdi-close-circle theme&#45;&#45;light" style="font-size: 18px;"></button>-->
                                    </span>
                                </span>
                            </v-list-item-content>
                        </v-list-item>
                    </v-list>
                </div>
            </v-card>
            <v-card style="flex-grow: 1">
                <div ref="formulize" class="formulize-advanced" style="width: 100%;height: 100%"></div>
            </v-card>
            <v-card class="formulize-custom-conversion-box">
                <div ref="formulize-custom-conversion-box" >
                    <div class="pa-2">
                        <v-text-field v-model="conversionSearch" label="검색" outlined dense hide-details clearable></v-text-field>
                    </div>
                    <v-tabs v-model="tab"  height="35">
                        <v-tab v-for="item in conversionColumns" :key="item.key" >
                            <span>{{ item.name }}</span>
                        </v-tab>
                    </v-tabs>
                    <v-tabs-items v-model="tab" style="height: 450px;overflow-y: auto">
                        <v-tab-item v-for="(item, index) in conversionColumns" :key="item.key">
                            <template v-if="index === tab">
                                <v-tabs v-model="item.tab" center-active color="blue" height="35">
                                    <v-tab v-for="innerTab in item.innerTabs" :key="innerTab.type">
                                        <span>{{ innerTab.type }}</span>
                                    </v-tab>
                                </v-tabs>
                                <v-tabs-items v-model="item.tab">
                                    <v-tab-item v-for="(innerTab, innerIndex) in item.innerTabs" :key="innerTab.key">
                                        <template v-if="innerIndex === item.tab">
                                            <v-treeview :items="innerTab.columns" return-object dense hoverable item-key="value" item-text="text"
                                              :search="conversionSearch"
                                            >
                                                <template v-slot:label="{ item, open, leaf }">
                                                    <template v-if="leaf">
                                                        <span class="mr-1 mb-1 v-chip v-chip--removable v-size--small blue formulize-custom"
                                                            :data-side="item.side"
                                                            :data-platform="item.platform"
                                                            :data-type="item.type" 
                                                            :data-value="item.value" 
                                                            :data-text="item.text" 
                                                            :data-label="item.label"
                                                            @click="insertItem(item)"
                                                        >
                                                            <span class="v-chip__content">
                                                                <span :title="item.value+'#'+item.text" class="ellipsis">{{item.text}}</span>
                                                            </span>
                                                        </span>
                                                    </template>
                                                    <template v-else>
                                                        <span :title="item.value+'#'+item.text" class="ellipsis">{{item.text}}</span>
                                                    </template>
                                                </template>
                                            </v-treeview>
                                        </template>
                                    </v-tab-item>
                                    
                                </v-tabs-items>
                            </template>
                        </v-tab-item>
                    </v-tabs-items>
                </div>
            </v-card>
        </div>
        <v-form class="input-form" ref="form" v-model="valid">
            <v-row>
                <v-col cols="12" md="12">
                    <v-text-field label="Column Name" v-model="item.text" :rules="mercury.base.rule.required" hide-detail></v-text-field>
                    <mp-prevent-submit-input></mp-prevent-submit-input>
                </v-col>
            </v-row>
        </v-form>
    </v-card>
    `,
    props: {
        form: {
            type: Object
        },
        column: {
            type: Object
        },
        dialog: {
            type: Object
        }
    },
    data: function () {
        return {
            tab: 0,
            formulize : null,
            dspColumns: [],
            conversionColumns: [],
            conversionSearch:'',
            item: {
                type: 'FORMULA',
                text: undefined,
                value: []
            },
            isNew: true,
            valid: true
        }
    },
    created() {
        if(this.column !== undefined){
            this.item.text = this.column.text;
            this.item.value = this.column.value;
            this.isNew = false;
        }

        const vd = '#';     //value_delimiter

        //Platform item 생성
        const report_type = this.form.data.report_type;
        this.dspColumns = mercury.base.util.deepCopy(this.form.group.dsp.columns.filter(it=>{
            return it.type === 'M_P' || it.type === 'M_C'
        }));

        this.dspColumns.forEach(c=>{
            c.label = c.text;
        });

        const conversionPlatformFilter = {}
        this.form.data.report_group_conversion_source.forEach(item=>{
            let conv = item.conversion_platform
            if(conv === this.form.data.platform){   //자체 전환인경우
                if(report_type !== undefined && report_type !== null){
                    conv = conv+'_'+report_type
                }
            }
            conv = conv + '_' + item.conversion_platform_id
            conversionPlatformFilter[conv] = true
        })

        //Conversion item 생성
        this.form.group.conversion.platforms.forEach((item=> {
            const platform = item.platform;
            const platformName = item.platform_name;
            let platformForItem = platform;
            if(platform === this.form.data.platform){   //자체 전환인경우
                if(report_type !== undefined && report_type !== null) {
                    platformForItem = platformForItem + '_' + report_type
                }
            }

            const conversionItemKey = platformForItem+'_'+item['platform_id'];
            if(conversionPlatformFilter[conversionItemKey] !== undefined){
                const conversionFields = this.form.group.conversion.fields[platformForItem];
                let conversionItems = this.form.group.conversion.items[conversionItemKey];
                conversionItems = conversionItems !== undefined ? mercury.base.util.deepCopy(conversionItems) : [];


                const innerTabs = []
                for (let type in conversionItems.type){
                    if(Object.prototype.hasOwnProperty.call(conversionItems.type, type)){
                        const columns = conversionItems.type[type];
                        columns.forEach(column=>{
                            let targetFields = conversionFields;
                            const columnType =  column['type'];
                            if(columnType !== 'CUSTOM' && columnType !== 'STANDARD'){
                                if (this.form.group.conversion.fields[platformForItem+'_'+columnType] !== undefined){
                                    targetFields  = this.form.group.conversion.fields[platformForItem+'_'+columnType];
                                }
                            }

                            column['side'] = 'CONVERSION'
                            column['value'] = platform + vd + column.value;
                            column['label'] = column.text;
                            column['type'] = column.type;

                            const children = mercury.base.util.deepCopy(targetFields);
                            let hasAnotherValue = false
                            for(let i = 0, ic = children.length; i < ic; i++){
                                const child = children[i];
                                if(child.value !== 'value'){
                                    hasAnotherValue = true;
                                    break;
                                }
                            }
                            if(hasAnotherValue){
                                children.forEach(child=>{
                                    child['side'] = column['side'];
                                    child['value'] = column['value'] + vd + child['value'];
                                    child['platform'] = platform;
                                    child['text'] = child['text'];
                                    child['label'] = column['label'] + vd + child['text'];
                                    child['type'] = column['type']
                                    child['platform_id'] = column['platform_id']
                                });

                                column['children'] = children;
                            }
                        });

                        innerTabs.push({
                            type: type,
                            columns: columns
                        });
                    }
                }

                innerTabs.reverse()

                // GA의 경우 EVENT가 너무 많아 이벤트 타입별로 쪼개기
                if(platformForItem === 'GA'){
                    const idx = innerTabs.findIndex(item=>{
                        return item.type === 'EVENT';
                    });
                    if (idx > -1){
                        const eventTab = innerTabs.splice(idx, 1);
                        const events = {};
                        eventTab[0].columns.forEach(item=>{
                            const sp = item.text.split('#')

                            let eventColumns = events[sp[0]];
                            if(eventColumns === undefined){
                                events[sp[0]] = eventColumns = []
                            }

                            item.text = sp[1]
                            eventColumns.push(item)
                        });

                        for(let eventType in events){
                            if(Object.prototype.hasOwnProperty.call(events, eventType)){
                                let type = eventType.toUpperCase();
                                if(type.startsWith('EVENT')){   //EVENTTYPE => EVENT_TYPE
                                    type = type.replace('EVENT', 'EVENT_');
                                }

                                innerTabs.push({
                                    type: type,
                                    columns: events[eventType]
                                })
                            }
                        }
                    }
                }

                this.conversionColumns.push({
                    'type': 'CONVERSION',
                    'key': conversionItemKey,
                    'name': platformName,
                    'tab': null,
                    'innerTabs': innerTabs,
                });
            }
        }));
    },
    mounted() {
        const target = this.$refs['formulize'];
        window.formula = this.formulize = new window.formulize.UI(target, {
            input: function(value){

            },
            pipe: {
                insert: function(data) {
                    if (data instanceof HTMLElement || data instanceof jQuery){
                        const $tag = $(data);
                        if ($tag.data('value')) {
                            data = $tag.data()
                        }else{
                            data = $tag
                        }
                    }

                    if (typeof data === 'object') {
                        let tag = '<div class="formulize-item formulize-custom side-'+data['side']+'"'
                        for (let k in data) {
                            if (data.hasOwnProperty(k)) {
                                tag += ' data-' + k + '="' + data[k] + '"'
                            }
                        }
                        tag += '>';

                        if (data['side'] === 'CONVERSION') {
                            tag += '<span class="mr-1 v-chip v-chip--label v-chip--no-color theme--dark v-size--x-small">'
                            tag += '    <span class="v-chip__content">' + mercury.base.skylab.getShortPlatformName(data.platform) + '</span>'
                            tag += '</span>'
                        }

                        tag += data.label+'</div>'
                        return $(tag)
                    }

                    return data
                },
                parse: function(elem) {
                    const $elem = $(elem);
                    if ($elem.data('value')){
                        return $elem.data()
                    }else{
                        return $elem.text();
                    }
                }
            }
        });

        this.formulize.insertData(this.item.value);
    },

    methods: {
        insertItem(item){
            const position = { x: 0, y: 0};
            try{
                const cu_po = this.formulize.cursor.position();
                position['x']= cu_po.left;
                position['y']= cu_po.top;
            }catch(e){
                //ignore
            }

            this.formulize.insert(item, position);
            this.formulize.textBox.focus()
        },
        saveItem() {
            if (!this.valid && this.$refs.form !== undefined) {
                return this.$refs.form.validate();//form validation 수행하여 화면에 에러 메시지 표시
            }

            const data = this.formulize.getExpression();

            if (typeof data === 'string'){
                this.mercury.base.lib.notify({message:data, type:'error'});
                return false;
            }else if(Array.isArray(data) && data.length === 0){
                this.mercury.base.lib.notify({message:'token is empty', type:'error'});
                return false;
            }

            this.item.value = this.formulize.getExpression()
            if(this.isNew){
                this.form.data.report_columns.push(this.item)
            }else{
                const idx = this.form.data.report_columns.findIndex(item=>{
                    return item.type === this.column.type && item.name === this.column.name && item.value === this.column.value
                });
                if (idx > -1){
                    const target = this.form.data.report_columns[idx];
                    target.text = this.item.text;
                    target.value = this.item.value;
                }
            }

            if(this.dialog){
                this.dialog.close() // mp-dialog 로 쓰인경우 닫기 위해
            }
        },
        resetItem(){

        }
    }
};

export default MpReportGroupFormula;