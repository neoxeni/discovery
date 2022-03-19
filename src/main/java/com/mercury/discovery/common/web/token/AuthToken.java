package com.mercury.discovery.common.web.token;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;

@Slf4j
public class AuthToken {
    private final String accessToken;

    private final Key key;

    private final String refreshToken;

    public AuthToken(String token, Key key) {
        this(token, key, null);
    }

    public AuthToken(String accessToken, Key key, String refreshToken) {
        this.accessToken = accessToken;
        this.key = key;
        this.refreshToken = refreshToken;
    }

    public boolean validate() {
        return this.getTokenClaims() != null;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public Claims getTokenClaims() {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (SecurityException e) {
            log.info("Invalid JWT signature.");
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
        }
        return null;
    }

    public Claims getExpiredTokenClaims() {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            return e.getClaims();
        }
        return null;
    }
}
