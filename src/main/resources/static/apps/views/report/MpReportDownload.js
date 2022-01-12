const baseHelper = Vuex.createNamespacedHelpers('base');

import MpReportDownloadMe from "/static/apps/components/platform/MpReportDownloadMe.js"
import MpReportDownloadClient from "/static/apps/components/platform/MpReportDownloadClient.js"

const MpReportDownload = {
    name: 'mp-report-download',
    components: {
        MpReportDownloadMe,
        MpReportDownloadClient
    },
    template: `
    <v-card outlined>
        <v-tabs v-model="tab" >
            <v-tab>개인</v-tab>
            <v-tab>광고주</v-tab>
        </v-tabs>
        <v-tabs-items v-model="tab">
            <v-tab-item>
                <mp-report-download-me></mp-report-download-me>
            </v-tab-item>
            <v-tab-item>
                <mp-report-download-client></mp-report-download-client>
            </v-tab-item>
        </v-tabs-items>
    </v-card>
    `,
    data: function () {
        return {
            tab: undefined,
        }
    },
    watch: {

    },
    methods: {

    }
};

export default MpReportDownload;