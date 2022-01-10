const baseHelper = Vuex.createNamespacedHelpers('base');
import crudMixIn from "/static/vue/mixin/crudMixIn.js";
import MpClientSpreadsheetShare from "/static/vue/components/platform/MpClientSpreadsheetShare.js"
import MpClientGoogleAnalyticsList from "/static/vue/components/platform/MpClientGoogleAnalyticsList.js"

const MpClientPlatform = {
    name: 'mp-client-platform',
    template: `
    <v-card outlined>
        <mp-search>
            <div class="w-190px">
                <v-select label="플랫폼" v-model="search.platform" @change="fetchData(true)" :items="mercury.base.util.concatArray(code.platform,[{text:'전체',value:''}], false)" outlined dense hide-details></v-select>
            </div>
            <div>
                <v-text-field v-model="search.platform_id" label="플랫폼 아이디" @keyup.enter="fetchData(true)" outlined dense hide-details></v-text-field>
            </div>
            <div class="search-form-btns">
                <mp-button color="primary" label="조회" icon="mdi-magnify" @click="fetchData(true)"></mp-button>
                <mp-button color="info" label="새로입력" icon="mdi-plus" @click="newItem()"></mp-button>
            </div>
        </mp-search>

        <v-data-table dense fixed-header :height="$settings.datatable.rows6" :footer-props="$settings.datatable.footer6" :headers="tbl.headers" :items="tbl.items">
            <template v-slot:header.actions="{ header }">
                <mp-button mode="icon" color="red" label="모든 Item 동기화" icon="mdi-source-branch-check" @click="syncItemsAll()"></mp-button>
            </template>
            <template v-slot:item.actions="{ item }">
                <template v-if="item.option_json['fb_sdf'] === true">
                    <input ref="uploader" class="d-none" type="file" @change="onFileChanged">
                    <mp-button mode="icon" :color="item.dsp_updated_at !== null ? 'green' : 'gray'" label="Platform Item 동기화" icon="mdi-attachment" v-if="item.support.DSP"
                    @click="onUploadClick(item, 'DSP')" :loading="item.loading.DSP" :disabled="item.loading.DSP"></mp-button>
                </template>
                <template v-else>
                    <mp-button mode="icon" :color="item.dsp_updated_at !== null ? 'green' : 'gray'" label="Platform Item 동기화" icon="mdi-source-branch-check" v-if="item.support.DSP"
                    @click="syncItems(item, 'DSP')" :loading="item.loading.DSP" :disabled="item.loading.DSP"></mp-button>
                </template>
                
                <mp-button mode="icon" :color="item.conversion_updated_at !== null ? 'blue' : 'gray'" label="Conversion Item 동기화" icon="mdi-source-branch-check" v-if="item.support.CONVERSION"
                @click="syncItems(item, 'CONVERSION')" :loading="item.loading.CONVERSION" :disabled="item.loading.CONVERSION"></mp-button>
            </template>
            <template v-slot:item.platform_name="{ item }">
                <div class="ellipsis"><a href="#" @click.stop.prevent="selectItem(item)" class="ellipsis">{{item.platform_name}}</a></div>
            </template>
            <template v-slot:item.platform_id="{ item }">
                <div class="ellipsis"><div class="ellipsis" :title="item.platform_id"> {{item.platform_id}}</div></div>
            </template>
            <template v-slot:item.option_id1="{ item }">
                <div class="ellipsis"><div class="ellipsis" :title="item.option_id1"> {{item.option_id1}}</div></div>
            </template>
            <template v-slot:item.option_id2="{ item }">
                <div class="ellipsis"><div class="ellipsis" :title="item.option_id2"> {{item.option_id2}}</div></div>
            </template>
            <template v-slot:item.option_id3="{ item }">
                <div class="ellipsis"><div class="ellipsis" :title="item.option_id3"> {{item.option_id3}}</div></div>
            </template>
            <template v-slot:item.status="{ item }">
                <v-chip :color="item.status === 'ACTIVE' ? 'green' : 'gray'" small>{{item['status_label']}}</v-chip>
            </template>
        </v-data-table>

        <mp-view :view.sync="view">
            <template v-slot:body="{edit}">
                <v-form class="input-form" ref="form" v-model="form.valid">
                    <v-row>
                        <v-col cols="12" md="2">
                            <v-select label="플랫폼" :value="form.data.platform" @change="changePlatform" :items="code.platform" :rules="mercury.base.rule.required" :readonly="edit" return-object></v-select>
                        </v-col>
                        <v-col cols="12" md="2">
                            <v-text-field label="플랫폼 아이디" v-model="form.data.platform_id" :rules="mercury.base.rule.required" :readonly="platformReadonly(form, edit)">
                                <template v-slot:append-outer v-if="form.data.platform === 'GA'"><v-btn icon small color="red" @click="showGoogleAnalyticsList()"><v-icon>mdi-cog</v-icon></v-btn></template> 
                            </v-text-field>
                        </v-col>
                        <v-col cols="12" md="4">
                            <v-text-field label="플랫폼 별칭" v-model="form.data.platform_name" :rules="mercury.base.rule.required"></v-text-field>
                        </v-col>
                        <v-col cols="12" md="2">
                            <v-select label="상태" v-model="form.data.status" :items="code.status"></v-select>
                        </v-col>
                        <v-col cols="12" md="2">
                            <v-select label="자동 동기화" v-model="form.data.option_json['gb_auto_update_sync']" :items="[{text:'예',value:true}, {text:'아니오',value:false}]" hint="자동으로 ITEM 동기화 여부"></v-select>
                        </v-col>
                      
                        <template v-if="form.data.platform === 'GA'">
                            <v-col cols="12" md="4">
                                <v-text-field label="Account Id" v-model="form.data.option_id3" :rules="mercury.base.rule.required"></v-text-field>
                            </v-col>
                            <v-col cols="12" md="4">
                                <v-text-field label="Web Property Id (UA-XXXXXXXX-Y)" v-model="form.data.option_id1" :rules="mercury.base.rule.required"></v-text-field>
                            </v-col>
                            <v-col cols="12" md="4">
                                <v-text-field label="Profile Id" v-model="form.data.option_id2" :rules="mercury.base.rule.required"></v-text-field>
                            </v-col>
                            <v-col cols="12" md="8">
                                <v-text-field label="필터" v-model="form.data.filter" placeholder="ex)ga:source=@dv360,ga:source=@sa360 와 같이 , 로 추가"></v-text-field>
                            </v-col>
                            <v-col cols="12" md="4">
                                <v-select label="Event 수집" v-model="form.data.option_json['ga_event']" :items="[{text:'예',value:true}, {text:'아니오',value:false}]"></v-select>
                            </v-col>
                        </template>
                        <template v-else-if="form.data.platform === 'SA360'">
                            <v-col cols="12" md="6">
                                <v-text-field label="Engine Account Id (GoogleAds platform id)" v-model="form.data.option_id1" :rules="mercury.base.rule.required"></v-text-field>
                            </v-col>
                            <v-col cols="12" md="6">
                                <v-select label="GA Integration" v-model="form.data.option_json['ga_integration']" :items="[{text:'예',value:true}, {text:'아니오',value:false}]"></v-select>
                            </v-col>
                        </template>
                        <template v-else-if="form.data.platform === 'CM'">
                            <v-col cols="12" md="12">
                                <v-text-field label="BidManager Advertiser Id" v-model="form.data.option_id1"></v-text-field>
                            </v-col>
                        </template>
                        <template v-else-if="form.data.platform === 'DV360'">
                            <v-col cols="12" md="12">
                                <v-text-field label="Campaign Manager Id" v-model="form.data.option_id1"></v-text-field>
                            </v-col>
                        </template>
                        <template v-else-if="form.data.platform === 'FACEBOOK'">
                            <v-col cols="12" md="12">
                                <v-select label="Ad Item Sync With SDF" v-model="form.data.option_json['fb_sdf']" :items="[{text:'예',value:true}, {text:'아니오',value:false}]"></v-select>
                            </v-col>
                        </template>
                        <template v-else-if="isSpreadSheetPlatform(form.data.platform_source)">
                            <v-col cols="12" md="12">
                                <v-text-field label="SpreadSheet Id" value="자동생성" v-model="form.data.option_id1" readonly hint="자동생성">
                                    <template v-slot:append-outer v-if="edit"><v-btn icon small @click="showSpreadsheetShare()"><v-icon>mdi-folder-account</v-icon></v-btn></template>
                                </v-text-field>
                            </v-col>
                        </template>
                        <template v-if="edit">
                            <v-col cols="12" md="6">
                                <v-text-field label="PLATFORM 동기화 시간" v-model="form.data.dsp_updated_at" disabled></v-text-field>
                            </v-col>
                            <v-col cols="12" md="6">
                                <v-text-field label="CONVERSION 동기화 시간" v-model="form.data.conversion_updated_at" disabled></v-text-field>
                            </v-col>
                        </template>
                    </v-row>
                </v-form>
            </template>
            <template v-slot:control="{edit}">
                <template v-if="edit">
                    <mp-button mode="text" color="warning" label="저장" icon="mdi-text-box-check" @click="saveItem('PUT')"></mp-button>
                    <mp-button mode="text" color="danger" label="삭제" icon="mdi-delete" @click="deleteItem(form.data)"></mp-button>
                </template>
                <template v-else>
                    <mp-button mode="text" color="warning" label="저장" icon="mdi-text-box-plus" @click="saveItem('POST')"></mp-button>
                    <mp-button mode="text" color="secondary" label="초기화" icon="mdi-refresh" @click="resetItem()"></mp-button>
                </template>
            </template>
        </mp-view>
    
        <mp-dialog :dialog="dialog.spreadsheetShare"></mp-dialog>
        <mp-dialog :dialog="dialog.googleAnalyticsList"></mp-dialog>
    </v-card>
    `,
    mixins: [crudMixIn],
    props: {
        client: {
            type: Object
        }
    },
    data: function () {
        return {
            url: '/LittleJoe/api/clientsplatforms/',
            search: {
                client_id: this.client.id,
                platform_id: '',
                platform: undefined
            },
            view: {
                title: 'Client Platform'
            },
            tbl: {
                headers: [
                    {text: '플랫폼', value: 'platform', width: 140},
                    {text: '플랫폼 아이디', value: 'platform_id', width: 160},
                    {text: '플랫폼 별칭', value: 'platform_name', width: 160},
                    {text: '추가 아이디1', value: 'option_id1', width: 130},
                    {text: '추가 아이디2', value: 'option_id2', width: 110},
                    {text: '추가 아이디3', value: 'option_id3', width: 110},
                    {text: '상태', value: 'status', width: 80},
                    {text: '수정자', value: 'user_name', width: 100},
                    {text: '수정일시', value: 'updated_at', width: 150, align: 'center'},
                    {text: '액션', value: 'actions', width: 60, sortable: false, align: 'center'},
                ],
                paging: 'client'
            },
            form: {
                init: {
                    client: this.client.id,
                    platform: undefined,
                    platform_id: undefined,
                    platform_source: 'API',
                    platform_name: undefined,
                    option_id1: undefined,
                    option_id2: undefined,
                    option_id3: undefined,
                    option_json: {

                    },
                    status:'ACTIVE',
                    filter: undefined
                },
                label: {
                    option_id1: '추가 아이디1',
                    option_id2: '추가 아이디2',
                    option_id3: '추가 아이디3'
                }
            },
            code: {
                status: mercury.base.code['CODES']['API_StatusShort'],
                platform: mercury.base.code['CODES']['API_Platform'],
                platform_unique: mercury.base.code['CODES']['API_Platform']
            },
            dialog: {
                spreadsheetShare: {
                    title: '스프레드 시트 공유 관리',
                    width: '100%',
                    visible: false,
                    component: MpClientSpreadsheetShare,
                    props: {spreadsheet: {}},
                    close: () => {this.dialog.spreadsheetShare.visible = false;}
                },
                googleAnalyticsList: {
                    title: 'Google Analytics 3 목록',
                    width: '100%',
                    visible: false,
                    component: MpClientGoogleAnalyticsList,
                    props: {},
                    close: () => {this.dialog.googleAnalyticsList.visible = false;}
                }
            },
            upload_item: {},
        }
    },
    mounted() {
        this.fetchData(true);//client paging 일때는 mounted 에서 한번 수행
    },
    methods: {
        getQueryString() {                      //mixIn override
            return this.mercury.base.util.param(this.search);
        },
        setDefaultIfUndefined(obj, key, defaultValue){
            if(obj[key] === undefined){
                obj[key] = defaultValue;
            }
        },
        onCrudMixInActions(actionName, data) {  //mixIn override
            if (actionName === 'beforeFetchData') {
                data.forEach(item=>{
                    item.loading = {DSP: false, CONVERSION: false};
                    item.support = {DSP: true, CONVERSION: true};

                    // gb_ 는 글로벌 설정
                    this.setDefaultIfUndefined(item.option_json, 'gb_auto_update_sync', true)

                    const platform = item.platform;
                    if (platform === 'GA') {
                        this.setDefaultIfUndefined(item.option_json, 'ga_event', false)
                        item.support.DSP = false;
                    }else if(platform === 'SA360'){
                        this.setDefaultIfUndefined(item.option_json, 'ga_integration', false)
                    }else if(platform === 'FACEBOOK'){
                        this.setDefaultIfUndefined(item.option_json, 'fb_sdf', false)
                    }
                });
            }else if (actionName === 'fetchData') {
                //여러개의 동일 플랫폼을 추가 할 수 있어 주석
                //this.removeAlreadySelectedPlatform(data);
            }else if (actionName === 'beforeSaveItem') {
                this.setOverlay(true);
                return true
            }else if (actionName === 'afterSaveItem') {
                this.setOverlay(false);
                return true
            }
        },
        removeAlreadySelectedPlatform(listData) {
            this.code.platform_unique = mercury.base.code['CODES']['API_Platform'].slice();
            listData.forEach(item => {
                const idx = this.code.platform_unique.findIndex(p => {
                    return p.value === item.platform
                });

                if (idx > -1) {
                    this.code.platform_unique.splice(idx, 1)
                }
            });
        },
        platformReadonly(item, edit){
            if (edit){
                return true
            }

            if(item.data.platform_source === 'SPREADSHEET'){
                return true
            }

            return item.data.platform === 'GA';
        },
        changePlatform(platform) {
            this.resetItem();
            this.form.data.platform = platform.value;
            this.form.data.platform_name = platform.text;
            this.form.data.platform_source = platform.etc2;
            if(platform.etc2 === 'SPREADSHEET'){
                this.form.data.platform_id = new Date().getTime()
            }
        },
        onUploadClick(item){
            this.upload_item = item
            this.isSelecting = true
            window.addEventListener('focus', () => {
                this.isSelecting = false
            }, { once: true })

            this.$refs.uploader.click()
        },
        onFileChanged(e) {
            const file = e.target.files[0];
            console.log(this.upload_item)
            const parts = {
                'myfile': file,
                'item': JSON.stringify(this.upload_item)
            }

            const item = this.upload_item
            const syncType = 'DSP'
            item.loading[syncType] = true;
            this.xAjaxMultipart({
                url:'/LittleJoe/common/simple_upload',
                method:'post',
                parts: parts
            }).then(resp=>{
                this.mercury.base.lib.notify(resp);
                if(resp.type === 'success'){
                    if(syncType === 'DSP') {
                        item.dsp_updated_at = moment().format('YYYY-MM-DD HH:mm:ss')
                    } else if(syncType === 'CONVERSION'){
                        item.conversion_updated_at = moment().format('YYYY-MM-DD HH:mm:ss')
                    }
                }
            }).finally(()=>{
                item.loading[syncType] = false;
            })

            // do something
        },
        syncItems(item, syncType) {
            if(syncType === 'CONVERSION'){
                if(item.platform === 'GA'){
                    if(!this.mercury.base.util.hasValue(item.option_id1) || !this.mercury.base.util.hasValue(item.option_id2)){
                        this.mercury.base.lib.notify({message:'GA CONVERSION 동기화를 위해서는 option_id1(web_property_id), option_id2(profile_id) 가 필요합니다.', type:'error'});
                        return;
                    }
                }
            }

            if(item.platform === 'SA360'){
                if(!this.mercury.base.util.hasValue(item.option_id1)){
                    this.mercury.base.lib.notify({message:'SA360 동기화를 위해서는 option_id1(EngineAccountId) 가 필요합니다.', type:'error'});
                    return;
                }
            }

            item.loading[syncType] = true;
            this.xAjaxJson({
                url: this.url + 'sync_items/',
                method: 'POST',
                data: Object.assign({sync_type: syncType, client_id: item.client}, item)
            }).then(resp=>{
                this.mercury.base.lib.notify(resp);
                if(resp.type === 'success'){
                    if(syncType === 'DSP') {
                        item.dsp_updated_at = moment().format('YYYY-MM-DD HH:mm:ss')
                    } else if(syncType === 'CONVERSION'){
                        item.conversion_updated_at = moment().format('YYYY-MM-DD HH:mm:ss')
                    }
                }
            }).finally(()=>{
                item.loading[syncType] = false;
            })
        },
        syncItemsAll(){
            this.mercury.base.lib.confirm(this.client['name']+ '의 등록된 모든 플랫폼을 동기화 하시겠습니까?').then(result => {
                if (result.isConfirmed) {
                    this.setOverlay(true);
                    this.xAjax({
                        url: '/LittleJoe/management',
                        method: 'GET',
                        data: 'job=sync_units_by_client&client_id='+this.client['id']
                    }).then((resp) => {
                        this.mercury.base.lib.notify(resp);
                    }).finally(() => {
                        this.setOverlay(false);
                    });
                }
            });
        },
        isSpreadSheetPlatform(platformSource) {
            return platformSource === 'SPREADSHEET';
        },
        showGoogleAnalyticsList(){
            const dialog = this.dialog.googleAnalyticsList;
            dialog.props.form = this.form;
            dialog.visible = true;
        },
        showSpreadsheetShare(){
            const dialog = this.dialog.spreadsheetShare;
            dialog.title = `[${this.client.name}:${this.form.data.platform_name}] 스프레드시트 공유 관리`;
            dialog.props.spreadsheetId = this.form.data.option_id1;
            dialog.visible = true;
        },
        ...baseHelper.mapActions([
            "setOverlay"
        ])
    }
};

export default MpClientPlatform;