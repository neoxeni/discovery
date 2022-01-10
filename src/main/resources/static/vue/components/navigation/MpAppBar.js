import MpPasswordChange from "/static/vue/components/navigation/MpPasswordChange.js"

const baseHelper = Vuex.createNamespacedHelpers('base');

const MpAppBar = {
    name: 'mp-app-bar',
    template: `
        <div class="ubcs-app-bar">
            <v-app-bar app flat dense>
                <v-app-bar-nav-icon class="hidden-lg-and-up" @click="$emit('click:nav-icon')" aria-label="menu"/>
                <a v-if="$vuetify.breakpoint.smOnly || $vuetify.breakpoint.mdOnly" href="/" class="d-flex align-center app-logo">
                    <v-img v-if="logo.src" :src="logo.src" :alt="logo.title" class="shrink" :width="logo.width" :height="logo.height"/>
                </a>
                <span class="ml-2 white--text">{{serverMode !== 'PROD' ? serverMode : ''}}</span>
                <v-spacer></v-spacer>
                <v-menu v-if="getUser && getUser.username" offset-y left>
                    <template v-slot:activator="{ on, attrs }">
                        <v-btn text tile v-bind="attrs" v-on="on">
                            <v-icon left>
                                mdi-account
                            </v-icon>
                            {{ getUser.username }}
                            <v-icon right>
                                mdi-chevron-down
                            </v-icon>
                        </v-btn>
                    </template>
                    <v-list v-if="profile.menuList" nav dense :min-width="150">
                        <v-list-item-group>
                            <v-list-item v-for="(menu, index) in profile.menuList" :key="index" dense :href="menu.href" v-on="menu.on">
                                <v-icon left dense>{{ menu.icon }} mdi-18px</v-icon>
                                <v-list-item-content>
                                    <v-list-item-title>{{ menu.title }}</v-list-item-title>
                                </v-list-item-content>
                            </v-list-item>
                            <!-- 테마 -->
                            <v-list-item dense >
                                <v-icon left dense >mdi-theme-light-dark mdi-18px</v-icon>
                                <v-list-item-content style="padding-left:4px;">
                                    <v-switch  v-model="theme" color="black" hide-details inset class="theme-switch">
                                        <template v-slot:label>
                                            {{themeLabel}}
                                        </template>
                                    </v-switch> 
                                </v-list-item-content>
                            </v-list-item>
                            <!-- 비밀번호 변경 -->
                            <v-list-item @click="showPasswordChange()">
                                <v-icon left dense>mdi-key-change mdi-18px</v-icon>
                                <v-list-item-content>
                                    <v-list-item-title>비빈번호 변경</v-list-item-title>
                                </v-list-item-content>
                            </v-list-item>
                            <!-- 로그아웃 -->
                            <v-list-item dense href="/LittleJoe/logout">
                                <v-icon left dense color="red">mdi-power mdi-18px</v-icon>
                                <v-list-item-content>
                                    <v-list-item-title>Logout</v-list-item-title>
                                </v-list-item-content>
                            </v-list-item>
                        </v-list-item-group>
                    </v-list>
                </v-menu>
            </v-app-bar>
            
            <v-dialog v-model="dialog.passwordChange.visible" :width="500" scrollable overlay-color="#fff">
              <mp-password-change v-on:close="closePasswordChange"></mp-password-change>              
            </v-dialog>
        </div>
    `,
    components: {
        MpPasswordChange
    },
    props: {
        logo: {
            type: Object,
            default: () => ({})
        },
        profile: {
            type: Object,
            default: () => ({})
        },
        serverMode: {
            type: String
        }
    },
    data: function(){
        return {
            theme: false,
            themeLabel: 'Light',
            dialog: {
                passwordChange: {
                    visible: false,
                }
            }
        };
    },
    computed: {
        ...baseHelper.mapGetters([
            'getUser'
        ])
    },
    watch: {
        theme: {
            handler(isDark){
                this.$vuetify.theme.isDark = isDark;
                if (isDark) {
                    this.mercury.base.util.setCookie('theme', 'black', 30);
                    $('body').addClass('ubcs-theme-dark');
                    this.themeLabel = 'Dark';
                } else {
                    this.mercury.base.util.setCookie('theme', null, 30);
                    $('body').removeClass('ubcs-theme-dark');
                    this.themeLabel = 'Light';
                }
            }
        }
    },
    created(){
        if(this.mercury.base.util.getCookie('theme') === 'black'){
            this.$vuetify.theme.isDark = true;
            this.theme = true;
            this.themeLabel = 'Dark';
            $('body').addClass('ubcs-theme-dark');
        }
    },
    methods: {
        showPasswordChange() {
            const dialog = this.dialog.passwordChange;
            dialog.visible = true;
        },
        closePasswordChange(){
            const dialog = this.dialog.passwordChange;
            dialog.visible = false;
        }
    }
};

export default MpAppBar;