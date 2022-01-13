package com.mercury.discovery.base.organization.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mercury.discovery.base.users.model.UserGroup;
import lombok.Data;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * client_department
 */

@Alias("Department")
@Data
public class Department implements Serializable {
    private static final long serialVersionUID = -2985321314856024758L;
    private Long id;

    private String parentDepartmentKey;
    private String departmentKey;
    private String name;
    private int sort;
    private String useYn;

    private Integer createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private Integer updatedBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private Integer clientId;

    private List<UserGroup> groups;
    private List<UserGroup> parentsGroups;
}
