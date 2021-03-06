export default {
    name: 'mp-base-group-members',
    template: `
        <div>
            <mp-search>
                <div>
                    <v-select label="구분" v-model="tbl.search.target" dense hide-details="auto" class="mr-2" :items="[{text:'전체',value:''},{text:'직원',value:'E'},{text:'부서',value:'D'}]" outlined dense hide-detail></v-select>
                </div>
                <div>
                    <v-text-field label="이름" v-model="tbl.search.dataNm" @keyup.enter="fetchData(true)" outlined dense hide-details></v-text-field>
                </div>
                <div class="search-form-btns">
                    <mp-button color="primary" label="조회" icon="mdi-magnify" @click="fetchData(true)"></mp-button>
                    <mp-button color="green" label="엑셀" icon="mdi-file-excel" @click="excelData()"></mp-button>
                    <mp-button color="danger" label="삭제" icon="mdi-trash-can" @click="deleteItem()"></mp-button>
                    <mp-button color="warning" label="새로입력" icon="mdi-plus" @click="newItem()"></mp-button>
                </div>
            </mp-search>
            <v-data-table dense fixed-header item-key="mapNo" :height="$settings.datatable.rows5" :footer-props="$settings.datatable.footer5" :headers="tbl.headers" :items="filteredItems" :items-per-page="5" show-select v-model="tbl.selectedItems">
        
                <template v-slot:item.target="{ item }">
                    <span v-if="item.target === 'D'"><i class="mdi mdi-microsoft-teams primary--text"></i></span>
                    <span v-else-if="item.target === 'E'"><i class="mdi mdi-account secondary--text"></i></span>
                    {{item.tooltip}}
                </template>
            </v-data-table>
        
        
            <mp-view :view.sync="view">
                <template v-slot:body="{edit}">
                    <mp-base-org-tree ref="group-mapping-tree" :selected.sync="mappings" label="추가 구성원" department multiple disabled-filed="none"></mp-base-org-tree>
                </template>
                <template v-slot:control="{edit}">
                    <v-btn color="green" text @click="saveItem()" elevation="2" outlined raised>Save</v-btn>
                </template>
            </mp-view>
        </div>
    `,
    props: {
        groupId: Number
    },
    data: function() {
        return {
            tbl: {
                headers: [
                    {text: '구분', value: 'target'},
                    {text: '이름', value: 'dataNm'},
                    {text: '사용여부',value: 'useYn',align: 'center',width: '90px'}
                ],
                items: [],
                total: 0,
                options: {},
                item: {},
                selectedItems: [],
                search: {
                    target: '',
                    dataNm: ''
                }
            },
            view: {
                mode: 'dialog',
                title: '그룹 구성원',
                show: false,
                edit: false
            },
            mappings: []
        };
    },
    computed: {
        filteredItems: function() {
            return this.tbl.items.filter(item => {
                let match = true;
                if (this.tbl.search.target !== '') {
                    match = item.target === this.tbl.search.target;
                }
                if (this.tbl.search.dataNm !== '') {
                    match = item.dataNm.indexOf(this.tbl.search.dataNm) > -1;
                }
                return match;
            });
        }
    },
    watch: {
        'tbl.options': {
            handler: 'fetchData',
            deep: true
        },
        'groupId': {
            handler: function() {
                this.fetchData(true);
            }
        }
    },
    mounted() {
        this.fetchData(true);
    },
    methods: {
        fetchData(isSearchFirst) {
            const groupId = this.groupId;
            this.tbl.search.target = '';
            this.tbl.search.dataNm = '';
            if (groupId === undefined) {
                this.tbl.items = [];
                this.tbl.total = 0;
                return;
            }
            let query = mercury.base.vue.dataTablesParam(this.tbl.options, {
                groupId: groupId
            });
            xAjax({
                url: '/base/groups/mappings',
                method: 'GET',
                data: query
            }).then(response => {
                this.tbl.items = response.data;
                this.tbl.total = response.recordsTotal;
            });
        },
        newItem() {
            this.view.show = true;
            this.view.edit = false;
            this.resetItem();
        },
        excelData() {
            const param = {
                target: this.tbl.search.target,
                dataNm: this.tbl.search.dataNm,
                groupId: this.groupId
            }


            let query = mercury.base.vue.dataTablesParam(this.tbl.options, param);
            mercury.base.lib.download({
                url: '/base/groups/mappings/excel',
                method: 'GET',
                data: query
            }).then(response => {
                console.log(response);
            });

        },
        deleteItem() {
            if (this.tbl.selectedItems.length === 0) {
                mercury.base.lib.notify('삭제할 항목을 선택해주세요.', {}, {
                    type: 'warning'
                });
                return;
            }
            xAjax({
                url: '/base/groups/mappings',
                method: 'DELETE',
                contentType: 'application/json',
                data: JSON.stringify(this.tbl.selectedItems)
            }).then(resp => {
                mercury.base.lib.notify(resp.message);
                this.fetchData(true);
            });
        },
        saveItem() {
            const mappings = [];
            const existMap = {};
            this.tbl.items.forEach(item => {
                existMap[item.target + '_' + item.targetId] = item;
            });
            let sortNo = 1;
            this.mappings.forEach((mapping, index) => {
                if (existMap[mapping.type + '_' + mapping.no] === undefined) {
                    mappings.push({
                        groupId: this.groupId,
                        target: mapping.type,
                        targetId: mapping.no,
                        useYn: 'Y',
                        sortNo: sortNo++
                    });
                }
            });
            xAjax({
                url: '/base/groups/mappings',
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(mappings)
            }).then(resp => {
                mercury.base.lib.notify(resp.message);
                this.fetchData(true);
                this.view.show = false;
            });
        },
        resetItem() {
            //입력폼 초기화
            this.mappings = [];
        }
    }
}