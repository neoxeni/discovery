package com.mercury.discovery.config.web.security.oauth.service;

import com.mercury.discovery.base.users.model.UserLogin;
import com.mercury.discovery.base.users.service.UserAuthService;
import com.mercury.discovery.config.web.security.oauth.entity.ProviderType;
import com.mercury.discovery.config.web.security.oauth.exception.OAuthProviderMissMatchException;
import com.mercury.discovery.config.web.security.oauth.info.OAuth2UserInfo;
import com.mercury.discovery.config.web.security.oauth.info.OAuth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {
    private final UserAuthService userAuthService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);
        try {
            return this.process(userRequest, user);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User user) {
        ProviderType providerType = ProviderType.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, user.getAttributes());
        UserLogin savedUser = userAuthService.getUserForLogin(userInfo.getId(), null);

        if (savedUser != null) {
            if (providerType != ProviderType.valueOf(savedUser.getProviderType())) {
                throw new OAuthProviderMissMatchException(
                        "Looks like you're signed up with " + providerType +
                                " account. Please use your " + savedUser.getProviderType() + " account to login."
                );
            }

            //updateUser(savedUser, userInfo);
        } else {
            createUser(userInfo, providerType);
            savedUser = userAuthService.getUserForLogin(userInfo.getId(), null);
        }

        savedUser.setAttributes(user.getAttributes());
        return savedUser;
    }

    private void createUser(OAuth2UserInfo userInfo, ProviderType providerType) {
        Map<String, Object> attributes = userInfo.toMap();
        attributes.put("provider", providerType.name());
        userAuthService.insertWithOAuth(attributes);
    }
}
