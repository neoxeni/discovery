package com.mercury.discovery.base.group.model;

import lombok.Data;

@Data
public class GroupMappingRequestDto {
    private Integer clientId;

    private Integer grpNo;

    private String grpCd;

    private String dataGbn;

    private String dataNm;

    boolean noPageable;
}
