export default {
    name: 'mp-base-group-select',
    template: `
        <div>
            <v-select label="그룹" v-model="group.item" :items="group.items" item-text="grpNm" item-value="id" @change="changeSelect" return-object>
                <template v-slot:selection="data">
                    <v-chip class="ma-2" x-small label>{{ data.item.grpCd }}</v-chip> {{ data.item.grpNm }}
                </template>
                <template v-slot:item="data">
                    <v-chip class="ma-2" x-small label>{{ data.item.grpCd }}</v-chip> {{ data.item.grpNm }}
                </template>
        
                <v-tooltip bottom slot="append-outer"><template v-slot:activator="{ on, attrs }">
                        <v-icon color="info" @click="newItem()" v-bind="attrs" v-on="on">mdi-plus</v-icon>
                    </template><span>추가</span></v-tooltip>
                <v-tooltip bottom slot="append-outer" v-if="group.item.id !== undefined"><template v-slot:activator="{ on, attrs }">
                        <v-icon color="info" @click="selectItem(group.item)" v-bind="attrs" v-on="on">mdi-pencil</v-icon>
                    </template><span>수정</span></v-tooltip>
            </v-select>
        
        
            <mp-view :view.sync="view">
                <template v-slot:body="{edit}">
                    <v-form v-model="form.valid" ref="form">
                        <v-container>
                            <v-row>
                                <v-col cols="12" md="6">
                                    <v-text-field label="그룹코드*" v-model="form.data.grpCd" :readonly="edit" required :rules="form.rules.grpCd"></v-text-field>
                                </v-col>
                                <v-col cols="12" md="6">
                                    <v-text-field label="그룹명*" v-model="form.data.grpNm" required :rules="form.rules.required"></v-text-field>
                                </v-col>
                                <v-col cols="12" md="6">
                                    <v-select label="사용여부*" v-model="form.data.useYn" :items="[{text:'사용',value:'Y'},{text:'미사용',value:'N'}]" required :rules="[v => (v && v.length > 0) || 'Required field']"></v-select>
                                </v-col>
                            </v-row>
                        </v-container>
                    </v-form>
                </template>
                <template v-slot:control="{edit}">
                    <template v-if="edit">
                        <mp-button mode="text" color="warning" label="저장" icon="mdi-text-box-check" @click="saveItem('PATCH')"></mp-button>
                        <mp-button mode="text" color="danger" label="삭제" icon="mdi-delete" @click="deleteItem(form.data)"></mp-button>
                    </template>
                    <template v-else>
                        <mp-button mode="text" color="warning" label="저장" icon="mdi-text-box-plus" @click="saveItem('POST')"></mp-button>
                        <mp-button mode="text" color="secondary" label="초기화" icon="mdi-refresh" @click="resetItem()"></mp-button>
                    </template>
                </template>
            </mp-view>
        
        </div>
    `,
    props: {
        id: Number,
        change: Function
    },
    data: function() {
        return {
            group: {
                items: [],
                item: {}
            },
            view: {
                mode: 'dialog',
                title: '그룹',
                show: false,
                edit: false
            },
            form: {
                init: {
                    id: undefined,
                    grpCd: undefined,
                    grpNm: undefined,
                    useYn: 'Y'
                },
                data: {},
                valid: false,
                rules: {
                    required: [v => v && v.length > 0 || 'Required field'],
                    grpCd: [v => v && v.length > 0 || 'Required field', v => {
                        if (this.view.edit) {
                            return true;
                        }
                        const same = this.group.items.filter(item => {
                            return item.grpCd === v;
                        });
                        if (same.length > 0) {
                            return v + ' exists';
                        }
                        return true;
                    }]
                }
            }
        };
    },
    computed: {},
    mounted() {
        this.fetchData();
    },
    methods: {
        fetchData(initialGrpCd) {
            xAjax({
                url: '/base/groups'
            }).then(resp => {
                this.group.items = resp;
                this.$nextTick(() => {
                    if (initialGrpCd !== undefined) {
                        const findItems = this.group.items.find(item => item.grpCd === initialGrpCd);
                        if (findItems !== undefined) {
                            this.group.item = findItems;
                            this.changeSelect(findItems);
                        }
                    }
                });
            });
        },
        selectItem(item) {
            this.view.show = true;
            this.view.edit = true;
            this.form.data = Object.assign({}, item);
        },
        newItem() {
            this.view.show = true;
            this.view.edit = false;
            this.resetItem();
        },
        saveItem(method) {
            if (!this.form.valid) {
                return this.$refs.form.validate();
            }
            xAjaxJson({
                url: '/base/groups',
                method: method,
                data: this.form.data
            }).then(response => {
                mercury.base.lib.notify(response.message);
                if (response['affected'] > 0) {
                    this.view.show = false;
                    this.fetchData(this.form.data.grpCd);
                }
            });
        },
        deleteItem() {
            const item = this.group.item;
            mercury.base.lib.confirm('[' + item.grpNm + '] 그룹 및 구성원을 모두 삭제하시겠습니까?').then(result => {
                if (result.isConfirmed) {
                    xAjax({
                        url: '/base/groups/' + item['id'],
                        method: 'DELETE'
                    }).then(response => {
                        mercury.base.lib.notify(response.message);
                        this.view.show = false;
                        this.fetchData();
                        this.changeSelect({});
                    });
                }
            });
        },
        resetItem() {
            //입력폼 초기화
            this.form.data = Object.assign({}, this.form.init);
            if (this.$refs.form !== undefined) {
                this.$refs.form.resetValidation();
            }
        },
        changeSelect(item) {
            this.$emit('update:id', item.id);
        }
    }
}