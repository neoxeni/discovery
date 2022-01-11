const MpBaseChipGroup = {
    name: 'mp-base-chip-group',
    template: `
        <div style="position: relative;">
            <v-chip-group :column="detail">
                <template v-if="group">
                    <v-chip small v-for="item in adUnitList" :key="item.text">
                        <span class="green darken-4" style="display: inline-block;padding: 0 5px;height: 15px;border-radius: 5px;margin-right: 5px;line-height: 14px;">{{item.value}}</span>
                        {{ item.text }}
                    </v-chip>
                </template>
                <template v-else>
                    <v-chip small v-for="item in adUnitList" :key="item.value">{{ item.text }}</v-chip>
                </template>
            </v-chip-group>
            <div class="text-center" style="position: absolute;left: 0;right: 0;bottom: -12px;">
                <v-btn small class="item-detail" title="아이템 보기 확장" @click="detail = !detail" style="height:14px">
                    <v-icon :style="detail ? 'margin-top:8px;':'margin-top:-8px;'">{{detail?'mdi-pan-up':'mdi-pan-down'}}</v-icon>
                </v-btn>
                <v-btn small class="item-group" title="아이템 묶음" @click="group = !group" style="height:14px">
                    <v-icon style="font-size: 16px">{{group?'mdi-select-group':'mdi-select'}}</v-icon>
                    <!--<span class="green darken-4" style="display: inline-block;padding: 0 5px;height: 12px;border-radius: 5px;margin-left: 5px;line-height: 11px;">{{adUnitList.length}}</span>-->
                </v-btn>
            </div>
        </div>
    `,
    props: {
        adUnit: Object
    },
    data: function () {
        return {
            detail: false,
            group: true
        };
    },
    computed: {
        adUnitList: function(){
            if(this.group){
                const groupNameMap = {}
                this.adUnit.report_group_ad_unit_item.forEach(item=>{
                    if(groupNameMap[item.text] === undefined){
                        groupNameMap[item.text] = {
                            text: item.text,
                            value: 0
                        }
                    }

                    groupNameMap[item.text]['value']++;
                });

                const groupList = []
                for(let i in groupNameMap){
                    groupList.push(groupNameMap[i])
                }
                return groupList;
            }else{
                return this.adUnit.report_group_ad_unit_item;
            }
        }
    },
    methods: {

    }
};

export default MpBaseChipGroup;