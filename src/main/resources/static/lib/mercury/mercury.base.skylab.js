/**
 * skylab project 전용 util
 *
 * [문서화를 위한 주석 규칙은](http://yui.github.io/yuidoc/syntax/index.html)
 * */
(function (mercury) {
    window.mercury = mercury;
    mercury.base = mercury.base || {};

    const UNITS = {
        LEVEL: {
            DV360_DISPLAY: ['CAMPAIGN', 'INSERTION_ORDER', 'LINE_ITEM', 'CREATIVE'],
            DV360_VIDEO: ['CAMPAIGN', 'INSERTION_ORDER', 'LINE_ITEM', 'AD_GROUP', 'AD'],
            CM: ['CAMPAIGN', 'PLACEMENT', 'AD', 'CREATIVE'],
            SA360: ['CAMPAIGN', 'AD_GROUP', 'AD'],
            FACEBOOK: ['CAMPAIGN', 'AD_GROUP', 'AD'],
            GADS: ['CAMPAIGN', 'AD_GROUP', 'AD'],
            TIKTOK: ['CAMPAIGN', 'AD_GROUP', 'AD'],
            NAVER_GFA: ['CAMPAIGN', 'AD_GROUP', 'AD'],
            NAVER_SEARCH: ['CAMPAIGN', 'AD_GROUP', 'AD'],
            NAVER_BRAND: ['CAMPAIGN', 'AD'],
            KAKAO_MOMENT: ['CAMPAIGN', 'AD_GROUP', 'AD'],
            KAKAO_KEYWORD: ['CAMPAIGN', 'AD_GROUP'],
            CRITEO: ['CAMPAIGN', 'AD_GROUP', 'AD'],
            SMR: ['CAMPAIGN', 'CREATIVE'],
            ZAPPLE: ['CAMPAIGN', 'AD_GROUP', 'AD'],
        },
    }

    const self = mercury.base.skylab = {
        GROUP : {
            custom: {
                columns: {
                    CALC:[
                        {side: 'DSP', platform:'' , type:'CALC', value:'CPC'  ,text:'CPC'},
                        {side: 'DSP', platform:'' , type:'CALC', value:'CPM'  ,text:'CPM'},
                        {side: 'DSP', platform:'' , type:'CALC', value:'CPV'  ,text:'CPV'},
                        {side: 'DSP', platform:'' , type:'CALC', value:'VTR'  ,text:'VTR'},
                    ]
                },
                fields: {
                    CALC: [{value:'value', text:'value'}],
                },
                selected:[]
            },
            dsp: {
                platforms: [],
                items: {},
                level: {
                    DV360_DISPLAY: UNITS['LEVEL']['DV360_DISPLAY'],
                    DV360_VIDEO: UNITS['LEVEL']['DV360_VIDEO'],
                    SA360: UNITS['LEVEL']['SA360'],
                    FACEBOOK: UNITS['LEVEL']['FACEBOOK'],
                    GADS: UNITS['LEVEL']['GADS'],
                    TIKTOK: UNITS['LEVEL']['TIKTOK'],
                    NAVER_GFA: UNITS['LEVEL']['NAVER_GFA'],
                    NAVER_SEARCH: UNITS['LEVEL']['NAVER_SEARCH'],
                    NAVER_BRAND: UNITS['LEVEL']['NAVER_BRAND'],
                    KAKAO_MOMENT: UNITS['LEVEL']['KAKAO_MOMENT'],
                    KAKAO_KEYWORD: UNITS['LEVEL']['KAKAO_KEYWORD'],
                    CRITEO: UNITS['LEVEL']['CRITEO'],
                    SMR: UNITS['LEVEL']['SMR'],
                    ZAPPLE: UNITS['LEVEL']['ZAPPLE'],
                },
                columns: [],
                fields: {
                    DV360_DISPLAY: [{value:'value', text:'value'}],
                    DV360_VIDEO: [{value:'value', text:'value'}],
                    SA360: [{value:'value', text:'value'}],
                    FACEBOOK: [{value:'value', text:'value'}],
                    GADS: [{value:'value', text:'value'}],
                    TIKTOK: [{value:'value', text:'value'}],
                    NAVER_GFA: [{value:'value', text:'value'}],
                    NAVER_SEARCH: [{value:'value', text:'value'}],
                    NAVER_BRAND: [{value:'value', text:'value'}],
                    KAKAO_MOMENT: [{value:'value', text:'value'}],
                    KAKAO_KEYWORD: [{value:'value', text:'value'}],
                    CRITEO: [{value:'value', text:'value'}],
                    ZAPPLE: [{value:'value', text:'value'}]
                }
            },
            conversion: {
                selected: [],
                platforms: [],
                items: {},
                fields:{
                    DV360_DISPLAY: [
                        {value:'post_click_conversions', text:'post_click_conversions'},
                        {value:'cm_post_click_revenue', text:'cm_post_click_revenue'},
                        {value:'post_view_conversions', text:'post_view_conversions'},
                        {value:'cm_post_view_revenue', text:'cm_post_view_revenue'},
                        {value:'total_conversions', text:'total_conversions'}
                    ],
                    DV360_VIDEO: [
                        {value:'conversions', text:'conversions'},
                        {value:'view_through_conversion', text:'view_through_conversion'},
                        {value:'total_conversion_value', text:'total_conversion_value'},
                    ],
                    CM: [
                        {value:'click_through_conversions',text:'click_through_conversions'},
                        {value:'click_through_revenue',text:'click_through_revenue'},
                        {value:'view_through_conversions',text:'view_through_conversions'},
                        {value:'view_through_revenue',text:'view_through_revenue'},
                        {value:'total_conversions',text:'total_conversions'},
                        {value:'total_revenue',text:'total_revenue'}
                    ],
                    GA: [
                        {value:'value', text:'value'}
                    ],
                    GA_EVENT: [
                        {value:'total_events', text:'total_events'},
                        {value:'unique_events', text:'unique_events'},
                        {value:'event_value', text:'event_value'},
                        // {value:'avg_event_value', text:'avg_event_value'},
                        // {value:'sessions_with_event', text:'sessions_with_event'},
                        // {value:'events_per_session_with_event', text:'events_per_session_with_event'},
                    ],
                    FACEBOOK: [
                        {value:'value', text:'value'},
                        {value:'total', text:'total'}
                    ],
                    GADS: [
                        {value:'conversions_value', text:'conversions_value'},
                        {value:'conversions', text:'conversions'},
                    ],
                    SA360: [
                        {value:'value', text:'value'}
                    ],
                    TIKTOK: [
                        {value:'value', text:'value'}
                    ],
                    TIKTOK_SELF: [
                        {value:'value', text:'value'}
                    ],
                    NAVER_GFA: [
                        {value:'value', text:'value'}
                    ],
                    NAVER_GFA_SELF: [
                        {value:'value', text:'value'}
                    ],
                    NAVER_SEARCH: [
                        {value:'value', text:'value'}
                    ],
                    NAVER_SEARCH_SELF: [
                        {value:'value', text:'value'}
                    ],
                    NAVER_BRAND: [
                        {value:'value', text:'value'}
                    ],
                    NAVER_BRAND_SELF: [
                        {value:'value', text:'value'}
                    ],
                    KAKAO_MOMENT: [
                        {value:'value', text:'value'}
                    ],
                    KAKAO_MOMENT_SELF: [
                        {value:'value', text:'value'}
                    ],
                    KAKAO_KEYWORD: [
                        {value:'value', text:'value'}
                    ],
                    KAKAO_KEYWORD_SELF: [
                        {value:'value', text:'value'}
                    ],
                    CRITEO: [
                        {value:'value', text:'value'}
                    ],
                    CRITEO_SELF: [
                        {value:'value', text:'value'}
                    ],
                },
            }
        },

        getGroup: function (){
            return mercury.base.util.deepCopy(self.GROUP)
        },

        getUnitLevel: function(){
            return mercury.base.util.deepCopy(UNITS.LEVEL)
        },
        getUnitLevelWithKeyword: function(){
            const result = mercury.base.util.deepCopy(UNITS.LEVEL)
            for(let platform in result){
                if(Object.prototype.hasOwnProperty.call(result, platform)){
                    if(platform === 'SA360' || platform === 'KAKAO_KEYWORD' || platform==='NAVER_SEARCH'){
                        result[platform].push('KEYWORD')
                    }
                }
            }

            return result
        },

        /**
         * @description 긴 platform 이름이 3자보다 큰 경우 3자로 substring 하여 리턴 한다.
         * 만약 3자 미만인 경우 원래의 값으로 반환 된다.
         *  
         * FACEBOOK -> FAC, GA -> GA
         *  
         * @param {String} platformName platform 이름
         * @return {String} 최대 3자의 짧아진 이름
         * */
        getShortPlatformName: function (platformName){
            if(platformName.length > 3){
                return platformName.substring(0,3);
            }else{
                return platformName;
            }
        },

        getAdItemUnitMeta: function(item){
            if(item.type === 'CAMPAIGN') {
                return {name:'C', color:'green'}
            }else if(item.type === 'AD_GROUP') {
                return {name:'G', color:'blue'}
            }else if(item.type === 'AD') {
                return {name:'A', color:'pink'}
            }else if(item.type === 'KEYWORD') {
                return {name:'K', color:'brown'}
            }else if(item.type === 'INSERTION_ORDER') {
                return {name:'I', color:'indigo'}
            }else if(item.type === 'LINE_ITEM') {
                return {name:'L', color:'deep-purple'}
            }else if(item.type === 'PLACEMENT') {
                return {name:'P', color:'blue'}
            }else if(item.type === 'CREATIVE') {
                return {name:'C', color:'pink'}
            }else{
                return {name: item.type.substring(0,1), color:'pink'}
            }
        },

        getAdItemStatusMeta: function(item){
            const status = item.status;
            if(status === 'ACTIVE') {
                return {name:'ACTIVE', color:'green', icon: 'mdi-play-circle mdi-18px'}
            }else if(status === 'PAUSED') {
                return {name:'PAUSED', color:'blue', icon: 'mdi-pause-circle mdi-18px'}
            }else if(status === 'REMOVED') {
                return {name:'REMOVED', color:'gray', icon: 'mdi-close-circle mdi-18px'}
            }else if(status === 'UNKNOWN') {
                return {name:'UNKNOWN', color:'brown', icon: 'mdi-alert-circle mdi-18px'}
            }

            return {name:'K', color:'brown', icon: 'mdi-crosshairs-question mdi-18px'}
        }
    };
})(window.mercury || {});