package com.mercury.discovery.base.group.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * tb_cmm_group
 */

@Alias("Group")
@Data
public class Group {

    private Integer cateId;

    private Integer grpNo;
    private String grpCd;
    private String grpNm;
    private String useYn;
    private String cateNm;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDt;
    private Integer regUserNo;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updDt;
    private Integer updUserNo;

    private Integer cmpnyNo;
    private String updEnableYn;
    private String callcenterYn;
}
