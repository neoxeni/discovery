package com.mercury.discovery.common.web.token;

import com.mercury.discovery.base.users.model.AppUser;
import com.mercury.discovery.base.users.model.TokenUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthTokenProvider {
    private Key secretKey;
    private final TokenProperties tokenProperties;
    private static final String AUTHORITIES_KEY = "authorities";

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes());
    }

    public TokenProperties getTokenProperties() {
        return tokenProperties;
    }

    public AuthToken convertAuthToken(String token) {
        return new AuthToken(token, secretKey);
    }

    public JwtBuilder getBuilder() {
        return Jwts.builder().signWith(secretKey).setHeaderParam("typ", "JWT");
    }


    public AuthToken getAuthToken(TokenUser tokenUser) {
        Date now = new Date();
        TokenProperties tokenProperties = getTokenProperties();
        String accessToken = getBuilder()
                .setId(tokenUser.getUserKey())                                          //jti
                .setSubject(tokenUser.getClientId() + ":" + tokenUser.getId())          //sub
                .setAudience(tokenUser.getName())                                       //aud
                .setIssuedAt(now)                                                       //iat
                .setExpiration(new Date(now.getTime() + tokenProperties.getExpire()))   //exp
                .claim("authorities", tokenUser.getRoles())
                .compact();

        String refreshToken = getBuilder()
                .setId(tokenUser.getUserKey())
                .setExpiration(new Date(now.getTime() + tokenProperties.getRefresh()))
                .compact();

        return new AuthToken(accessToken, secretKey, refreshToken);
    }

    public Authentication getAuthentication(AuthToken authToken) {
        if (authToken.validate()) {
            Claims claims = authToken.getTokenClaims();
            String sub = claims.get("sub", String.class);
            String[] subSp = sub.split(":");
            List<?> authorities = claims.get("authorities", List.class);

            AppUser tokenUser = new AppUser();
            tokenUser.setClientId(Integer.parseInt(subSp[0]));
            tokenUser.setId(Integer.parseInt(subSp[1]));
            tokenUser.setName(claims.get("aud", String.class));
            tokenUser.setUserKey(claims.get("jti", String.class));
            tokenUser.setRoles(new HashSet(authorities));
            tokenUser.setToken(authToken.getAccessToken());

            List<GrantedAuthority> sbAuthorities = new ArrayList<>();
            authorities.forEach(role -> {
                sbAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            });

            return new UsernamePasswordAuthenticationToken(tokenUser, authToken, sbAuthorities);
        } else {
            throw new TokenValidFailedException();
        }
    }
}
