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
    private String code;
    private String name;
    //Group 객체와 동일한 이름:E

    //GroupMapping 객체와 동일한 이름:S
    private Integer mapNo;  //시퀀스
    private String target; //부서:D, 사원:E
    private Integer targetId; //부서:deptNo, 사원:empNo
    //GroupMapping 객체와 동일한 이름:E

    private Integer clientId;
}
