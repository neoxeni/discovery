const baseHelper = Vuex.createNamespacedHelpers('base');
import MpAdvertiserSelect from "/static/vue/components/ui/MpAdvertiserSelect.js";
import MpAdItemDetail from "/static/vue/components/ui/MpAdItemDetail.js";

const MpAdItemTable = {
    name: 'mp-ad-item-table',
    components: {
        MpAdvertiserSelect
    },
    template: `
        <v-card outlined>
            <mp-search>
                <div class="w-200px">
                    <mp-advertiser-select label="광고주" v-model="search.client_id" @change="changeClient" outlined dense hide-details></mp-advertiser-select>
                </div>
                <div class="w-190px">
                    <v-select label="플랫폼" @change="changePlatform" :items="platforms" return-object :item-value="item => item.platform +' - '+ item.platform_id" item-text="platform_name" outlined dense hide-details >
                        <template v-slot:item="data">
                            <v-chip class="mr-2" x-small label>{{ data.item.platform }}</v-chip> {{data.item.platform_name}}
                        </template>
                    </v-select>
                </div>
                <div class="w-100px">
                    <v-select label="상태" v-model="search.status" @change="setTabData" :items="mercury.base.util.concatArray(code.adItemStatus,[{text:'전체',value:''}], false)" outlined dense hide-details></v-select>
                </div>
              
                <div>
                    <v-text-field label="검색" placeholder="Item Name Or Item Id" v-model="search.keyword" clearable @click:clear="clearKeyword()" @keyup.enter="setTabData" outlined dense hide-details></v-text-field>
                </div>
            </mp-search>
          
            <v-tabs v-model="tabIndex" grow @change="changeLevel">
                <v-tab v-for="tab in tabs" :key="tab.type">
                    <v-chip :color="getType(tab)['color']" small>{{tab['type']}}</v-chip>
                    <v-spacer></v-spacer>
                    <v-chip color="danger" small close @click:close="filterClose(tab)" :style="{visibility:  tab.selected.length > 0 ? 'visible':'hidden'}">{{tab.selected.length}} Selected</v-chip>
                </v-tab>
            </v-tabs>

            <v-tabs-items v-model="tabIndex">
                <v-tab-item v-for="tab in tabs" :key="tab.type">
                    <v-data-table dense fixed-header :show-select="tab.useShow" item-key="uuid"  v-model="tab.selected"
                                  :height="$settings.datatable.rows20" :footer-props="$settings.datatable.footer20" :headers="info.tableHeader" :items="tab.items">
                        <template v-slot:item.text="{ item }">
                            <div class="ellipsis">
                                <a href="#" @click.stop.prevent="selectItem(item)" class="ellipsis" :title="item.text">{{item.text}}</a>
                                <div class="ellipsis grey--text" :title="item.type+'_'+item.value">{{item.value}}</div>
                            </div>
                        </template>
                        <template v-slot:item.status="{ item }">
                            <v-chip :color="item['status'] === 'ACTIVE' ? 'green' : 'gray'" small title="status">{{item['status']}}</v-chip><br/>
                            <v-chip color="gray" small title="effective_status">{{item['effective_status']}}</v-chip>
                        </template>
                        <template v-slot:item.start_date="{ item }">
                            {{item['start_date'] !== null ? item['start_date'] : 'UNSET'}}<br/>
                            {{item['end_date'] !== null ? item['end_date'] : 'UNSET'}}
                        </template>
                    </v-data-table>
                </v-tab-item>
            </v-tabs-items>
            <mp-dialog :dialog="dialog.detail"></mp-dialog>
        </v-card>
    `,
    data: function () {
        return {
            search: {
                data_format: 'LIST',
                type: undefined,
                client_id: undefined,
                platform: undefined,
                platform_id: undefined,
                status: '',
                keyword: ''
            },
            form: {
                type: 'code',
                data: {},
                valid: false,
            },
            platforms: [],
            units: [],
            tabs: [],
            tabIndex: 0,
            info: {
                tableHeader: [
                    {text: '이름', value: 'text', width: 300},
                    {text: '상태', value: 'status', width: 120},
                    {text: '시작/종료', value: 'start_date', width: 80},
                    {text: 'cm_placement_id', value: 'cm_placement_id', width: 100},
                    {text: 'utm_source', value: 'utm_source', width: 100},
                    {text: 'utm_medium', value: 'utm_medium', width: 100},
                    {text: 'utm_content', value: 'utm_content', width: 100},
                    {text: 'utm_term', value: 'utm_term', width: 100},
                    {text: 'utm_campaign', value: 'utm_campaign', width: 100},
                    {text: 'utm_id', value: 'utm_id', width: 100},
                ],
                unitLevel: this.mercury.base.skylab.getUnitLevelWithKeyword()
            },
            code: {
                adItemStatus: mercury.base.code['CODES']['API_AdItemStatus'],
            },
            dialog: {
                detail: {
                    title: '플랫폼 관리',
                    width: '100%',
                    visible: false,
                    component: MpAdItemDetail,
                    props: {},
                    close: () => {
                        this.dialog.detail.visible = false;
                    }
                }
            }
        };
    },
    methods: {
        selectItem(item) {
            const dialog = this.dialog.detail;
            dialog.title = item.text;
            dialog.props.item = item;
            dialog.visible = true;
        },
        changeClient() {
            this.units = []
            this.search.platform = undefined
            this.search.platform_id = undefined
            this.platforms = []
            this.tabs.splice(0)
            this.setOverlay(true)
            this.xAjax({
                url: '/LittleJoe/api/clientsplatforms/?client_id=' + this.search.client_id
            }).then(resp => {
                resp.forEach(item => {
                    if (item['platform'] === 'DV360') {
                        const copyDisplay = this.mercury.base.util.deepCopy(item);
                        copyDisplay['platform'] = 'DV360_DISPLAY';
                        copyDisplay['platform_name'] = copyDisplay['platform_name'] + '_DISPLAY';
                        this.platforms.push(copyDisplay);

                        const copyVideo = this.mercury.base.util.deepCopy(item);
                        copyVideo['platform'] = 'DV360_VIDEO';
                        copyVideo['platform_name'] = copyVideo['platform_name'] + '_VIDEO';
                        this.platforms.push(copyVideo)
                    } else {
                        if (item['platform'] !== 'GA') {
                            this.platforms.push(item)
                        }
                    }
                });
            }).finally(() => {
                this.setOverlay(false);
            });
        },
        changePlatform(targetPlatform) {
            this.units = []
            this.search.platform = targetPlatform['platform']
            this.search.platform_id = targetPlatform['platform_id']
            const platformLevels = this.info.unitLevel[targetPlatform['platform']]
            const tabs = []
            platformLevels.forEach(item => {
                let useShow = true;
                if (item === 'AD' || item === 'CREATIVE' || item === 'KEYWORD') {
                    useShow = false;
                }

                if (targetPlatform['platform'] === 'CM' && item === 'CREATIVE') {
                    return  // CM은 CREATIVE 데이터를 가져오지 않았음
                }

                tabs.push({
                    type: item,
                    items: [],
                    useShow: useShow,
                    selected: [],
                })
            })

            this.tabIndex = 0;
            this.tabs.splice(0)
            this.tabs.push(...tabs)
            this.search.type = platformLevels[this.tabIndex]
            this.$nextTick(() => {
                this.setOverlay(true);
                this.xAjax({
                    url: '/LittleJoe/api/clientsplatforms/units/',
                    data: this.search
                }).then(resp => {
                    this.units.push(...resp)
                    this.setTabData()
                }).finally(() => {
                    this.setOverlay(false);
                });
            });
        },
        changeLevel(tabIndex) {
            let tab = this.tabs[tabIndex]
            if (tab === undefined) {
                this.tabIndex = 0
                tab = this.tabs[0];
            }
            this.search.type = tab ? tab.type : 'CAMPAIGN'
            this.setTabData();
        },
        setTabData() {
            const tabIndex = this.tabIndex;
            const tab = this.tabs[tabIndex];
            if (tab === undefined) {
                return;
            }

            const unitLevels = this.info.unitLevel[this.search.platform]
            const targetType = unitLevels[tabIndex]
            const filterMap = {}
            for (let i = 0, ic = tabIndex; i < ic; i++) {
                const filterSelected = this.tabs[i].selected;
                if (filterSelected.length > 0) {
                    filterMap[unitLevels[i]] = []
                    filterSelected.forEach(fs => {
                        filterMap[unitLevels[i]].push(fs.value)
                    });
                }
            }

            const checkStatus = this.mercury.base.util.hasText(this.search.status)
            const checkKeyword = this.mercury.base.util.hasText(this.search.keyword)
            const checkFilter = Object.keys(filterMap).length > 0 && Object.getPrototypeOf(filterMap) === Object.prototype
            let checkSubtype = false
            let subType = undefined
            if( this.search.platform === 'DV360_VIDEO' || this.search.platform === 'DV360_DISPLAY'){
                if(targetType === 'INSERTION_ORDER' || targetType === 'LINE_ITEM'){
                    checkSubtype = true
                    subType = this.search.platform.split('_')[1]
                    if(targetType === 'LINE_ITEM' && subType === 'VIDEO'){
                        subType = 'TRUEVIEW'
                    }
                }
            }

            let items = this.units.filter(item => {
                if (item.type !== targetType) {
                    return false
                }

                if (checkStatus) {
                    if (this.search.status !== item.status) {
                        return false
                    }
                }

                if (checkSubtype) {
                    if(item.sub_type !== subType){
                        console.log(item.sub_type, subType)
                        return false
                    }
                }

                if (checkKeyword) {
                    const keyword = this.search.keyword.trim();
                    const containText = item.text.includes(keyword)
                    const containValue = item.value.includes(keyword)
                    if (!containText && !containValue) {
                        return false
                    }
                }

                if (checkFilter && tabIndex > 0) {
                    for (let unitType in filterMap) {
                        const values = filterMap[unitType]
                        let isInclude = false
                        values.forEach(value => {
                            if (item[unitType + '_ID'] === value) {
                                isInclude = true
                            }
                        })

                        if (!isInclude) {
                            return false
                        }
                    }
                }

                return true;
            });

            tab.items.splice(0)
            tab.items.push(...items)
        },
        getType(item) {
            return this.mercury.base.skylab.getAdItemUnitMeta(item)
        },
        getStatus(item) {
            return this.mercury.base.skylab.getAdItemStatusMeta(item)
        },
        filterClose(tab) {
            tab.selected.splice(0)
            this.setTabData();
        },
        clearKeyword() {
            this.search.keyword = '';
            this.setTabData();
        },
        ...baseHelper.mapActions([
            "setOverlay"
        ]),
    }
};
export default MpAdItemTable;