package com.mercury.discovery.config.web.security.form.handler;

import com.mercury.discovery.base.users.service.UserAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class FormAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    UserAuthService userAuthService;

    private String defaultTargetUrl = "/";
    private final RequestCache requestCache = new HttpSessionRequestCache();
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public FormAuthenticationSuccessHandler(String defaultTargetUrl) {
        this.defaultTargetUrl = defaultTargetUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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

        if (!user.isAccountNonLocked()) {
            errorNum = 2;
            userAuthService.logout(request, response);
            redirectStrategy.sendRedirect(request, response, "/login?error=" + errorNum);
        } else if (userAuthService.isTemporaryPassword(user)) {
            errorNum = 6;
            request.setAttribute("error", errorNum);
            request.setAttribute("username", user.getUsername());
            request.getRequestDispatcher("/changePassword").forward(request, response);
        } else {
            String targetUrl = defaultTargetUrl;
            SavedRequest savedRequest = requestCache.getRequest(request, response);
            if (savedRequest != null) {
                targetUrl = savedRequest.getRedirectUrl();
                if (targetUrl.endsWith("/error") || targetUrl.contains("/ws-stomp")) {
                    targetUrl = defaultTargetUrl;
                }
            }

            userAuthService.afterLoginSuccess(request, response);
            redirectStrategy.sendRedirect(request, response, targetUrl);
        }
    }
}
