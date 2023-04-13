package com.mercury.discovery.base.users.model;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Alias("DatabaseClientRegistration")
@Data
public class DatabaseClientRegistration{
    private Long id;

    private String registrationId;
    private String clientName;
    private String clientId;
    private String clientSecret;
    private String issuerUri;
    private String scope;

    private String clientAuthenticationMethod;
    private String authorizationGrantType;
    private String redirectUri;
    private String authorizationUri;

    private String tokenUri;
    private String jwkSetUri;
    private String authenticationMethod;
    private String userNameAttributeName;
    private String userInfoUri;
}
