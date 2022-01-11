package com.mercury.discovery.config.web;


import com.mercury.discovery.base.users.service.handler.CustomAuthenticationFailureHandler;
import com.mercury.discovery.base.users.service.handler.CustomAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Web Security 설정
 * https://velog.io/@tlatldms/Spring-boot-Spring-security-JWT-Redis-mySQL-2%ED%8E%B8
 * https://yookeun.github.io/java/2017/07/23/spring-jwt/
 */

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityContextConfig extends WebSecurityConfigurerAdapter {

    // favicon 요청등 정적인 요청 처리 시 필터 등록 제외
    @Override
    public void configure(WebSecurity web) throws Exception {
        //super.configure(web);
        //web.ignoring().antMatchers("/static/**", "/ws-stomp/**");//spring-security filter bypass
        //web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
        
        //위에 걸로는 안먹히는듯
        web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers(new AntPathRequestMatcher("/**.html"))
                .requestMatchers(new AntPathRequestMatcher("/ws-stomp/**"))
                .requestMatchers(new AntPathRequestMatcher("/static/**"));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //super.configure(http);//호출하지 않음
        http
                .httpBasic()
                .authenticationEntryPoint(new NoPopupBasicAuthenticationEntryPoint())
                .and()

                //.exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .csrf().disable() // rest api이므로 csrf 보안이 필요없으므로 disable처리.
                .headers()
                .frameOptions().sameOrigin() // SockJS는 기본적으로 HTML iframe 요소를 통한 전송을 허용하지 않도록 설정되는데 해당 내용을 해제한다.

                .and()

                .cors()
                .configurationSource(corsConfigurationSource())
                .and()

                .authorizeRequests()
                .antMatchers("/static/**").permitAll()
                .mvcMatchers("/", "/changePassword", "/changePasswordOk", "/login", "/logout", "/health/*").permitAll()
                // .requestMatchers(CorsUtils::isPreFlightRequest, endpointsMatcher).permitAll()
                .anyRequest().permitAll(); // 나머지 리소스에 대한 접근 설정


        // 2. 로그인 설정
        http.formLogin()// 권한없이 페이지 접근하면 로그인 페이지로 이동한다.
                .permitAll()
                .loginPage("/login")    // 로그인 페이지 url
                .usernameParameter("userId")
                .successHandler(customAuthenticationSuccessHandler())
                .failureHandler(customAuthenticationFailureHandler())
        ;

        // 3. 로그아웃 설정
        /*
        http.logout()   // 로그아웃 처리
                //.addLogoutHandler(customLogoutHandler())
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/") // 로그아웃 성공시
                .invalidateHttpSession(true)
                ;
         */

        //@Async를 처리하는 쓰레드에서도 SecurityContext를 공유받을 수 있다.
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        List<String> all = new ArrayList<>();
        all.add("*");

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(all);
        configuration.setAllowedMethods(all);
        configuration.setAllowedHeaders(all);

        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        String defaultUrl = "/";
        return new CustomAuthenticationSuccessHandler(defaultUrl);
    }

    @Bean
    public AuthenticationFailureHandler customAuthenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }


    /**
     * [Disable browser authentication dialog in spring security](https://stackoverflow.com/questions/31424196/disable-browser-authentication-dialog-in-spring-security)
     */
    public static class NoPopupBasicAuthenticationEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response,
                             AuthenticationException authException) throws IOException {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
        }
    }
}
