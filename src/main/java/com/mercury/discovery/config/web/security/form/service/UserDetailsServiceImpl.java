package com.mercury.discovery.config.web.security.form.service;

import com.mercury.discovery.base.users.model.AppUser;
import com.mercury.discovery.base.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserService userService;

    @Autowired
    private HttpServletRequest request;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String clientId = request.getParameter("clientId");
        AppUser appUser = userService.getUserForLogin(username, clientId);

        if (appUser == null) {
            throw new UsernameNotFoundException(username);
        }

        return appUser;
    }

    public void afterLoginSuccess(AppUser appUser) {
        appUser.setPassword("");
        appUser.setPasswordErrCount(0);
        appUser.setLastLoginAt(LocalDateTime.now());

        //성공시 사용자 정보업데이트 passwordErrCount, lastLoginAt, lastIpAddress 등등
        userService.successLoginInfo(appUser);

        //사용자 role 세팅
        userService.setAppUserRoles(appUser);

        //api 서버 토큰발행
        String token = userService.getApiToken(appUser);
        appUser.setJwt(token);

        userService.cacheUser(appUser);
    }

    public void plusPasswordErrorCount(String username) {
        userService.plusPasswordErrorCount(username);
    }
}
