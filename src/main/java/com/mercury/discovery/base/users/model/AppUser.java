package com.mercury.discovery.base.users.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.*;

@Alias("AppUser")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppUser extends TokenUser {
    private static final long serialVersionUID = -4937821332640048273L;

    private String username;
    @JsonIgnore
    private String password;
    private int passwordErrCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime passwordUpdatedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLogoutAt;

    private String lastIpAddress;
    private String providerType;

    private String nickname;
    private String phone;
    private String email;
    private String avatar;

    private String identification;
    private String extensionNo;
    private String positionCd;
    private String dutyCd;

    private int sort;
    private UserStatus status;

    private Integer createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private Integer updatedBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private Long departmentId;

    private String departmentName;
    private String positionName;
    private String dutyName;

    private List<UserGroup> groups = new ArrayList<>();
    public void setGroups(List<UserGroup> groups) {
        this.groups = groups;

        Set<String> roles = new HashSet<>();
        groups.forEach(group -> {
            roles.add(group.getCode());
        });
        setRoles(roles);
    }
}