import MpBaseGroupSelect from './MpBaseGroupSelect.js';
import MpBaseGroupMembers from './MpBaseGroupMembers.js';

export default {
    name: 'mp-base-group',
    components: {
        MpBaseGroupSelect,
        MpBaseGroupMembers
    },
    template: `
        <v-card outlined>
            <v-card-text>
                <v-row>
                    <v-col cols="12" md="12">
                        <mp-base-group-select :id.sync="id"></mp-base-group-select>
                    </v-col>
                </v-row>
            </v-card-text>
            <template v-if="id !== undefined">
                <v-card-title>
                    <span>그룹 구성원</span>
                </v-card-title>
                <v-card-text>
                    <mp-base-group-members :id="id"></mp-base-group-members>
                </v-card-text>
            </template>
        </v-card>
    `,
    data: function() {
        return {
            id: undefined
        };
    }
};
