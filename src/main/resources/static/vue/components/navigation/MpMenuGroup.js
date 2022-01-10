const MpMenuGroup = {
    name: 'mp-menu-group',
    template: `
        <v-list-group v-if="menu.id" v-model="isActive" :group="group" v-bind="$attrs" prepend-icon="" append-icon="">
            <template #activator>
                <v-list-item dense>
                    <v-list-item-content>
                        <v-list-item-title v-if="menu.name" v-text="menu.name"/>
                    </v-list-item-content>
                </v-list-item>
                <v-icon v-if="isActive">mdi-chevron-up</v-icon>
                <v-icon v-else>mdi-chevron-down</v-icon>
            </template>
    
            <template v-for="childMenu in menu.children">
                <mp-menu-group v-if="childMenu.children.length > 0" :key="childMenu.id" :menu="childMenu" :level="level + 1" sub-group/>
    
                <v-list-item v-else :key="childMenu.id" :to="childMenu.path" tag="a" dense>
                    <v-list-item-title>{{ childMenu.name }}</v-list-item-title>
                </v-list-item>
            </template>
        </v-list-group>
    `,
    props: {
        menu: {
            type: Object,
            default: () => ({})
        },
        level: {
            type: Number,
            default: 0
        }
    },
    data: () => ({isActive: null}),
    computed: {
        group() {
            return this.genGroup(this.menu.children);
        }
    },
    methods: {
        genGroup(menuChildren) {
            return menuChildren.reduce((acc, cur) => {
                acc.push(cur.children && cur.children.length > 0 ? this.genGroup(cur.children) : cur.path);
                return acc;
            }, []).join('|');
        }
    }
};

export default MpMenuGroup;