const baseHelper = Vuex.createNamespacedHelpers('base');
import crudMixIn from "/static/apps/mixin/crudMixIn.js";

const MpPasswordChange = {
    name: 'mp-password-change',
    mixins: [crudMixIn],
    template: `
    <v-card outlined>
        <v-card-title class="headline lighten-2">패스워드 변경<v-spacer></v-spacer>
            <v-btn icon @click="closePasswordChange()"><v-icon>mdi-close</v-icon></v-btn>
        </v-card-title>
        <v-card-text>
            <div>
                <v-text-field v-model="form.data.nowPassword" label="현재 비밀번호" :type="'password'" :rules="mercury.base.rule.required"></v-text-field>
            </div>
            <div>
                <v-text-field v-model="form.data.newPassword1" label="신규 비밀번호" :type="'password'" :rules="mercury.base.rule.password"></v-text-field>
            </div>
            <div>
                <v-text-field v-model="form.data.newPassword2" label="신규 비밀번호 재입력" :type="'password'" :rules="mercury.base.rule.password"></v-text-field>
            </div>
        </v-card-text>
        <v-card-actions>
            <v-spacer></v-spacer>
            <mp-button mode="text" color="warning" label="변경" icon="mdi-text-box-plus" @click="changePassword()"></mp-button>
        </v-card-actions>
    </v-card>
    `,
    data: function(){
        return {
            form: {
                data: {
                    nowPassword: '',
                    newPassword1: '',
                    newPassword2: ''
                },
            }
        };
    },
    methods: {
        changePassword() {
            this.xAjaxJson({
                url: '/LittleJoe/api/users/password_change/',
                method: 'PUT',
                data: this.form.data
            }).then((resp) => {
                this.mercury.base.lib.notify(resp);
                this.closePasswordChange();
            }).finally(() => {
                this.setOverlay(false);
            });
        },
        closePasswordChange() {
          this.$emit('close')
        },
        ...baseHelper.mapActions([
            "setOverlay"
        ])
    }
};

export default MpPasswordChange;