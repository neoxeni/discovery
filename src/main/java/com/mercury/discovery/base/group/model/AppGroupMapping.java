package com.mercury.discovery.base.group.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * tb_cmm_app_group_map
 */

@Alias("AppGroupMapping")
@Data
@EqualsAndHashCode(of = {"appGrpNo", "dataGbn", "dataNo"})
public class AppGroupMapping {
    private Integer mapNo;
    private Integer appGrpNo;
    private String appGrpNm;
    private String dataGbn;
    private Integer dataNo;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDt;
    private Integer regUserNo;
    private String useYn;
    private Integer sort;
    private String updEnableYn;

}
