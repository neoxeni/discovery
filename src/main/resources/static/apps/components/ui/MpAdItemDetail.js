const MpAdItemDetail = {
    name: 'mp-ad-item-detail',
    template: `
    <div>
        <v-form v-model="form.valid" ref="form">
            <v-row>
                <v-col cols="12" md="3">
                    <v-text-field label="type" v-model="form.data.type" readonly></v-text-field>
                </v-col>
                <v-col cols="12" md="3">
                    <v-text-field label="sub_type" v-model="form.data.sub_type" readonly></v-text-field>
                </v-col>
                <v-col cols="12" md="3">
                    <v-text-field label="status" v-model="form.data.status" readonly></v-text-field>
                </v-col>
                <v-col cols="12" md="3">
                    <v-text-field label="effective_status" v-model="form.data.effective_status" readonly></v-text-field>
                </v-col>
                <v-col cols="12" md="6">
                    <v-text-field label="시작일" v-model="form.data.start_date" readonly></v-text-field>
                </v-col>
                <v-col cols="12" md="6">
                    <v-text-field label="종료일" v-model="form.data.end_date" readonly></v-text-field>
                </v-col>
            </v-row>
            <template v-if="hasConversionData">
                <v-row>
                    <v-col cols="12" md="12">
                        <v-text-field label="conversion_url" v-model="form.data.conversion_url">
                            <template v-slot:append-outer><v-btn icon small color="green" @click="extractUrl(form.data.conversion_url)" title="utm 정보 추출"><v-icon>mdi-file-link-outline</v-icon></v-btn></template>
                        </v-text-field>
                    </v-col>
                    <v-col cols="12" md="4">
                        <v-text-field label="utm_source" v-model="form.data.utm_source"></v-text-field>
                    </v-col>
                    <v-col cols="12" md="4">
                        <v-text-field label="utm_medium" v-model="form.data.utm_medium"></v-text-field>
                    </v-col>
                    <v-col cols="12" md="4">
                        <v-text-field label="utm_campaign" v-model="form.data.utm_campaign"></v-text-field>
                    </v-col>
                    <v-col cols="12" md="4">
                        <v-text-field label="utm_id" v-model="form.data.utm_id"></v-text-field>
                    </v-col>
                    <v-col cols="12" md="4">
                        <v-text-field label="utm_term" v-model="form.data.utm_term"></v-text-field>
                    </v-col>
                    <v-col cols="12" md="4">
                        <v-text-field label="utm_content" v-model="form.data.utm_content"></v-text-field>
                    </v-col>
                    <v-col cols="12" md="6">
                        <v-text-field label="cm_campaign_id" v-model="form.data.cm_campaign_id"></v-text-field>
                    </v-col>
                    <v-col cols="12" md="6">
                        <v-text-field label="cm_placement_id" v-model="form.data.cm_placement_id"></v-text-field>
                    </v-col>
                    <v-col cols="12" md="6">
                        <v-text-field label="cm_ad_id" v-model="form.data.cm_ad_id"></v-text-field>
                    </v-col>
                    <v-col cols="12" md="6">
                        <v-text-field label="cm_creative_id" v-model="form.data.cm_creative_id"></v-text-field>
                    </v-col>
                    <v-col cols="12" md="12" class="text-right">
                        <mp-button mode="text" color="warning" label="저장" icon="mdi-file-document-edit" @click="saveItem()">저장</mp-button>
                    </v-col>
                </v-row>
            </template>
        </v-form>
        <v-tabs v-model="rawTab">
            <v-tab>Object</v-tab>
            <v-tab>String</v-tab>
        </v-tabs>
        <v-tabs-items v-model="rawTab" outlined rounded>
            <v-tab-item>
                <div ref="object"></div>
            </v-tab-item>
            <v-tab-item>
                <pre style="overflow-x: auto ">{{rawJson}}</pre>
            </v-tab-item>
        </v-tabs-items>
    </div>
    `,
    props: {
        item: {
            type: Object
        }
    },
    watch: {
        'item.id': {
            handler(itemId) {
                this.getRaw(itemId);
            },
        }
    },
    data: function () {
        return {
            rawTab: 0,
            rawJson: {},
            form: {
                type: 'code',
                data: {
                    id : undefined,
                    platform : undefined,
                    platform_id : undefined,
                    type : undefined,
                    sub_type : undefined,
                    status : undefined,
                    parent : undefined,
                    value : undefined,
                    text : undefined,
                    utm_source : undefined,
                    utm_medium : undefined,
                    utm_id : undefined,
                    utm_campaign : undefined,
                    utm_term : undefined,
                    utm_content : undefined,
                    cm_campaign_id : undefined,
                    cm_placement_id : undefined,
                    cm_ad_id : undefined,
                    cm_creative_id : undefined,
                    conversion_url: undefined,
                    etc1 : undefined,
                    etc2 : undefined,
                    etc3 : undefined,
                    etc4 : undefined,
                    sort : undefined,
                    leaf : undefined,
                    description : undefined,
                    uuid : undefined,
                    client_id : undefined,
                    created_at : undefined,
                    updated_at : undefined,
                    raw: undefined
                },
                valid: false,
            },
        }
    },
    computed: {
        hasConversionData() {
            const type = this.form.data.type;
            return type==='AD' || type==='KEYWORD' || type==='CREATIVE';
        }
    },
    mounted() {
        this.getRaw(this.item.id)
    },
    methods: {
        extractUrl(url){
            if(url === undefined || url === ''){
                this.mercury.base.lib.notify({message:'tracking url을 입력해주세요.', type:'warning'});
                return;
            }

            this.xAjaxJson({
                url: '/LittleJoe/common/extract_url',
                method: 'POST',
                data: {url: url}
            }).then(resp=>{
                this.form.data['utm_source'] = resp['utm_source'] || ''
                this.form.data['utm_medium'] = resp['utm_medium'] || ''
                this.form.data['utm_campaign'] = resp['utm_campaign'] || ''
                this.form.data['utm_id'] = resp['utm_id'] || ''
                this.form.data['utm_term'] = resp['utm_term'] || ''
                this.form.data['utm_content'] = resp['utm_content'] || ''
                this.form.data['cm_campaign_id'] = resp['cm_campaign_id'] || ''
                this.form.data['cm_placement_id'] = resp['cm_placement_id'] || ''
                this.form.data['cm_ad_id'] = resp['cm_ad_id'] || ''
                this.form.data['cm_creative_id'] = resp['cm_creative_id'] || ''
            })
        },
        saveItem() {
            this.xAjax({
                url: '/LittleJoe/api/clientsplatforms/units_update/',
                method:'PATCH',
                data: this.form.data
            }).then(resp=>{
                Object.assign(this.item, this.form.data)
                this.mercury.base.lib.notify(resp);
            })
        },
        bindRaw(raw){
            const target = this.$refs['object'];
            if(target !== undefined){
                const children = target.children;
                for (let i = 0, ic = children.length; i < ic; i++) {
                    target.removeChild(children[i]);
                }
                target.appendChild(this.mercury.base.util.printJSONObject(raw));
            }
            this.rawJson = JSON.stringify(raw, null, 4);
        },
        getRaw(id){
            this.xAjax({
                url: '/LittleJoe/api/clientsplatforms/get_raw/',
                data: 'id='+id
            }).then(resp=>{
                this.form.data = resp
                this.bindRaw(this.form.data.raw)
            })
        }
    }
};

export default MpAdItemDetail;