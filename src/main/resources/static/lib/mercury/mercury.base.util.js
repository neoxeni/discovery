/**
 * util은 해당 프로젝트가 아니여도 모든 프로젝트에서 사용할 수 있도록 공통 스크립트만 작성한다.
 *     - 화면내의 dom을 처리 하는 작업을 하지 않도록 한다.   (dom 조작은 ui를 사용)
 *     - util은 단독으로도 사용할 수있도록 선별하여 작성한다. (ui, lib, vue 등을 호출하지 말자)
 *
 * [문서화를 위한 주석 규칙은](http://yui.github.io/yuidoc/syntax/index.html)
 * */
(function (mercury) {
    window.mercury = mercury;
    mercury.base = mercury.base || {};

    const KEYCODE = {
        ENTER: 13,
        ESCAPE: 27,
        ARROW_LEFT: 37,
        ARROW_UP: 38,
        ARROW_RIGHT: 39,
        ARROW_DOWN: 40,
        QUESTION: 191,
        CTRL: 17,
        ALT: 18,
        m: 77,
        k: 75,
    };

    const self = mercury.base.util = {
        KEYCODE: KEYCODE,

        /**
         * path에 포함된 pathVariables를 pathParams를 통해 binding한다.
         *
         * mercury.base.util.bindPath('/base/{category1}/list/{category2}',{category1:'board',category2:'notice'});
         * =>  /base/board/list/notice
         *
         * @param {String} pathExpression 경로로 pathVariables를 포함할 수도 있다.
         * @param {Object} pathParams binding할 object
         *
         * @return pathVariable이 모두 바인딩된 경로
         * */
        bindPath: function(pathExpression, pathParams){
            let path = pathExpression;
            if (!path.match(/^\//)) {
                path = '/' + path;
            }

            if (pathParams) {
                path = path.replace(/{([\w-]+)}/g, function (fullMatch, key) {
                    if (pathParams.hasOwnProperty(key)) {
                        return pathParams[key];
                    }
                });
            }

            return path;
        },

        /**
         * 해당 문자열이 값을 가지고 있는지 체크 한다. null, undefined, 공백 을 체크한다.
         * @param {String} str 문자열
         *
         * @return {Boolean} 값이 있는지 여부
         */
        hasText: function(str){
            return str !== undefined && str !== null && str.trim() !== '';
        },

        /**
         * camelCase를 snakeCase로 변경한다
         * TypeOfData.AlphaBeta => type_of_data_alpha_beta
         *
         * @param {String} s camelCase 문자열
         * 
         * @returns {String} snake case로 변경된 문자열
         * */
        snakeCase: function(s){
            return s.replace(/\.?([A-Z]+)/g, function (x,y){return "_" + y.toLowerCase()}).replace(/^_/, "")
        },


        /**
         * 문자열에 포함된 html tag를 제거 한다.
         *
         * @param {String} s html이 포함된 문자열
         *
         * @returns {String} html이 제거된 문자열
         * */
        stripHtml: function(s){
            return s.replace(/<[^>]*>?/gm, '')
        },


        /**
         * UUID 생성
         * e12dddb3-3265-461f-823a-d738d0f742be
         *
         * @returns {String} javascript uuid
         * */
        uuid: function () {
            return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
                const r = Math.random() * 16 | 0, v = c === 'x' ? r : (r & 0x3 | 0x8);
                return v.toString(16);
            });
        },

        /**
         * @description 파일 사이즈를 읽기 좋게 표시해준다.
         *
         * 100 => 100 B
         * 10000 => 9.77 KB
         * 10000000 => 9.54 MB
         *
         * @param {Number} size 파일사이즈
         * @returns {String} 포맷된 파일 사이즈
         * */
        fileSize(size){
            const i = Math.floor( Math.log(size) / Math.log(1024) );
            return ( size / Math.pow(1024, i) ).toFixed(2) * 1 + ' ' + ['B', 'kB', 'MB', 'GB', 'TB'][i];
        },

        /**
         * @description     저장된 브라우저 cookie중 name에 해당하는 값을 가져온다.
         * @param  {string } key        가져올 쿠키의 key값
         * @return {string }            key에 해당하는 value값
         */
        getCookie: function (key) {
            const value = document.cookie.match('(^|;) ?' + key + '=([^;]*)(;|$)');
            return value? value[2] : null;
        },

        /**
         * @description    브라우저 쿠키 세팅한다. value가 null 인경우는 쿠키를 삭제한다.
         * @param {string } key        저장할 쿠키 이름
         * @param {string||null } value        저장할 쿠키 값, null인경우 해당키 삭제
         * @param {Number } expireDays    만료일자
         */
        setCookie: function (key, value, expireDays) {
            const date = new Date();


            if(value !== null){
                if(expireDays === undefined){
                    expireDays = 7;
                }
                date.setTime(date.getTime() + expireDays * 60 * 60 * 24 * 1000);
                document.cookie = key + '=' + value + ';expires=' + date.toUTCString() + ';path=/';
            }else{
                document.cookie = key + "= " + "; expires=" + date.toUTCString() + "; path=/";
            }
        },

        setLocalStorage: function(key, value){
            let saveValue = value;
            if(typeof value === 'object'){
                saveValue = JSON.stringify(value)
            }

            localStorage.setItem(key, saveValue);
        },
        getLocalStorage: function(key){
            let restoreValue = localStorage.getItem(key)
            if(restoreValue !== null){
                try{
                    restoreValue = JSON.parse(restoreValue)
                }catch(e){

                }
            }

            return restoreValue;
        },

        /**
         * @description    오늘 날짜 기준 특정 기간 추출(startDate, endDate)
         * @param {Number} startOffset 시작
         * @param {Number} endOffset 종료
         * @param {string} period day: '일', week: '주', month: '월', year: '년'
         */
        getDateRange (startOffset = 0, endOffset = 0, period = 'day') {
            return [
                moment().subtract(startOffset, period).startOf(period),
                moment().subtract(endOffset, period).endOf(period),
            ];
        },

        getDateRangeFormat: function (range) {
            return moment(range['start']).format('YYYY-MM-DD') + ' ~ ' + moment(range['end']).format('YYYY-MM-DD');
        },

        /**
         * @description fetchData 등에서 range에 들어 있는 date 객체를 날짜 검색을 하기위한 값으로 변경 후 searchObject에 넣어준다.
         *
         * @param {Object} searchObject 검색용 서치 객체
         * @param {String} rangeKeyName range: { start: Date, end: Date} 형태에서 range
         * @param {String} startFieldName searchObject에 넣을 fieldName
         * @param {String} endFieldName true면 뒤, false면 앞
         *
         */
        setDateRangeParam: function (searchObject, rangeKeyName, startFieldName, endFieldName) {
            const range = searchObject[rangeKeyName];
            if(range === undefined){
                throw Error('[' +rangeKeyName+'] is not defined in searchObject');
            }

            searchObject[startFieldName] = moment(range['start']).format('YYYY-MM-DD 00:00:00');
            searchObject[endFieldName] = moment(range['end']).format('YYYY-MM-DD 23:59:59.999999');
        },
        
        /**
         * @description 전달된 value가 값을 가지고 있는지 체크 null, undefined, 공백인지를 체크 한다.
         *
         * @param {String} value 값
         * @returns {Boolean} 값이 있는지 여부
         */
        hasValue: function(value){
            return !(value === null || value === undefined || value === '');
        },
        
        /**
         * @description array의 앞, 뒤를 지정해서 items를 추가한다.
         * @param {Array} src 원본 array
         * @param {Array} items 추가할 array
         * @param {Boolean} isAppend true면 뒤, false면 앞
         * @returns {Array} 합쳐진 하나의 array
         */
        concatArray: function (src, items, isAppend) {
            return isAppend ? src.concat(items) : items.concat(src);
        },

        /**
         * @description 전달된 object가 array인지 확인 한다.
         * @param {Object} obj array인지 확인할 Object
         * @returns {Boolean} array 인지 여부
         */
        isArray: function(obj){
            return Object.prototype.toString.call(obj) === "[object Array]"
        },

        /**
         * @description 전달된 object의 깊은 복사를 수행한다.
         * @param {Object} obj 대상 object
         * @returns {Object} 깊은 복사된 obj 객체
         */
        deepCopy: function(obj){
            return JSON.parse(JSON.stringify(obj));
        },

        /**
         * @description 전달된 object 를 변경하지 못하도록 불변하게 만든다..
         * @param {Object} obj 대상 object
         * @returns {Object} immutable object
         */
        deepFreeze: function(obj){
            Object.keys(obj).forEach(prop => {
                if (typeof obj[prop] === 'object' && !Object.isFrozen(obj[prop])) self.deepFreeze(obj[prop]);
            });
            return Object.freeze(obj);
        },

        /**
         * @description 숫자 세자리마다 콤마(,) 금액
         * @param {string} str 숫자형 스트링
         * @returns {string} 소수점 포함 금액 포맷팅된 문자열
         */
        addComma(str){
            return Number(str).toLocaleString('en').split(".")[0];
        },

        /**
         * @description obj 의 값을 queryString 형태로 변경한다. $.param 과 동일한 기능
         *
         * @param {Object} obj queryString 형태로 변경할 Object
         * @param {String} prefix key앞에 붙일 prefix
         * @return {String} a=1&b=1 형태의 쿼리 스트링
         * */
        param(obj, prefix) {
            //return $.param(obj)

            let str = [], p;
            for (p in obj) {
                if (obj.hasOwnProperty(p)) {
                    let k = prefix ? prefix + "[" + p + "]" : p;
                    let v = obj[p];
                    if(v === undefined){
                        v = '';
                    }

                    str.push((v !== null && typeof v === "object") ? self.param(v, k) : encodeURIComponent(k) + "=" + encodeURIComponent(v));
                }
            }
            return str.join("&");
        },

        /**
         * @description vuetify 의 v-data-table 사용 시 ajax전송에 필요한 queryString 정보를 만들때 사용한다.
         *
         * @param {Object} options v-data-table options.sync="options" 옵션과 연결된 options
         *      const {groupBy, groupDesc, itemsPerPage, multiSort, mustSort, page, sortBy, sortDesc} = options
         * @param {Object} data 해당 data-table에서 필요한 커스텀 데이터
         * @param {Array} excludeKeys data에서 제외시킬 필드이 름
         *      {custNo : 1}
         * */
        dataTablesParam: function (options, data, excludeKeys) {
            const page = options.page ? options.page : 1;
            const size = options.itemsPerPage ? options.itemsPerPage : 5;
            const sortBy = options.sortBy ? options.sortBy : [];
            const sortDesc = options.sortDesc ? options.sortDesc : [];

            let query = '';
            query += 'pageType=dataTables&page=' + page + '&size=' + size;
            for (let i = 0, ic = sortBy.length; i < ic; i++) {
                query += '&sort=' + self.snakeCase(sortBy[i]) + ',' + (sortDesc[i] ? 'DESC' : 'ASC');
            }

            if (data) {
                query += '&' + $.param(data);
            }

            return query;
        },

        /**
         * @description vuetify 의 v-data-table 사용 시 options 정보를 리셋한다
         * @param {Object} options v-data-table options.sync="options" 옵션과 연결된 options
         * */
        dataTablesReset: function (options) {
            Object.assign(options, {
                page: 1,
                sortBy: [],
                sortDesc: [],
                groupBy: [],
                groupDesc: []
            });
        },

        /**
         * @description roles 안에 arr에 해당하는 role이 있는지 검사한다.
         * @param {Array} roles 사용자의 roles
         * @param {Array||String} arr 검사하고자 하는 role
         * */
        hasAnyRole: function (roles, arr){
            const rolesMap = roles.reduce(function(map, obj) {
                map[obj] = true;
                return map;
            }, {});

            if (typeof arr === 'string') {
                return !!rolesMap[arr];
            } else {
                for (let i = 0, ic = arr.length; i < ic; i++) {
                    if (rolesMap[arr[i]] !== undefined) {
                        return true;
                    }
                }
            }

            return false;
        },

        flatten: function (into, node){
            if(node == null) return into;
            if(Array.isArray(node)) return node.reduce(self.flatten, into);
            into.push(node);
            return self.flatten(into, node.children);
        },

        /**
         * jo
         *
         * @param {String||Object} jsonObject JSON
         * @return {Element} <div class="print-json-root"></div>
         */
        printJSONObject: function printJSONObject(jsonObject) {function jsonToString(e) {var t=typeof e;if("object"!=t||null===e)return"string"==t&&(e='"'+e+'"'),String(e);var n,r,s=[],a=e&&e.constructor==Array;for(n in e)r=e[n],t=typeof r,"string"==t?r='"'+jsonEscape(r)+'"':"object"==t&&null!==r&&(r=jsonToString(r)),s.push((a?"":'"'+jsonEscape(n)+'":')+String(r));return(a?"[":"{")+String(s)+(a?"]":"}")}function jsonEscape(e){return e.replace(/\\n/g,"\\n").replace(/\'/g,"\\'").replace(/\"/g,'\\"').replace(/\&/g,"\\&").replace(/\r/g,"\\r").replace(/\t/g,"\\t").replace(/\b/g,"\\b").replace(/\f/g,"\\f")}function stringToJson(str){return""===str&&(str='""'),eval("var p="+str+";"),p}function isArray(e){try{return Array.isArray(e)}catch(t){return void 0!==e.length}}function objLength(e){var t=0;try{t=Object.keys(e).length}catch(n){for(var r in e)e.hasOwnProperty(r)&&t++}return t}if("string"==typeof jsonObject&&(jsonObject=stringToJson(jsonObject)),void 0===window.printJSONExpandableCallback){try{var cssText="";cssText+=".print-json-root ul{ margin: 0; padding: 0; }.print-json-root ul li > ul{ padding-left: 20px; } .print-json-root li{list-style: none;}\n",cssText+=".print-json-string{}\n",cssText+=".print-json-boolean{color:#e91e63;}\n",cssText+=".print-json-number{color:#2196f3;}\n",cssText+=".print-json-key{font-weight: bold;}\n",cssText+=".print-json-icon{vertical-align:text-bottom;display: inline-block;width: 15px;height: 15px;background-repeat: no-repeat;background-position: 3px 4px;}\n",cssText+=".print-json-plus{cursor:pointer;background-image:url(data:image/gif;base64,R0lGODlhCQAJAIABAAAAAP///yH5BAEAAAEALAAAAAAJAAkAAAIRhI+hG7bwoJINIktzjizeUwAAOw==);}\n",cssText+=".print-json-minus{cursor:pointer;background-image:url(data:image/gif;base64,R0lGODlhCQAJAIABAAAAAP///yH5BAEAAAEALAAAAAAJAAkAAAIQhI+hG8brXgPzTHllfKiDAgA7);}\n",cssText+=".print-json-array{cursor:pointer;background-image:url(data:image/gif;base64,R0lGODlhCQAJAIABAAAAAP///yH5BAEAAAEALAAAAAAJAAkAAAIRhI+hG7bwoJINIktzjizeUwAAOw==);}\n",cssText+=".print-json-table{border-collapse:collapse;}\n";var style=document.createElement("style");style.setAttribute("type","text/css"),style.setAttribute("id","printJSONInternalCSS"),document.getElementsByTagName("head")[0].appendChild(style);try{style.styleSheet.cssText=cssText}catch(e){style.innerHTML=cssText}}catch(e){alert("style 생성 실패 : \n"+e.message)}window.printJSONExpandableCallback=function(e,t){for(var n=t.nextSibling;"UL"!=n.nodeName;)n=n.nextSibling;if(n){var r=n.style.display;if("none"==r){n.style.display="",t.className="print-json-icon print-json-minus";var s=t.jsonData;for(var a in s){var i=s[a],o=0,l=typeof i,c=i,p="print-json-"+l,d=null;"object"==l&&(isArray(i)?(o=i.length,c="Array["+o+"]",o>0&&(d=document.createElement("I"),d.setAttribute("class","print-json-icon print-json-plus"),d.setAttribute("onclick","printJSONViewArrayCallback(event,this)"),d.jsonData=i)):(o=objLength(i),c="Object"));var A=document.createElement("LI"),u=document.createElement("span"),j=document.createElement("SPAN");j.setAttribute("class","print-json-key"),j.appendChild(document.createTextNode(a));var b=document.createTextNode(": "),h=document.createElement("SPAN");h.setAttribute("class",p),h.appendChild(document.createTextNode(c)),null!=d&&h.appendChild(d);var m=document.createElement("I");if(m.setAttribute("class","print-json-icon"),o>0&&(m.setAttribute("class","print-json-icon print-json-plus"),m.setAttribute("onclick","printJSONExpandableCallback(event,this)"),m.jsonData=i),u.appendChild(j),u.appendChild(b),u.appendChild(h),A.appendChild(m),A.appendChild(u),o>0){var g=document.createElement("ul");g.style.display="none",A.appendChild(g)}n.appendChild(A)}}else n.style.display="none",t.className="print-json-icon print-json-plus",n.innerHTML=""}},window.printJSONViewArrayCallback=function(e,t){try{if("print-json-icon print-json-plus"==t.className){t.className="print-json-icon print-json-minus";var n=t.jsonData,r="<thead><tr>";if(r+="<th>seq</th>",n.length>0){var s=n[0],a=typeof s;if("object"==a)for(var i in s)r+="<th>"+i+"</th>";else r+="<th>contents</th>"}r+="</tr></thead>";for(var o=[],l=0,c=n.length;c>l;l++){var p="<tr>";p+="<td>"+l+"</td>";var s=n[l],a=typeof s;if("object"==a)for(var i in s)p+="<td>"+s[i]+"</td>";else p+="<td>"+s+"</td>";p+="</tr>\n",o.push(p)}var d="<tbody>"+o.join("")+"</tbody>",A=document.createElement("table");A.setAttribute("border","1"),A.setAttribute("class","table table-striped table-bordered table-condensed print-json-table"),A.innerHTML=r+d;for(var u=t.parentNode;"LI"!=u.nodeName;)u=u.parentNode;try{u.insertBefore(A,u.getElementsByTagName("UL")[0])}catch(j){u.appendChild(A)}}else{t.className="print-json-icon print-json-plus";for(var u=t.parentNode;"LI"!=u.nodeName;)u=u.parentNode;var A=u.getElementsByTagName("TABLE")[0];u.removeChild(A)}}catch(j){console.log(j)}}}var ul=document.createElement("UL"),li=document.createElement("LI"),span=document.createElement("SPAN"),text=document.createTextNode("Object:"+typeof jsonObject),icon=document.createElement("I");icon.setAttribute("class","print-json-icon"),icon.setAttribute("class","print-json-icon print-json-plus"),objLength(jsonObject)>0&&(icon.setAttribute("onclick","printJSONExpandableCallback(event,this)"),icon.jsonData=jsonObject);var childUL=document.createElement("UL");childUL.style.display="none",span.appendChild(text),li.appendChild(icon),li.appendChild(span),li.appendChild(childUL),ul.appendChild(li);var div=document.createElement("DIV");return div.setAttribute("class","print-json-root"),div.setAttribute("style","position:relative"),div.appendChild(ul),div},
    };
})(window.mercury || {});