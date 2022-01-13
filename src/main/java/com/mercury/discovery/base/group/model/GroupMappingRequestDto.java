package com.mercury.discovery.base.group.model;

import lombok.Data;

@Data
public class GroupMappingRequestDto {
    private Integer clientId;

    private Long groupId;

    private String code;

    private String target;

    private String dataNm;

    boolean noPageable;
}
