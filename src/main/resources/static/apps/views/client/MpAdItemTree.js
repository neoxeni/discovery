const baseHelper = Vuex.createNamespacedHelpers('base');
import MpAdvertiserSelect from "/static/apps/components/ui/MpAdvertiserSelect.js";
import MpAdItemDetail from "/static/apps/components/ui/MpAdItemDetail.js";
const MpAdItemTree = {
    name: 'mp-ad-item-tree',
    components: {
        MpAdvertiserSelect,
        MpAdItemDetail
    },
    template: `
        <v-row>
            <v-col cols="12" md="6">
                <v-card>
                    <v-card-title class="title-with-search">
                        <mp-search>
                            <div class="w-200px">
                                <mp-advertiser-select label="광고주" v-model="search.client_id" @change="changeClient" outlined dense hide-details></mp-advertiser-select>
                            </div>
                            <div class="w-190px">
                                <v-select label="플랫폼" v-model="search.platform_id" @change="changePlatform" :items="platforms" return-object item-value="platform_id" item-text="platform_name" outlined dense hide-details >
                                    <template v-slot:item="data">
                                        <v-chip class="mr-2" x-small label>{{ data.item.platform }}</v-chip> {{data.item.platform_name}}
                                    </template>
                                </v-select>
                            </div>
                            <div>
                                <v-text-field label="검색" v-model="search.keyword" outlined dense hide-details>
                                    <template v-slot:append>
                                        <v-menu bottom left>
                                            <template v-slot:activator="{ on, attrs }">
                                                <v-btn icon small color="grey" v-bind="attrs" v-on="on">
                                                    <v-icon>mdi-dots-vertical</v-icon>
                                                </v-btn>
                                            </template>
                                            <v-list>
                                                <v-list-item link @click="expandAll">
                                                    <v-list-item-title>
                                                        <v-icon>mdi-arrow-expand-all</v-icon> Expand
                                                    </v-list-item-title>
                                                </v-list-item>
                                                <v-list-item link @click="collapseAll">
                                                    <v-list-item-title>
                                                        <v-icon>mdi-arrow-collapse-all</v-icon> Collapse
                                                    </v-list-item-title>
                                                </v-list-item>
                                            </v-list>
                                        </v-menu>
                                    </template>
                                </v-text-field>
                            </div>
                        </mp-search>
                    </v-card-title>
                    <v-card-text class="div-scroll-y">
                        <v-treeview
                            dense
                            hoverable
                            :active.sync="active"
                            :items="units"
                            :search="search.keyword"
                            :filter="filter"
                            :open.sync="open"
                            item-key="uuid"
                            item-text="text"
                            activatable
                            color="warning"
                            transition
                            return-object
                            ref="treeview"
                        >
                            <template v-slot:prepend="{ item }">
                                <v-chip x-small chip :color="getType(item).color" :title="item.type">{{getType(item).name}}</v-chip>
                                <v-icon :color="getStatus(item).color">{{getStatus(item).icon}}</v-icon>
                                <v-chip x-small chip>{{item.value}}</v-chip>
                            </template>
                        </v-treeview>
                    </v-card-text>
                </v-card>
            </v-col>
            <v-col cols="12" md="6">
                <v-card>
                    <v-card-title v-if="!selected">
                        Select a AD ITEM
                    </v-card-title>
                    <template v-else>
                        <v-card-title style="padding: 11px">
                            <v-chip small chip :color="getType(selected).color" class="mr-2">{{getType(selected).name}}</v-chip> {{selected.text}}
                        </v-card-title>
                        <v-card-text class="div-scroll-y" v-show="selected">
                            <mp-ad-item-detail :item="selected"></mp-ad-item-detail>
                        </v-card-text>
                    </template>
                </v-card>
            </v-col>
        </v-row>
    `,
    data: function() {
        return {
            paths: '',
            search: {
                data_format: 'TREE',
                client_id: undefined,
                platform: undefined,
                platform_id: undefined,
                keyword: ''
            },
            form: {
                type: 'code',
                data: {},
                valid: false,
            },
            platforms: [],
            units: [],
            active: [],
            open: [],
        };
    },
    computed: {
        selected() {
            if (!this.active.length) return undefined
            this.form.data = Object.assign({}, this.active[0])
            return this.active[0];
        },
        filter () {
            return (item, search, textKey) => {
                return item[textKey].toUpperCase().indexOf(search.toUpperCase()) > -1 || item['value'].indexOf(search) > -1
            }
        }
    },
    methods: {
        changeClient() {
            this.units = []
            this.search.platform = undefined
            this.search.platform_id = undefined
            this.platforms = []
            this.setOverlay(true)
            this.xAjax({
                url: '/LittleJoe/api/clientsplatforms/?client_id='+this.search.client_id
            }).then(resp=>{
                /*resp.forEach(item=>{
                    if(item['platform'] !== 'GA' && item['platform'] !== 'CM'){
                        this.platforms.push(item)
                    }
                })*/
                this.platforms.push(...resp);
            }).finally(() => {
                this.setOverlay(false);
            });
        },
        changePlatform(targetPlatform) {
            this.search.platform = targetPlatform['platform']
            this.search.platform_id = targetPlatform['platform_id']
            this.units = []
            this.setOverlay(true)
            this.xAjax({
                url: '/LittleJoe/api/clientsplatforms/units/',
                data: this.search
            }).then(resp=>{
                this.units.push(...resp)
            }).finally(() => {
                this.setOverlay(false);
            });
        },
        getType(item) {
            return this.mercury.base.skylab.getAdItemUnitMeta(item)
        },
        getStatus(item) {
            return this.mercury.base.skylab.getAdItemStatusMeta(item)
        },
        expandAll() {
            this.$refs.treeview.updateAll(true);
        },
        collapseAll() {
            this.$refs.treeview.updateAll(false);
        },
        ...baseHelper.mapActions([
            "setOverlay"
        ]),
    }
};
export default MpAdItemTree;