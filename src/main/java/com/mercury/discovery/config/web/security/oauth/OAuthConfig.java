package com.mercury.discovery.config.web.security.oauth;

import com.mercury.discovery.config.web.security.oauth.filter.TokenAuthenticationFilter;
import com.mercury.discovery.config.web.security.oauth.handler.OAuth2AuthenticationFailureHandler;
import com.mercury.discovery.config.web.security.oauth.handler.OAuth2AuthenticationSuccessHandler;
import com.mercury.discovery.config.web.security.oauth.service.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.mercury.discovery.config.web.security.oauth.token.AuthTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@RequiredArgsConstructor
public class OAuthConfig {
    @Value("${apps.api.jwt.secret:8sknjlO3NPTBqo319DHLNqsQAfRJEdKsETOds}")   // default defaultSecretKey
    private String secretKey;

    @Bean
    public AuthTokenProvider tokenProvider() {
        return new AuthTokenProvider(secretKey);
    }

    /*
     * 토큰 필터 설정
     * */
    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider());
    }

    /*
     * 쿠키 기반 인가 Repository
     * 인가 응답을 연계 하고 검증할 때 사용.
     * */
    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    /*
     * Oauth 인증 성공 핸들러
     * */
    @Bean
    public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        return new OAuth2AuthenticationSuccessHandler(
                tokenProvider(),
                oAuth2AuthorizationRequestBasedOnCookieRepository()
        );
    }

    /*
     * Oauth 인증 실패 핸들러
     * */
    @Bean
    public OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler() {
        return new OAuth2AuthenticationFailureHandler(oAuth2AuthorizationRequestBasedOnCookieRepository());
    }
}
