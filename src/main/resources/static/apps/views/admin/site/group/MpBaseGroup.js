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
                        <mp-base-group-select :grp-no.sync="grpNo"></mp-base-group-select>
                    </v-col>
                </v-row>
            </v-card-text>
            <template v-if="grpNo !== undefined">
                <v-card-title>
                    <span>그룹 구성원</span>
                </v-card-title>
                <v-card-text>
                    <mp-base-group-members :grp-no="grpNo"></mp-base-group-members>
                </v-card-text>
            </template>
        </v-card>
    `,
    data: function() {
        return {
            grpNo: undefined
        };
    }
};
