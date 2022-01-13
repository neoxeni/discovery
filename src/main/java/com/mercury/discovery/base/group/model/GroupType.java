package com.mercury.discovery.base.group.model;

public enum GroupType {
    ROLE("권한"),
    ;
    private final String name;

    GroupType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
