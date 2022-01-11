package com.mercury.discovery.base.users.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Alias("AppUser")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppUser extends TokenUser implements UserDetails {
    private static final long serialVersionUID = -4937821332640048273L;

    @JsonIgnore
    private Collection<? extends GrantedAuthority> authorities;

    private String username;
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

    private String nickname;
    private String phone;
    private String email;

    private String identification;
    private String extensionNo;
    private String positionCd;
    private String dutyCd;

    private int sort;
    private String status;

    private LocalDate joinDate;
    private boolean isRetire;
    private LocalDate retireDate;

    private Integer createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private Integer updatedBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private Integer departmentId;

    private List<UserRole> roles;
    private List<UserAppRole> appRoles;
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

    public String getRolesJson() throws JsonProcessingException {
        Map<String, String> rolePairs = new HashMap<>();
        if (roles != null) {
            for (UserRole role : roles) {
                rolePairs.put(role.getGrpCd(), role.getGrpNm());
            }
        }

        if (appRoles != null) {
            for (UserAppRole role : appRoles) {
                rolePairs.put("$APP$_" + role.getAppGrpCd(), role.getAppGrpNm());
            }
        }
        return new ObjectMapper().writeValueAsString(rolePairs);
    }

    @Override
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

    // -------------- 계정에 대한 디테일한 설정 -----------------
    @Override
    public boolean isAccountNonExpired() {
        //org.springframework.security.authentication.AccountExpiredException: 사용자 계정의 유효 기간이 만료 되었습니다.
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        //org.springframework.security.authentication.LockedException: 사용자 계정이 잠겨 있습니다.
        return passwordErrCount < 6;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        //org.springframework.security.authentication.CredentialsExpiredException: 자격 증명 유효 기간이 만료되었습니다.
        LocalDateTime now = LocalDateTime.now();
        return now.isBefore(passwordUpdatedAt.plusMonths(6));
    }

    @Override
    public boolean isEnabled() {
        //org.springframework.security.authentication.DisabledException: 유효하지 않은 사용자입니다.
        return !"UNAUTHORIZED".equals(status);
    }
}