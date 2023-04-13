package com.mercury.discovery.config.web.security.oauth;

import com.mercury.discovery.base.users.model.DatabaseClientRegistration;
import com.mercury.discovery.base.users.service.ClientRegistrationRepositoryImpl;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.util.Assert;

//https://www.baeldung.com/spring-security-5-oauth2-login
//DefaultOAuth2AuthorizationRequestResolver     Line:150 Debug


public class JdbcClientRegistrationRepository implements ClientRegistrationRepository {

    private final ClientRegistrationRepositoryImpl clientRegistrationRepository;

    public JdbcClientRegistrationRepository(ClientRegistrationRepositoryImpl clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {
        Assert.hasText(registrationId, "registrationId cannot be empty");

        DatabaseClientRegistration providerConfiguration = clientRegistrationRepository.findByRegistrationId(registrationId);

        String[] scopes = providerConfiguration.getScope().split(",");
        return ClientRegistration.withRegistrationId(providerConfiguration.getRegistrationId())
                .clientId(providerConfiguration.getClientId())
                .clientSecret(providerConfiguration.getClientSecret())
                .clientName(providerConfiguration.getClientName())
                .authorizationGrantType(new AuthorizationGrantType(providerConfiguration.getAuthorizationGrantType()))
                .authorizationUri(providerConfiguration.getAuthorizationUri())
                .clientAuthenticationMethod(new ClientAuthenticationMethod(providerConfiguration.getClientAuthenticationMethod()))
                .scope(scopes)
                .issuerUri(providerConfiguration.getIssuerUri())
                .jwkSetUri(providerConfiguration.getJwkSetUri())
                .tokenUri(providerConfiguration.getTokenUri())
                .userInfoAuthenticationMethod(new AuthenticationMethod(providerConfiguration.getAuthenticationMethod()))
                .userInfoUri(providerConfiguration.getUserInfoUri())
                .userNameAttributeName(providerConfiguration.getUserNameAttributeName())
                .redirectUri(providerConfiguration.getRedirectUri())
                .build();
    }


}
