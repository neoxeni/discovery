package com.mercury.discovery.base.group.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * cmm_group_mapping_history
 */

@Data
public class GroupMappingHistory {
    private Long id;

    private Long groupId;
    private Long groupMappingId;

    private String dataGbn;
    private Integer dataNo;
    private String action;
    private String regIp;

    private Integer createdBy;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private Integer clientId;

    public void of(GroupMapping groupMapping) {
        this.groupMappingId = groupMapping.getGroupId();
        this.groupId = groupMapping.getGroupId();
        this.dataGbn = groupMapping.getDataGbn();
        this.dataNo = groupMapping.getDataNo();
    }
}
