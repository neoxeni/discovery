package com.mercury.discovery.base.organization.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.mercury.discovery.base.users.model.UserRole;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;
import java.util.List;

/**
 * cmm_dept
 */

@Alias("Department")
@Data
public class Department {
    private Integer deptNo;
    private Integer clientId;
    private String deptCd;
    private String deptNm;
    private Integer dpth;
    private Integer sort;
    private String useYn;
    private Integer pDeptNo;


    private Integer createdBy;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private Integer updatedBy;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private List<UserRole> roles;
    private List<UserRole> parentsRoles;

    public void setpDeptNo(Integer pDeptNo) {
        this.pDeptNo = pDeptNo;
    }

    public Integer getpDeptNo() {
        return pDeptNo;
    }


}
