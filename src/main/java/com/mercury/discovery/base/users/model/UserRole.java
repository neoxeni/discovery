package com.mercury.discovery.base.users.model;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;

@Alias("UserRole")
@Data
public class UserRole implements Serializable {

    private static final long serialVersionUID = -5343591561182333422L;

    //Group 객체와 동일한 이름:S
    private Integer grpNo;
    private String grpCd;
    private String grpNm;
    //Group 객체와 동일한 이름:E

    //GroupMapping 객체와 동일한 이름:S
    private Integer mapNo;  //시퀀스
    private String dataGbn; //부서:D, 사원:E
    private Integer dataNo; //부서:deptNo, 사원:empNo
    //GroupMapping 객체와 동일한 이름:E

    private Integer clientId;
}
