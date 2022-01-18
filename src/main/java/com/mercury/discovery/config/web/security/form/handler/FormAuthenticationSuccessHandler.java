package com.mercury.discovery.config.web.security.form.handler;

import com.mercury.discovery.base.users.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FormAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Autowired
    UserAuthService userAuthService;

    private final RequestCache requestCache = new HttpSessionRequestCache();

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
            getRedirectStrategy().sendRedirect(request, response, "/login?error=" + errorNum);
        } else if (userAuthService.isTemporaryPassword(user)) {
            errorNum = 6;
            request.setAttribute("error", errorNum);
            request.setAttribute("username", user.getUsername());
            request.getRequestDispatcher("/changePassword").forward(request, response);
        } else {
            String targetUrl = getDefaultTargetUrl();
            SavedRequest savedRequest = requestCache.getRequest(request, response);
            if (savedRequest != null) {
                targetUrl = savedRequest.getRedirectUrl();
                if (targetUrl.endsWith("/error") || targetUrl.contains("/ws-stomp")) {
                    targetUrl = getDefaultTargetUrl();
                }
            }

            userAuthService.afterLoginSuccess(request, response);
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        }
    }
}
