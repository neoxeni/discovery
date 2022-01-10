package com.mercury.discovery.base.users.service.handler;

import com.mercury.discovery.base.users.model.AppUser;
import com.mercury.discovery.base.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class CustomSessionListener implements HttpSessionListener {
    private final UserService userService;

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        //여기서는 appUser를 구할수 없다..
        //AppUser appUser = getAppUser(event);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        AppUser appUser = getAppUser(event);
        log.info("session Destroyed {}", appUser);
    }

    private AppUser getAppUser(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        SecurityContext context = (SecurityContext) session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);

        if (context != null) {
            Object principal = context.getAuthentication().getPrincipal();
            if (principal instanceof AppUser) {
                AppUser appUser = (AppUser) principal;

                userService.updateLogoutInfo(appUser);

                return appUser;
            }
        }

        return null;
    }
}
