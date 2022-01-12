const baseHelper = Vuex.createNamespacedHelpers('base');
const MpReportGroupMultiCopy = {
    name: 'mp-report-group-multi-copy',
    template: `
    <v-card outlined>
        <v-form class="input-form" ref="form" v-model="form.valid" style="min-height: 350px">
            <v-row v-for="(item, index) in form.items" :key="index">
                <v-col cols="12" md="2">
                    <v-text-field label="광고주" v-model="item['client_name']" readonly></v-text-field>
                </v-col>
                <v-col cols="12" md="2">
                    <v-text-field label="플랫폼" v-model="item.platform" readonly></v-text-field>
                </v-col>
                <v-col cols="12" md="2">
                    <v-text-field label="레포트 그룹명" v-model="item.name" :rules="form.rules.report_group_name"></v-text-field>
                </v-col>
                <v-col cols="12" md="2">
                    <mp-date-range-picker label="기간" :start.sync="item.start_date" :end.sync="item.end_date" shape="underline"></mp-date-range-picker>
                </v-col>
                <v-col cols="12" md="2">
                    <mp-number-field label="예산 (광고주 보고 기준)" :value.sync="item.budget"></mp-number-field>
                </v-col>
                <v-col cols="12" md="2">
                    <v-select label="권한 및 이메일" v-model="item.role" :items="[{text:'없음',value:'NONE'},{text:'수정',value:'EDIT'},{text:'이메일',value:'EMAIL'},{text:'수정+이메일',value:'ALL'}]"></v-select>
                </v-col>
            </v-row>
        </v-form>
    </v-card>
    `,

    props: {
        reports: {
            type: Array
        },
        dialog: {
            type: Object
        }
    },
    computed: {
        ...baseHelper.mapGetters([
            'getUsers',
        ])
    },

    data: function () {
        return {
            form: {
                valid: false,
                items: [],
                rules: {
                    report_group_name: [
                        name => (name !== undefined && name.length > 0) || '필수 입력 항목입니다.',
                        name => (name !== undefined && name.length < 29) || '이름은 29자를 초과 할 수 없습니다.',
                        name => !(/[\[\]/:*?]/.test(name)) || '[]:*?/ 문자는 포함될 수 없습니다.',
                    ]
                }
            }
        }
    },
    mounted() {
        const copyList = this.mercury.base.util.deepCopy(this.reports);
        copyList.forEach(item=>{
            item.role = 'ALL'
        })

        this.form.items.push(...copyList)
    },
    methods: {
        saveItem(){
            if (!this.form.valid && this.$refs.form !== undefined) {
                return this.$refs.form.validate();//form validation 수행하여 화면에 에러 메시지 표시
            }

            this.mercury.base.lib.confirm(this.form.items.length + '개의 레포트를 복사 하시겠습니까?').then(result => {
                if (result.isConfirmed) {
                    this.xAjaxJson({
                        url: '/LittleJoe/api/reportgroups/copy/',
                        method: 'POST',
                        data: this.form.items
                    }).then((resp)=>{
                        this.mercury.base.lib.notify(resp);
                        if (this.dialog){
                            if (typeof this.dialog.refresh === 'function'){
                                this.dialog.refresh();
                            }

                            this.dialog.close();
                        }
                    });
                }
            })
        }
    }
};

export default MpReportGroupMultiCopy;