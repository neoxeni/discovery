package com.mercury.discovery.base.users.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Alias("AppUser")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppUser extends TokenUser implements UserDetails {
    private static final long serialVersionUID = -4937821332640048273L;

    @JsonIgnore
    private Collection<? extends GrantedAuthority> authorities;

    private String accessToken;

    private Map<String, Object> external;

    // from tb_cmm_login
    //private String username;  // principal - same as id
    private String psswd;  // credential
    private Integer psswdErrNum;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime psswdUpdDt;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginDt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLogoutDt;

    private String lastIpAddress;


    // from tb_cmm_emp
    private Integer empNo;
    private String cmpnyEmpCd;
    private Integer deptNo;
    private String empNm;
    private String extTelNo;
    private String hireDd;  //LocalDate
    private String rtrmntYn;
    private String rtrmntDd;//LocalDate

    private Integer regEmpNo;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDt;

    private Integer updEmpNo;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updDt;

    private String phoneUserId;
    private String phoneUserTel;
    private String phoneExtensionNo;

    private String postnCd;
    private String dutyCd;
    private Integer cmpnyNo;
    private Integer empSort;
    private String email;

    private String empTp;
    private String realEmpNm;
    private String userKey;

    // for nm (code, dept)
    private String deptNm;
    private String postnNm;
    private String dutyNm;
    private String deptCd;

    //for company
    private String status;

    private List<UserRole> roles;
    private List<UserAppRole> appRoles;
    private Set<String> rolesSet;

    //tb_ir_agent에서 가져오는 상담사별 개인 옵션:S
    private String chatAcceptAutoYn = "N";    //채팅 자동 수락 여부
    private int chatAcceptMaxCnt;          //채팅 최대 동시 진행 상담 건수
    //tb_ir_agent에서 가져오는 상담사별 개인 옵션:E

    private boolean isIdle = false;


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
            for (int i = 0, ic = roles.size(); i < ic; i++) {
                UserRole role = roles.get(i);
                rolePairs.put(role.getGrpCd(), role.getGrpNm());
                /*List<UserAppRole> appRoles = role.getAppRoles();
                if (appRoles != null) {
                    for (UserAppRole appRole : appRoles) {
                        rolePairs.put("APP_" + appRole.getAppGrpCd(), appRole.getAppGrpNm());
                    }
                }*/
            }
        }

        if (appRoles != null) {
            for (int i = 0, ic = appRoles.size(); i < ic; i++) {
                UserAppRole role = appRoles.get(i);
                rolePairs.put("$APP$_" + role.getAppGrpCd(), role.getAppGrpNm());
            }
        }
        return new ObjectMapper().writeValueAsString(rolePairs);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // ROLE
        return authorities;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        if (authorities != null) {
            this.authorities = authorities;
            this.rolesSet = new HashSet<>();
            new ArrayList<GrantedAuthority>(authorities).forEach(grantedAuthority -> rolesSet.add(grantedAuthority.getAuthority()));
        }
    }

    // -------------- 계정에 아이디 검증 -----------------
    @Override
    public String getPassword() {
        return getPsswd();
    }

    @Override
    public String getUsername() {
        return getId();
    }
    // -----------------------------------------------


    // -------------- 계정에 대한 디테일한 설정 -----------------
    @Override
    public boolean isAccountNonExpired() {
        //org.springframework.security.authentication.AccountExpiredException: 사용자 계정의 유효 기간이 만료 되었습니다.
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        //org.springframework.security.authentication.LockedException: 사용자 계정이 잠겨 있습니다.
        return psswdErrNum < 6;
        //return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        //org.springframework.security.authentication.CredentialsExpiredException: 자격 증명 유효 기간이 만료되었습니다.
        LocalDateTime now = LocalDateTime.now();
        return now.isBefore(psswdUpdDt.plusMonths(6));
        //return true;
    }

    @Override
    public boolean isEnabled() {
        //org.springframework.security.authentication.DisabledException: 유효하지 않은 사용자입니다.
        return !"UNAUTHORIZED".equals(status);
        //return true;
    }
    // -----------------------------------------------
}