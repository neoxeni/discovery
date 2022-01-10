package com.mercury.discovery.base.company.model;

import com.mercury.discovery.utils.MessagesUtils;

public enum CompanyStatus {

    UNAUTHORIZED("메일인증 미완료"),
    AUTHORIZED("메일인증 완료"),
    REGULAR("정회원(서비스가입)")
;
    private String label;

    CompanyStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return MessagesUtils.getEnumMessage(this, this.label);
    }
}
