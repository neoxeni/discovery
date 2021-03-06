package com.mercury.discovery.config.web.security;

import com.mercury.discovery.common.web.token.AuthTokenAuthenticationFilter;
import com.mercury.discovery.config.web.security.form.handler.FormAuthenticationFailureHandler;
import com.mercury.discovery.config.web.security.form.handler.FormAuthenticationSuccessHandler;
import com.mercury.discovery.config.web.security.oauth.handler.OAuth2AuthenticationFailureHandler;
import com.mercury.discovery.config.web.security.oauth.handler.OAuth2AuthenticationSuccessHandler;
import com.mercury.discovery.config.web.security.oauth.service.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.mercury.discovery.config.web.security.oauth.service.OAuth2UserServiceImpl;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Web Security ??????
 * https://velog.io/@tlatldms/Spring-boot-Spring-security-JWT-Redis-mySQL-2%ED%8E%B8
 * https://yookeun.github.io/java/2017/07/23/spring-jwt/
 */

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityContextConfig extends WebSecurityConfigurerAdapter {
    private final OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final OAuth2UserServiceImpl oAuth2UserService;

    // favicon ????????? ????????? ?????? ?????? ??? ?????? ?????? ??????
    @Override
    public void configure(WebSecurity web) throws Exception {
        //super.configure(web);
        //web.ignoring().antMatchers("/static/**", "/ws-stomp/**");//spring-security filter bypass
        //web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());

        //?????? ????????? ???????????????
        web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers(new AntPathRequestMatcher("/**.html"))
                .requestMatchers(new AntPathRequestMatcher("/ws-stomp/**"))
                .requestMatchers(new AntPathRequestMatcher("/static/**"));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //super.configure(http);//???????????? ??????
        http
                .httpBasic()
                .authenticationEntryPoint(new SecurityAuthenticationEntryPoint())
                .and()
                    //.exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                    .csrf().disable() // rest api????????? csrf ????????? ?????????????????? disable??????.
                    .headers()
                    .frameOptions().sameOrigin() // SockJS??? ??????????????? HTML iframe ????????? ?????? ????????? ???????????? ????????? ??????????????? ?????? ????????? ????????????.
                .and()
                    .cors()
                    .configurationSource(corsConfigurationSource())
                .and()
                    .authorizeRequests()
                    .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                    .antMatchers("/static/**").permitAll()
                    .mvcMatchers("/changePassword", "/changePasswordOk", "/login", "/logout", "/health/*").permitAll()
                    // .requestMatchers(CorsUtils::isPreFlightRequest, endpointsMatcher).permitAll()
                    .anyRequest().authenticated(); // ????????? ???????????? ?????? ?????? ??????

        // 2. ????????? ??????
        http.formLogin()// ???????????? ????????? ???????????? ????????? ???????????? ????????????.
                .permitAll()
                .loginPage("/login")    // ????????? ????????? url
                .successHandler(customAuthenticationSuccessHandler())
                .failureHandler(customAuthenticationFailureHandler())
        ;

        // 3. ???????????? ??????
        /*
        http.logout()   // ???????????? ??????
                //.addLogoutHandler(customLogoutHandler())
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/") // ???????????? ?????????
                .invalidateHttpSession(true)
        ;
        */

        // 4. Oauth2
        http.oauth2Login()
                .permitAll()
                .loginPage("/login")
                .authorizationEndpoint()
                .baseUri("/oauth2/authorization")
                .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository)
            .and()
                .redirectionEndpoint()
                .baseUri("/*/oauth2/code/*")
            .and()
                .userInfoEndpoint()//OAuth 2 ????????? ?????? ?????? ????????? ????????? ????????? ?????? ????????? ??????
                .userService(oAuth2UserService)//????????? ??????(?????? ????????????)?????? ????????? ????????? ????????? ???????????? ????????? ??????????????? ?????? ?????? ?????? ??????
            .and()
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler)
        ;

        // 5. Token ?????? ??????
        http
            .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            //.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)   //?????? ????????????
        ;

        //@Async??? ???????????? ?????????????????? SecurityContext??? ???????????? ??? ??????.
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    /*
     * ?????? ?????? ??????
     * */
    @Bean
    public AuthTokenAuthenticationFilter tokenAuthenticationFilter() {
        return new AuthTokenAuthenticationFilter();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new FormAuthenticationSuccessHandler();
    }

    @Bean
    public AuthenticationFailureHandler customAuthenticationFailureHandler() {
        return new FormAuthenticationFailureHandler();
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

    /**
     * [Disable browser authentication dialog in spring security](https://stackoverflow.com/questions/31424196/disable-browser-authentication-dialog-in-spring-security)
     */
    public static class SecurityAuthenticationEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
        }
    }

    /*@Component
    @RequiredArgsConstructor
    public static class SecurityAccessDeniedHandler implements AccessDeniedHandler {

        private final HandlerExceptionResolver handlerExceptionResolver;

        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
            //response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage());
            handlerExceptionResolver.resolveException(request, response, null, accessDeniedException);
        }
    }*/
}
