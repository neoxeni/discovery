/**
 * lib 은 third party library 등을 보다 쉽게 사용하기 위해 한단계 각 library를  wrapping 한다.
 * 1. 사내 개발 표준 구축
 * 2. 다른 library 로 변경 및 공통 기능 추가 대응
 *
 * [문서화를 위한 주석 규칙은](http://yui.github.io/yuidoc/syntax/index.html)
 * */
(function (mercury, $) {
    window.mercury = mercury;
    window.API_HOST = '/api/v2';
    mercury.base = mercury.base || {};

    /**
     * xAjax와 동일하나 contentType을 application/json으로 고정하고
     * Object 형태의 데이터를 stringify 하여 body데이터로 보낸다.
     *
     * 보통 POST,PATCH,PUT 등의 전송에서 Controller @RequestBody 를 통해 데이터 바인딩을 수행할 때 사용된다.
     * */
    window.xAjaxJson = function xAjaxJson(options) {
        const jsonOption = {
            contentType: "application/json"
        };

        if (typeof options.data === 'object') {
            jsonOption['data'] = JSON.stringify(options.data);
        } else {//이미 stringify 된경우
            jsonOption['data'] = options.data;
        }

        return xAjax(Object.assign(options, jsonOption));
    };

    window.xAjax = function xAjax(options) {
        if (options['usePrefixUrl'] !== false) {
            options['url'] = window.API_HOST + options['url'];
        }

        const paramError = options.error;
        return new Promise(function (resolve, reject) {
            const dOptions = {
                success: function (response, textStatus, jqXHR) {
                    resolve(response);
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    if (typeof paramError === 'function') {
                        paramError(jqXHR, textStatus, errorThrown);
                    }

                    const status = jqXHR.status;
                    const stautsText = jqXHR.statusText
                    const error = jqXHR['responseJSON'];

                    if (error === undefined) {  //JSON Error가 없다는건 HTML로 에러가 떨어졌다.
                        const respText = jqXHR['responseText'];
                        if(respText !== undefined && respText.startsWith('<!DOCTYPE html>')){//python error page
                            const regex = /(<pre class="exception_value">)(.*?)(<\/pre>)/ig
                            const errorText = regex.exec(respText);
                            if(errorText !== undefined){
                                try{
                                    self.notify({message: errorText[2], title: stautsText, type: 'error'});
                                }catch(e){
                                    self.notify({message:  "[" + jqXHR.status + "]사용자 요청이 실패하였습니다.", title: stautsText, type: 'error'});
                                }

                                reject(error);
                                return;
                            }
                        }

                        if (status === 401) {
                            self.notify({message: '세션이 만료되었습니다.', title: '세션만료' + stautsText, type: 'error'});
                            reject(error);
                        } else {
                            self.notify({message: "[" + jqXHR.status + "]사용자 요청이 실패하였습니다.", title: "통신오류", type: 'error'});
                            reject(error);
                        }
                    } else {
                        if(status === 400){
                            let message = '<ul>';
                            const errors = error.errors;

                            for(let i = 0, ic = errors.length; i < ic; i++){
                                const errorDesc = errors[i];
                                message += '<li>[' + errorDesc['field'] +':'+ errorDesc['value']+']<br/> '+errorDesc['reason']+'<br/><br/></li>';
                            }
                            message += '</ul>';

                            self.notify({message:message, title: '['+status+']' + error.error, type: 'error'});
                            reject(error);
                            return;
                        }

                        self.notify({message: error.message, title: '['+status+']' + error.error, type: 'error'});
                        reject(error);
                    }
                }
            };

            delete options.error;
            $.ajax($.extend({}, dOptions, options))
        });
    };


    window.xAjaxMultipart = function (options) {
        const formData = new FormData();

        const isFile = function isFile(value) {
            return value.lastModified && value.lastModifiedDate && value.name && value.size && value.type;
        };

        if (options.parts) {
            let _iteratorNormalCompletion = true;
            let _didIteratorError = false;
            let _iteratorError = undefined;

            try {
                for (let _iterator = Object.entries(options.parts)[Symbol.iterator](), _step; !(_iteratorNormalCompletion = (_step = _iterator.next()).done); _iteratorNormalCompletion = true) {
                    const _step$value = _slicedToArray(_step.value, 2);

                    const key = _step$value[0];
                    const value = _step$value[1];

                    const blob = new Blob([JSON.stringify(value)], {
                        type: 'application/json'
                    });
                    formData.append(key, blob);
                }
            } catch (err) {
                _didIteratorError = true;
                _iteratorError = err;
            } finally {
                try {
                    if (!_iteratorNormalCompletion && _iterator['return']) {
                        _iterator['return']();
                    }
                } finally {
                    if (_didIteratorError) {
                        throw _iteratorError;
                    }
                }
            }
        }

        if (options.fileParts) {
            let _iteratorNormalCompletion2 = true;
            let _didIteratorError2 = false;
            let _iteratorError2 = undefined;

            try {
                for (let _iterator2 = Object.entries(options.fileParts)[Symbol.iterator](), _step2; !(_iteratorNormalCompletion2 = (_step2 = _iterator2.next()).done); _iteratorNormalCompletion2 = true) {
                    const _step2$value = _slicedToArray(_step2.value, 2);

                    const key = _step2$value[0];
                    const value = _step2$value[1];

                    if (Array.isArray(value)) {
                        value.forEach(function (e) {
                            formData.append(key, e);
                        });
                    } else {
                        formData.append(key, value);
                    }
                }
            } catch (err) {
                _didIteratorError2 = true;
                _iteratorError2 = err;
            } finally {
                try {
                    if (!_iteratorNormalCompletion2 && _iterator2['return']) {
                        _iterator2['return']();
                    }
                } finally {
                    if (_didIteratorError2) {
                        throw _iteratorError2;
                    }
                }
            }
        }

        return xAjax(Object.assign(options, {
            contentType: false,
            processData: false,
            data: formData
        }));
    }


    const self = mercury.base.lib = {
        messages:{
            PROCESS: '요청하신 작업이 처리되었습니다.'
        },
        code: function (code, options) {
            const dOptions = {
                type: 'object',   //option,
                useYn: 'Y',
                defaultValue: ''
            };
            options = Object.assign(dOptions, options || {});

            let children = mercury.base.company.code.CODES[code] || [];
            if (options.useYn !== 'ALL') {
                children = children.filter(child => child.useYn === options.useYn);
            }

            if (options.type === 'option') {
                let option = '';

                children.forEach(child => {
                    let selected = '';
                    if (options['defaultValue'] === child['cd']) {
                        selected = ' selected="selected" '
                    }

                    option += `<option value="${child.cd}" ${selected} data-div-cd="${child.divCd}" data-etc1="${child.etc1}" data-etc2="${child.etc2}" data-etc3="${child.etc3}" data-etc4="${child.etc4}">${child.name}</option>`;
                });
                return option;
            } else if (options.type === 'v-object') {
                children.forEach(child => {
                    child.text = child.name;
                    child.value = child.cd;
                });
            }

            return children;
        },

        download: function (options) {
            return new Promise(function (resolve, reject) {
                // https://github.com/johnculviner/jquery.fileDownload/issues/129
                // successCallback 이 호출되지 않는 경우 response 에 set cookie fileDownload=true
                const defaultOptions = {
                    successCallback: function (url) {
                        resolve({url: url});
                    },
                    failCallback: function (responseHtml, url, error) {
                        let json;
                        try {
                            let jsonString = responseHtml.replace(/<[^>]*>?/gm, '');
                            jsonString = jsonString.replace(/\n/g, '')
                            json = JSON.parse(jsonString);
                            self.notify({message: json.detail, type: 'error'});
                        } catch (e) {
                            json = undefined;
                            self.notify({message: 'The file could not be found.', type: 'error'});
                        }

                        reject({html: responseHtml, url: url, json: json});
                    }
                };
                defaultOptions.data = options.data;
                defaultOptions.httpMethod = options.method || 'GET';

                $.fileDownload(options.url, defaultOptions);
            });
        },

        alert: function (msg, options) {
            msg = msg.replace(/\n/g, '<br/>');
            options = options || {};
            const dOptions = {
                html: msg,
                //title: "Are you sure?",
                //icon: "warning",
                showCancelButton: false,
                confirmButtonText: "OK"
            };

            return Swal.fire($.extend({}, dOptions, options));
        },

        confirm: function (msg, options) {
            msg = msg.replace(/\n/g, '<br/>');
            options = options || {};
            const dOptions = {
                html: msg,
                showCancelButton: true,
                confirmButtonText: "OK",
                cancelButtonText: "Cancel",
                reverseButtons: true
            };

            return Swal.fire($.extend({}, dOptions, options));
        },

        notify: function (msg, options, settings) {
            //https://github.com/CodeSeven/toastr
            //https://codeseven.github.io/toastr/demo.html

            let toastType = 'success'; //warning, success, error, info
            let toastTitle = undefined;
            let toastMessage;
            let positionClass = "toast-bottom-left";
            let timeOut = "2000";

            if(typeof msg === 'object'){
                if (msg['message'] !== undefined) { //type, title, message
                    toastMessage = msg['message'];
                }else{                              //POST, PUT 등의 객체 리턴
                    toastMessage = self.messages.PROCESS
                }

                if(msg['type'] !== undefined){
                    toastType = msg['type'];
                }

                if(msg['title'] !== undefined){
                    toastTitle = msg['title'];
                }
            }else if(msg === undefined){    //DELETE 요청 또는 실제로 없는경우
                toastMessage = self.messages.PROCESS
            }else{  // 추후 삭제예정
                toastMessage = msg;

                if(settings && settings.type){
                    toastType = settings.type;
                }

                if(options){
                    toastTitle = options.title;
                }
            }

            console.log(toastType, toastTitle, toastMessage)

            if(toastType === 'error' && timeOut === "2000"){
                timeOut = "10000";
            }

            return window.toastr[toastType](toastMessage, toastTitle, {positionClass: positionClass, timeOut: timeOut});
        }
    };



})(window.mercury || {}, jQuery);