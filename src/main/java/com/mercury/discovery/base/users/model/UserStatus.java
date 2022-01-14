package com.mercury.discovery.base.users.model;

public enum UserStatus{

    ACTIVE("활성화"),
    INACTIVE("비활성화"),
    NO_CLIENT("클라이언트 미설정"),
    UNAUTHORIZED("미인증"),
    ;

    private final String name;

    UserStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
