package com.mercury.discovery.base.code.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * cmm_code
 */

@Alias("Code")
@Data
public class Code implements Serializable {
    private static final long serialVersionUID = 5435721294464776663L;

    private String cd;
    private String divCd;
    private String parentCd;
    private String cdNm;
    private Integer sortNo;
    private String dtl;
    private String useYn;

    private String etc1;
    private String etc2;
    private String etc3;
    private String etc4;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startApplyAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endApplyAt;

    private Integer createdBy;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private Integer updatedBy;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    private Integer clientId;
}
