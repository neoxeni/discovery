package com.mercury.discovery.base.users.model;

import lombok.Data;
import org.apache.ibatis.type.Alias;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;


@Alias("UserLogin")
@Data
public class UserLogin implements UserDetails, OidcUser{
    private Integer userId;
    private String providerType;
    private String username;
    private String password;
    private int passwordErrCount;
    private LocalDateTime passwordUpdatedAt;
    private UserStatus status;

    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    // -------------- UserDetails: 계정에 대한 디테일한 설정 -----------------
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
        return status == UserStatus.ACTIVE;
    }

    // -------------- OidcUser: OAuth 관련 설정 -----------------
    @Override
    public String getName() {
        return username;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Map<String, Object> getClaims() {
        return attributes;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return null;
    }

    @Override
    public OidcIdToken getIdToken() {
        return null;
    }
}
