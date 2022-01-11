package com.mercury.discovery.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercury.discovery.base.users.model.TokenUser;
import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * jti: JWT의 고유 식별자로서, 주로 중복적인 처리를 방지하기 위하여 사용됩니다. 일회용 토큰에 사용하면 유용합니다.
 * appId
 * iss: 토큰 발급자 (issuer)
 * userId:clientId:userNo:UserType
 * sub: 토큰 제목 (subject)
 * 사용안함
 * aud: 토큰 대상자 (audience)
 * name
 * exp: 토큰의 만료시간 (expiraton), 시간은 NumericDate 형식으로 되어있어야 하며 (예: 1480849147370) 언제나 현재 시간보다 이후로 설정되어있어야합니다.
 * iat: 토큰이 발급된 시간 (issued at), 이 값을 사용하여 토큰의 age 가 얼마나 되었는지 판단 할 수 있습니다.
 * nbf: Not Before 를 의미하며, 토큰의 활성 날짜와 비슷한 개념입니다. 여기에도 NumericDate 형식으로 날짜를 지정하며, 이 날짜가 지나기 전까지는 토큰이 처리되지 않습니다.
 */

@Slf4j
@Component
public class JwtTokenProvider {

    @Getter
    @Value("${apps.api.jwt.secret:defaultSecretKey}")   // default defaultSecretKey
    private String secretKey;

    @Getter
    @Value("${apps.api.jwt.expire:3600000}")           // default 1시간(3600000) 1일(86400000)
    private long tokenValidMillisecond; // 1시간만 토큰 유효

    public Object triggerException(String authorization) {
        throw new JwtException("authorization header required. must follow the scheme 'authorization bearer jwt'. yours:" + authorization);
    }

    public TokenUser getRefreshUserFromJwt(String jwt) {
        try {
            String jwtWithoutPrefix = jwt.startsWith("bearer ") ? jwt.substring(7) : jwt;
            String[] jwts = jwtWithoutPrefix.split("\\.");
            String userInfo = new String(Base64.getDecoder().decode(jwts[1]));

            ObjectMapper mapper = new ObjectMapper();
            Map<?, ?> map = mapper.readValue(userInfo, Map.class);

            TokenUser user = new TokenUser();
            user.setUserKey((String) map.get("jti"));
            user.setName((String) map.get("aud"));
            String iss = (String) map.get("iss");

            String[] issuerSplit = iss.split(":");
            user.setId(Integer.parseInt(issuerSplit[0]));
            user.setClientId(Integer.parseInt(issuerSplit[1]));
            user.setName(issuerSplit[2]);
            String refreshJwt = getJwtFromUser(user, true);
            user.setJwt(refreshJwt);

            return user;
        } catch (IOException e) {
            throw new IllegalArgumentException("jwt is not validate");
        }
    }

    public String getJwtFromUser(TokenUser tokenUser, boolean isRefresh) {
        Date createdAt = isRefresh ? new Date() : new Date(tokenUser.getIssuedAt());
        Date expiredAt = isRefresh ? new Date(createdAt.getTime() + tokenValidMillisecond) : new Date(tokenUser.getExpiredAt());


        String jwt = Jwts.builder()
                .setIssuer(tokenUser.getId() + ":" + tokenUser.getClientId() + ":" + tokenUser.getName())
                .setSubject(IDGenerator.getUUID())  //고객의 경우 conversation ID로 사용
                .setId(tokenUser.getUserKey())      // api 아이디 (UUID)
                .setIssuedAt(createdAt)             // 토큰 발행일자
                .setExpiration(expiredAt)           // 유효시간 설정
                .setAudience(tokenUser.getName())   // 이름
                .signWith(SignatureAlgorithm.HS256, secretKey) // 암호화 알고리즘, secret값 세팅
                .compact();

        if (isRefresh) {
            tokenUser.setIssuedAt(createdAt.getTime());
            tokenUser.setExpiredAt(expiredAt.getTime());
            tokenUser.setJwt(jwt);
        }


        return jwt;
    }

    public String getSubject(TokenUser user) {
        String jwt = user.getJwt();
        String jwtWithoutPrefix = jwt.startsWith("bearer ") ? jwt.substring(7) : jwt;

        Jws<Claims> jws = getClaims(jwtWithoutPrefix);
        Claims claims = jws.getBody();

        return claims.getSubject();
    }

    public TokenUser getUserFromJwt(String jwt) {
        String jwtWithoutPrefix = jwt.startsWith("bearer ") || jwt.startsWith("Bearer ") ? jwt.substring(7) : jwt;

        Jws<Claims> jws = getClaims(jwtWithoutPrefix);
        Claims claims = jws.getBody();


        String issuer = claims.getIssuer();
        String[] issuerSplit = issuer.split(":");

        TokenUser user = new TokenUser();

        try {
            user.setId(Integer.parseInt(issuerSplit[0]));
        } catch (NumberFormatException nfe) {
            user.setId(0);
        }
        user.setClientId(Integer.parseInt(issuerSplit[1]));
        user.setName(issuerSplit[2]);

        user.setUserKey(claims.getId());
        user.setName(claims.getAudience());

        user.setJwt(jwt);
        user.setExpiredAt(claims.getExpiration().getTime());
        user.setIssuedAt(claims.getIssuedAt().getTime());

        return user;
    }

    public JwtBuilder jwtBuilder() {
        return Jwts.builder().signWith(SignatureAlgorithm.HS256, secretKey);
    }

    public Jws<Claims> getClaims(String jwt) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt);
        } catch (SignatureException ex) {
            log.error("Invalid Signature JWT signature {} {}", jwt, ex.getMessage());
            throw ex;
        } catch (MalformedJwtException ex) {
            log.error("Invalid Malformed JWT token {} {}", jwt, ex.getMessage());
            throw ex;
        } catch (ExpiredJwtException ex) {
            log.trace("Invalid Expired JWT token {} {}", jwt, ex.getMessage());//실 상황에서 너무 많이 찍혀 로그 레벨을 trace로
            throw ex;
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token {} {}", jwt, ex.getMessage());
            throw ex;
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty. {} {}", jwt, ex.getMessage());
            throw ex;
        }
    }
}
