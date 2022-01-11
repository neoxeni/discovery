const baseHelper = Vuex.createNamespacedHelpers('base');

const MpClientGoogleAnalyticsList = {
    name: 'mp-client-google-analytics-list',
    template: `
    <v-card outlined>
        <mp-search>
            <div>
                <v-text-field v-model="search.platform_id" label="GA3 계정 아이디" @keyup.enter="fetchData(true)" outlined dense hide-details></v-text-field>
            </div>
            <div class="search-form-btns">
               <mp-button color="primary" label="조회" icon="mdi-magnify" @click="fetchData(true)"></mp-button>
            </div>
        </mp-search>
        
        <v-data-table dense fixed-header :height="$settings.datatable.rows10" :footer-props="$settings.datatable.footer10" :headers="tbl.headers" :items="tbl.items" >
            <template v-slot:item.profile_name="{ item }">
                <div class="ellipsis"><a href="#" @click.stop.prevent="selectItem(item)" class="ellipsis">{{item['profile_name']}}</a></div>
            </template>
            <template v-slot:item.website_url="{ item }">
                <div class="ellipsis"><div class="ellipsis" :title="item['website_url']">{{item['website_url']}}</div></div>
            </template>
            <template v-slot:item.profile_id="{ item }">
                <div :class="item['profile_id'] === item['default_profile_id'] ? 'success--text' : ''"
                     :title="item['profile_id'] === item['default_profile_id'] ? 'Default Profile Id' : ''"
                >{{item['profile_id']}}</div>
            </template>
            <template v-slot:item.e_commerce_tracking="{ item }">
                  <v-icon :color="item['bot_filtering_enabled'] ? 'green' : 'gray'" title="봇 필터링">mdi-robot</v-icon>  
                  <v-icon :color="item['e_commerce_tracking'] ? 'green' : 'gray'" title="전자상거래">mdi-credit-card</v-icon>
                  <v-icon :color="item['enhanced_e_commerce_tracking'] ? 'green' : 'gray'" title="향상된 전자상거래">mdi-credit-card-multiple</v-icon>
            </template>
            <template v-slot:item.updated="{ item }">
                <span :title="'생성일:'+item['created']">{{item['updated']}}</span>
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
        form: {
            type: Object
        },
        dialog: {
            type: Object
        }
    },
    data: function () {
        return {
            url: '/LittleJoe/api/clientsplatforms/',
            search: {
                platform_id: undefined,
            },
            view: {
                title: 'Client Spreadsheet Share',
                show: false
            },
            tbl: {
                headers: [
                    {text: 'web_property_level', value: 'web_property_level', width: 115},
                    {text: 'web_property_id', value: 'web_property_id', width: 100},
                    {text: 'web_property_name', value: 'web_property_name', width: 90},
                    {text: 'profile_id', value: 'profile_id', width: 90},
                    {text: 'profile_name', value: 'profile_name', width: 180},
                    {text: 'type', value: 'type', width: 60},
                    {text: 'status', value: 'e_commerce_tracking', width: 90, sortable:false},
                    {text: 'website_url', value: 'website_url', width: 150},
                    {text: 'updated', value: 'updated', width: 150},
                ],
                paging: 'client',
                items: [],

            },
            email: undefined
        }
    },
    mounted() {

    },
    methods: {
        fetchData() {
            this.setOverlay(true);
            this.xAjax({
                url: this.url + 'get_ga_profiles/',
                data: {
                    'platform_id': this.search.platform_id
                }
            }).then(resp=>{
                this.tbl.items = resp;
                this.tbl.total = resp.length;
            }).finally(() => {
                this.setOverlay(false);
            });
        },
        selectItem(item) {
            console.log(item)
            this.form.data.option_id1 = item['web_property_id']
            this.form.data.option_id2 = item['profile_id']
            this.form.data.option_id3 = item['account_id']
            this.form.data.platform_id = item['profile_id']+'_'+this.form.data['client']
            if (this.form.data.platform_name === '' || this.form.data.platform_name === undefined){
                this.form.data.platform_name = item['profile_name']
            }

            if(this.dialog){
                this.dialog.close() // mp-dialog 로 쓰인경우 닫기 위해
            }
        },
        ...baseHelper.mapActions([
            "setOverlay"
        ])
    }
};

export default MpClientGoogleAnalyticsList;