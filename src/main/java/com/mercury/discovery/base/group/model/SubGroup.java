package com.mercury.discovery.base.group.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/*
* cmm_sub_group
* */

@Alias("SubGroup")
@Data
public class SubGroup {
    private Integer subGrpNo;
    private String subGrpCd;
    private String subGrpNm;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    private Integer createdBy;
    private Integer updatedBy;
    private Integer clientId;
}
