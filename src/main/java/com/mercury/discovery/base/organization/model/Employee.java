package com.mercury.discovery.base.organization.model;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;


/**
 * cmm_emp
 */

@Alias("Employee")
@Data
public class Employee {
    // from cmm_emp
    private Integer empNo;
    private String cmpnyEmpCd;
    private Integer deptNo;
    private String empNm;
    private String extTelNo;
    private String hireDd;  //LocalDate
    private String rtrmntYn;
    private String rtrmntDd;//LocalDate

    private Integer createdBy;
    private LocalDateTime createdAt;
    private Integer updatedBy;
    private LocalDateTime updatedAt;

    private String phoneUserId;
    private String phoneUserTel;
    private String phoneExtensionNo;

    private String postnCd;
    private String dutyCd;
    private Integer clientId;
    private Integer empSort;
    private String email;

    private String lastIpAddress;
    private String empTp;
    private String realEmpNm;
    private String userKey;
}
