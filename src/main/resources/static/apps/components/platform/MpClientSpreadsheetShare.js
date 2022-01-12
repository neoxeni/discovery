const baseHelper = Vuex.createNamespacedHelpers('base');


const MpClientSpreadsheetShare = {
    name: 'mp-client-spreadsheet-share',
    template: `
    <v-card outlined>
        <mp-search>
            <div>
                <v-text-field v-model="search.email" label="이메일" @keyup.enter="fetchData(true)" outlined dense hide-details></v-text-field>
            </div>
            <div class="search-form-btns">
               <mp-button color="primary" label="조회" icon="mdi-magnify" @click="fetchData(true)"></mp-button>
               <mp-button color="info" label="추가" icon="mdi-plus" @click="newItem()"></mp-button>
            </div>
        </mp-search>
        
        <v-data-table dense fixed-header :height="$settings.datatable.rows6" :footer-props="$settings.datatable.footer6" :headers="tbl.headers" :items="tbl.items" >
            <template v-slot:item.actions="{ item }">
                <mp-button mode="icon" label="권한 삭제" icon="mdi-delete" color="red" @click="deleteItem(item)"></mp-button>
            </template>
        </v-data-table>
      
        <mp-view :view.sync="view">
            <template v-slot:body="{edit}">
                <v-row>
                    <v-col cols="12" md="12">
                        <v-text-field label="이메일" v-model="email" :rules="mercury.base.rule.required"></v-text-field>
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
    mixins: [],
    props: {
        spreadsheetId: {
            type: String
        }
    },
    data: function () {
        return {
            url: '/LittleJoe/api/clientsplatforms/',
            search: {
                email: undefined,
            },
            view: {
                title: 'Client Spreadsheet Share',
                show: false
            },
            tbl: {
                headers: [
                    {text: 'ID', value: 'id', width: 140},
                    {text: '이메일', value: 'email', width: 180},
                    {text: '사용자명', value: 'name', width: 90},
                    {text: '액션', value: 'actions', width: 60, sortable: false, align: 'center'},
                ],
                paging: 'client',
                items: [],

            },
            email: undefined
        }
    },
    mounted() {
        this.fetchData();
    },
    methods: {
        fetchData() {
            this.setOverlay(true);
            this.xAjax({
                url: this.url + 'get_shares/',
                data: {
                    'file_id': this.spreadsheetId
                }
            }).then(resp=>{
                this.tbl.items = resp;
                this.tbl.total = resp.length;
            }).finally(() => {
                this.setOverlay(false);
            });
        },
        newItem(){
            this.view.edit = false;
            this.view.show = true;
        },
        deleteItem(item) {
            this.mercury.base.lib.confirm("정말로 권한을 해지 하시겠습니까?").then(result => {
                if (result.isConfirmed) {
                    this.setOverlay(true);
                    this.xAjax({
                        url: this.url + 'delete_permission/',
                        method: 'DELETE',
                        data: {
                            'permission_id': item.id,
                            'file_id': this.spreadsheetId
                        }
                    }).then(resp => {
                        this.setOverlay(false);
                        this.mercury.base.lib.notify(resp);
                        this.fetchData();
                    }).catch(() => {
                        this.setOverlay(false);
                    });
                }
            });
        },
        saveItem(){
            if(!this.mercury.base.util.hasValue(this.email)){
                this.mercury.base.lib.notify({message:'email을 입력해 주세요.', type:'error'});
                return;
            }

            this.setOverlay(true);
            this.xAjax({
                url: this.url + 'add_permission/',
                method: 'POST',
                data: {
                    'email': this.email,
                    'file_id': this.spreadsheetId
                }
            }).then((resp) => {
                this.setOverlay(false);
                this.mercury.base.lib.notify(resp);
                this.fetchData();
            }).catch(() => {
                this.setOverlay(false);
            });
        },
        ...baseHelper.mapActions([
            "setOverlay"
        ])
    }
};

export default MpClientSpreadsheetShare;