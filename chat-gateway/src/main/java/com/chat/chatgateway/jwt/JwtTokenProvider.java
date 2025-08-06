package com.chat.chatgateway.jwt;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtTokenProvider {

    private String SECRET;

    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${jwt.token-time}")
    private long ACCESS_TOKEN_TIME;

    @Value("${jwt.refresh.token-time}")
    private long ACCESS_REFRESH_TOKEN_TIME;
    @PostConstruct
    protected void init(){
        SECRET = Base64.getEncoder().encodeToString(SECRET_KEY.getBytes());
    }



    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
            return false;
        }
    }

    public Claims getClaimsFromJwtToken(String token) {
        try {
            return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
