package com.mercury.discovery.config.web.security.form.service;

import com.mercury.discovery.base.users.model.UserLogin;
import com.mercury.discovery.base.users.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
@Service
public class FormUserDetailsServiceImpl implements UserDetailsService {
    private final UserAuthService userAuthService;

    @Autowired
    private HttpServletRequest request;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String clientId = request.getParameter("clientId");
        UserLogin userLogin = userAuthService.getUserForLogin(username, clientId);

        if (userLogin == null) {
            throw new UsernameNotFoundException(username);
        }

        return userLogin;
    }


}
