import crudMixIn from "/static/vue/mixin/crudMixIn.js";
const baseHelper = Vuex.createNamespacedHelpers('base');
const MpReportGroupAuth = {
    name: 'mp-report-group-auth',
    mixins: [crudMixIn],
    template: `
    <v-card outlined>
        <mp-search>
            <div>
                <v-text-field v-model="search.username" label="사용자 아이디" @keyup.enter="fetchData(true)" outlined dense hide-details></v-text-field>
            </div>
            <div class="search-form-btns">
                <mp-button color="primary" label="조회" icon="mdi-magnify" @click="fetchData(true)"></mp-button>
                <mp-button color="info" label="새로입력" icon="mdi-plus" @click="newItem()"></mp-button>
            </div>
        </mp-search>

        <v-data-table dense fixed-header :height="$settings.datatable.rows10" :footer-props="$settings.datatable.footer10" :headers="tbl.headers" :items="tbl.items">
            <template v-slot:item.username="{ item }">
                <div class="ellipsis"><a href="#" @click.stop.prevent="selectItem(item)" class="ellipsis">{{item.username}}</a></div>
            </template>
            <template v-slot:item.is_edit="{ item }">
                <mp-boolean-yn mode="boolean" :value="String(item.is_edit)"></mp-boolean-yn>
            </template>
            <template v-slot:item.is_email="{ item }">
                <mp-boolean-yn mode="boolean" :value="String(item.is_email)"></mp-boolean-yn>
            </template>
            <template v-slot:item.is_active="{ item }">
                <v-chip color="green" v-if="item.is_active === true" small>활성</v-chip>
                <v-chip color="gray" v-else small>비활성</v-chip>
            </template>
        </v-data-table>

        <mp-view :view.sync="view">
            <template v-slot:body="{edit}">
                <v-form class="input-form" ref="form" v-model="form.valid">
                    <v-row>
                        <template v-if="edit">
                            <v-col cols="12" md="6">
                                <v-text-field label="사용자아이디" v-model="form.data.username" :readonly="edit"></v-text-field>
                            </v-col>  
                            <v-col cols="12" md="6">
                                <v-text-field label="이메일" v-model="form.data.email" :readonly="edit"></v-text-field>
                            </v-col>  
                        </template>
                        <template v-else>
                            <v-col cols="12" md="12">
                                <v-autocomplete
                                    v-model="form.data.user"
                                    :items="notIncludedUsers"
                                    label="사용자"
                                    item-text="username"
                                    item-value="id"
                                    hide-detail
                                >
                                    <template v-slot:selection="data">
                                        <v-chip small>{{ data.item.username }}</v-chip>
                                    </template>
                                    <template v-slot:item="data">
                                        <template v-if="typeof data.item !== 'object'">
                                            <v-list-item-content v-text="data.item"></v-list-item-content>
                                        </template>
                                        <template v-else>
                                            <v-list-item-avatar>
                                                
                                            </v-list-item-avatar>
                                            <v-list-item-content>
                                                <v-list-item-title v-html="data.item.username"></v-list-item-title>
                                                <v-list-item-subtitle v-html="data.item.email"></v-list-item-subtitle>
                                            </v-list-item-content>
                                        </template>
                                    </template>
                                </v-autocomplete>
                            </v-col>
                        </template>
                        
                        <v-col cols="12" md="6">
                            <v-select label="수정" v-model="form.data.is_edit" :items="[{text:'예',value:true},{text:'아니오',value:false}]"></v-select>
                        </v-col>
                        <v-col cols="12" md="6">
                            <v-select label="이메일수신" v-model="form.data.is_email" :items="[{text:'예',value:true},{text:'아니오',value:false}]"></v-select>
                        </v-col>    
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
    </v-card>
    `,

    props: {
        report: {
            type: Object
        }
    },
    computed: {
        ...baseHelper.mapGetters([
            'getUsers',
        ]),
        notIncludedUsers(){
            return this.getUsers.filter(user=>{
                return true
            })
        }
    },

    data: function () {
        return {
            url: '/LittleJoe/api/reportgroupauths/',
            search: {
                report_group_id: this.report.id,
                username: undefined
            },
            view: {
                mode: 'dialog',
                title: 'Client Platform'
            },
            tbl: {
                headers: [
                    {text: 'username', value: 'username', width: 120},
                    {text: 'email', value: 'email', width: 120},
                    {text: '수정', value: 'is_edit', width: 60},
                    {text: '이메일 수신', value: 'is_email', width: 60},
                    {text: '유저상태', value: 'is_active', width: 60}
                ],
                paging: 'client'
            },
            form: {
                init: {
                    report_group: this.report.id,
                    user: undefined,
                    is_view: true,  //기본 true
                    is_edit: false,
                    is_down: false,
                    is_email: false,
                }
            },
            code: {
                platform: mercury.base.code['CODES']['API_Platform'],
                platform_unique: mercury.base.code['CODES']['API_Platform']
            }
        }
    },
    mounted() {
        this.fetchData(true);//client paging 일때는 mounted 에서 한번 수행
    },
    methods: {
        getQueryString() {                      //mixIn override
            return this.mercury.base.util.param(this.search);
        },
        onCrudMixInActions(actionName, data) {  //mixIn override

        },

    }
};

export default MpReportGroupAuth;