package com.mercury.discovery.config.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;

import javax.servlet.annotation.WebListener;

@Configuration
@WebListener
public class MyRequestContextListener extends RequestContextListener {
    //UserDetailsServiceImpl 등과 같이 @Autowired HttpServletRequest request 를 사용시 필요.. 해당 클래스가 설정이 안되면 아래의 에러
    //org.springframework.security.authentication.InternalAuthenticationServiceException: No thread-bound request found: Are you referring to request attributes outside of an actual web request, or processing a request outside of the originally receiving thread? If you are actually operating within a web request and still receive this message, your code is probably running outside of DispatcherServlet: In this case, use RequestContextListener or RequestContextFilter to expose the current request.
}
