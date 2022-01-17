package com.mercury.discovery.config.web.security.oauth.info;

import java.util.HashMap;
import java.util.Map;

public abstract class OAuth2UserInfo {
    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public abstract String getId();

    public abstract String getName();

    public abstract String getEmail();

    public abstract String getImageUrl();

    public Map<String, Object> toMap(){
        Map<String, Object> toMap = new HashMap<>();

        toMap.put("id", getId());
        toMap.put("name", getName());
        toMap.put("email", getEmail());
        toMap.put("avatar", getImageUrl());

        return toMap;
    }
}
