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

    const status = [
        {text: '활성', value: 'ACTIVE'},
        {text: '비활성', value: 'INACTIVE'}
    ];

    const currency = [
        {text: 'KRW', value: 'KRW'},
        {text: 'USD', value: 'USD'}
    ];

    const tradeType = [
        {text: 'GROSS', value: 'GROSS'},
        {text: 'NET', value: 'NET'}
    ];


    const self = mercury.base.code = {
        status: status,
        currency: currency,
        tradeType: tradeType,
    };
})(window.mercury || {});