import MpBaseOrganizationTreeCard from "./MpBaseOrganizationTreeCard.js";
import MpBaseGroupCombo from "../group/MpBaseGroupCombo.js";





export default {
    name: 'mp-base-organization',
    components: {
        MpBaseGroupCombo,
        MpBaseOrganizationTreeCard
    },
    template: `
        <v-row>
            <v-col cols="12" md="5">
                <mp-base-organization-tree-card :changed="treeChanged" :dnd="control.dnd" ref="jstree-card"></mp-base-organization-tree-card>
            </v-col>
        
            <v-col cols="12" md="7" v-if="item.id !== undefined">
                <v-card>
                    <v-card-title>
                        <span v-html="paths"></span>
                        <template v-if="mode === 'organization'">
                            <span class="position-right" v-show="item.dataType === 'D'">
                                <v-tooltip bottom><template v-slot:activator="{ on, attrs }">
                                        <v-btn color="success" small v-bind="attrs" v-on="on" @click="newDepartment()">
                                            <v-icon>mdi-microsoft-teams</v-icon>
                                        </v-btn>
                                    </template><span>부서 추가</span>
                                </v-tooltip>
                                <v-tooltip bottom><template v-slot:activator="{ on, attrs }">
                                        <v-btn color="success" small v-bind="attrs" v-on="on" @click="newEmployee()">
                                            <v-icon>mdi-account</v-icon>
                                        </v-btn>
                                    </template><span>직원 추가</span>
                                </v-tooltip>
                            </span>
                        </template>
                    </v-card-title>
                    <v-form v-model="validation.valid" ref="form">
                        <template v-if="type === 'company'">
                            <v-card-text>
                                <v-row>
                                    <v-col cols="12" md="4">
                                        <v-text-field label="회사번호*" v-model="company.clientId" required readonly></v-text-field>
                                    </v-col>
                                    <v-col cols="12" md="4">
                                        <v-text-field label="회사아이디*" v-model="company.cmpnyId" required readonly></v-text-field>
                                    </v-col>
                                    <v-col cols="12" md="4">
                                        <v-text-field label="회사이름*" v-model="company.cmpnyNm" required :rules="[v => (v && v.length > 0) || 'Required field']"></v-text-field>
                                    </v-col>
        
                                    <v-col cols="12" md="4">
                                        <v-text-field label="전화번호" v-model="company.telNo"></v-text-field>
                                    </v-col>
                                    <v-col cols="12" md="4">
                                        <v-text-field label="CEO" v-model="company.ceoNm"></v-text-field>
                                    </v-col>
                                    <v-col cols="12" md="4">
                                        <v-text-field label="도메인" v-model="company.domain">
                                            <v-tooltip bottom slot="append-outer">
                                                <template v-slot:activator="{ on, attrs }">
                                                    <v-icon :color="company.domainUseYn === 'Y' ? 'green' : 'grey'" @click="toggleDomain" v-bind="attrs" v-on="on">{{company.domainUseYn === 'Y' ? 'mdi-link-variant' : 'mdi-link-variant-off'}}</v-icon>
                                                </template>
                                                <span>도메인 사용 여부</span>
                                            </v-tooltip>
                                        </v-text-field>
                                    </v-col>
        
                                    <v-col cols="12" md="4">
                                        <v-text-field label="콜 대표번호" v-model="company.callTelNo"></v-text-field>
                                    </v-col>
                                    <v-col cols="12" md="4">
                                        <v-text-field label="SMS 발신 번호" v-model="company.smsTelNo"></v-text-field>
                                    </v-col>
                                    <v-col cols="12" md="4">
                                        <v-text-field label="SMTP 이메일" v-model="company.email"></v-text-field>
                                    </v-col>
                                    <v-col cols="12" md="4">
                                        <v-text-field v-model="company.emailPw" label="SMTP 비밀번호" :readonly="company.emailPw==='PROTECTED'" :type="company.emailPw==='PROTECTED' ? 'text': 'password'" autocomplete="new-password">
                                            <v-tooltip bottom slot="append">
                                                <template v-slot:activator="{ on, attrs }">
                                                    <v-icon :color="company.email === undefined ? 'grey' : 'info'" v-if="control.company && company.emailPw==='PROTECTED'" v-bind="attrs" v-on="on" @click="editEmailPassword(company)">mdi-security</v-icon>
                                                </template><span>비밀번호 변경</span>
                                            </v-tooltip>
                                        </v-text-field>
                                    </v-col>
                                    <v-col cols="12" md="4">
                                        <v-text-field label="SMTP 호스트" v-model="company.smtpHost"></v-text-field>
                                    </v-col>
                                    <v-col cols="12" md="4">
                                        <v-text-field label="SMTP 포트" v-model="company.smtpPort" type="number">
                                            <v-tooltip bottom slot="append-outer">
                                                <template v-slot:activator="{ on, attrs }">
                                                    <v-icon v-if="control.company" :color="company.smtpSslYn === 'Y' ? 'green' : 'grey'" v-bind="attrs" v-on="on" @click="company.smtpSslYn = company.smtpSslYn === 'Y'?'N':'Y'">mdi-security</v-icon>
                                                </template><span>SMTP 보안연결 여부</span>
                                            </v-tooltip>
                                        </v-text-field>
                                    </v-col>
                                    <v-col cols="12" md="12">
                                        <v-text-field label="주소" v-model="company.addr"></v-text-field>
                                    </v-col>
                                    <v-col cols="12" md="12">
                                        <v-textarea label="비고" v-model="company.cntnt"></v-textarea>
                                    </v-col>
        
                                    <v-col cols="12" md="12" class="text-right" v-if="control.company">
                                        <v-btn class="ml-1" color="warning" @click="saveItem" outlined small text>
                                            <v-icon>mdi-text-box-check mdi-18px</v-icon>{{edit ? '저장':'추가'}}
                                        </v-btn>
                                    </v-col>
                                    
                                    <template v-if="control.group">
                                        <v-col cols="12" md="12">
                                            <mp-base-group-combo :items="roles" :selected-items.sync="companyRoles" label="그룹"></mp-base-group-combo>
                                        </v-col>
            
                                        <v-col cols="12" md="12" class="text-right">
                                            <v-btn class="ml-1" color="warning" @click="saveGroupItem" outlined small text>
                                                <v-icon>mdi-text-box-check mdi-18px</v-icon>그룹 저장
                                            </v-btn>
                                        </v-col>
                                    </template>
        
                                </v-row>
        
                                <mp-view :view.sync="viewEmail">
                                    <template v-slot:body="{edit}">
                                        <v-form class="input-form" ref="form-email-password" v-model="viewEmail.data.valid">
                                            <v-row>
                                                <v-col cols="12" md="6">
                                                    <v-text-field type="password" label="새 비밀번호" v-model="viewEmail.data.newPassword" :rules="mercury.base.rule.required"></v-text-field>
                                                </v-col>
                                                <v-col cols="12" md="6">
                                                    <v-text-field type="password" label="새 비밀번호 확인" v-model="viewEmail.data.confirmPassword" :rules="mercury.base.rule.required"></v-text-field>
                                                </v-col>
                                            </v-row>
                                        </v-form>
                                    </template>
                                    <template v-slot:control="{edit}">
                                        <mp-button mode="text" color="warning" label="저장" icon="mdi-text-box-check" @click="saveEmailPasswordItem"></mp-button>
                                    </template>
                                </mp-view>
                            </v-card-text>
                        </template>
        
        
                        <template v-else-if="type === 'department'">
                            <v-card-text>
                                <v-row>
                                    <!--<v-col cols="12" md="6">
                                            <v-text-field label="부서코드*" v-model="department.deptCd" :rules="[v => (v && v.length > 0) || 'Required field']"></v-text-field>
                                        </v-col>-->
                                    <v-col cols="12" md="12">
                                        <v-text-field label="부서명*" v-model="department.name" :rules="[v => (v && v.length > 0) || 'Required field']"></v-text-field>
                                    </v-col>
                                    <v-col cols="12" md="6">
                                        <v-text-field label="정렬번호*" v-model="department.sort" type="number" :rules="[v => (v !== '' && !isNaN(v)) || 'Required field']"></v-text-field>
                                    </v-col>
                                    <v-col cols="12" md="6">
                                        <v-select label="사용여부" v-model="department.useYn" :items="[{text:'사용',value:'Y'},{text:'미사용',value:'N'}]"></v-select>
                                    </v-col>
        
                                    <v-col cols="12" md="12" class="text-right" v-if="control.department">
                                        <v-btn class="ml-1" color="warning" @click="saveItem" outlined small text>
                                            <v-icon>mdi-text-box-check mdi-18px</v-icon>{{edit ? '저장':'추가'}}
                                        </v-btn>
                                    </v-col>
                                    
                                    <template v-if="control.group">
                                        <v-col cols="12" md="12">
                                            <mp-base-group-combo :items="roles" :selected-items.sync="department.parentsRoles" readonly label="상위그룹"></mp-base-group-combo>
                                        </v-col>
                                        <v-col cols="12" md="12">
                                            <mp-base-group-combo :items="roles" :selected-items.sync="departmentRoles" label="그룹"></mp-base-group-combo>
                                        </v-col>

                                        <v-col cols="12" md="12" class="text-right">
                                            <v-btn class="ml-1" color="warning" @click="saveGroupItem" outlined small text>
                                                <v-icon>mdi-text-box-check mdi-18px</v-icon>그룹 저장
                                            </v-btn>
                                        </v-col>
                                    </template>
                                </v-row>
                            </v-card-text>
                        </template>
        
        
                        <template v-else-if="type === 'employee'">
                            <v-card-text>
                                <v-row>
                                    <v-col cols="12" md="4">
                                        <v-text-field label="ID" v-model="employee.username" :readonly="edit" :rules="[v => (v && v.length > 0) || 'Required field']"></v-text-field>
                                    </v-col>
                                    <v-col cols="12" md="4">
                                        <v-text-field label="이름" v-model="employee.name" :rules="[v => (v && v.length > 0) || 'Required field']"></v-text-field>
                                    </v-col>
                                    <v-col cols="12" md="4">
                                        <v-text-field label="별명" v-model="employee.nickname" :rules="[v => (v && v.length > 0) || 'Required field']"></v-text-field>
                                    </v-col>
                                    <v-col cols="12" md="4">
                                        <v-text-field label="부서" v-model="employee.departmentName" readonly></v-text-field>
                                    </v-col>
                                    <v-col cols="12" md="4">
                                        <v-select label="직위" v-model="employee.positionCd" :items="codes.postnCd">
                                            <v-tooltip bottom slot="append-outer">
                                                <template v-slot:activator="{ on, attrs }">
                                                    <v-icon color="info" v-if="control.employee" v-bind="attrs" v-on="on" @click="viewPostn.show = true">mdi-pencil</v-icon>
                                                </template>
                                                <span>직위 수정</span>
                                            </v-tooltip>
                                        </v-select>
                                    </v-col>
                                    <v-col cols="12" md="4">
                                        <v-select label="직책" v-model="employee.dutyCd" :items="codes.dutyCd">
                                            <v-tooltip bottom slot="append-outer">
                                                <template v-slot:activator="{ on, attrs }">
                                                    <v-icon color="info" v-if="control.employee" v-bind="attrs" v-on="on" @click="viewDuty.show = true">mdi-pencil</v-icon>
                                                </template>
                                                <span>직책 수정</span>
                                            </v-tooltip>
                                        </v-select>
                                    </v-col>
                                    <v-col cols="12" md="4">
                                        <v-text-field label="이메일" v-model="employee.email"></v-text-field>
                                    </v-col>
                                    <v-col cols="12" md="4">
                                        <v-text-field label="전화번호" v-model="employee.phone"></v-text-field>
                                    </v-col>
                                    <v-col cols="12" md="4">
                                        <v-text-field label="내선번호" v-model="employee.extensionNo"></v-text-field>
                                    </v-col>
                                    <v-col cols="12" md="4">
                                        <v-text-field label="생성일" v-model="employee.createdAt" readonly></v-text-field>
                                    </v-col>
                                    <v-col cols="12" md="4">
                                        <v-text-field label="수정일" v-model="employee.updatedAt" readonly></v-text-field>
                                    </v-col>
                                    <v-col cols="12" md="4">
                                        <v-select label="상태" v-model="employee.status" :items="codes.userStatus"></v-select>
                                    </v-col>
        
                                    <v-col cols="12" md="12">
                                        <v-text-field label="유저키" v-model="employee.userKey" readonly placeholder="자동생성"></v-text-field>
                                    </v-col>
        
                                    <v-col cols="12" md="12" class="text-right">
                                        <v-btn class="ml-1" color="error" @click="resetPassword" v-if="control.password" v-show="edit === true && type === 'employee'" outlined small text >비밀번호 초기화</v-btn>
                                        <v-btn class="ml-1" color="warning" @click="saveItem" outlined small text v-if="control.employee">
                                            <v-icon>mdi-text-box-check mdi-18px</v-icon>{{edit ? '저장':'추가'}}
                                        </v-btn>
                                    </v-col>
                                    
                                    <template v-if="control.group">
                                        <v-col cols="12" md="12">
                                            <mp-base-group-combo :items="roles" :selected-items.sync="employeeDepartmentRoles" readonly label="상위그룹"></mp-base-group-combo>
                                        </v-col>
                                        <v-col cols="12" md="12">
                                            <mp-base-group-combo :items="roles" :selected-items.sync="employeeRoles" label="그룹"></mp-base-group-combo>
                                        </v-col>
                                        <v-col cols="12" md="12" class="text-right">
                                            <v-btn class="ml-1" color="warning" @click="saveGroupItem" outlined small text>
                                                <v-icon>mdi-text-box-check mdi-18px</v-icon>그룹 저장
                                            </v-btn>
                                        </v-col>
                                    </template>
                                </v-row>
        
                                <mp-view :view.sync="viewPostn">
                                    <template v-slot:body="{edit}">
                                        <v-form class="input-form" ref="form-postn" v-model="viewPostn.data.valid">
                                            <mp-button mode="text" color="gray" label="추가" icon="mdi-plus-box" @click="addPostnItem()" class="layout-tabs-plus-btn"></mp-button>
                                            <v-row v-for="(item, index) in codes.postnCd" :key="item.cd">
                                                <v-col cols="12" md="6">
                                                    <v-text-field type="text" label="이름" v-model="item.cdNm" :rules="mercury.base.rule.required" hide-details="auto"></v-text-field>
                                                </v-col>
                                                <v-col cols="12" md="6">
                                                    <v-text-field type="text" label="순서" v-model="item.sortNo" :rules="mercury.base.rule.number" hide-details="auto">
                                                        <v-tooltip bottom slot="append-outer"><template v-slot:activator="{ on, attrs }">
                                                                <v-icon color="danger" v-bind="attrs" v-on="on" @click="removePostnItem(item, index)">mdi-minus-box</v-icon>
                                                            </template><span>삭제</span></v-tooltip>
                                                    </v-text-field>
        
                                                </v-col>
                                            </v-row>
                                        </v-form>
                                    </template>
                                    <template v-slot:control="{edit}" v-if="control.company">
                                        <mp-button mode="text" color="warning" label="저장" icon="mdi-text-box-check" @click="savePostnItem"></mp-button>
                                    </template>
                                </mp-view>
        
                                <mp-view :view.sync="viewDuty">
                                    <template v-slot:body="{edit}">
                                        <v-form class="input-form" ref="form-duty" v-model="viewDuty.data.valid">
                                            <mp-button mode="text" color="gray" label="추가" icon="mdi-plus-box" @click="addDutyItem()" class="layout-tabs-plus-btn"></mp-button>
                                            <v-row v-for="(item,index) in codes.dutyCd" :key="item.cd">
                                                <v-col cols="12" md="6">
                                                    <v-text-field type="text" label="이름" v-model="item.cdNm" :rules="mercury.base.rule.required" hide-details="auto"></v-text-field>
                                                </v-col>
                                                <v-col cols="12" md="6">
                                                    <v-text-field type="text" label="순서" v-model="item.sortNo" :rules="mercury.base.rule.number" hide-details="auto">
                                                        <v-tooltip bottom slot="append-outer"><template v-slot:activator="{ on, attrs }">
                                                                <v-icon color="danger" v-bind="attrs" v-on="on" @click="removeDutyItem(item, index)">mdi-minus-box</v-icon>
                                                            </template><span>삭제</span></v-tooltip>
                                                    </v-text-field>
                                                </v-col>
                                            </v-row>
                                        </v-form>
                                    </template>
                                    <template v-slot:control="{edit}">
                                        <mp-button mode="text" color="warning" label="저장" icon="mdi-text-box-check" @click="saveDutyItem"></mp-button>
                                    </template>
                                </mp-view>
                            </v-card-text>
                        </template>
        
                        <template v-else>
                            <v-card-text>
                                UNKNOWN TYPE {{type}}
                            </v-card-text>
                        </template>
                    </v-form>
                </v-card>
            </v-col>
        </v-row>
    `,
    props:{
        mode:{//organization, password, group
            type: String,
            default: 'organization'
        }
    },
    data: function() {
        return {
            control:{
                company: true,
                employee: true,
                department: true,
                password: true,
                dnd: true,
                group: true
            },
            item: {},
            paths: '',
            company: {},
            department: {},
            employee: {},
            employeeDepartmentRoles: [],
            //직원의 부서에 포함된 roles
            companyRoles: [],
            //role 변경본
            employeeRoles: [],
            //role 변경본
            departmentRoles: [],
            //role 변경본
            edit: false,
            type: undefined,
            //company, department, employee
            codes: {
                postnCd: mercury.base.lib.code('OR02', {
                    type: 'v-object'
                }),
                dutyCd: mercury.base.lib.code('OR01', {
                    type: 'v-object'
                }),
                userStatus: mercury.base.lib.code('UserStatus', {
                    type: 'v-object'
                })
            },
            roles: [],
            validation: {
                valid: false
            },
            viewEmail: {
                mode: 'dialog',
                title: '이메일 비밀번호 변경',
                show: false,
                edit: false,
                data: {
                    valid: true,
                    newPassword: undefined,
                    confirmPassword: undefined
                }
            },
            viewPostn: {
                mode: 'dialog',
                title: '직위 변경',
                show: false,
                edit: false,
                data: {
                    valid: true
                }
            },
            viewDuty: {
                mode: 'dialog',
                title: '직책 변경',
                show: false,
                edit: false,
                data: {
                    valid: true
                }
            }
        };
    },
    created: function () {
        //organization, password, group
        let control = {};
        if (this.mode === 'organization') {
            control = {
                company: true,
                employee: true,
                department: true,
                password: true,
                dnd: true,
                group: true
            };
        } else if (this.mode === 'password') {
            control = {
                company: false,
                employee: false,
                department: false,
                password: true,
                dnd: false,
                group: false
            };
        } else if (this.mode === 'group') {
            control = {
                company: false,
                employee: false,
                department: false,
                password: false,
                dnd: false,
                group: true
            };
        }
        Object.assign(this.control, control);
    },
    mounted: function() {
        xAjax({
            url: '/base/groups'
        }).then(resp => {
            this.roles = resp;
        });
    },
    methods: {
        newDepartment() {
            this.edit = false;
            this.type = 'department';
            this.department = {
                parentDepartmentKey: this.item.departmentKey,
                departmentKey: '',
                name: '',
                sort: 0,
                useYn: 'Y'
            };
            this.departmentRoles = [];
        },
        newEmployee() {
            this.edit = false;
            this.type = 'employee';
            this.employee = {
                name: '',
                nickname: '',
                phone: '',
                email: '',
                username: '',

                identification: undefined,
                extensionNo: undefined,
                positionCd: undefined,
                dutyCd: undefined,
                sort: 0,
                status: undefined,

                userKey: '',
                departmentId: this.item.id,
                departmentName: this.item.text
            };
            this.employeeRoles = [];
            this.employeeDepartmentRoles = [];
        },
        resetPassword() {
            mercury.base.lib.confirm('패스워드는 사용자의 ID로 초기화 됩니다.<br/>패스워드 초기화를 하시겠습니까?').then(result => {
                if (result.isConfirmed) {
                    xAjax({
                        url: '/base/users/' + this.employee['userKey'] + '/password',
                        method: 'PATCH'
                    }).then(resp => {
                        mercury.base.lib.notify(resp.message);
                    });
                }
            });
        },
        editEmailPassword(item) {
            Object.assign(this.viewEmail.data, {
                valid: true,
                newPassword: undefined,
                confirmPassword: undefined
            });
            this.viewEmail.title = item.email + ' Password 변경';
            this.viewEmail.show = true;
            this.viewEmail.edit = true;
        },
        saveEmailPasswordItem() {
            if (!this.viewEmail.data.valid && this.$refs['form-email-password'] !== undefined) {
                mercury.base.lib.notify('입력값중 유효하지 않은 값이 있습니다.', {}, {
                    type: 'danger'
                });
                return this.$refs['form-email-password'].validate();
            }
            if (this.viewEmail.data.newPassword !== this.viewEmail.data.confirmPassword) {
                mercury.base.lib.notify('새 비밀번호와 확인 비밀번호가 일치하지 않습니다.', {}, {
                    type: 'danger'
                });
                return this.$refs['form-email-password'].validate();
            }
            xAjax({
                url: '/base/companies/email/password',
                method: 'PATCH',
                data: this.viewEmail.data
            }).then(response => {
                if (response.affected === 0) {
                    mercury.base.lib.notify(response.message, {}, {
                        type: 'warning'
                    });
                } else {
                    mercury.base.lib.notify(response.message);
                    this.viewEmail.show = false;
                }
            });
        },
        saveItem() {
            if (!this.validation.valid && this.$refs.form !== undefined) {
                return this.$refs.form.validate();
            }
            let url = '';
            let method = '';
            let data = {};
            let name = ''; //tree 적용 이름
            //tree 적용 이름
            if (this.type === 'employee') {
                url = '/base/users';
                method = this.edit ? 'PATCH' : 'POST';
                data = this.employee;
                name = this.employee.empNm;
                data['authorities'] = [];
            } else if (this.type === 'department') {
                url = '/base/organizations/departments';
                method = this.edit ? 'PATCH' : 'POST';
                data = this.department;
                name = this.department.deptNm;
            } else if (this.type === 'company') {
                url = '/base/companies';
                method = 'PATCH';
                data = this.company;
                name = this.company.cmpnyNm;
            }
            xAjax({
                url: url,
                method: method,
                contentType: 'application/json',
                data: JSON.stringify(data)
            }).then(resp => {
                mercury.base.lib.notify(resp.message);
                if (method === 'PATCH') {
                    this.$refs['jstree-card'].renameNode(this.item, name);
                } else {
                    location.reload();
                }
            });
        },
        saveGroupItem() {
            let dataGbn = '';
            let dataNo = '';
            let target = [];
            const originGroupMapByGrpNo = {}; // grpNo: {data}
            // grpNo: {data}
            if (this.type === 'employee') {
                dataGbn = 'E';
                dataNo = this.employee.empNo;
                this.employee.roles.forEach(role => {
                    if (role.dataGbn === 'E') {
                        originGroupMapByGrpNo[role.grpNo] = role;
                    }
                });
                target = this.employeeRoles;
            } else if (this.type === 'department') {
                dataGbn = 'D';
                dataNo = this.department.deptNo;
                this.department.roles.forEach(role => {
                    originGroupMapByGrpNo[role.grpNo] = role;
                });
                target = this.departmentRoles;
            } else if (this.type === 'company') {
                dataGbn = 'D';
                dataNo = this.company.rootDepartment.deptNo;
                this.company.rootDepartment.roles.forEach(role => {
                    originGroupMapByGrpNo[role.grpNo] = role;
                });
                target = this.companyRoles;
            }
            const groupMappings = []; //삭제할 목록을 찾는다.
            //삭제할 목록을 찾는다.
            for (let grpNo in originGroupMapByGrpNo) {
                if (originGroupMapByGrpNo.hasOwnProperty(grpNo)) {
                    const oriItem = originGroupMapByGrpNo[grpNo];
                    const oriItemInTarget = target.find(item => {
                        return item.grpNo === oriItem['grpNo'];
                    });
                    if (oriItemInTarget === undefined) {
                        oriItem['useYn'] = 'N'; //삭제할 항목은 N으로
                        //삭제할 항목은 N으로
                        groupMappings.push(oriItem);
                    }
                }
            } //추가할 항목을 찾는다.
            target.forEach(item => {
                const mapNo = item.mapNo;
                const oriItem = originGroupMapByGrpNo[item.grpNo];
                if (oriItem === undefined) {
                    groupMappings.push({
                        dataGbn: dataGbn,
                        dataNo: dataNo,
                        grpCd: item.grpCd,
                        grpNm: item.grpNm,
                        grpNo: item.grpNo,
                        useYn: 'Y',
                        mapNo: null //새로 추가되어야 함
                    });
                }
            }); //새로 추가되어야 함

            xAjax({
                url: '/base/groups/mappings',
                method: 'PATCH',
                contentType: 'application/json',
                data: JSON.stringify(groupMappings)
            }).then(resp => {
                mercury.base.lib.notify(resp.message);
                this.$refs['jstree-card'].selectNode(this.item.id);
            });
        },
        toggleDomain() {
            if (this.company.domainUseYn === 'Y') {
                this.company.domainUseYn = 'N';
            } else {
                this.company.domainUseYn = 'Y';
            }
        },
        addPostnItem() {
            this.codes.postnCd.push({
                sortNo: this.codes.postnCd.length + 1
            });
        },
        removePostnItem(item, index) {
            this.codes.postnCd.splice(index, 1);
        },
        savePostnItem() {
            if(this.viewPostn.data.valid){
                xAjaxJson({
                    url: '/base/organizations/posts',
                    method: 'PATCH',
                    data: this.codes.postnCd
                }).then(resp => {
                    mercury.base.lib.notify(resp.message);
                    resp.object.forEach((data)=>{
                        data['text'] = data.cdNm;
                        data['value'] = data.cd;
                    });
                    this.codes.postnCd = resp.object;
                });
            }
        },
        addDutyItem() {
            this.codes.dutyCd.push({
                sortNo: this.codes.dutyCd.length + 1
            });
        },
        removeDutyItem(item, index) {
            this.codes.dutyCd.splice(index, 1);
        },
        saveDutyItem() {
            if(this.viewDuty.data.valid) {
                xAjaxJson({
                    url: '/base/organizations/jobs',
                    method: 'PATCH',
                    data: this.codes.dutyCd
                }).then(resp => {
                    mercury.base.lib.notify(resp.message);
                    resp.object.forEach((data)=>{
                        data['text'] = data.cdNm;
                        data['value'] = data.cd;
                    });
                    this.codes.dutyCd = resp.object;
                });
            }
        },
        treeChanged(e, data, instance) {
            if (data.action === 'deselect_all') {
                //refresh 시에도 발생한다.
                return;
            }
            const node = data.node;
            console.log(node)

            this.item = Object.assign(Object.assign({}, node.original), Object.assign({}, node.data));

            const no = this.item.id;

            if (node.parent === '#') {
                this.paths = '<i class="text-primary">' + node.text + '</i>';
                xAjax({
                    url: '/base/companiesMe'
                }).then(resp => {
                    this.company = resp;
                    this.edit = true;
                    this.type = 'company';
                    return xAjax({
                        url: '/base/organizations/departments/' + no
                    });
                }).then(resp => {
                    this.$set(this.company, 'rootDepartment', resp);
                    this.companyRoles = resp.roles.slice();
                });
            } else {
                const prePath = instance.get_path(node.parent, ' > ');
                this.paths = '<i>' + prePath + '</i>' + '<i class="text-danger"> > </i>' + '<i class="text-primary">' + node.text + '</i>';
                const dataType = node.original['dataType'];
                if (dataType === 'D') {
                    xAjax({
                        url: mercury.base.util.bindPath('/base/organizations/departments/{departmentKey}',{departmentKey:this.item.departmentKey})
                    }).then(resp => {
                        this.department = resp;
                        if (resp.roles !== null) {
                            this.departmentRoles = resp.roles.slice(); //shallow copy
                        } else //shallow copy
                        {
                            this.departmentRoles = [];
                        }
                        this.edit = true;
                        this.type = 'department';
                    });
                } else if (dataType === 'E') {
                    xAjax({
                        url: mercury.base.util.bindPath('/base/organizations/employees/{userKey}',{userKey:this.item.userKey})
                    }).then(resp => {
                        this.employee = resp;
                        if (resp.roles !== null) {
                            this.employeeRoles = resp.roles.filter(role => role.dataGbn === 'E');
                            this.employeeDepartmentRoles = resp.roles.filter(role => role.dataGbn === 'D');
                        } else {
                            this.employeeRoles = [];
                            this.employeeDepartmentRoles = [];
                        }
                        this.edit = true;
                        this.type = 'employee';
                    });
                }
            }
        }
    }
};
