package com.mercury.discovery.base.organization.model;

import lombok.Data;

@Data
public class OrganizationSearchDto {
    private String pageType;

    private int cmpnyNo;

    private int level;

    private int pDeptNo;

    private String str;

    private String useYn = "Y";

    private String rtrmntYn = "N";

    private String rootYn = "N";
}
