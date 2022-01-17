package com.mercury.discovery.base.users.service;


import com.mercury.discovery.base.users.model.AppUser;
import com.mercury.discovery.base.users.model.UserLogin;
import com.mercury.discovery.base.users.model.UserStatus;
import com.mercury.discovery.common.web.token.AuthTokenProvider;
import com.mercury.discovery.common.web.token.TokenProperties;
import com.mercury.discovery.utils.HttpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional
public class UserAuthService {
    private final UserService userService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthTokenProvider authTokenProvider;

    @Transactional(readOnly = true)
    public UserLogin getUserForLogin(String username, String clientId) {
        return userRepository.findByUsernameForLogin(username, clientId);
    }

    public Authentication afterLoginSuccess(HttpServletRequest request, HttpServletResponse response) {
        UserLogin userLogin = (UserLogin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        AppUser appUser = userService.findById(userLogin.getUserId());
        appUser.setLastIpAddress(HttpUtils.getRemoteAddr(request));

        //성공시 사용자 정보업데이트 passwordErrCount, lastLoginAt, lastIpAddress 등등
        appUser.setPasswordErrCount(0);
        appUser.setLastLoginAt(LocalDateTime.now());
        userService.updateLoginInfo(appUser);

        List<GrantedAuthority> authorities = new ArrayList<>();
        appUser.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        });

        processToken(appUser, request, response);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(appUser, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        //cacheUser(appUser);
        return authenticationToken;
    }

    private void processToken(AppUser appUser, HttpServletRequest request, HttpServletResponse response) {
        Date now = new Date();
        TokenProperties tokenProperties = authTokenProvider.getTokenProperties();
        String accessToken = authTokenProvider.getBuilder()
                .setId(appUser.getUserKey())
                .setSubject(appUser.getClientId() + ":" + appUser.getId())
                .setAudience(appUser.getName())
                .setIssuedAt(now)
                .claim("authorities", appUser.getRoles())
                .setExpiration(new Date(now.getTime() + tokenProperties.getExpire()))
                .compact();

        appUser.setToken(accessToken);

        String refreshToken = authTokenProvider.getBuilder()
                .setId(appUser.getUserKey())
                .setExpiration(new Date(now.getTime() + tokenProperties.getRefresh()))
                .compact();

        //refreshToken DB 저장 로직 추가

        int cookieMaxAge = (int) (tokenProperties.getRefresh() / 60);
        HttpUtils.deleteCookie(request, response, "refresh_token");
        HttpUtils.addCookie(response, "refresh_token", refreshToken, cookieMaxAge);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
    }

    public boolean isTemporaryPassword(UserDetails user) {
        return user.getPassword().equals(passwordEncoder.encode(user.getUsername()));
    }

    public int insertWithOAuth(Map<String, Object> attributes) {
        LocalDateTime now = LocalDateTime.now();

        AppUser user = new AppUser();

        user.setUsername((String) attributes.get("id"));
        user.setNickname((String) attributes.get("name"));
        user.setName((String) attributes.get("name"));
        user.setEmail((String) attributes.get("email"));
        user.setAvatar((String) attributes.get("avatar"));
        user.setProviderType((String) attributes.get("provider"));

        user.setCreatedBy(0);
        user.setCreatedAt(now);
        user.setUpdatedBy(0);
        user.setUpdatedAt(now);
        user.setStatus(UserStatus.NO_CLIENT);

        userService.insert(user);

        return user.getId();
    }


}
