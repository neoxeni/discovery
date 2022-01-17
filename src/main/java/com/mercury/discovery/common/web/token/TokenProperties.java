package com.mercury.discovery.common.web.token;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "apps.api.jwt")
public class TokenProperties {
    private String secret;

    private String token;

    private Long expire;

    private Long refresh;
}
