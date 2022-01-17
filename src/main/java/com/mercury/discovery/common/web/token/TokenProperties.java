package com.mercury.discovery.common.web.token;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "apps.api.jwt")
public class TokenProperties {
    private String secret;

    private Long expire;

    private Long refresh;
}
