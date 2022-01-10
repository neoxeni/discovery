package com.mercury.discovery.base.organization.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of={"type", "no"})
public class DeptEmpSearchDto {
    private String type;    //type D: 부서, E: 사원

    private String userKey;  // IR쪽 사용

    private int no;         //D일때 부서번호, E일때 사원번호

    private String name;    //D일때 부서명, E일때 사원명
}
