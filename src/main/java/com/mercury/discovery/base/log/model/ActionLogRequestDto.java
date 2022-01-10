package com.mercury.discovery.base.log.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActionLogRequestDto {
    private Integer cmpnyNo;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    private String divCd;
    private String empNm;
    private String userId;
    private String ip;
}
