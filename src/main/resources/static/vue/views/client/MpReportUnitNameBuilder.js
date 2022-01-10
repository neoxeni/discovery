import MpAdvertiserSelect from "/static/vue/components/ui/MpAdvertiserSelect.js";
const MpReportUnitNameBuilder = {
    name: 'mp-report-unit-name-builder',
    components: {
        MpAdvertiserSelect
    },
    template: `
    <v-card outlined>
        <v-form class="input-form" ref="form" v-model="form.valid">
            <v-row>
                <v-col cols="12" md="4">
                    <mp-advertiser-select label="광고주" v-model="form.data.client" @change="changeAdvertiser" return-object outlined dense hide-details></mp-advertiser-select>
                </v-col>
                <v-col cols="12" md="4">
                    <v-select label="플랫폼" v-model="form.data.platform" :items="getStructureKeys" outlined dense hide-details @change="changePlatform"></v-select>
                </v-col>
                <v-col cols="12" md="4">
                    <v-select label="유형" v-model="form.data.level" :items="units.level[form.data.platform]" outlined dense hide-details @change="changeLevel"></v-select>
                </v-col>
            </v-row>
            <v-row>
                <v-col cols="12" md="3" v-for="item in getStructure" :key="item.name">
                    <template v-if="item.type === 'text'">
                        <v-text-field v-model="form.data[item.name]" :label="item.text" outlined dense hide-details :readonly="item.readonly===true" :rules="mercury.base.rule.required"></v-text-field>
                    </template>
                    <template v-else-if="item.type === 'select'">
                        <v-select v-model="form.data[item.name]" :label="item.text" :items="getSelectItems(item)" outlined dense hide-details :rules="mercury.base.rule.required"></v-select>
                    </template>
                    <template v-else-if="item.type === 'date'">
                        <mp-date-picker v-model="form.data[item.name]" :label="item.text" type="date" readonly></mp-date-picker>
                    </template>
                    <template v-else>
                        UNKNOWN {{item.text}}
                    </template>
                </v-col>
                <v-col cols="12" v-show="getCompleteObject.value !== undefined">
                    <input type="hidden" ref="textToCopy" :value="getCompleteObject.value"/>
                    <v-alert :type="getCompleteObject.complete ? 'success' : 'warning'" prominent outlined dense border="left">
                        <v-row align="center">
                            <v-col class="grow" v-html="getCompleteObject.value"></v-col>
                            <v-col class="shrink" v-show="getCompleteObject.complete">
                                <v-btn icon small color="green" @click.prevent.stop="copyText()" title="복사"><v-icon>mdi-content-copy</v-icon></v-btn>
                            </v-col>
                        </v-row>
                    </v-alert>
                </v-col>
            </v-row>
        </v-form>
    </v-card>
    `,
    data: function () {
        return {
            form: {
                delimiter: ' | ',
                init: {
                    media : undefined,
                    goal : 'CONV',
                    device : 'ALL',
                    date : moment().format('YYYY-MM-DD'),
                    campaignName : undefined,
                    adGroupName : undefined,
                    ioName : undefined,
                    liName : undefined,
                    adName : undefined,
                    creativeName : undefined,
                    promotion : undefined
                },
                data: {
                    client: undefined,
                    advertiser: undefined,//영문이름
                    platform: undefined,
                    level : undefined
                },
                valid: false
            },
            fields:{
                advertiser: {
                    name: 'advertiser',
                    text: '광고주',
                    type: 'text',
                    readonly: true
                },
                platform: {
                    name: 'platform',
                    text: '플랫폼',
                    type: 'text',
                    readonly: true,
                },
                target_platform: {
                    name: 'target_platform',
                    text: '플랫폼',
                    type: 'select',
                    items: ['DV360', 'GOOGLEADS', 'FBIG', 'SA360', 'NAVER', 'DAUM']
                },
                media: {
                    name: 'media',
                    text: '매체종류',
                    type: 'select',
                    items: []
                },
                goal: {
                    name: 'goal',
                    text: '캠페인목표',
                    type: 'select',
                    items: ['CONV', 'CLICK', 'VIEW', 'REACH', 'IMPR']
                },
                device: {
                    name: 'device',
                    text: '기기',
                    type: 'select',
                    items: ['ALL', 'PC', 'MO']
                },
                date: {
                    name: 'date',
                    text: '날짜',
                    type: 'date'
                },
                campaignName: {
                    name: 'campaignName',
                    text: '캠페인명',
                    type: 'text'
                },
                adGroupName: {
                    name: 'adGroupName',
                    text: 'Ad Group 명',
                    type: 'text'
                },
                ioName: {
                    name: 'ioName',
                    text: 'IO 명',
                    type: 'text'
                },
                liName: {
                    name: 'liName',
                    text: 'LI 명(타켓팅 옵션)',
                    type: 'text'
                },
                adName: {
                    name: 'adName',
                    text: 'Ad 명',
                    type: 'text'
                },
                creativeName: {
                    name: 'creativeName',
                    text: '소재명',
                    type: 'text'
                },
                promotion: {
                    name: 'promotion',
                    text: '프로모션',
                    type: 'text'
                }
            },
            structure:{
                DV360_DISPLAY: {
                    CAMPAIGN: ['media'],
                    INSERTION_ORDER: ['advertiser', 'media', 'goal', 'ioName'],
                    LINE_ITEM: ['advertiser', 'media', 'goal', 'liName', 'device'],
                    CREATIVE: ['platform', 'media', 'goal', 'ioName', 'creativeName', 'device', 'date']
                },
                DV360_VIDEO: {
                    CAMPAIGN: ['media'],
                    INSERTION_ORDER: ['advertiser', 'media', 'goal', 'ioName'],
                    LINE_ITEM: ['advertiser', 'media', 'goal', 'liName', 'device'],
                    AD_GROUP: ['liName', 'adGroupName'],
                    AD: ['adGroupName', 'adName', 'date']
                },
                CM: {
                    CAMPAIGN: ['advertiser', 'target_platform', 'media', 'goal'],
                    PLACEMENT: ['target_platform', 'media', 'goal', 'ioName', 'creativeName', 'device', 'date'],
                    AD: ['target_platform', 'media', 'goal', 'promotion'],
                    CREATIVE: ['target_platform', 'media']
                },
                GADS: {
                    CAMPAIGN: ['advertiser', 'media', 'goal', 'campaignName'],
                    AD_GROUP: ['campaignName', 'adGroupName', 'device']
                },
                FACEBOOK: {
                    CAMPAIGN: ['advertiser', 'goal', 'campaignName'],
                    AD_GROUP: ['campaignName', 'adGroupName', 'device'],
                    AD: ['adGroupName', 'media', 'adName', 'device', 'date']
                },
                TIKTOK: {
                    CAMPAIGN: ['advertiser', 'goal', 'campaignName'],
                    AD_GROUP: ['campaignName', 'adGroupName', 'device'],
                    AD: ['adGroupName', 'media', 'adName', 'device', 'date']
                }
            },
            code: {
                type: mercury.base.code['CODES']['API_BatchScheduleType'],
                status: mercury.base.code['CODES']['API_BatchScheduleStatus'],
                media: {
                    DV360_DISPLAY: ['DISPLAY'],
                    DV360_VIDEO: ['VIDEO'],
                    GADS: ['DISPLAY', 'VIDEO', 'SEARCH'],
                    FACEBOOK: ['DISPLAY', 'VIDEO', 'FEED'],
                    TIKTOK: ['DISPLAY', 'VIDEO', 'FEED'],
                    CM: ['DISPLAY', 'VIDEO', 'SEARCH', 'FEED']
                }
            },
            units: {
                level : {
                    DV360_DISPLAY: ['CAMPAIGN', 'INSERTION_ORDER', 'LINE_ITEM', 'CREATIVE'],
                    DV360_VIDEO: ['CAMPAIGN', 'INSERTION_ORDER', 'LINE_ITEM', 'AD_GROUP', 'AD'],
                    CM: ['CAMPAIGN', 'PLACEMENT', 'AD', 'CREATIVE'],
                    FACEBOOK: ['CAMPAIGN', 'AD_GROUP', 'AD'],
                    TIKTOK: ['CAMPAIGN', 'AD_GROUP', 'AD'],
                    GADS: ['CAMPAIGN', 'AD_GROUP']  // 'AD' 제외
                }//this.mercury.base.skylab.getUnitLevel()
            }
        }
    },
    created(){
        this.form.data = Object.assign({}, this.form.data, this.form.init);
    },
    computed: {
        getStructureKeys(){
            return Object.keys(this.structure)
        },
        getStructure(){
            if (this.form.data.client === undefined || this.form.data.platform === undefined || this.form.data.level === undefined){
                return undefined
            }

            const arr = []
            this.structure[this.form.data.platform][this.form.data.level].forEach(it=>{
                arr.push(this.fields[it])
            })
            return arr;
        },
        getCompleteObject(){
            const result = {
                complete: false,
                value: undefined
            }
            const structure = this.getStructure;
            if(structure !== undefined){
                if (!this.form.valid && this.$refs.form !== undefined) {
                    this.$refs.form.validate();//form validation 수행하여 화면에 에러 메시지 표시
                }

                let value= '';
                let hasUndefined = false;
                structure.forEach((item, index)=>{
                    if(index > 0){
                        value += this.form.delimiter
                    }

                    let val = this.form.data[item.name];
                    if(val === undefined || val === ''){
                        hasUndefined = true
                        value += '<span class="danger--text">'+item.text+'</span>';
                    }else{
                        if(item.type === 'date'){
                            val = val.replace(/-/g, '');
                        }

                        if(val === 'DV360_DISPLAY' || val === 'DV360_VIDEO'){
                            val = 'DV360';
                        }

                        value += val;
                    }
                });

                if(!hasUndefined){
                    result['complete'] = true;
                }

                result['value'] = value;
            }

            return result;
        }
    },
    methods: {
        getSelectItems(item){
            if(item.name === 'media'){
                return this.code.media[this.form.data.platform]
            }else{
                return item.items;
            }
        },
        changeAdvertiser(value){
            this.form.data.advertiser = value.eng_name;
            this.resetName();
        },
        changePlatform(value){
            this.form.data.level = undefined;
            this.resetName()
        },
        changeLevel(value){
            this.resetName()
        },
        resetName(){
            Object.assign(this.form.data, this.form.init);
        },
        copyText () {
            let textToCopy = this.$refs.textToCopy;
            try{
                textToCopy.setAttribute('type', 'text')
                textToCopy.select()
                document.execCommand("copy");
                this.mercury.base.lib.notify('clipboard에 복사되었습니다.');
            }finally {
                textToCopy.setAttribute('type', 'hidden')
            }
        }
    }
};

export default MpReportUnitNameBuilder;