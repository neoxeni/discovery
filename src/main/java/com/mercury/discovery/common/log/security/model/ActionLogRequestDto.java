package com.mercury.discovery.common.log.security.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActionLogRequestDto {
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    private Integer clientId;
    private String divCd;
    private String userNm;
    private String userId;
    private String ip;
}
