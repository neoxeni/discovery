package com.mercury.discovery.base.users.service.handler;

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
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        String cmpnyId = request.getParameter("cmpnyId");
        AppUser appUser = userService.getUserForLogin(userId, cmpnyId);


        if (appUser == null) {
            throw new UsernameNotFoundException(userId);
        }

        return appUser;
    }

    public void afterLoginSuccess(AppUser appUser) {
        //password clear
        appUser.setPsswd("");
        //appUser.setPsswdErrNum(0);
        appUser.setLastLoginDt(LocalDateTime.now());

        //성공시 사용자 정보업데이트 psswdErrNum, lastLoginDt, lastIpAddress 등등
        userService.successLoginInfo(appUser);

        //사용자 role 세팅
        userService.setAppUserRoles(appUser);

        //api 서버 토큰발행
        String token = userService.getApiToken(appUser);
        appUser.setJwt(token);

        userService.cacheUser(appUser);
    }
}
