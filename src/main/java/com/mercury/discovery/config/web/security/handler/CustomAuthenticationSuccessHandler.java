package com.mercury.discovery.config.web.security.handler;

import com.mercury.discovery.base.users.model.AppUser;
import com.mercury.discovery.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    PasswordEncoder passwordEncoder;

    private String defaultUrl = "/";
    private final RequestCache requestCache = new HttpSessionRequestCache();
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public CustomAuthenticationSuccessHandler(String defaultUrl) {
        this.defaultUrl = defaultUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        appUser.setLastIpAddress(HttpUtils.getRemoteAddr(request));

        int errorNum;

        /*
        //Ajax 지원
        String accept = request.getHeader("accept");
        boolean isForward = accept == null || !accept.matches(".*application/json.*");
        if(!isForward){//JSON인 경우
            MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
            MediaType jsonMimeType = MediaType.APPLICATION_JSON;
            if (jsonConverter.canWrite(appUser.getClass(), jsonMimeType)) {
                jsonConverter.write(appUser, jsonMimeType, new ServletServerHttpResponse(response));
            }
            return;
        }
        */

        if (appUser.getPasswordErrCount() > 6) {
            this.logout(request, response);
            errorNum = 2;
            redirectStrategy.sendRedirect(request, response, "/login?error=" + errorNum);
        } else if (appUser.getPassword().equals(passwordEncoder.encode(appUser.getUsername()))) {
            errorNum = 6;
            request.setAttribute("error", errorNum);
            request.setAttribute("userId", appUser.getId());
            request.setAttribute("username", appUser.getUsername());
            request.getRequestDispatcher("/changePassword").forward(request, response);
        } else {
            userDetailsService.afterLoginSuccess(appUser);

            SavedRequest savedRequest = requestCache.getRequest(request, response);
            if (savedRequest != null) {
                String targetUrl = savedRequest.getRedirectUrl();
                if (targetUrl.endsWith("/error") || targetUrl.contains("/ws-stomp")) {
                    log.error("savedRequest.getRedirectUrl() is {}", targetUrl);
                    targetUrl = defaultUrl;
                }

                redirectStrategy.sendRedirect(request, response, targetUrl);
            } else {
                redirectStrategy.sendRedirect(request, response, defaultUrl);
            }
        }
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
    }
}
