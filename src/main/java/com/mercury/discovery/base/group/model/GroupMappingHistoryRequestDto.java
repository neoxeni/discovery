package com.mercury.discovery.base.group.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GroupMappingHistoryRequestDto {
    private Integer cmpnyNo;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    private String action;
    private Integer grpNo;
    private String dataGbn;
    private String dataNm;
    private String regEmpNm;
}
