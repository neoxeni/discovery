package com.mercury.discovery.base.users.model;
import java.util.Arrays;


public enum UserType {
    USER("USER", "일반 사용자 권한"),
    ADMIN("ADMIN", "관리자 권한"),
    GUEST("GUEST", "게스트 권한");

    private final String code;
    private final String name;

    UserType(String code, String name){
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static UserType of(String code) {
        return Arrays.stream(UserType.values())
                .filter(r -> r.getCode().equals(code))
                .findAny()
                .orElse(GUEST);
    }
}
