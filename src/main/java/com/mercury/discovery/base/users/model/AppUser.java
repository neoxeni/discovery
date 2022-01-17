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

    @JsonIgnore
    private Collection<? extends GrantedAuthority> authorities;

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

    private Set<String> rolesSet;

    public boolean hasAnyRole(String... roles) {
        if (rolesSet == null) {
            return false;
        }

        for (String role : roles) {
            if (rolesSet.contains("ROLE_" + role) || rolesSet.contains(role)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasAuthority(Collection<? extends GrantedAuthority> authorities, String authority) {
        if (authorities == null) {
            return false;
        }

        for (GrantedAuthority grantedAuthority : authorities) {
            if (grantedAuthority.getAuthority().equals(authority)) {
                return true;
            }
        }
        return false;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        if (authorities != null) {
            this.authorities = authorities;
            this.rolesSet = new HashSet<>();
            new ArrayList<GrantedAuthority>(authorities).forEach(grantedAuthority -> rolesSet.add(grantedAuthority.getAuthority()));
        }
    }
}