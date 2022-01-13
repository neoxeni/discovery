package com.mercury.discovery.base.group.model;

import lombok.Data;

@Data
public class GroupMappingRequestDto {
    private Integer clientId;

    private Long grpNo;

    private String code;

    private String dataGbn;

    private String dataNm;

    boolean noPageable;
}
