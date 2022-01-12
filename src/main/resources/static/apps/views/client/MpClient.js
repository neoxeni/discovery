import crudMixIn from "/static/apps/mixin/crudMixIn.js";
import MpClientPlatform from "/static/apps/components/platform/MpClientPlatform.js"

const MpClient = {
    name: 'mp-client',
    mixins: [crudMixIn],
    template: `
    <v-card outlined>
        <mp-search>
            <div>
                <v-text-field v-model="search.name" label="이름" @keyup.enter="fetchData(true)" outlined dense hide-details></v-text-field>
            </div>
            <div class="w-150px">
                <v-select label="상태" v-model="search.status" @change="fetchData(true)" :items="mercury.base.util.concatArray(code.status,[{text:'전체',value:''}], false)" outlined dense hide-details></v-select>
            </div>
            <div class="search-form-btns">
                <mp-button color="primary" label="조회" icon="mdi-magnify" @click="fetchData(true)"></mp-button>
                <mp-button color="info" label="새로입력" icon="mdi-plus" @click="newItem()"></mp-button>
            </div>
        </mp-search>

        <v-data-table dense fixed-header :height="$settings.datatable.rows10" :footer-props="$settings.datatable.footer10" :headers="tbl.headers" :items="tbl.items" :options.sync="tbl.options" :server-items-length="tbl.total" >
            <template v-slot:item.name="{ item }">
                <div class="ellipsis"><a href="#" @click.stop.prevent="selectItem(item)" class="ellipsis" :title="item.name">{{item.name}}</a></div>
            </template>
            <template v-slot:item.eng_name="{ item }">
                <div class="ellipsis"><div class="ellipsis" :title="item.eng_name">{{item.eng_name}}</div></div>
            </template>
            <template v-slot:item.status="{ item }">
                <v-chip :color="item.status === 'ACTIVE' ? 'green' : 'gray'" small>{{item['status_label']}}</v-chip>
            </template>
            <template v-slot:item.actions="{ item }">
                <mp-button mode="icon" color="warning" label="플랫폼" icon="mdi-sitemap" @click="showClientPlatform(item)"></mp-button>
            </template>
        </v-data-table>

        <mp-view :view.sync="view">
            <template v-slot:body="{edit}">
                <v-form class="input-form" ref="form" v-model="form.valid">
                    <v-row>
                        <v-col cols="12" md="3">
                            <v-text-field label="한글 이름" v-model="form.data.name" :rules="mercury.base.rule.required"></v-text-field>
                        </v-col>
                        <v-col cols="12" md="3">
                            <v-text-field label="영문 이름" :value="form.data.eng_name" @input="changeValidName" :rules="rules.alpha_" placement="대문자_구분"></v-text-field>
                        </v-col>
                        <v-col cols="12" md="3">
                            <v-select label="업종" v-model="form.data.industry_code" :items="code.industry_code" :rules="mercury.base.rule.required"></v-select>
                        </v-col>
                        <v-col cols="12" md="3">
                            <v-select label="상태" v-model="form.data.status" :items="code.status"></v-select>
                        </v-col>
                        
                        
                        <v-col cols="12" md="12">
                            <v-textarea label="비고" v-model="form.data.desc" auto-grow clearable rows="2" row-height="30" :rules="mercury.base.rule.required"></v-textarea>
                        </v-col>
                    </v-row>
                </v-form>
            </template>
            <template v-slot:control="{edit}">
                <template v-if="edit">
                    <mp-button mode="text" color="warning" label="저장" icon="mdi-file-document-edit" @click="saveItem('PUT')"></mp-button>
<!--                    <mp-button mode="text" color="danger" label="삭제" icon="mdi-delete" @click="deleteItem(form.data)"></mp-button>-->
                </template>
                <template v-else>
                    <mp-button mode="text" color="warning" label="저장" icon="mdi-text-box-plus" @click="saveItem('POST')"></mp-button>
                    <mp-button mode="text" color="secondary" label="초기화" icon="mdi-refresh" @click="resetItem()"></mp-button>
                </template>
            </template>
        </mp-view>
    
        <mp-dialog :dialog="dialog.platform"></mp-dialog>
    </v-card>
    `,
    data: function () {
        return {
            url: '/LittleJoe/api/clients/',
            search: {
                name: '',
                status: undefined
            },
            view: {
                title: 'Client'
            },
            tbl: {
                headers: [
                    {text: 'ID', value: 'id', width: 50, align: 'right'},
                    {text: '이름', value: 'name', width: 140},
                    {text: '영문이름', value: 'eng_name', width: 140},
                    {text: '설명', value: 'desc'},
                    {text: '업종', value: 'industry_code_label', width: 80},
                    {text: '상태', value: 'status', width: 80},
                    {text: '액션', value: 'actions', width: 80, sortable: false},
                    {text: '수정자', value: 'user_name', width: 120, align: 'center'},
                    {text: '수정일시', value: 'updated_at', width: 160, align: 'center'}
                ]
            },
            form: {
                init: {
                    name: undefined,
                    eng_name: undefined,
                    status: 'ACTIVE',
                    desc: undefined,
                    industry_code: undefined,
                }
            },
            rules: {
                alpha_:[
                    v => (v && String(v).trim().length > 0) || '필수 입력 항목입니다',
                    v => /^[a-zA-Z0-9_]*$/.test(v) || '영문, 숫자, _ 만 가능합니다.'
                ]
            },
            code: {
                status: mercury.base.code['CODES']['API_StatusShort'],
                industry_code: mercury.base.code['CODES']['API_IndustryCode']
            },
            dialog: {
                platform: {
                    title: '플랫폼 관리',
                    width: '100%',
                    visible: false,
                    component: MpClientPlatform,
                    props: {client: {}},
                    close: () => {this.dialog.platform.visible = false;}
                }
            }
        }
    },
    methods: {
        showClientPlatform(item) {
            const dialog = this.dialog.platform;
            dialog.title = item.name + ' 플랫폼 관리';
            dialog.props.client = item;
            dialog.visible = true;
        },
        changeValidName(val){
            this.$nextTick(()=>{
                val = val.replace(/[^a-zA-Z0-9_]+/g, "").replace(/\s+/g, "_");
                this.form.data.eng_name = val.toUpperCase()
            })
        }
    }
};

export default MpClient;