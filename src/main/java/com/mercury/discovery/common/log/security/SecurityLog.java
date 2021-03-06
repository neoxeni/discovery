package com.mercury.discovery.common.log.security;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class SecurityLog {
    private Long id;
    private Integer userId;
    private String ip;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private String menu;
    private String subMenu;
    private String action;
    private String actionUrl;
    private String inputVal;
    private String language;
    private String etc1;
    private String etc2;
    private String etc3;
    private String etc4;
    private String etc5;
    private String divCd;



    private Integer clientId;
}
