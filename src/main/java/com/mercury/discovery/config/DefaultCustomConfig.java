package com.mercury.discovery.config;

import com.mercury.discovery.base.users.service.handler.CustomPasswordEncoder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * on-premise 형태의 외부 설치 형태의 프로젝트 인경우 해당 고객사의 기간계 등과
 * 연계할 수 있도록 Custom한 주요 Default 빈들을 정의 한다.
 * 해당 클래스에 Bean정의 시 외부에서 교체할 수 있도록
 * ConditionalOnMissingBean, ConditionalOnProperty 등을 처리하여 제어한다.
 */
@Configuration
public class DefaultCustomConfig {
    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        return new CustomPasswordEncoder();
    }
}
