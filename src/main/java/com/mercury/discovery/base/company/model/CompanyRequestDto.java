package com.mercury.discovery.base.company.model;

import lombok.Data;

@Data
public class CompanyRequestDto {
    private Integer clientId;


    private String status;//0:비활성, 1:활성
}
