package com.mercury.discovery.config.web.security.oauth.info.impl;

import com.mercury.discovery.config.web.security.oauth.info.OAuth2UserInfo;

import java.util.Map;

public class OktaOAuth2UserInfo extends OAuth2UserInfo {

    public OktaOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return null;
    }
}
