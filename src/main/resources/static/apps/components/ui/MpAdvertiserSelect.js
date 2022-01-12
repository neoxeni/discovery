const baseHelper = Vuex.createNamespacedHelpers('base');

const MpAdvertiserSelect = {
    name: 'mp-advertiser-select',
    template: `
        <v-select @change="changeSelect" :items="items"  v-bind="$attrs">
            <template v-slot:item="data">
                <v-chip class="mr-2" x-small label :color="data.item.status === 'ACTIVE'? 'green': 'gray'" v-if="data.item.value !== ''">{{ data.item.value }}</v-chip> {{data.item.text}}
            </template>
        </v-select>
    `,
    props: {
        change: {
            type: Function,
            default: function _default(item) {
                console.log('default', item)
            }
        },
        addItems: {
            type: Array,
            default: () => ([])
        }
    },
    created(){
        this.items = this.mercury.base.util.concatArray(this.getClients,this.addItems, false)
    },
    computed: {
        ...baseHelper.mapGetters([
            'getClients'
        ])
    },
    data: function () {
        return {
            items: undefined
        }
    },
    methods: {
        changeSelect(val){
            this.$emit("input", val);
            this.$emit('change',val);
        },
    }
};

export default MpAdvertiserSelect;