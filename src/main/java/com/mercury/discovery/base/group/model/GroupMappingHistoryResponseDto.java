package com.mercury.discovery.base.group.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@EqualsAndHashCode(callSuper = true)
@Alias("GroupMappingHistoryResponseDto")
@Data
public class GroupMappingHistoryResponseDto extends GroupMappingHistory{
    private String name;
    private String dataNm;
    private String regEmpNm;
}
