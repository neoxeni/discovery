const baseHelper = Vuex.createNamespacedHelpers('base');

const MpOverlay = {
    name: 'mp-overlay',
    template: `
        <v-overlay :value="getOverlay" :z-index="999999999">
            <v-progress-circular indeterminate size="64"></v-progress-circular>
        </v-overlay>
    `,
    created(){

    },
    computed: {
        ...baseHelper.mapGetters([
            'getOverlay'
        ])
    }
};

export default MpOverlay;