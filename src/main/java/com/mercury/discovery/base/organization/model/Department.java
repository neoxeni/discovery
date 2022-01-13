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
 * client_department
 */

@Alias("Department")
@Data
public class Department {
    private Long id;

    private String parentDepartmentKey;
    private String departmentKey;
    private String name;
    private int sort;
    private String useYn;

    private Integer createdBy;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private Integer updatedBy;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private Integer clientId;

    private List<UserRole> roles;
    private List<UserRole> parentsRoles;
}
