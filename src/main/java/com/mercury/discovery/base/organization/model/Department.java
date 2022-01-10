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
 * tb_cmm_dept
 */

@Alias("Department")
@Data
public class Department {
    private Integer deptNo;
    private Integer cmpnyNo;
    private String deptCd;
    private String deptNm;
    private Integer dpth;
    private Integer sortNo;
    private String useYn;
    private Integer pDeptNo;


    private Integer regEmpNo;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDt;
    private Integer updEmpNo;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updDt;

    private List<UserRole> roles;
    private List<UserRole> parentsRoles;

    public void setpDeptNo(Integer pDeptNo) {
        this.pDeptNo = pDeptNo;
    }

    public Integer getpDeptNo() {
        return pDeptNo;
    }


}
