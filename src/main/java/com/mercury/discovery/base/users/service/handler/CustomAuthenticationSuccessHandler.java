package com.mercury.discovery.base.users.service.handler;


import com.mercury.discovery.base.users.model.AppUser;
import com.mercury.discovery.base.users.service.UserRepository;
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
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    private String defaultUrl;
    private RequestCache requestCache = new HttpSessionRequestCache();
    private RedirectStrategy redirectStratgy = new DefaultRedirectStrategy();

    public CustomAuthenticationSuccessHandler(String defaultUrl) {
        this.defaultUrl = defaultUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        String tempPw = "";

        SavedRequest savedRequest = requestCache.getRequest(request, response);
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        tempPw = appUser.getPsswd();

        appUser.setLastIpAddress(HttpUtils.getRemoteAddr(request));
        userDetailsService.afterLoginSuccess(appUser);

        String id = appUser.getId();
        String encodeId = passwordEncoder.encode(id);
        int errorNum = 0;

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

        if(appUser.getPsswdErrNum() > 6) {
            this.logout(request, response);
            errorNum = 2;
            redirectStratgy.sendRedirect(request, response, "/login?error="+errorNum);
        } else if(encodeId.equals(tempPw)) {
            errorNum = 6;
            request.setAttribute("error", errorNum);
            request.setAttribute("empNo", appUser.getEmpNo());
            request.setAttribute("userId", appUser.getId());
            request.getRequestDispatcher("/changePassword").forward(request, response);
        } else {
            // 로그인 오류 count 초기화
            userRepository.resetPasswordCnt(appUser.getEmpNo());

            if (savedRequest != null) {
                String targetUrl = savedRequest.getRedirectUrl();
                if (targetUrl.endsWith("/error") || targetUrl.contains("/ws-stomp")) {
                    log.error("savedRequest.getRedirectUrl() is {}", targetUrl);
                    targetUrl = defaultUrl;
                }

                redirectStratgy.sendRedirect(request, response, targetUrl);
            } else {
                redirectStratgy.sendRedirect(request, response, defaultUrl);
            }


        }

    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
    }

}
