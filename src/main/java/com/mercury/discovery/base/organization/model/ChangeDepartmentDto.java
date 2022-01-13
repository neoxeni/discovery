package com.mercury.discovery.base.organization.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChangeDepartmentDto {
    private String moveType;    //E: 직원, D:부서

    private Integer clientId;

    private Integer empNo;

    private Integer deptNo;

    private Integer pDeptNo;

    private Integer updatedBy;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;



    public Integer getpDeptNo() {
        return pDeptNo;
    }
}
