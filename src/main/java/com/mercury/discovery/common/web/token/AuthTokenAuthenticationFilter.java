package com.mercury.discovery.common.web.token;


import com.mercury.discovery.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class AuthTokenAuthenticationFilter extends OncePerRequestFilter {
    //[Access Token & Refresh Token 인증 구현](https://cotak.tistory.com/102)
    @Autowired
    AuthTokenProvider authTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String tokenStr = HttpUtils.getAccessToken(request);
        if (StringUtils.hasLength(tokenStr)) {
            AuthToken token = authTokenProvider.convertAuthToken(tokenStr);
            if (token.validate()) {
                Authentication authentication = authTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
