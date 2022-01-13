package com.mercury.discovery.base.group.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
* cmm_app_group
* */

@Alias("AppGroup")
@Data
public class AppGroup {
    private Integer appGrpNo;
    private Integer cateId;
    private String appGrpCd;
    private String appGrpNm;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    private Integer createdBy;
    private Integer updatedBy;
    private Integer clientId;
    private String updEnableYn;
}
