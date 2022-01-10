/**
 * 공통으로 사용하는 validation rules 를 정의한다.
 *
 * <v-text-field label="이름" v-model="form.data.name" :rules="mercury.base.rule.required"></v-text-field>
 * */
(function (mercury) {
    window.mercury = mercury;
    mercury.base = mercury.base || {};

    mercury.base.rule = {
        password: [
            password => (password && password.length > 0) || '필수 입력 항목입니다',
            password => /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,16}$/.test(password) || '최소 8자~16자로 하나 이상의 대문자, 숫자 및 특수 문자가 모두 포함되어야 합니다.'
        ],
        required: [v => (v && String(v).trim().length > 0) || '필수 입력 항목입니다'],
        requiredObject: [v => (v.value !== '' && v.value !== undefined) ? true : '필수 입력 항목입니다'],
        number: [v => v !== null && v !== undefined && (String(v) !== '' && !isNaN(v)) || '숫자 입력 항목입니다'],
        timeHHmmss:[
            v => (v && v.length > 0 && /^([0-1][0-9]|2[0-3]):([0-5][0-9])(:[0-5][0-9])$/.test(v)) || '형식이 올바르지 않습니다. (HH:mm:ss)'
        ],
        timeHHmm:[
            v => (v && v.length > 0 && /^([0-1][0-9]|2[0-3]):([0-5][0-9])$/.test(v)) || '형식이 올바르지 않습니다. (HH:mm)'
        ],
        email: [
            (v) => !!v || '필수 입력 항목입니다',
            (v) => /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/.test(v) || '이메일 형식이 아닙니다'
        ],
        sortNumber:[
            v => (v !== '' && !isNaN(v) && v > -1) || '필수 입력 항목입니다'
        ]
    };
})(window.mercury || {});