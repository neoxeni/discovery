package com.mercury.discovery.config.web.security.oauth.service;


import com.mercury.discovery.base.users.model.AppUser;
import com.mercury.discovery.base.users.model.UserStatus;
import com.mercury.discovery.base.users.service.UserService;
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

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);

        try {
            return this.process(userRequest, user);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User user) {
        ProviderType providerType = ProviderType.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, user.getAttributes());
        AppUser savedUser = userService.getUserForLogin(userInfo.getId(), null);

        if (savedUser != null) {
            if (providerType != savedUser.getProviderType()) {
                throw new OAuthProviderMissMatchException(
                        "Looks like you're signed up with " + providerType +
                        " account. Please use your " + savedUser.getProviderType() + " account to login."
                );
            }
            updateUser(savedUser, userInfo);
        } else {
            savedUser = createUser(userInfo, providerType);
        }

        return savedUser;
    }

    private AppUser createUser(OAuth2UserInfo userInfo, ProviderType providerType) {
        LocalDateTime now = LocalDateTime.now();
        AppUser user = new AppUser();

        user.setUsername(userInfo.getId());
        user.setNickname(userInfo.getName());
        user.setName(userInfo.getName());
        user.setEmail(userInfo.getEmail());
        user.setAvatarUrl(userInfo.getImageUrl());
        user.setProviderType(providerType);
        user.setCreatedBy(0);
        user.setCreatedAt(now);
        user.setUpdatedBy(0);
        user.setModifiedAt(now);
        user.setStatus(UserStatus.NO_CLIENT);

        userService.insert(user);

        return user;
    }

    private AppUser updateUser(AppUser user, OAuth2UserInfo userInfo) {
        if (userInfo.getName() != null && !user.getUsername().equals(userInfo.getName())) {
            user.setUsername(userInfo.getName());
        }

        if (userInfo.getImageUrl() != null && !userInfo.getImageUrl().equals(user.getAvatarUrl())) {
            user.setAvatarUrl(userInfo.getImageUrl());
        }

        return user;
    }
}
