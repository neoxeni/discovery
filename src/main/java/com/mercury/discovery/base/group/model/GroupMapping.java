package com.mercury.discovery.base.group.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * cmm_group_mapping
 */

@Alias("GroupMapping")
@Data
@EqualsAndHashCode(of = {"groupId", "dataGbn", "dataNo"})
public class GroupMapping {
    private Long id;

    private String dataGbn;
    private Integer dataNo;
    private String useYn;
    private Integer sort;

    private Integer createdBy;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private Long groupId;
}