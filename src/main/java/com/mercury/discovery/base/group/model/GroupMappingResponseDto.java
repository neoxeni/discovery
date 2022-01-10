package com.mercury.discovery.base.group.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@EqualsAndHashCode(callSuper = true)
@Alias("GroupMappingResponseDto")
@Data
public class GroupMappingResponseDto extends GroupMapping{
    private String dataNm;
    private String tooltip;
    private String dataCd;
}
