package com.mercury.discovery.base.users.service;


import com.mercury.discovery.base.users.model.AppUser;
import com.mercury.discovery.utils.HttpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional
public class UserAuthService {
    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public Authentication afterLoginSuccess(HttpServletRequest request, HttpServletResponse response) {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        appUser.setLastIpAddress(HttpUtils.getRemoteAddr(request));

        //성공시 사용자 정보업데이트 passwordErrCount, lastLoginAt, lastIpAddress 등등
        appUser.setPassword(null);
        appUser.setPasswordErrCount(0);
        appUser.setLastLoginAt(LocalDateTime.now());
        userService.updateLoginInfo(appUser);
        userService.setAppUserRoles(appUser);

        //cacheUser(appUser);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(appUser, null, appUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        return authenticationToken;
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
}
