package com.mercury.discovery.common.web.token;

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

        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasLength(authorization)) {
            String[] sp = authorization.split(" ");
            if ("bearer".equalsIgnoreCase(sp[0])) {
                AuthToken token = authTokenProvider.convertAuthToken(sp[1]);
                if (token.validate()) {
                    Authentication authentication = authTokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } else {
                log.info("UNKNOWN authorization {}", authorization);
            }
        }

        filterChain.doFilter(request, response);
    }
}
