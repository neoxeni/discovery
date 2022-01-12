import MpMenuGroup from "/static/apps/components/navigation/MpMenuGroup.js";

const MpNavigationDrawer = {
    name: 'mp-navigation-drawer',
    template: `
        <div class="ubcs-navigation-drawer">
            <v-navigation-drawer app v-model="drawer" :width="width">
                <template #prepend>
                    <v-toolbar flat dense>
                        <a href="/" class="d-flex align-center app-logo">
                            <v-img v-if="logo.src" :src="logo.src" :alt="logo.title" class="shrink" :width="logo.width" :height="logo.height"/>
                            <span v-if="logo.title" class="title" style="color:#ff7f00 !important;">{{ logo.title }}</span>
                        </a>
                    </v-toolbar>
                </template>
    
                <v-list expand v-if="serviceMenu.children && serviceMenu.children.length > 0">
                    <mp-menu-group v-for="menu in serviceMenu.children" :menu="menu" :key="menu.id"/>
                </v-list>
            </v-navigation-drawer>
        </div>
    `,
    components: {
        MpMenuGroup
    },
    model: {
        prop: 'show',
        event: 'change'
    },
    props: {
        logo: {
            type: Object,
            default: () => ({})
        },
        serviceMenu: {
            type: Object,
            default: () => ({})
        },
        show: {
            type: Boolean,
            default: null
        },
        width: {
            type: [Number, String],
            default: 190,
        }
    },
    computed: {
        drawer: {
            get() {
                return this.show;
            },
            set(drawer) {
                this.$emit('change', drawer);
            }
        }
    }
};

export default MpNavigationDrawer;