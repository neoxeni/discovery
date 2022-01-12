const baseHelper = Vuex.createNamespacedHelpers('base');
import crudMixIn from "/static/apps/mixin/crudMixIn.js";

import MpBaseChipGroup from "/static/apps/components/ui/MpBaseChipGroup.js";
import MpAdvertiserSelect from "/static/apps/components/ui/MpAdvertiserSelect.js";

import MpReportGroupAuth from "/static/apps/components/platform/MpReportGroupAuth.js"
import MpReportGroupMultiCopy from "/static/apps/components/platform/MpReportGroupMultiCopy.js"

import MpReportGroupColumnPlatform from "/static/apps/components/platform/MpReportGroupColumnPlatform.js"
import MpReportGroupFormula from "/static/apps/components/platform/MpReportGroupFormula.js"
import MpReportGroupUnits from "/static/apps/components/platform/MpReportGroupUnits.js"
import MpReportGroupUnitsGroup from "/static/apps/components/platform/MpReportGroupUnitsGroup.js"
import MpReportGroupUnitsLike from "/static/apps/components/platform/MpReportGroupUnitsLike.js"
import MpReportSpreadSheetList from "/static/apps/components/platform/MpReportSpreadSheetList.js"

const MpReportGroup = {
    name: 'mp-report-group',
    mixins: [crudMixIn],
    components: {
        MpBaseChipGroup,
        MpAdvertiserSelect
    },
    template: `
    <v-card outlined>
        <mp-search>
            <div class="w-200px">
                <mp-advertiser-select label="광고주" v-model="search.client" @change="fetchData(true)" :add-items="[{text:'전체',value:''}]" outlined dense hide-details></mp-advertiser-select>
            </div>
            <div class="w-190px">
                <v-select label="플랫폼" v-model="search.platform" @change="fetchData(true)" :items="mercury.base.util.concatArray(code.platform.filter(a=>a.etc1 === 'DSP'),[{text:'전체',value:''}], false)" outlined dense hide-details></v-select>
            </div>
            <div class="w-150px">
                <v-text-field v-model="search.name" label="레포트 그룹명" clearable @click:clear="search.name='';fetchData(true)" @keyup.enter="fetchData(true)" outlined dense hide-details></v-text-field>
            </div>
            <div class="w-150px">
                <v-select label="상태" v-model="search.status" @change="fetchData(true)" :items="mercury.base.util.concatArray(code.status,[{text:'전체',value:''}], false)" outlined dense hide-details></v-select>
            </div>
            <div class="w-150px" v-if="mercury.base.util.hasAnyRole(getUser.groups,['Admin','Trader'])">
                <v-select label="보기" v-model="search.view" @change="fetchData(true)" :items="[{text:'전체',value:''}, {text:'내가 생성',value:'mine'}, {text:'공유',value:'share'}]" outlined dense hide-details></v-select>
            </div>
            <div class="search-form-btns">
                <mp-button color="primary" label="조회" icon="mdi-magnify" @click="fetchData(true)"></mp-button>
                <mp-button color="info" label="새로입력" icon="mdi-plus" @click="newItem()" v-if="mercury.base.util.hasAnyRole(getUser.groups,['Admin','Trader'])"></mp-button>
                <template v-if="tbl.selected.length > 0">
                    <v-menu max-height="400px" min-width="250px">
                        <template v-slot:activator="{ on, attrs }">
                            <v-btn color="primary" v-bind="attrs" v-on="on" class="ml-1">
                                <v-icon>mdi-dots-vertical</v-icon>
                            </v-btn>
                        </template>
                        <v-list>
                            <v-list-item @click="resetMultiItem()">선택해제</v-list-item>
                            <v-divider></v-divider>
                            <v-list-item @click="copyMultiItem()">복사</v-list-item>
                            <v-list-item @click="activeMultiItem(true)">활성</v-list-item>
                            <v-list-item @click="activeMultiItem(false)">비활성</v-list-item>
                        </v-list>
                    </v-menu>
                </template>
            </div>
        </mp-search>

        <v-data-table v-model="tbl.selected" :show-select="tbl.show_select" dense fixed-header :height="$settings.datatable.rows20" :footer-props="$settings.datatable.footer20" :headers="tbl.headers" :items="tbl.items" :options.sync="tbl.options" :server-items-length="tbl.total">
            <template v-slot:item.client="{ item }">
                <div class="ellipsis"><div class="ellipsis" :title="'['+item.client+']'+item['client_name']">{{item['client_name']}}</div></div>
            </template>
            <template v-slot:item.name="{ item }">
                <div class="ellipsis"><a href="#" @click.stop.prevent="selectItem(item)" class="ellipsis" :title="item.name">{{ item.name }}</a></div>
            </template>
            <template v-slot:item.platform="{ item }">
                {{item.platform}} 
                <template v-if="item.platform==='DV360'">
                    <v-icon :title="item.report_type" v-if="item.report_type==='VIDEO'" color="red" style="vertical-align: text-top;font-size: 20px !important;">mdi-youtube</v-icon>
                    <v-icon :title="item.report_type" v-else color="blue" style="margin-left:2px;vertical-align: text-bottom">mdi-picture-in-picture-bottom-right</v-icon>                
                </template>
            </template>
            <template v-slot:item.status="{ item }">
                <v-icon :color="canEdit(item) ? 'green' : 'gray'" title="수정">mdi-file-document-edit</v-icon>
                <v-icon :color="canEmail(item) ? 'green' : 'gray'" title="이메일수신">mdi-email-check</v-icon>
                <v-chip :color="item.status === 'ACTIVE' ? 'green' : 'gray'" small>{{item['status_label']}}</v-chip>
            </template>
            <template v-slot:item.actions="{ item }">
                <mp-button mode="icon" color="green" label="다운로드" icon="mdi-file-excel" @click="downloadReport(item)"></mp-button>
                <mp-button mode="icon" color="green" label="사용자" icon="mdi-account-cog" @click="showReportGroupAuth(item)" v-if="mercury.base.util.hasAnyRole(getUser.groups,['Admin','Trader'])"></mp-button>
            </template>
        </v-data-table>

        <mp-view :view.sync="view">
            <template v-slot:body="{edit}">
                <v-form class="input-form" ref="form" v-model="form.valid">
                    <v-row>
                        <v-col cols="12" md="3">
                            <mp-advertiser-select label="광고주" v-model="form.data.client" @change="onChangeClient" :rules="mercury.base.rule.required" :readonly="edit"></mp-advertiser-select>
                        </v-col>
                        <v-col cols="12" md="3">
                            <mp-date-range-picker label="기간" :start.sync="form.data.start_date" :end.sync="form.data.end_date" shape="underline"></mp-date-range-picker>
                        </v-col>
                        <v-col cols="12" md="3">
                            <mp-number-field label="예산 (광고주 보고 기준)" :value.sync="form.data.budget"></mp-number-field>
                        </v-col>
                        <v-col cols="12" md="3">
                            <v-select label="VAT" v-model="form.data.vat" :items="[{text:'없음',value:0},{text:'적용',value:10}]" :rules="mercury.base.rule.number"></v-select>
                        </v-col>
                        <v-col cols="12" md="3">
                            <v-text-field label="레포트 그룹명" v-model="form.data.name" :rules="form.rules.report_group_name"></v-text-field>
                        </v-col>
                        <v-col cols="12" md="3">
                            <v-select label="상태" v-model="form.data.status" :items="code.status"></v-select>
                        </v-col>
                        <v-col cols="12" md="3">
                            <v-select label="KPI" v-model="form.data.kpi" :items="code.kpi"></v-select>
                        </v-col>
                        <v-col cols="12" md="3">
                            <v-text-field :label="form.data.kpi === 'ROAS' ? 'KPI Goal (%)': 'KPI Goal'" :value="form.data.kpi_goal | comma" @input="val => form.data.kpi_goal = val" :rules="mercury.base.rule.required" class="mp-number-field"></v-text-field>
                        </v-col>
                        <v-col cols="12" md="3">
                            <v-select label="통화" v-model="form.data.currency" :items="code.currency"></v-select>
                        </v-col>
                        <v-col cols="12" md="3">
                            <v-select label="거래방식" v-model="form.data.trade_type" :items="code.tradeType"></v-select>
                        </v-col>
                        <template v-if="form.data.id !== undefined && form.data.platform !== 'DV360'">
                            <v-col cols="12" md="2">
                                <v-text-field label="대행사 수수료 (%)" v-model="form.data.agency_commission" :rules="mercury.base.rule.number"></v-text-field>
                            </v-col>
                            <v-col cols="12" md="2">
                                <v-text-field label="머큐리 수수료 (%)" v-model="form.data.mercury_commission" :rules="mercury.base.rule.number"></v-text-field>
                            </v-col>
                            <v-col cols="12" md="2">
                                <v-text-field label="markup (%)" v-model="form.data.markup" :rules="mercury.base.rule.number"></v-text-field>
                            </v-col>
                        </template>
                        <template v-else>
                            <v-col cols="12" md="3">
                                <v-text-field label="대행사 수수료 (%)" v-model="form.data.agency_commission" :rules="mercury.base.rule.number"></v-text-field>
                            </v-col>
                            <v-col cols="12" md="3">
                                <v-text-field label="머큐리 수수료 (%)" v-model="form.data.mercury_commission" :rules="mercury.base.rule.number"></v-text-field>
                            </v-col>
                        </template>
                    </v-row>
                    <v-row>
                        <v-col cols="12" md="3">
                            <v-select label="Ad Unit 통합" v-model="form.data.ad_unit_integration" :items="code.adUnitIntegration"></v-select>
                        </v-col>
                        <v-col cols="12" md="3">
                            <v-select label="Ad Unit 요약" v-model="form.data.ad_unit_summary" :items="code.adUnitSummary"></v-select>
                        </v-col>
                        <v-col cols="12" md="3"></v-col>  <!-- 라인 맞출려고 -->
                        <v-col cols="12" md="3"></v-col>  <!-- 라인 맞출려고 -->
                    </v-row>
                    
                    <template v-if="form.data.client !== undefined">
                    <v-row>
                        <template v-if="form.data.id === undefined">
                            <template v-if="form.data.platform !== undefined">
                                <v-col cols="12" md="6">
                                    <v-select label="Platform" :items="form.group.dsp.platforms" :rules="mercury.base.rule.required" return-object item-value="platform_id" item-text="platform_name" @change="changePlatform">
                                        <template v-slot:item="data">
                                            <v-chip class="mr-2" x-small label :title="data.item.platform_id">{{ data.item.platform }}</v-chip> {{data.item.platform_name}}
                                        </template>
                                    </v-select>
                                </v-col>
                                <template v-if="form.data.platform_source === 'API'">
                                    <template v-if="form.data.platform === 'DV360'">
                                        <v-col cols="12" md="6">
                                            <v-select label="레포트 유형" v-model="form.data.report_type" :items="[{text:'DISPLAY',value:'DISPLAY'}, {text:'VIDEO',value:'VIDEO'}]" :rules="mercury.base.rule.required"></v-select>
                                        </v-col>
                                    </template>
                                    <template v-else>
                                        <v-col cols="12" md="6">
                                            <v-text-field label="markup" v-model="form.data.markup" :rules="mercury.base.rule.number"></v-text-field>
                                        </v-col>
                                    </template>
                                </template>
                                <template v-else-if="form.data.platform_source === 'SPREADSHEET'">
                                    <v-col cols="12" md="3">
                                        <v-text-field label="스프레드시트 GID" v-model="form.data.report_id" :rules="mercury.base.rule.required" readonly>
                                            <template v-slot:append-outer><v-btn icon small color="red" @click="showSpreadSheetInfo(form.data)"><v-icon>mdi-cog</v-icon></v-btn></template>
                                        </v-text-field>
                                    </v-col>
                                    <v-col cols="12" md="3">
                                        <v-text-field label="markup" v-model="form.data.markup" :rules="mercury.base.rule.number"></v-text-field>
                                    </v-col>
                                </template>
                            </template>
                            <template v-else>
                                <v-col cols="12" md="12">
                                    <v-select label="Platform" :items="form.group.dsp.platforms" :rules="mercury.base.rule.required" return-object item-value="platform_id" item-text="platform_name" @change="changePlatform">
                                        <template v-slot:item="data">
                                            <v-chip class="mr-2" x-small label :title="data.item.platform_id">{{ data.item.platform }}</v-chip> {{data.item.platform_name}}
                                        </template>
                                    </v-select>
                                </v-col>
                            </template>
                            <v-col cols="12" md="12">
                                <p class="text-center">레포트 설정은 기본 정보 저장 후 가능합니다.</p>
                            </v-col>
                        </template>
                        <template v-else>
                            <v-col cols="12" md="6">
                                <v-card>
                                    <v-card-title>
                                        <v-text-field :label="'Platform ('+form.data.platform+')'" readonly :value="getPlatformName(form.data.platform, form.data.platform_id, 'Platform')" :title="form.data.platform_id"></v-text-field>
                                        <template v-if="form.data.platform_source === 'API'">
                                            <v-text-field v-model="form.data.report_type" readonly>
                                                <template v-slot:append-outer>
                                                    <v-menu max-height="400px" min-width="250px">
                                                        <template v-slot:activator="{ on, attrs }">
                                                            <v-btn color="grey" icon small v-bind="attrs" v-on="on">
                                                                <v-icon>mdi-plus-circle</v-icon>
                                                            </v-btn>
                                                        </template>
                                                        <v-list>
                                                            <v-list-item @click="addAdUnit('ITEM')">ITEM</v-list-item>
                                                            <v-list-item @click="addAdUnit('LIKE')">LIKE</v-list-item>
                                                            <v-divider></v-divider>
                                                            <v-list-item @click="addAdUnit('GROUP')">GROUP</v-list-item>
                                                            <v-divider></v-divider>
                                                            <v-list-item @click="addAdUnit('UNIT')">UNIT</v-list-item>  
                                                            <v-list-item @click="addAdUnit('RAW')">RAW</v-list-item>
                                                        </v-list>
                                                    </v-menu>
                                                </template>
                                            </v-text-field>  
                                        </template>
                                        <template v-else-if="form.data.platform_source === 'SPREADSHEET'">
                                            <v-text-field label="Sheet Id" v-model="form.data.report_id" readonly>
                                                <template v-slot:append-outer>
                                                    <v-menu max-height="400px" min-width="250px">
                                                        <template v-slot:activator="{ on, attrs }">
                                                            <v-btn color="grey" icon small v-bind="attrs" v-on="on">
                                                                <v-icon>mdi-plus-circle</v-icon>
                                                            </v-btn>
                                                        </template>
                                                        <v-list>
                                                            <v-list-item @click="addAdUnit('UNIT')">UNIT</v-list-item>
                                                            <v-list-item @click="addAdUnit('RAW')">RAW</v-list-item>
                                                        </v-list>
                                                    </v-menu>
                                                </template>
                                            </v-text-field>
                                        </template>
                                        
                                    </v-card-title>
                                    <v-card-text>
                                        <draggable v-model="form.data.report_group_ad_unit" handle=".order-mover">
                                            <transition-group>
                                                <v-card class="mt-2 pa-2" :elevation="24" v-for="(item, idx) in form.data.report_group_ad_unit" :key="item.id">
                                                    <v-row>
                                                        <template v-if="item.search_type === 'RAW' || item.search_type === 'UNIT'">
                                                            <v-col cols="12" md="12">
                                                                <v-text-field label="유형" v-model="item.type" :rules="mercury.base.rule.required" readonly>
                                                                    <template v-slot:append-outer>
                                                                        <v-btn icon small color="red" @click="removeAdUnit(item, idx)"><v-icon>mdi-minus-circle</v-icon></v-btn>
                                                                    </template>
                                                                </v-text-field>
                                                            </v-col>
                                                        </template>
                                                        <template v-else>
                                                            <v-col cols="12" md="6">
                                                                <v-select label="유형" v-model="item.type" :rules="mercury.base.rule.required" :items="form.group.dsp.level[form.data.platform+platformReportType]" @change="changeAdUnit(item)">
                                                                    <template v-slot:prepend>
                                                                        <v-btn icon small color="grey" class="order-mover" title="드래그로 순서 변경"><v-icon>mdi-elevator</v-icon></v-btn>
                                                                    </template>
                                                                </v-select>
                                                            </v-col>
                                                            <v-col cols="12" md="6">
                                                                <v-text-field label="이름" v-model="item.name" :rules="mercury.base.rule.required" >
                                                                    <template v-slot:append-outer>
                                                                        <v-btn v-if="item.search_type === 'ITEM'" icon small color="green" @click="modifyAdUnitItem(item)" :disabled="item.type === undefined" title="ITEM"><v-icon>mdi-table-edit</v-icon></v-btn>
                                                                        <v-btn v-else-if="item.search_type === 'LIKE'" icon small color="green" @click="showLikeHelper(item)" :disabled="item.type === undefined" title="LIKE"><v-icon>mdi-contain</v-icon></v-btn>
                                                                        <v-btn icon small color="red" @click="removeAdUnit(item, idx)"><v-icon>mdi-minus-circle</v-icon></v-btn>
                                                                    </template>
                                                                </v-text-field>
                                                            </v-col>
                                                        </template>
                                                        
                                                        <template v-if="item.search_type === 'ITEM'">
                                                            <v-col cols="12" md="12">
                                                                <mp-base-chip-group :ad-unit="item"></mp-base-chip-group>
                                                            </v-col>
                                                        </template>
                                                        <template v-else-if="item.search_type === 'LIKE'">
                                                            <v-col cols="12" md="12">
                                                                <v-text-field label="AD ITEM 검색식" v-model="item.report_group_ad_unit_item[0].item_id" :rules="mercury.base.rule.required" readonly></v-text-field>
                                                            </v-col>
                                                        </template>
                                                        
                                                    </v-row>
                                                </v-card>
                                            </transition-group>
                                        </draggable>
                                    </v-card-text>
                                </v-card>
                            </v-col>
                            <v-col cols="12" md="6">
                                <v-card>
                                    <v-card-title>
                                        <v-select label="Conversion" v-model="form.group.conversion.selected" :items="form.group.conversion.platforms" return-object item-value="platform_id" item-text="platform_name" >
                                            <template v-slot:item="data">
                                                <v-chip class="mr-2" x-small label :title="data.item.platform_id">{{ data.item.platform }}</v-chip> {{data.item.platform_name}}
                                            </template>
                                            <template v-slot:append-outer>
                                                <v-btn color="grey" icon small @click="addConversionUnit"><v-icon>mdi-plus-circle</v-icon></v-btn>
                                            </template>
                                        </v-select>
                                    </v-card-title>
                                    <v-card-text>
                                        <v-card class="mt-2 pa-2" :elevation="24" v-for="(item, idx) in form.data.report_group_conversion_source" :key="item.id">
                                            <v-row>
                                                <template v-if="item.conversion_platform === 'CM'">
                                                    <v-col cols="12" md="6">
                                                        <v-text-field :label="'Conversion ('+item.conversion_platform+')'" :value="getPlatformName(item.conversion_platform, item.conversion_platform_id, 'Conversion')" readonly></v-text-field>
                                                    </v-col>
                                                    <v-col cols="12" md="6">
                                                        <v-select label="Report Type" v-model="item.report_type" :items="[{text:'STANDARD', value:'STANDARD'}, {text:'FLOODLIGHT', value:'FLOODLIGHT'}]">
                                                            <template v-slot:append-outer><v-btn icon small color="red" @click="removeConversionUnit(item, idx)"><v-icon>mdi-minus-circle</v-icon></v-btn></template>
                                                        </v-select>
                                                    </v-col>
                                                </template>
                                                <template v-else>
                                                    <v-col cols="12" md="12">
                                                        <v-text-field :label="'Conversion ('+item.conversion_platform+')'" :value="getPlatformName(item.conversion_platform, item.conversion_platform_id, 'Conversion')" readonly>
                                                            <template v-slot:append-outer><v-btn icon small color="red" @click="removeConversionUnit(item, idx)"><v-icon>mdi-minus-circle</v-icon></v-btn></template>      
                                                        </v-text-field>
                                                    </v-col>
                                                </template>
                                            </v-row>
                                        </v-card>
                                    </v-card-text>
                                </v-card>
                            </v-col>
                        </template>
                    </v-row>
                    
                    <v-row v-if="form.data.id !== undefined">
                        <v-col cols="12">
                            <v-card>
                                <v-card-title>
                                    <span class="headline">Report Column <small class="text-subtitle-2" style="color:#767070">컬럼 선택 및 위치 변경이 가능합니다.</small></span> 
                                    <v-spacer></v-spacer>
                                    <v-btn small color="green" class="ml-1" @click="modifyPlatformColumn()">
                                        <v-icon>mdi-table-edit mdi-24px</v-icon> PLATFORM
                                    </v-btn>
                                    <v-menu top left offset-y attach max-height="400px" min-width="250px" :close-on-content-click="false">
                                        <template v-slot:activator="{ on, attrs }">
                                            <v-btn small color="orange" v-bind="attrs" v-on="on" class="ml-1">
                                                <v-icon class="mr-1">mdi-calculator-variant mdi-24px</v-icon> CALC
                                            </v-btn>
                                        </template>
                                        <v-list>
                                            <v-list-item v-for="item in selectableCalcItems" :key="item.value">
                                                <template v-slot:default="{ active }">
                                                    <v-list-item-action>
                                                        <v-checkbox v-model="item.selected" @change="changeReportColumn(item,$event)"></v-checkbox>
                                                    </v-list-item-action>
                                                    <v-list-item-content>
                                                        <v-list-item-title>{{item.text}}</v-list-item-title>
                                                        <v-list-item-subtitle>{{item.value}}</v-list-item-subtitle>
                                                    </v-list-item-content>
                                                </template>
                                            </v-list-item>
                                        </v-list>
                                    </v-menu>
                                    <!--<v-btn icon small @click="modifyReportColumn"><v-icon>mdi-table-edit</v-icon></v-btn>-->
                                    <v-btn small color="red" class="ml-1" @click="modifyFormulaColumn(undefined)">
                                        <v-icon>mdi-sigma mdi-24px</v-icon> FORMULA
                                    </v-btn>
                                </v-card-title>
                               
                                <v-card-text>
                                    <draggable v-model="form.data.report_columns">
                                        <transition-group>
                                            <span v-for="(item, i) in form.data.report_columns" :key="item.name+'_'+i">
                                                <v-menu bottom right transition="scale-transition" origin="top left" @input="changeReportColumnMenu(item, $event)">
                                                    <template v-slot:activator="{ on }">
                                                        <v-chip small :color="getReportColumnColor(item)" :close="item.side=== 'DSP' || item.type === 'CALC' || item.type === 'FORMULA'" @click:close="removeReportColumn(item)" class="mr-1 mb-1">
                                                            <v-chip class="mr-1" x-small label v-if="item.side !== 'DSP' && item.type !== 'CALC' && item.type !== 'FORMULA'">{{getShortPlatformName(item)}}</v-chip>
                                                            <span v-bind="item.attrs" v-on="on"  :title="item.value">{{ item.text }}</span>
                                                        </v-chip>
                                                    </template>
                                                    <v-card width="300">
                                                        <v-list dark>
                                                            <v-list-item>
                                                                <v-list-item-avatar>{{getShortPlatformName(item)}}</v-list-item-avatar>
                                                                <v-list-item-content>
                                                                    <v-list-item-title class="ellipsis"><a href="#" class="ellipsis" @click.stop.prevent="modifyReportColumn(item)">
                                                                        <template v-if="item.is_edit_mode === true">
                                                                            <v-text-field v-model="item.text" dense hide-detail></v-text-field>
                                                                        </template>
                                                                        <template v-else>
                                                                            {{ item.text }}
                                                                        </template>
                                                                        </a></v-list-item-title>
                                                                    <v-list-item-subtitle>{{ item.value }}</v-list-item-subtitle>
                                                                </v-list-item-content>
                                                                <v-list-item-action>
                                                                    <v-btn icon>
                                                                        <v-icon>mdi-close-circle</v-icon>
                                                                    </v-btn>
                                                                </v-list-item-action>
                                                            </v-list-item>
                                                        </v-list>
                                                    </v-card>
                                                </v-menu>
                                            </span>
                                        </transition-group>
                                    </draggable>
                                </v-card-text>
                            </v-card>
                        </v-col>
                    </v-row>
                    </template>
                </v-form>
            </template>
            <template v-slot:control.prepend="{edit}">
                <template v-if="form.data.edit_mode !== 'copy'">
                    <mp-button mode="text" color="green" label="복사" icon="mdi-content-copy" @click="copyItem('MODAL')" v-if="canEdit(form.data)"></mp-button>
                </template>
            </template>
            <template v-slot:control="{edit}">
                <template v-if="edit">
                    <template v-if="form.data.edit_mode === 'copy'">
                        <mp-button mode="text" color="green" label="레포트 & 사용자 저장" icon="mdi-content-copy" @click="copyItem('SAVE', true)" v-if="canEdit(form.data)"></mp-button>
                        <mp-button mode="text" color="green" label="레포트만 저장" icon="mdi-content-copy" @click="copyItem('SAVE', false)" v-if="canEdit(form.data)"></mp-button>
                    </template>
                    <template v-else>
                        <mp-button mode="text" color="warning" label="저장" icon="mdi-file-document-edit" @click="saveItem('PUT')" v-if="canEdit(form.data)"></mp-button>
                        <mp-button mode="text" color="danger" label="삭제" icon="mdi-delete" @click="deleteItem(form.data)" v-if="canEdit(form.data)"></mp-button>
                    </template>
                </template>
                <template v-else>
                    <mp-button mode="text" color="warning" label="저장" icon="mdi-text-box-plus" @click="saveItem('POST')"></mp-button>
                    <mp-button mode="text" color="secondary" label="초기화" icon="mdi-refresh" @click="resetItem()"></mp-button>
                </template>
            </template>
        </mp-view>
        
        <mp-dialog :dialog="dialog.auth"></mp-dialog>
        <mp-dialog :dialog="dialog.column_platform"></mp-dialog>
        <mp-dialog :dialog="dialog.formula"></mp-dialog>
        <mp-dialog :dialog="dialog.units"></mp-dialog>
    
        <mp-dialog :dialog="dialog.units_group"></mp-dialog>
        <mp-dialog :dialog="dialog.like"></mp-dialog>
        <mp-dialog :dialog="dialog.multi_copy"></mp-dialog>
        <mp-dialog :dialog="dialog.spreadsheet_list"></mp-dialog>
    
    </v-card>
    `,
    props: {},
    data: function () {
        return {
            url: '/LittleJoe/api/reportgroups/',
            search: {
                client: undefined,
                platform: undefined,
                name: '',
                status: undefined,
                view: '',
            },
            view: {
                width: '100%',
                mode:'dialog',
                title: 'ReportGroup'
            },
            tbl: {
                headers: [
                    {text: 'id', value: 'id', width: 60, align: 'right'},
                    {text: '광고주', value: 'client', width: 110},
                    {text: '플랫폼', value: 'platform', width: 140},
                    {text: '레포트 그룹명', value: 'name', width: 200},
                    {text: '시작일', value: 'start_date', width: 90, align: 'center'},
                    {text: '종료일', value: 'end_date', width: 90, align: 'center'},
                    {text: '상태', value: 'status', width: 120},
                    {text: '수정자', value: 'user_name', width: 120, align: 'center'},
                    {text: '수정일시', value: 'updated_at', width: 160, align: 'center'},
                    {text: '', value: 'actions', width: 60, align: 'center', sortable: false}
                ],
                selected: [],
                show_select: false
            },
            form: {
                justCreatedItemId: undefined,    //이게 값이 있는경우 재 조회후 바로 해당 아이템을 선택
                platform_name_map: {},
                init_report_group_ad_unit:{
                    id: undefined,
                    name: undefined,
                    type: undefined,
                    search_type: 'ITEM',
                    report_group_id: undefined,
                    report_group_ad_unit_item: []
                },
                init: {
                    id: undefined,
                    client: undefined,
                    platform: undefined,
                    platform_source: 'API',
                    platform_id: undefined,
                    name: undefined,
                    start_date: moment().format('YYYY-MM-DD'),
                    end_date: moment().add(1,'months').format('YYYY-MM-DD'),
                    currency: 'KRW',
                    budget: 0,
                    vat: 0,
                    markup: 0,
                    mercury_commission: 0,
                    agency_commission: 0,
                    kpi: 'ETC',
                    kpi_goal: 'NONE',
                    trade_type: 'NET',
                    status: 'ACTIVE',
                    ad_unit_integration: 'SEPARATION',
                    ad_unit_summary: 'NONE',

                    report_columns: [],
                    report_type: undefined,
                    report_id: undefined,   // spreadsheet 기반인 경우 gid

                    report_group_ad_unit: [],
                    report_group_conversion_source: []
                },

                group: this.mercury.base.skylab.getGroup(),
                rules: {
                    report_group_name: [
                        name => (name !== undefined && name.length > 0) || '필수 입력 항목입니다.',
                        name => (name !== undefined && name.length < 29) || '이름은 29자를 초과 할 수 없습니다.',
                        name => !(/[\[\]/:*?]/.test(name)) || '[]:*?/ 문자는 포함될 수 없습니다.',
                    ]
                }
            },
            code: {
                platform: mercury.base.code['CODES']['API_Platform'],
                status: mercury.base.code['CODES']["API_StatusShort"],
                currency: mercury.base.code['CODES']["LittleJoe_Currency"],
                tradeType: mercury.base.code['CODES']["LittleJoe_TradeType"],
                kpi: mercury.base.code['CODES']["API_Kpi"],
                adUnitIntegration: mercury.base.code['CODES']["LittleJoe_AdUnitIntegration"],
                adUnitSummary: mercury.base.code['CODES']["LittleJoe_AdUnitSummary"],
            },
            dialog: {
                auth: {
                    title: '레포트 유저 권한 관리',
                    visible: false,
                    component: MpReportGroupAuth,
                    props: {report: {}},
                    close: () => {this.dialog.auth.visible = false;}
                },
                multi_copy: {
                    title: 'Report 복사',
                    visible: false,
                    width: '100%',
                    component: MpReportGroupMultiCopy,
                    props: {},
                    refresh: ()=> {
                        this.tbl.selected.splice(0)
                        this.fetchData(true)
                    },
                    close: () => {
                        this.dialog.multi_copy.visible = false;
                    },
                    actions:{
                        right: [
                            {
                                name:'저장',
                                color: 'danger',
                                method: 'saveItem'
                            }
                        ]
                    }
                },
                column_platform: {
                    title: 'Platform Column',
                    visible: false,
                    width: '100%',
                    component: MpReportGroupColumnPlatform,
                    props: {form: this.form, column: []},
                    close: () => {
                        this.dialog.column_platform.visible = false;
                    },
                    actions: {
                        right: [
                            {
                                name: '저장',
                                color: 'warning',
                                method: 'saveItem'
                            }
                        ]
                    }
                },
                formula: {
                    title: 'Formula Column',
                    visible: false,
                    width: '100%',
                    component: MpReportGroupFormula,
                    props: {form: this.form, column: []},
                    close: () => {
                        this.dialog.formula.visible = false;
                    },
                    actions: {
                        right: [
                            {
                                name: '저장',
                                color: 'warning',
                                method: 'saveItem'
                            }
                        ]
                    }
                },
                units: {
                    title: 'ITEM 선택',
                    visible: false,
                    width: '100%',
                    component: MpReportGroupUnits,
                    props: {form: this.form},
                    close: () => {
                        this.dialog.units.visible = false;
                    }
                },
                units_group: {
                    title: '그룹으로 등록',
                    visible: false,
                    width: '100%',
                    component: MpReportGroupUnitsGroup,
                    props: {form: this.form},
                    close: () => {
                        this.dialog.units_group.visible = false;
                    }
                },
                like: {
                    title: 'LIKE 선택',
                    visible: false,
                    width: '100%',
                    component: MpReportGroupUnitsLike,
                    props: {form: this.form},
                    close: () => {
                        this.dialog.like.visible = false;
                    }
                },
                spreadsheet_list: {
                    title: 'SPREADSHEET GID 선택',
                    visible: false,
                    width: '100%',
                    component: MpReportSpreadSheetList,
                    props: {form: this.form},
                    close: () => {
                        this.dialog.spreadsheet_list.visible = false;
                    }
                }
            }
        }
    },
    computed: {
        platformReportType: function(){
            let report_type = this.form.data.report_type;
            if(report_type === undefined || report_type === null){
                report_type = '';
            }else{
                report_type = '_'+report_type
            }

            return report_type;
        },

        selectableCalcItems: function(){
            const rc_map = {};
            this.form.data.report_columns.filter(rc=> rc.type === 'CALC').forEach(item=>{
                rc_map[item.type+'_'+item.value] = item;
            });

            this.form.group.custom.columns['CALC'].forEach(item=>{
                item['platform'] = this.form.data.platform;
                this.$set(item, 'selected', rc_map[item.type+'_'+item.value] !== undefined)
            });

            return this.form.group.custom.columns['CALC'];
        },
        ...baseHelper.mapGetters([
            'getUser',
            'getClients',
        ])
    },
    watch: {

    },
    created(){
        try{
            //새로고침시 search 값 복구하기
            Object.assign(this.search, this.mercury.base.util.getLocalStorage('MpReportGroup_search'))
        }catch(e){

        }

        this.tbl.show_select = this.canEdit(this.form.data)
    },
    methods: {
        canEdit(item) {
            return this.getUser['is_superuser'] || this.getUser['id'] === item['created_by'] ||
                mercury.base.util.hasAnyRole(this.getUser.groups,['Admin']) || item.is_edit === true;
        },
        canDelete(item) {
            console.log(this.getUser['is_superuser'] || this.getUser['id'] === item['created_by'])
            return this.getUser['is_superuser'] || this.getUser['id'] === item['created_by'];
        },
        canEmail(item) {
            return item.is_email === true
        },
        getPlatformName(platform, platform_id, from){
            const platformInfo = this.form.platform_name_map[platform+'_'+platform_id];
            if(platformInfo !== undefined){
                return platformInfo.platform_name;
            }
            return platform
        },
        changePlatform(item) {
            this.form.data.platform = item.platform;
            this.form.data.platform_id = item.platform_id;
            this.form.data.platform_source = item.platform_source;
        },
        downloadReport(item) {
            this.setOverlay(true);
            this.mercury.base.lib.download({
                url: this.url + 'excel/?id=' + item.id,
                method: 'GET'
            }).finally(() => {
                this.setOverlay(false);
            });
        },
        getClientPlatform(client_id, report_group_id){
            return this.xAjax({
                url: '/LittleJoe/api/clientsplatforms/?client_id='+this.form.data.client
            }).then(resp=>{

                const reportDspPlatform = this.form.data.platform;
                const reportDspPlatformId = this.form.data.platform_id;

                const platforms = [];
                const conversions =[];
                resp.forEach(item=>{
                    const itemPlatform = item['platform'];
                    const itemPlatformId = item['platform_id'];
                    const platform = {
                        report_type: 'STANDARD',
                        platform: itemPlatform,
                        platform_id: itemPlatformId,
                        platform_name: item['platform_name'],
                        platform_source: item['platform_source'],
                        report_group_id: report_group_id
                    }

                    const key = item.platform+'_'+itemPlatformId;
                    this.form.platform_name_map[key] = platform;

                    if(itemPlatform === 'CM' || itemPlatform === 'GA'){
                        conversions.push(platform);
                    }else{
                        platforms.push(platform);

                        //선택한 DSP와 같은 platform인경우 자체 Conversion
                        if(reportDspPlatform === itemPlatform && reportDspPlatformId === itemPlatformId){
                            conversions.push(platform);
                        }
                    }
                });

                if(reportDspPlatform === 'SA360'){    //SA360의 경우 CM 전환은 보지 않는다.
                    const idx = conversions.findIndex(item=>{
                        return item['platform'] === 'CM'
                    });

                    if( idx > -1){
                        conversions.splice(idx, 1);
                    }
                }

                this.form.group.dsp.platforms = platforms;
                this.form.group.conversion.platforms = conversions;
            })
        },
        onChangeClient(client_id, report_group_id){
            this.form.data.platform = undefined
            this.form.data.platform_id = undefined
            return this.getClientPlatform(client_id, report_group_id)
        },
        onCrudMixInActions(actionName, actionItem, actionOptions) {
            if( actionName === 'fetchData'){
                if(this.form.justCreatedItemId !== undefined){
                    const idx = this.tbl.items.findIndex(item=>item.id === this.form.justCreatedItemId);
                    this.form.justCreatedItemId = undefined;
                    if(idx>-1){
                        this.selectItem(this.tbl.items[idx]);
                    }
                }

                this.mercury.base.util.setLocalStorage('MpReportGroup_search', this.search)
            }else if( actionName === 'beforeSaveItem'){
                if(this.form.data.id !== undefined){    //최초 생성이 아니라 수정인 경우
                    const report_group_ad_unit =  actionItem.report_group_ad_unit;
                    const report_group_ad_unit_length = report_group_ad_unit.length
                    if (report_group_ad_unit_length === 0){
                        this.mercury.base.lib.notify({message:'Platform에 최소 1개의 Ad Unit은 필수 입니다.', type:'error'});
                        return false;
                    }else if(report_group_ad_unit_length > 0){
                        const ad_unit_item = {type:'D', value:'ad_unit_name', text: 'ad_unit_name', side: 'DSP', platform: this.form.data.platform}
                        const idx = actionItem.report_columns.findIndex(column=>{//actionItem 에서도 제거
                            return column.type === ad_unit_item.type && column.value === ad_unit_item.value;
                        });
    
                        //UNIT OR RAW는 무조건 1개밖에 없다.
                        if(report_group_ad_unit[0].search_type === 'UNIT' || report_group_ad_unit[0].search_type === 'RAW'){
                            if(idx > -1){
                                this.removeReportColumn(ad_unit_item);  //UNIT OR RAW 인경우 ad_unit_name 제거
                                this.mercury.base.lib.notify({message:'Report Column의 ad_unit_name은 RAW 또는 UNIT 타입을 지원하지 않아 삭제되었습니다.', type:'warning'});
                                actionItem.report_columns.splice(idx, 1)
                            }
                        }else{
                            if(idx === -1){
                                this.mercury.base.lib.notify({message:'Report Column 정의에 ad_unit_name 이 없습니다.', type:'warning'});
                            }
                        }
                    }
                }
                
                

                actionItem['budget'] = String(actionItem['budget']).replace(/,/g,'')
                return true
            }else if( actionName === 'saveItem'){
                this.mercury.base.lib.notify(actionItem);
                this.form.justCreatedItemId = actionItem['id'];
                this.fetchData(actionOptions.method === 'POST');
                return false;
            }else if( actionName === 'newItem'){
                this.view.title = 'ReportGroup 생성'
                this.form.group.dsp.platforms.splice(0);
                this.form.group.conversion.platforms.splice(0);

            }else if( actionName === 'beforeSelectItem'){
                if(typeof actionItem.report_columns === 'string'){
                    actionItem.origin_report_columns = JSON.parse(actionItem.report_columns);
                    actionItem.report_columns = JSON.parse(actionItem.report_columns);
                }
            }else if( actionName === 'selectItem'){
                this.view.title = '[수정]'+this.form.data.name;
                this.setOverlay(true);
                this.getClientPlatform(this.form.data.client, this.form.data.id).then(()=>{
                    return this.xAjax({
                        url:this.url + 'report/',
                        data: {
                            id: actionItem['id'],
                            report_type: actionItem['report_type'],
                            platform: actionItem['platform'],
                            platform_id: actionItem['platform_id'],
                            client: actionItem['client'],
                        }
                    })
                }).then(resp=>{
                    // ITEM 선택을 열때만 사용 (MpReportGroupUnits, MpReportGroupUnitsGroup)
                    this.form.group.dsp.items = Object.assign({}, resp['client_platform_ad_unit'])
                    this.form.group.dsp.columns = resp['report_columns']
                    // FORMULA 열때만 사용 (MpReportGroupFormula)
                    this.form.group.conversion.items = Object.assign({}, resp['client_platform_conversion'])

                    //방어코딩 LIKE 저장시 에러가 나는경우
                    resp['report_group_ad_unit'].forEach(item=>{
                        if(item.search_type === 'LIKE' && item.report_group_ad_unit_item.length === 0){
                            item.report_group_ad_unit_item.push({'item_id': undefined})
                        }
                    })

                    this.form.data.report_group_ad_unit = resp['report_group_ad_unit']
                    this.form.data.report_group_conversion_source = resp['report_group_conversion_source']

                    if(this.form.data.report_columns.length === 0){
                        this.form.data.report_columns = []
                        const defaultValues = ['date', 'ad_unit_name', 'revenue', 'spend', 'cost', 'impressions', 'clicks' ]
                        let defaultDspReportColumns = this.form.group.dsp.columns.filter(item=>{
                            return defaultValues.includes(item.value)
                        });

                        this.form.data.report_columns.push(...defaultDspReportColumns)
                    }else{
                        this.$nextTick(()=>{
                            this.form.data.report_columns = mercury.base.util.deepCopy(this.form.data.origin_report_columns)
                        });
                    }
                }).finally(() => {
                    this.setOverlay(false);
                });
            }
        },

        addAdUnit(searchType) {
            if (this.form.data.platform === undefined || this.form.data.platform === ''){
                this.mercury.base.lib.notify({message:'DSP 플랫폼을 선택해주세요.', type:'warning'});
                return;
            }

            const rowTypeIdx = this.form.data.report_group_ad_unit.findIndex(unit=>{
                return unit.search_type === 'RAW' || unit.search_type === 'UNIT'
            })
            if(rowTypeIdx>-1){
                const currentSearchType = this.form.data.report_group_ad_unit[0].search_type
                if(searchType !== 'RAW' && searchType !== 'UNIT') {
                    this.form.data.report_group_ad_unit.splice(rowTypeIdx, 1)   //등록된 RAW 를 삭제
                }else{
                    if(currentSearchType === searchType){
                        this.mercury.base.lib.notify({message:'이미 '+searchType+' TYPE AD UNIT은 등록되어 있습니다.', type:'warning'});
                        return;
                    }else{
                        this.form.data.report_group_ad_unit.splice(rowTypeIdx, 1)   //등록된 RAW 를 삭제
                    }
                }
            }

            const default_ad_unit = this.mercury.base.util.deepCopy(this.form.init_report_group_ad_unit);
            default_ad_unit['search_type'] = searchType;
            default_ad_unit['id'] = new Date().getTime();
            default_ad_unit['report_group_id'] = this.form.data.id;

            if(searchType === 'ITEM'){
                this.form.data.report_group_ad_unit.push(default_ad_unit)
            }else if(searchType === 'LIKE'){
                default_ad_unit.report_group_ad_unit_item.push({
                    'item_id': undefined
                })
                this.form.data.report_group_ad_unit.push(default_ad_unit)
            }else if(searchType === 'GROUP'){
                this.mercury.base.lib.confirm('그룹 등록은 등록된 모든 Ad Unit 을 삭제 하고 새로 선택합니다. 계속 하시겠습니까?').then(result => {
                    if (result.isConfirmed) {
                        this.form.data.report_group_ad_unit.splice(0)
                        const dialog = this.dialog.units_group;
                        dialog.title = 'Unit 그룹으로 등록';
                        dialog.props.form = this.form;
                        dialog.props.unit = default_ad_unit
                        dialog.visible = true;
                    }
                });
            }else if(searchType === 'RAW' || searchType === 'UNIT') {
                this.mercury.base.lib.confirm(searchType + ' 데이터 타입은 등록된 모든 Ad Unit 을 삭제합니다. 계속 하시겠습니까?').then(result => {
                    if (result.isConfirmed) {
                        default_ad_unit['type'] = searchType
                        default_ad_unit['name'] = searchType
                        this.form.data.report_group_ad_unit.splice(0)          //기존에 등록된걸 모두 지움
                        this.form.data.report_group_ad_unit.push(default_ad_unit)
                    }
                });
            }else {
                this.form.data.report_group_ad_unit.push(default_ad_unit)
            }
        },
        removeAdUnit(adUnit, index){
            this.mercury.base.lib.confirm((index+1)+' 번째 AdUnit ' +(adUnit.name || '')+ '을 삭제 하시겠습니까?').then(result => {
                if (result.isConfirmed) {
                    this.form.data.report_group_ad_unit.splice(index,1)
                }
            });
        },
        changeAdUnit(adUnit){
            if(adUnit.search_type === 'ITEM'){
                adUnit.report_group_ad_unit_item.splice(0);
            }else if(adUnit.search_type === 'UNIT'){
                adUnit.name = adUnit.type; //이름은 따로 입력하지 않고 type으로 대체
            }//else LIKE 일때는 아무것도 하지 않음
        },
        addConversionUnit() {
            const conversionSelect = this.form.group.conversion.selected;
            if (conversionSelect['platform_id'] === undefined){
                this.mercury.base.lib.notify({message:'Conversion 항목을 선택 후 추가해주세요.', type:'warning'});
                return;
            }

            const existIdx = this.form.data.report_group_conversion_source.findIndex(item=>{
                return item.conversion_platform === conversionSelect.platform && item.report_type === conversionSelect.report_type
            })
            if(existIdx>-1){
                this.mercury.base.lib.notify({message: conversionSelect['platform'] + '는 이미 선택되어 있습니다.', type:'warning'});
                return;
            }

            this.form.data.report_group_conversion_source.push({
                id: new Date().getTime(),
                conversion_platform_id: conversionSelect['platform_id'],
                conversion_platform: conversionSelect['platform'],
                report_type: conversionSelect['report_type'],
                report_group_id: this.form.data.id
            })
        },
        removeConversionUnit(conversionUnit, idx) {
            this.form.data.report_group_conversion_source.splice(idx,1)
        },
        changeReportColumnMenu(item, value){
            if(value === false && item['is_edit_mode']){
                if(item['text'] === ''){        //값이 공백인경우 value 로 세팅.
                    item['text'] = item['value']
                }
                item['is_edit_mode'] = false;
                delete item['is_edit_mode'];    //modifyReportColumn 에서 추가한 값 삭제
            }
        },
        modifyReportColumn(item){
            if(item.type === 'FORMULA'){
                this.modifyFormulaColumn(item)
            }else{
                this.$set(item,'is_edit_mode', true)
            }
        },
        modifyPlatformColumn(item){
            const dialog = this.dialog.column_platform;
            dialog.props.form = this.form;
            dialog.props.columns = this.form.group.dsp.columns
            dialog.visible = true;
        },
        modifyFormulaColumn(item){
            const dialog = this.dialog.formula;
            dialog.props.form = this.form;
            dialog.props.column = item
            dialog.visible = true;
        },
        modifyAdUnitItem(item){
            const dialog = this.dialog.units;
            dialog.title = '['+item.type+'] ' + (item.name || '') + ' ITEM 선택';
            dialog.props.form = this.form;
            dialog.props.unit = item
            dialog.visible = true;
        },
        showLikeHelper(item){
            const dialog = this.dialog.like;
            dialog.title = '['+item.type+'] ' + (item.name || '') + ' LIKE 선택';
            dialog.props.form = this.form;
            dialog.props.unit = item
            dialog.visible = true;
        },
        showSpreadSheetInfo(){
            const dialog = this.dialog.spreadsheet_list;
            dialog.props.form = this.form;
            dialog.visible = true;
        },
        copyMultiItem(){
            const dialog = this.dialog.multi_copy;
            dialog.props.reports = this.tbl.selected;
            dialog.visible = true;
        },
        activeMultiItem(isActive){
            const msg = '선택된 '+(this.tbl.selected.length)+'개의 리포트를 '+(isActive ? '활성화' : '비활성화') + ' 하시겠습니까?'
            this.mercury.base.lib.confirm(msg).then(result => {
                if (result.isConfirmed) {
                    const ids = []
                    this.tbl.selected.forEach(item=>{
                        ids.push(item.id)
                    });

                    this.xAjaxJson({
                        url: this.url + 'active/',
                        method: 'PATCH',
                        data: {
                            active: isActive,
                            ids: ids
                        }
                    }).then((resp)=>{
                        this.mercury.base.lib.notify(resp);
                        this.resetMultiItem();
                        this.fetchData(false);
                    });
                }
            });
        },
        resetMultiItem(){
            this.tbl.selected.splice(0)
        },
        copyItem(action, withUserSetting){
            if(action === 'MODAL'){
                this.mercury.base.lib.confirm('['+this.form.data.name + '] 레포트를 복사 하시겠습니까?').then(result => {
                    if (result.isConfirmed) {
                        const copyFormData = this.mercury.base.util.deepCopy(this.form.data)
                        copyFormData['name'] = copyFormData['name'] +' [복사]'
                        copyFormData['edit_mode'] = 'copy'
                        copyFormData['origin_id'] = this.form.data.id

                        this.form.data = Object.assign({}, this.mercury.base.util.deepCopy(this.form.init), copyFormData);
                        this.view.edit = true;
                        this.view.show = true;
                        this.view.title = this.form.data.name+ ' 복사하기'
                    }
                });
            }else if(action === 'SAVE'){
                const data = this.mercury.base.util.deepCopy(this.form.data);
                data['id'] = undefined; // 새 아이템으로 저장하기 위해
                data['created_by'] = undefined;
                data['created_at'] = undefined;
                data['updated_at'] = undefined;

                this.xAjaxJson({
                    url: this.url,
                    method: 'POST',
                    data: data
                }).then(resp=>{
                    this.form.data.id = resp['id'];
                    data['id'] = resp['id'];

                    return this.xAjaxJson({
                        url: this.url+ data.id+'/',
                        method: 'PUT',
                        data: data
                    })
                }).then(resp=>{
                    if(withUserSetting === true){
                        this.xAjaxJson({
                            url: '/LittleJoe/api/reportgroupauths/copy_auth/',
                            method: 'POST',
                            data: {from_report_group_id: this.form.data.origin_id, to_report_group_id: this.form.data.id}
                        }).then(()=>{
                            this.mercury.base.lib.notify(resp);
                            this.fetchData(true);
                        })
                    }else{
                        this.mercury.base.lib.notify(resp);
                        this.fetchData(true);
                    }
                });
            }
        },
        showReportGroupAuth(item){
            const dialog = this.dialog.auth;
            dialog.title = item.name + ' 레포트 유저 관리';
            dialog.props.report = item;
            dialog.visible = true;
        },

        changeReportColumn(item, checked){
            if(checked){
                this.form.data.report_columns.push(item)
            }else{
                this.removeReportColumn(item)
            }
        },
        removeReportColumn(item){
            const idx = this.form.data.report_columns.findIndex(column=>{
                return column.type === item.type && column.value === item.value;
            });
            if(idx > -1){
                this.form.data.report_columns.splice(idx,1)
            }
        },
        getReportColumnColor(item){
            if(item.side === 'DSP'){
                if(item.type === 'CALC'){ //calculation
                    return 'orange'
                }else if (item.type === 'D'){ //dimensions
                    return 'blue'
                }

                return 'green' //metrics
            }else if(item.type === 'FORMULA'){
                return 'red'
            }else { //CONVERSION= CM, GA, 등등
                return 'grey'
            }
        },
        getShortPlatformName(item){
            return mercury.base.skylab.getShortPlatformName(item.type)
        },

        ...baseHelper.mapActions([
            "setOverlay"
        ])
    }
};

export default MpReportGroup;