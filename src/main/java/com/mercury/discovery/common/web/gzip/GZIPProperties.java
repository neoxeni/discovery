package com.mercury.discovery.common.web.gzip;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component("GZIPProperties")
@ConfigurationProperties(prefix = "gzip")
@Data
public class GZIPProperties {

    /*
    *
    * gzip:
        filter-patterns:
            - ${apps.request-mapping}/base/codes/scripts/*
            -
            -
    *
    *
    * */
    private Set<String> filterPatterns;
}
