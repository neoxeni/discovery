package com.mercury.discovery.base.group.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * tb_cmm_group_map_hst
 */

@Data
public class GroupMappingHistory {
    private Integer seqNo;
    private Integer mapNo;
    private Integer grpNo;
    private String dataGbn;
    private Integer dataNo;

    private String action;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDt;
    private Integer regEmpNo;
    private String regIp;
    private Integer cmpnyNo;

    public void of(GroupMapping groupMapping){
        this.mapNo = groupMapping.getMapNo();
        this.grpNo = groupMapping.getGrpNo();
        this.dataGbn = groupMapping.getDataGbn();
        this.dataNo = groupMapping.getDataNo();
    }

    public void of(AppGroupMapping groupMapping){
        this.mapNo = groupMapping.getMapNo();
        this.grpNo = groupMapping.getAppGrpNo();
        this.dataGbn = groupMapping.getDataGbn();
        this.dataNo = groupMapping.getDataNo();
    }
}
